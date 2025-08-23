<template>
  <PageLayout :title="$t('serverManage.title')">
    <v-container class="py-8">
      <!-- 服务器基本信息 -->
      <v-card elevation="2" class="mb-6">
        <v-card-title class="d-flex align-center">
          <v-icon :color="themeStore.currentColors.primary" class="me-2">mdi-server</v-icon>
          {{ $t('serverManage.serverInfo') }}
        </v-card-title>
        
        <v-divider />
        
        <v-card-text class="pa-6">
          <v-row v-if="server">
            <v-col cols="12" md="6">
              <div class="mb-4">
                <div class="text-subtitle-2 text-medium-emphasis mb-1">{{ $t('serverManage.serverName') }}</div>
                <div class="font-weight-bold">{{ server.serverName || server.orderNumber }}</div>
              </div>
              
              <div class="mb-4">
                <div class="text-subtitle-2 text-medium-emphasis mb-1">{{ $t('serverManage.status') }}</div>
                <v-chip :color="getStatusColor(server.status)" size="small">
                  <v-icon start size="16">{{ getStatusIcon(server.status) }}</v-icon>
                  {{ $t(`order.status.${server.status.toLowerCase()}`) }}
                </v-chip>
              </div>
              
              <div class="mb-4">
                <div class="text-subtitle-2 text-medium-emphasis mb-1">{{ $t('serverManage.operatingSystem') }}</div>
                <div class="d-flex align-center">
                  <v-icon class="me-2" size="20">{{ getOsIcon(server.osName) }}</v-icon>
                  {{ server.osName }} {{ server.osVersion }}
                </div>
              </div>
            </v-col>
            
            <v-col cols="12" md="6">
              <div class="mb-4">
                <div class="text-subtitle-2 text-medium-emphasis mb-1">{{ $t('serverManage.serverSpecs') }}</div>
                <div>{{ server.cpuCores }}C / {{ server.memoryGb }}GB RAM / {{ server.storageGb }}GB</div>
              </div>
              
              <div class="mb-4">
                <div class="text-subtitle-2 text-medium-emphasis mb-1">{{ $t('serverManage.expiryDate') }}</div>
                <div :class="getExpiryClass(server.expiresAt)">
                  {{ formatDate(server.expiresAt) }}
                </div>
              </div>
              
              <div class="mb-4">
                <div class="text-subtitle-2 text-medium-emphasis mb-1">{{ $t('serverManage.sshPort') }}</div>
                <div>{{ server.sshPort || 22 }}</div>
              </div>
            </v-col>
          </v-row>
          
          <v-skeleton-loader v-else type="article" />
        </v-card-text>
      </v-card>

      <!-- 管理操作 -->
      <v-row>
        <!-- 电源管理 -->
        <v-col cols="12" md="6">
          <v-card elevation="2" class="h-100">
            <v-card-title class="d-flex align-center">
              <v-icon :color="themeStore.currentColors.warning" class="me-2">mdi-power</v-icon>
              {{ $t('serverManage.powerManagement') }}
            </v-card-title>
            
            <v-divider />
            
            <v-card-text class="pa-6">
              <div class="mb-4">
                <p class="text-body-2 text-medium-emphasis mb-4">
                  {{ $t('serverManage.powerDescription') }}
                </p>
                
                <div class="d-flex flex-column gap-3">
                  <v-btn
                    :color="themeStore.currentColors.success"
                    variant="outlined"
                    block
                    :loading="actionLoading.start"
                    :disabled="server?.status !== 'ACTIVE' || isActionDisabled"
                    @click="performAction('start')"
                  >
                    <v-icon start>mdi-play</v-icon>
                    {{ $t('serverManage.start') }}
                  </v-btn>
                  
                  <v-btn
                    :color="themeStore.currentColors.warning"
                    variant="outlined"
                    block
                    :loading="actionLoading.restart"
                    :disabled="server?.status !== 'ACTIVE' || isActionDisabled"
                    @click="performAction('restart')"
                  >
                    <v-icon start>mdi-restart</v-icon>
                    {{ $t('serverManage.restart') }}
                  </v-btn>
                  
                  <v-btn
                    :color="themeStore.currentColors.error"
                    variant="outlined"
                    block
                    :loading="actionLoading.stop"
                    :disabled="server?.status !== 'ACTIVE' || isActionDisabled"
                    @click="performAction('stop')"
                  >
                    <v-icon start>mdi-stop</v-icon>
                    {{ $t('serverManage.stop') }}
                  </v-btn>
                </div>
              </div>
            </v-card-text>
          </v-card>
        </v-col>
        
        <!-- 密码管理 -->
        <v-col cols="12" md="6">
          <v-card elevation="2" class="h-100">
            <v-card-title class="d-flex align-center">
              <v-icon :color="themeStore.currentColors.info" class="me-2">mdi-key</v-icon>
              {{ $t('serverManage.passwordManagement') }}
            </v-card-title>
            
            <v-divider />
            
            <v-card-text class="pa-6">
              <div class="mb-4">
                <p class="text-body-2 text-medium-emphasis mb-4">
                  {{ $t('serverManage.passwordDescription') }}
                </p>
                
                <v-text-field
                  v-model="newPassword"
                  :label="$t('serverManage.newPassword')"
                  :type="showPassword ? 'text' : 'password'"
                  variant="outlined"
                  density="compact"
                  :append-inner-icon="showPassword ? 'mdi-eye' : 'mdi-eye-off'"
                  @click:append-inner="showPassword = !showPassword"
                  :rules="passwordRules"
                  class="mb-3"
                />
                
                <v-btn
                  :color="themeStore.currentColors.primary"
                  variant="outlined"
                  block
                  :loading="actionLoading.resetPassword"
                  :disabled="!newPassword || server?.status !== 'ACTIVE' || isActionDisabled"
                  @click="performAction('resetPassword')"
                >
                  <v-icon start>mdi-key-change</v-icon>
                  {{ $t('serverManage.resetPassword') }}
                </v-btn>
                
                <v-btn
                  :color="themeStore.currentColors.secondary"
                  variant="text"
                  block
                  class="mt-2"
                  @click="generateRandomPassword"
                >
                  <v-icon start>mdi-dice-6</v-icon>
                  {{ $t('serverManage.generatePassword') }}
                </v-btn>
              </div>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>

      <!-- 系统信息和监控 -->
      <v-row class="mt-6">
        <!-- 系统监控 -->
        <v-col cols="12" md="6">
          <v-card elevation="2" class="h-100">
            <v-card-title class="d-flex align-center">
              <v-icon :color="themeStore.currentColors.success" class="me-2">mdi-monitor</v-icon>
              {{ $t('serverManage.systemMonitoring') }}
            </v-card-title>
            
            <v-divider />
            
            <v-card-text class="pa-6">
              <div v-if="systemInfo">
                <div class="mb-3">
                  <div class="text-subtitle-2 mb-1">{{ $t('serverManage.cpuUsage') }}</div>
                  <v-progress-linear
                    :model-value="systemInfo.cpuUsage"
                    :color="getUsageColor(systemInfo.cpuUsage)"
                    height="20"
                    rounded
                  >
                    <template v-slot:default="{ value }">
                      <strong>{{ Math.ceil(value) }}%</strong>
                    </template>
                  </v-progress-linear>
                </div>
                
                <div class="mb-3">
                  <div class="text-subtitle-2 mb-1">{{ $t('serverManage.memoryUsage') }}</div>
                  <v-progress-linear
                    :model-value="systemInfo.memoryUsage"
                    :color="getUsageColor(systemInfo.memoryUsage)"
                    height="20"
                    rounded
                  >
                    <template v-slot:default="{ value }">
                      <strong>{{ Math.ceil(value) }}%</strong>
                    </template>
                  </v-progress-linear>
                </div>
                
                <div class="mb-3">
                  <div class="text-subtitle-2 mb-1">{{ $t('serverManage.diskUsage') }}</div>
                  <v-progress-linear
                    :model-value="systemInfo.diskUsage"
                    :color="getUsageColor(systemInfo.diskUsage)"
                    height="20"
                    rounded
                  >
                    <template v-slot:default="{ value }">
                      <strong>{{ Math.ceil(value) }}%</strong>
                    </template>
                  </v-progress-linear>
                </div>
                
                <div class="text-center mt-4">
                  <v-btn
                    :color="themeStore.currentColors.primary"
                    variant="text"
                    :loading="refreshingInfo"
                    @click="refreshSystemInfo"
                  >
                    <v-icon start>mdi-refresh</v-icon>
                    {{ $t('common.refresh') }}
                  </v-btn>
                </div>
              </div>
              
              <div v-else class="text-center py-4">
                <v-icon size="48" color="grey-lighten-2" class="mb-2">mdi-information-outline</v-icon>
                <div class="text-body-2 text-medium-emphasis">
                  {{ $t('serverManage.noSystemInfo') }}
                </div>
              </div>
            </v-card-text>
          </v-card>
        </v-col>
        
        <!-- 网络信息 -->
        <v-col cols="12" md="6">
          <v-card elevation="2" class="h-100">
            <v-card-title class="d-flex align-center">
              <v-icon :color="themeStore.currentColors.info" class="me-2">mdi-network</v-icon>
              {{ $t('serverManage.networkInfo') }}
            </v-card-title>
            
            <v-divider />
            
            <v-card-text class="pa-6">
              <v-list v-if="server" density="compact">
                <v-list-item>
                  <template v-slot:prepend>
                    <v-icon color="primary">mdi-ip</v-icon>
                  </template>
                  <v-list-item-title>{{ $t('serverManage.ipAddress') }}</v-list-item-title>
                  <v-list-item-subtitle>{{ server.ipAddress || 'N/A' }}</v-list-item-subtitle>
                </v-list-item>
                
                <v-list-item>
                  <template v-slot:prepend>
                    <v-icon color="info">mdi-ethernet</v-icon>
                  </template>
                  <v-list-item-title>{{ $t('serverManage.bandwidth') }}</v-list-item-title>
                  <v-list-item-subtitle>{{ server.bandwidthMbps }}Mbps</v-list-item-subtitle>
                </v-list-item>
                
                <v-list-item>
                  <template v-slot:prepend>
                    <v-icon color="success">mdi-console-network</v-icon>
                  </template>
                  <v-list-item-title>{{ $t('serverManage.sshAccess') }}</v-list-item-title>
                  <v-list-item-subtitle>
                    {{ server.ipAddress }}:{{ server.sshPort || 22 }}
                  </v-list-item-subtitle>
                </v-list-item>
              </v-list>
              
              <div class="text-center mt-4">
                <v-btn
                  :color="themeStore.currentColors.success"
                  variant="outlined"
                  :disabled="server?.status !== 'ACTIVE'"
                  @click="openSSHConnection"
                >
                  <v-icon start>mdi-console</v-icon>
                  {{ $t('serverManage.connectSSH') }}
                </v-btn>
              </div>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>
    </v-container>
    
    <!-- 确认对话框 -->
    <v-dialog v-model="confirmDialog" max-width="400">
      <v-card>
        <v-card-title class="d-flex align-center">
          <v-icon :color="getActionColor(pendingAction)" class="me-2">
            {{ getActionIcon(pendingAction) }}
          </v-icon>
          {{ $t(`serverManage.confirm${capitalize(pendingAction)}`) }}
        </v-card-title>
        
        <v-divider />
        
        <v-card-text class="pa-6">
          <p>{{ $t(`serverManage.confirm${capitalize(pendingAction)}Description`) }}</p>
          <div v-if="pendingAction === 'resetPassword'" class="mt-3">
            <v-alert type="warning" variant="tonal" class="mb-3">
              {{ $t('serverManage.passwordWarning') }}
            </v-alert>
            <div class="text-subtitle-2 mb-1">{{ $t('serverManage.newPassword') }}:</div>
            <div class="font-weight-bold">{{ newPassword }}</div>
          </div>
        </v-card-text>
        
        <v-card-actions class="px-6 pb-6">
          <v-spacer />
          <v-btn
            variant="text"
            @click="confirmDialog = false"
          >
            {{ $t('common.cancel') }}
          </v-btn>
          <v-btn
            :color="getActionColor(pendingAction)"
            @click="executeAction"
          >
            {{ $t('common.confirm') }}
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </PageLayout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useThemeStore } from '@/stores/theme'
import PageLayout from '@/components/PageLayout.vue'
import { orderApi, type OrderDTO } from '@/api/order'

const route = useRoute()
const router = useRouter()
const { t } = useI18n()
const themeStore = useThemeStore()

// 响应式数据
const server = ref<OrderDTO | null>(null)
const loading = ref(false)
const newPassword = ref('')
const showPassword = ref(false)
const confirmDialog = ref(false)
const pendingAction = ref('')
const refreshingInfo = ref(false)

// 系统信息（模拟数据，实际应从API获取）
const systemInfo = ref({
  cpuUsage: 45,
  memoryUsage: 68,
  diskUsage: 32
})

// 操作加载状态
const actionLoading = ref({
  start: false,
  restart: false,
  stop: false,
  resetPassword: false
})

// 计算属性
const isActionDisabled = computed(() => {
  return Object.values(actionLoading.value).some(loading => loading)
})

// 密码验证规则
const passwordRules = [
  (v: string) => !!v || t('validation.required'),
  (v: string) => v.length >= 8 || t('validation.minLength', { min: 8 }),
  (v: string) => /[A-Z]/.test(v) || t('serverManage.passwordUppercase'),
  (v: string) => /[a-z]/.test(v) || t('serverManage.passwordLowercase'),
  (v: string) => /[0-9]/.test(v) || t('serverManage.passwordNumber')
]

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

// 获取使用率颜色
const getUsageColor = (usage: number) => {
  if (usage >= 80) return themeStore.currentColors.error
  if (usage >= 60) return themeStore.currentColors.warning
  return themeStore.currentColors.success
}

// 获取操作颜色
const getActionColor = (action: string) => {
  switch (action) {
    case 'start': return themeStore.currentColors.success
    case 'restart': return themeStore.currentColors.warning
    case 'stop': return themeStore.currentColors.error
    case 'resetPassword': return themeStore.currentColors.primary
    default: return themeStore.currentColors.primary
  }
}

// 获取操作图标
const getActionIcon = (action: string) => {
  switch (action) {
    case 'start': return 'mdi-play'
    case 'restart': return 'mdi-restart'
    case 'stop': return 'mdi-stop'
    case 'resetPassword': return 'mdi-key-change'
    default: return 'mdi-cog'
  }
}

// 首字母大写
const capitalize = (str: string) => {
  return str.charAt(0).toUpperCase() + str.slice(1)
}

// 生成随机密码
const generateRandomPassword = () => {
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*'
  let password = ''
  
  // 确保包含大写字母、小写字母和数字
  password += 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'[Math.floor(Math.random() * 26)]
  password += 'abcdefghijklmnopqrstuvwxyz'[Math.floor(Math.random() * 26)]
  password += '0123456789'[Math.floor(Math.random() * 10)]
  
  // 填充剩余字符
  for (let i = 3; i < 12; i++) {
    password += chars[Math.floor(Math.random() * chars.length)]
  }
  
  // 打乱顺序
  newPassword.value = password.split('').sort(() => Math.random() - 0.5).join('')
}

// 执行操作
const performAction = (action: string) => {
  pendingAction.value = action
  confirmDialog.value = true
}

// 执行确认的操作
const executeAction = async () => {
  confirmDialog.value = false
  const action = pendingAction.value
  
  actionLoading.value[action as keyof typeof actionLoading.value] = true
  
  try {
    // 这里应该调用实际的API
    await new Promise(resolve => setTimeout(resolve, 2000)) // 模拟API调用
    
    // 显示成功消息
    console.log(`${action} executed successfully`)
    
    // 如果是重启或停止，可能需要更新服务器状态
    if (action === 'stop') {
      if (server.value) {
        server.value.status = 'SUSPENDED'
      }
    }
    
  } catch (error) {
    console.error(`Failed to execute ${action}:`, error)
  } finally {
    actionLoading.value[action as keyof typeof actionLoading.value] = false
    pendingAction.value = ''
    
    // 重置密码后清空输入框
    if (action === 'resetPassword') {
      newPassword.value = ''
    }
  }
}

// 刷新系统信息
const refreshSystemInfo = async () => {
  refreshingInfo.value = true
  try {
    // 模拟API调用获取系统信息
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // 模拟随机数据
    systemInfo.value = {
      cpuUsage: Math.floor(Math.random() * 100),
      memoryUsage: Math.floor(Math.random() * 100),
      diskUsage: Math.floor(Math.random() * 100)
    }
  } catch (error) {
    console.error('Failed to refresh system info:', error)
  } finally {
    refreshingInfo.value = false
  }
}

// 打开SSH连接
const openSSHConnection = () => {
  if (server.value?.ipAddress) {
    // 这里可以实现SSH连接功能，比如打开Web Terminal或复制SSH命令
    const sshCommand = `ssh root@${server.value.ipAddress} -p ${server.value.sshPort || 22}`
    navigator.clipboard.writeText(sshCommand)
    console.log('SSH command copied to clipboard:', sshCommand)
  }
}

// 加载服务器信息
const loadServerInfo = async () => {
  const serverId = route.params.id as string
  if (!serverId) {
    router.push('/dashboard')
    return
  }
  
  loading.value = true
  try {
    const response = await orderApi.getOrderById(parseInt(serverId))
    if (response.success) {
      server.value = response.data
      // 模拟添加IP地址（实际应该从API获取）
      if (server.value) {
        server.value.ipAddress = '192.168.1.100'
      }
    }
  } catch (error) {
    console.error('Failed to load server info:', error)
    router.push('/dashboard')
  } finally {
    loading.value = false
  }
}

// 组件挂载时加载数据
onMounted(() => {
  loadServerInfo()
})
</script>

<style scoped>
.v-progress-linear {
  border-radius: 10px;
}
</style>
