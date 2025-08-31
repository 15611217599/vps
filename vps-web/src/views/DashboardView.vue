<template>
  <PageLayout>
    <v-container class="py-8">
      <!-- 统计概览 -->
      <v-row class="mb-6">
        <v-col cols="12" sm="6" md="3">
          <v-card elevation="2" class="text-center pa-4">
            <v-icon size="48" :color="themeStore.currentColors.primary" class="mb-2">
              mdi-server
            </v-icon>
            <div class="text-h4 font-weight-bold">{{ stats.totalServers }}</div>
            <div class="text-body-2 text-medium-emphasis">总服务器数</div>
          </v-card>
        </v-col>
        
        <v-col cols="12" sm="6" md="3">
          <v-card elevation="2" class="text-center pa-4">
            <v-icon size="48" :color="themeStore.currentColors.success" class="mb-2">
              mdi-check-circle
            </v-icon>
            <div class="text-h4 font-weight-bold">{{ stats.activeServers }}</div>
            <div class="text-body-2 text-medium-emphasis">活跃服务器</div>
          </v-card>
        </v-col>
        
        <v-col cols="12" sm="6" md="3">
          <v-card elevation="2" class="text-center pa-4">
            <v-icon size="48" :color="themeStore.currentColors.warning" class="mb-2">
              mdi-pause-circle
            </v-icon>
            <div class="text-h4 font-weight-bold">{{ stats.suspendedServers }}</div>
            <div class="text-body-2 text-medium-emphasis">暂停服务器</div>
          </v-card>
        </v-col>
        
        <v-col cols="12" sm="6" md="3">
          <v-card elevation="2" class="text-center pa-4">
            <v-icon size="48" :color="themeStore.currentColors.info" class="mb-2">
              mdi-clock-outline
            </v-icon>
            <div class="text-h4 font-weight-bold">{{ stats.expiringServers }}</div>
            <div class="text-body-2 text-medium-emphasis">即将到期</div>
          </v-card>
        </v-col>
      </v-row>

      <!-- 服务器列表 -->
      <v-card elevation="2">
        <v-card-title class="d-flex align-center justify-space-between">
          <div class="d-flex align-center">
            <v-icon :color="themeStore.currentColors.primary" class="me-2">mdi-server-network</v-icon>
            服务器列表
          </div>
          
          <div class="d-flex align-center gap-2">
            <v-text-field
              v-model="searchQuery"
              placeholder="搜索服务器"
              prepend-inner-icon="mdi-magnify"
              variant="outlined"
              density="compact"
              hide-details
              class="me-2"
              style="max-width: 300px;"
            />
            
            <v-select
              v-model="statusFilter"
              :items="statusOptions"
              placeholder="筛选状态"
              variant="outlined"
              density="compact"
              hide-details
              style="min-width: 150px;"
            />
            
            <v-btn
              :color="themeStore.currentColors.primary"
              @click="refreshServers"
              :loading="loading"
            >
              <v-icon>mdi-refresh</v-icon>
            </v-btn>
          </div>
        </v-card-title>
        
        <v-divider />
        
        <v-card-text class="pa-0">
          <v-data-table
            :headers="headers"
            :items="filteredServers"
            :loading="loading"
            :items-per-page="10"
            class="elevation-0"
          >
            <template v-slot:item.status="{ item }">
              <v-chip
                :color="getStatusColor(item.status)"
                size="small"
                variant="elevated"
              >
                <v-icon start size="16">{{ getStatusIcon(item.status) }}</v-icon>
                {{ getStatusText(item.status) }}
              </v-chip>
            </template>
            
            <template v-slot:item.serverInfo="{ item }">
              <div class="d-flex flex-column">
                <div class="font-weight-bold">{{ item.serverName || item.orderNumber }}</div>
                <div class="text-caption text-medium-emphasis">
                  {{ item.cpuCores }}C / {{ item.memoryGb }}GB / {{ item.storageGb }}GB
                </div>
              </div>
            </template>
            
            <template v-slot:item.osInfo="{ item }">
              <div class="d-flex align-center">
                <v-icon class="me-2" size="20">{{ getOsIcon(item.osName) }}</v-icon>
                <div>
                  <div>{{ item.osName }}</div>
                  <div class="text-caption text-medium-emphasis">{{ item.osVersion }}</div>
                </div>
              </div>
            </template>
            
            <template v-slot:item.expiresAt="{ item }">
              <div class="d-flex flex-column">
                <div>{{ formatDate(item.expiresAt) }}</div>
                <div class="text-caption" :class="getExpiryClass(item.expiresAt)">
                  {{ getExpiryText(item.expiresAt) }}
                </div>
              </div>
            </template>
            
            <template v-slot:item.actions="{ item }">
              <div class="d-flex gap-1">
                <v-btn
                  size="small"
                  variant="text"
                  :color="themeStore.currentColors.primary"
                  @click="viewServerDetails(item)"
                >
                  <v-icon>mdi-eye</v-icon>
                </v-btn>
                
                <v-btn
                  v-if="item.status === 'ACTIVE'"
                  size="small"
                  variant="text"
                  :color="themeStore.currentColors.success"
                  @click="connectToServer(item)"
                >
                  <v-icon>mdi-console</v-icon>
                </v-btn>
                
                <v-btn
                  v-if="item.status === 'ACTIVE' && item.server"
                  size="small"
                  variant="text"
                  :color="getInstallButtonColor(item.server)"
                  @click="handleServerAction(item)"
                  :title="getInstallButtonTitle(item.server)"
                >
                  <v-icon>{{ getInstallButtonIcon(item.server) }}</v-icon>
                </v-btn>
                
                <v-btn
                  size="small"
                  variant="text"
                  :color="themeStore.currentColors.info"
                  @click="manageServer(item)"
                >
                  <v-icon>mdi-cog</v-icon>
                </v-btn>
              </div>
            </template>
            
            <template v-slot:no-data>
              <div class="text-center py-8">
                <v-icon size="64" color="grey-lighten-2" class="mb-4">
                  mdi-server-off
                </v-icon>
                <div class="text-h6 text-medium-emphasis mb-2">
                  暂无服务器
                </div>
                <div class="text-body-2 text-medium-emphasis mb-4">
                  您还没有购买任何服务器，立即购买开始使用吧
                </div>
                <v-btn
                  :color="themeStore.currentColors.primary"
                  @click="router.push('/sales')"
                >
                  购买服务器
                </v-btn>
              </div>
            </template>
          </v-data-table>
        </v-card-text>
      </v-card>
    </v-container>
    
    <!-- 服务器详情对话框 -->
    <v-dialog v-model="detailDialog" max-width="800">
      <v-card v-if="selectedServer">
        <v-card-title class="d-flex align-center">
          <v-icon :color="themeStore.currentColors.primary" class="me-2">mdi-server</v-icon>
          {{ selectedServer.serverName || selectedServer.orderNumber }}
        </v-card-title>
        
        <v-divider />
        
        <v-card-text class="pa-6">
          <v-row>
            <v-col cols="12" md="6">
              <div class="mb-4">
                <div class="text-subtitle-2 text-medium-emphasis mb-1">订单号</div>
                <div class="font-weight-bold">{{ selectedServer.orderNumber }}</div>
              </div>
              
              <div class="mb-4">
                <div class="text-subtitle-2 text-medium-emphasis mb-1">状态</div>
                <v-chip :color="getStatusColor(selectedServer.status)" size="small">
                  {{ getStatusText(selectedServer.status) }}
                </v-chip>
              </div>
              
              <div class="mb-4">
                <div class="text-subtitle-2 text-medium-emphasis mb-1">计费周期</div>
                <div>{{ getBillingPeriodText(selectedServer.billingPeriod) }}</div>
              </div>
            </v-col>
            
            <v-col cols="12" md="6">
              <div class="mb-4">
                <div class="text-subtitle-2 text-medium-emphasis mb-1">服务器规格</div>
                <div>{{ selectedServer.cpuCores }}C / {{ selectedServer.memoryGb }}GB RAM / {{ selectedServer.storageGb }}GB</div>
              </div>
              
              <div class="mb-4">
                <div class="text-subtitle-2 text-medium-emphasis mb-1">操作系统</div>
                <div class="d-flex align-center">
                  <v-icon class="me-2" size="20">{{ getOsIcon(selectedServer.osName) }}</v-icon>
                  {{ selectedServer.osName }} {{ selectedServer.osVersion }}
                </div>
              </div>
              
              <div class="mb-4">
                <div class="text-subtitle-2 text-medium-emphasis mb-1">到期时间</div>
                <div :class="getExpiryClass(selectedServer.expiresAt)">
                  {{ formatDate(selectedServer.expiresAt) }}
                </div>
              </div>
            </v-col>
          </v-row>
        </v-card-text>
        
        <v-card-actions class="px-6 pb-6">
          <v-spacer />
          <v-btn
            variant="text"
            @click="detailDialog = false"
          >
            关闭
          </v-btn>
          
          <v-btn
            v-if="selectedServer.status === 'ACTIVE'"
            :color="themeStore.currentColors.success"
            @click="connectToServer(selectedServer)"
          >
            <v-icon start>mdi-console</v-icon>
            连接
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- 安装系统对话框 -->
    <ServerInstallDialog
      v-model="installDialog"
      :server-id="selectedServerId"
      @installed="onInstallComplete"
    />
  </PageLayout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useThemeStore } from '@/stores/theme'
import { useRouter } from 'vue-router'
import PageLayout from '@/components/PageLayout.vue'
import ServerInstallDialog from '@/components/ServerInstallDialog.vue'
import { orderApi, type OrderDTO } from '@/api/order'
const themeStore = useThemeStore()
const router = useRouter()

// 响应式数据
const loading = ref(false)
const searchQuery = ref('')
const statusFilter = ref('')
const servers = ref<OrderDTO[]>([])
const detailDialog = ref(false)
const selectedServer = ref<OrderDTO | null>(null)
const installDialog = ref(false)
const selectedServerId = ref<number>(0)

// 状态选项
const statusOptions = computed(() => [
  { title: '全部状态', value: '' },
  { title: '待付款', value: 'PENDING' },
  { title: '已付款', value: 'PAID' },
  { title: '处理中', value: 'PROCESSING' },
  { title: '活跃', value: 'ACTIVE' },
  { title: '暂停', value: 'SUSPENDED' },
  { title: '已取消', value: 'CANCELLED' },
  { title: '已过期', value: 'EXPIRED' }
])

// 表格头部
const headers = computed(() => [
  { title: '服务器信息', key: 'serverInfo', sortable: false },
  { title: '状态', key: 'status', sortable: true },
  { title: '操作系统', key: 'osInfo', sortable: false },
  { title: '到期时间', key: 'expiresAt', sortable: true },
  { title: '操作', key: 'actions', sortable: false, align: 'center' as const }
])

// 过滤后的服务器列表
const filteredServers = computed(() => {
  let filtered = servers.value
  
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(server => 
      server.orderNumber.toLowerCase().includes(query) ||
      server.serverName?.toLowerCase().includes(query) ||
      server.osName.toLowerCase().includes(query)
    )
  }
  
  if (statusFilter.value) {
    filtered = filtered.filter(server => server.status === statusFilter.value)
  }
  
  return filtered
})

// 统计信息
const stats = computed(() => {
  const total = servers.value.length
  const active = servers.value.filter(s => s.status === 'ACTIVE').length
  const suspended = servers.value.filter(s => s.status === 'SUSPENDED').length
  const expiring = servers.value.filter(s => {
    const expiryDate = new Date(s.expiresAt)
    const now = new Date()
    const daysUntilExpiry = Math.ceil((expiryDate.getTime() - now.getTime()) / (1000 * 60 * 60 * 24))
    return daysUntilExpiry <= 7 && daysUntilExpiry > 0
  }).length
  
  return {
    totalServers: total,
    activeServers: active,
    suspendedServers: suspended,
    expiringServers: expiring
  }
})

// 获取状态颜色
const getStatusColor = (status: string) => {
  switch (status) {
    case 'ACTIVE': return themeStore.currentColors.success
    case 'SUSPENDED': return themeStore.currentColors.warning
    case 'CANCELLED': case 'EXPIRED': return themeStore.currentColors.error
    case 'PROCESSING': return themeStore.currentColors.info
    default: return themeStore.currentColors.secondary
  }
}

// 获取状态图标
const getStatusIcon = (status: string) => {
  switch (status) {
    case 'ACTIVE': return 'mdi-check-circle'
    case 'SUSPENDED': return 'mdi-pause-circle'
    case 'CANCELLED': case 'EXPIRED': return 'mdi-close-circle'
    case 'PROCESSING': return 'mdi-clock-outline'
    default: return 'mdi-help-circle'
  }
}

// 获取操作系统图标
const getOsIcon = (osName: string) => {
  const os = osName.toLowerCase()
  if (os.includes('ubuntu') || os.includes('debian')) return 'mdi-ubuntu'
  if (os.includes('centos') || os.includes('redhat') || os.includes('rocky') || os.includes('almalinux')) return 'mdi-redhat'
  if (os.includes('windows')) return 'mdi-microsoft-windows'
  if (os.includes('alpine')) return 'mdi-docker'
  return 'mdi-linux'
}

// 格式化日期
const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}

// 获取到期时间样式类
const getExpiryClass = (expiresAt: string) => {
  const expiryDate = new Date(expiresAt)
  const now = new Date()
  const daysUntilExpiry = Math.ceil((expiryDate.getTime() - now.getTime()) / (1000 * 60 * 60 * 24))
  
  if (daysUntilExpiry <= 0) return 'text-error'
  if (daysUntilExpiry <= 7) return 'text-warning'
  return 'text-success'
}

// 获取到期时间文本
const getExpiryText = (expiresAt: string) => {
  const expiryDate = new Date(expiresAt)
  const now = new Date()
  const daysUntilExpiry = Math.ceil((expiryDate.getTime() - now.getTime()) / (1000 * 60 * 60 * 24))
  
  if (daysUntilExpiry <= 0) return '已过期'
  if (daysUntilExpiry <= 7) return `${daysUntilExpiry}天后到期`
  return `剩余${daysUntilExpiry}天`
}

// 刷新服务器列表
const refreshServers = async () => {
  loading.value = true
  try {
    const response = await orderApi.getUserOrders()
    if (response.success) {
      servers.value = response.data || []
    }
  } catch (error) {
    console.error('Failed to load servers:', error)
  } finally {
    loading.value = false
  }
}

// 查看服务器详情
const viewServerDetails = (server: OrderDTO) => {
  selectedServer.value = server
  detailDialog.value = true
}

// 连接到服务器
const connectToServer = (server: OrderDTO) => {
  // TODO: 实现SSH连接功能
  console.log('Connect to server:', server)
}

// 管理服务器
const manageServer = (server: OrderDTO) => {
  router.push(`/server-manage/${server.id}`)
}

// 处理服务器操作（安装或查看进度）
const handleServerAction = (server: OrderDTO) => {
  if (!server.server) return
  
  if (server.server.installStatus === 'INSTALLING') {
    // 如果正在安装，跳转到进度页面
    router.push(`/server-install-progress/${server.server.id}`)
  } else {
    // 否则打开安装对话框
    selectedServerId.value = server.server.id
    installDialog.value = true
  }
}

// 获取安装按钮颜色
const getInstallButtonColor = (server: any) => {
  if (!server) return 'grey'
  
  switch (server.installStatus) {
    case 'INSTALLING': return 'warning'
    case 'COMPLETED': return 'success'
    case 'FAILED': return 'error'
    default: return 'primary'
  }
}

// 获取安装按钮图标
const getInstallButtonIcon = (server: any) => {
  if (!server) return 'mdi-cog-sync'
  
  switch (server.installStatus) {
    case 'INSTALLING': return 'mdi-progress-clock'
    case 'COMPLETED': return 'mdi-check-circle'
    case 'FAILED': return 'mdi-alert-circle'
    default: return 'mdi-cog-sync'
  }
}

// 获取安装按钮标题
const getInstallButtonTitle = (server: any) => {
  if (!server) return '安装系统'
  
  switch (server.installStatus) {
    case 'INSTALLING': return '查看安装进度'
    case 'COMPLETED': return '重新安装系统'
    case 'FAILED': return '重新安装系统'
    default: return '安装系统'
  }
}

// 安装完成回调
const onInstallComplete = () => {
  refreshServers()
}

// 获取状态文本
const getStatusText = (status: string) => {
  const statusMap: Record<string, string> = {
    'PENDING': '待付款',
    'PAID': '已付款',
    'PROCESSING': '处理中',
    'ACTIVE': '活跃',
    'SUSPENDED': '暂停',
    'CANCELLED': '已取消',
    'EXPIRED': '已过期'
  }
  return statusMap[status] || status
}

// 获取计费周期文本
const getBillingPeriodText = (period: string) => {
  const periodMap: Record<string, string> = {
    'HOURLY': '按小时',
    'DAILY': '按天',
    'WEEKLY': '按周',
    'MONTHLY': '按月',
    'QUARTERLY': '按季度',
    'YEARLY': '按年'
  }
  return periodMap[period] || period
}

// 组件挂载时加载数据
onMounted(() => {
  refreshServers()
})
</script>

<style scoped>
.cursor-pointer {
  cursor: pointer;
}

.cursor-pointer:hover {
  transform: translateY(-2px);
  transition: transform 0.2s ease;
}
</style>