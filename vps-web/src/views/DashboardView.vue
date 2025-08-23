<template>
  <PageLayout :title="$t('dashboard.myServers')">
    <v-container class="py-8">
      <!-- 统计概览 -->
      <v-row class="mb-6">
        <v-col cols="12" sm="6" md="3">
          <v-card elevation="2" class="text-center pa-4">
            <v-icon size="48" :color="themeStore.currentColors.primary" class="mb-2">
              mdi-server
            </v-icon>
            <div class="text-h4 font-weight-bold">{{ stats.totalServers }}</div>
            <div class="text-body-2 text-medium-emphasis">{{ $t('dashboard.totalServers') }}</div>
          </v-card>
        </v-col>
        
        <v-col cols="12" sm="6" md="3">
          <v-card elevation="2" class="text-center pa-4">
            <v-icon size="48" :color="themeStore.currentColors.success" class="mb-2">
              mdi-check-circle
            </v-icon>
            <div class="text-h4 font-weight-bold">{{ stats.activeServers }}</div>
            <div class="text-body-2 text-medium-emphasis">{{ $t('dashboard.activeServers') }}</div>
          </v-card>
        </v-col>
        
        <v-col cols="12" sm="6" md="3">
          <v-card elevation="2" class="text-center pa-4">
            <v-icon size="48" :color="themeStore.currentColors.warning" class="mb-2">
              mdi-pause-circle
            </v-icon>
            <div class="text-h4 font-weight-bold">{{ stats.suspendedServers }}</div>
            <div class="text-body-2 text-medium-emphasis">{{ $t('dashboard.suspendedServers') }}</div>
          </v-card>
        </v-col>
        
        <v-col cols="12" sm="6" md="3">
          <v-card elevation="2" class="text-center pa-4">
            <v-icon size="48" :color="themeStore.currentColors.info" class="mb-2">
              mdi-clock-outline
            </v-icon>
            <div class="text-h4 font-weight-bold">{{ stats.expiringServers }}</div>
            <div class="text-body-2 text-medium-emphasis">{{ $t('dashboard.expiringServers') }}</div>
          </v-card>
        </v-col>
      </v-row>

      <!-- 服务器列表 -->
      <v-card elevation="2">
        <v-card-title class="d-flex align-center justify-space-between">
          <div class="d-flex align-center">
            <v-icon :color="themeStore.currentColors.primary" class="me-2">mdi-server-network</v-icon>
            {{ $t('dashboard.serverList') }}
          </div>
          
          <div class="d-flex align-center gap-2">
            <v-text-field
              v-model="searchQuery"
              :placeholder="$t('dashboard.searchServers')"
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
              :placeholder="$t('dashboard.filterStatus')"
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
                {{ $t(`order.status.${item.status.toLowerCase()}`) }}
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
                  {{ $t('dashboard.noServers') }}
                </div>
                <div class="text-body-2 text-medium-emphasis mb-4">
                  {{ $t('dashboard.noServersDescription') }}
                </div>
                <v-btn
                  :color="themeStore.currentColors.primary"
                  @click="router.push('/sales')"
                >
                  {{ $t('dashboard.orderServer') }}
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
                <div class="text-subtitle-2 text-medium-emphasis mb-1">{{ $t('dashboard.orderNumber') }}</div>
                <div class="font-weight-bold">{{ selectedServer.orderNumber }}</div>
              </div>
              
              <div class="mb-4">
                <div class="text-subtitle-2 text-medium-emphasis mb-1">{{ $t('dashboard.status') }}</div>
                <v-chip :color="getStatusColor(selectedServer.status)" size="small">
                  {{ $t(`order.status.${selectedServer.status.toLowerCase()}`) }}
                </v-chip>
              </div>
              
              <div class="mb-4">
                <div class="text-subtitle-2 text-medium-emphasis mb-1">{{ $t('dashboard.billingPeriod') }}</div>
                <div>{{ $t(`order.billingPeriod.${selectedServer.billingPeriod}`) }}</div>
              </div>
            </v-col>
            
            <v-col cols="12" md="6">
              <div class="mb-4">
                <div class="text-subtitle-2 text-medium-emphasis mb-1">{{ $t('dashboard.serverSpecs') }}</div>
                <div>{{ selectedServer.cpuCores }}C / {{ selectedServer.memoryGb }}GB RAM / {{ selectedServer.storageGb }}GB</div>
              </div>
              
              <div class="mb-4">
                <div class="text-subtitle-2 text-medium-emphasis mb-1">{{ $t('dashboard.operatingSystem') }}</div>
                <div class="d-flex align-center">
                  <v-icon class="me-2" size="20">{{ getOsIcon(selectedServer.osName) }}</v-icon>
                  {{ selectedServer.osName }} {{ selectedServer.osVersion }}
                </div>
              </div>
              
              <div class="mb-4">
                <div class="text-subtitle-2 text-medium-emphasis mb-1">{{ $t('dashboard.expiryDate') }}</div>
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
            {{ $t('common.close') }}
          </v-btn>
          
          <v-btn
            v-if="selectedServer.status === 'ACTIVE'"
            :color="themeStore.currentColors.success"
            @click="connectToServer(selectedServer)"
          >
            <v-icon start>mdi-console</v-icon>
            {{ $t('dashboard.connect') }}
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </PageLayout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useThemeStore } from '@/stores/theme'
import { useRouter } from 'vue-router'
import PageLayout from '@/components/PageLayout.vue'
import { orderApi, type OrderDTO } from '@/api/order'

const { t } = useI18n()
const themeStore = useThemeStore()
const router = useRouter()

// 响应式数据
const loading = ref(false)
const searchQuery = ref('')
const statusFilter = ref('')
const servers = ref<OrderDTO[]>([])
const detailDialog = ref(false)
const selectedServer = ref<OrderDTO | null>(null)

// 状态选项
const statusOptions = computed(() => [
  { title: t('dashboard.allStatus'), value: '' },
  { title: t('order.status.pending'), value: 'PENDING' },
  { title: t('order.status.paid'), value: 'PAID' },
  { title: t('order.status.processing'), value: 'PROCESSING' },
  { title: t('order.status.active'), value: 'ACTIVE' },
  { title: t('order.status.suspended'), value: 'SUSPENDED' },
  { title: t('order.status.cancelled'), value: 'CANCELLED' },
  { title: t('order.status.expired'), value: 'EXPIRED' }
])

// 表格头部
const headers = computed(() => [
  { title: t('dashboard.serverInfo'), key: 'serverInfo', sortable: false },
  { title: t('dashboard.status'), key: 'status', sortable: true },
  { title: t('dashboard.operatingSystem'), key: 'osInfo', sortable: false },
  { title: t('dashboard.expiryDate'), key: 'expiresAt', sortable: true },
  { title: t('dashboard.actions'), key: 'actions', sortable: false, align: 'center' as const }
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
  
  if (daysUntilExpiry <= 0) return t('dashboard.expired')
  if (daysUntilExpiry <= 7) return t('dashboard.expiringSoon', { days: daysUntilExpiry })
  return t('dashboard.daysRemaining', { days: daysUntilExpiry })
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
  // TODO: 跳转到服务器管理页面
  console.log('Manage server:', server)
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