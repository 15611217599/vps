<template>
  <HomeLayout>
    <v-container class="py-8">
      <!-- 服务器仪表盘标题 -->
      <div class="d-flex align-center mb-6">
        <v-btn
          icon="mdi-arrow-left"
          variant="text"
          class="me-3"
          @click="router.back()"
        />
        <div>
          <h1 class="text-h4 font-weight-bold">服务器控制台</h1>
          <p class="text-subtitle-1 text-medium-emphasis mb-0">
            {{ serverProps.orderNumber }}
          </p>
        </div>
        <v-spacer />
        <v-btn
          :color="themeStore.currentColors.primary"
          variant="outlined"
          prepend-icon="mdi-refresh"
          :loading="refreshing"
          @click="refreshAllData"
        >
          刷新数据
        </v-btn>
      </div>

      <!-- 服务器配置信息 -->
      <v-card elevation="2" class="mb-6" v-if="server && !loading">
        <v-card-title class="d-flex align-center">
          <v-icon :color="themeStore.currentColors.primary" class="me-2">mdi-server</v-icon>
          服务器配置信息
          <v-spacer />
          <v-chip
:color="getStatusColor((server?.status || order?.status) || 'unknown')"
            variant="flat"
            size="small"
          >
<v-icon start size="16">{{ getStatusIcon((server?.status || order?.status) || 'unknown') }}</v-icon>
            {{ getStatusText(server?.status || order?.status) }}
          </v-chip>
        </v-card-title>
        
        <v-divider />
        
        <v-card-text class="pa-6">
          <v-row>
            <!-- 第一列：基本信息 -->
            <v-col cols="12" md="3">
              <v-list density="compact">
                <v-list-item>
                  <template v-slot:prepend>
                    <v-icon color="primary">mdi-tag</v-icon>
                  </template>
                  <v-list-item-title>订单号</v-list-item-title>
                  <v-list-item-subtitle>{{ serverProps.orderNumber }}</v-list-item-subtitle>
                </v-list-item>
                
                <v-list-item v-if="serverProps.ipAddress">
                  <template v-slot:prepend>
                    <v-icon color="info">mdi-ip</v-icon>
                  </template>
                  <v-list-item-title>IP地址</v-list-item-title>
                  <v-list-item-subtitle>{{ serverProps.ipAddress }}</v-list-item-subtitle>
                </v-list-item>
                
                <v-list-item v-if="serverProps.sshPort">
                  <template v-slot:prepend>
                    <v-icon color="success">mdi-ethernet</v-icon>
                  </template>
                  <v-list-item-title>SSH端口</v-list-item-title>
                  <v-list-item-subtitle>{{ serverProps.sshPort }}</v-list-item-subtitle>
                </v-list-item>
                
                <v-list-item v-if="serverProps.status">
                  <template v-slot:prepend>
                    <v-icon :color="serverProps.status === 'online' ? 'success' : 'error'">mdi-server</v-icon>
                  </template>
                  <v-list-item-title>服务器状态</v-list-item-title>
                  <v-list-item-subtitle>{{ serverProps.status === 'online' ? '在线' : '离线' }}</v-list-item-subtitle>
                </v-list-item>
              </v-list>
            </v-col>
            
            <!-- 第二列：系统配置 -->
            <v-col cols="12" md="3">
              <v-list density="compact">
                <v-list-item v-if="serverProps.osName">
                  <template v-slot:prepend>
                    <v-icon color="warning">{{ getOsIcon(serverProps.osName) }}</v-icon>
                  </template>
                  <v-list-item-title>操作系统</v-list-item-title>
                  <v-list-item-subtitle>{{ serverProps.osName }} {{ serverProps.osVersion || '' }}</v-list-item-subtitle>
                </v-list-item>
                
                <v-list-item v-if="serverProps.cpuCores">
                  <template v-slot:prepend>
                    <v-icon color="secondary">mdi-cpu-64-bit</v-icon>
                  </template>
                  <v-list-item-title>CPU核心</v-list-item-title>
                  <v-list-item-subtitle>{{ serverProps.cpuCores }}</v-list-item-subtitle>
                </v-list-item>
                
                <v-list-item v-if="serverProps.memoryGb">
                  <template v-slot:prepend>
                    <v-icon color="info">mdi-memory</v-icon>
                  </template>
                  <v-list-item-title>内存</v-list-item-title>
                  <v-list-item-subtitle>{{ serverProps.memoryGb }}</v-list-item-subtitle>
                </v-list-item>
                
                <v-list-item v-if="serverProps.storageGb">
                  <template v-slot:prepend>
                    <v-icon color="primary">mdi-harddisk</v-icon>
                  </template>
                  <v-list-item-title>存储空间</v-list-item-title>
                  <v-list-item-subtitle>{{ serverProps.storageGb }}</v-list-item-subtitle>
                </v-list-item>
                
                <v-list-item v-if="serverProps.groupName">
                  <template v-slot:prepend>
                    <v-icon color="teal">mdi-folder</v-icon>
                  </template>
                  <v-list-item-title>服务器分组</v-list-item-title>
                  <v-list-item-subtitle>{{ serverProps.groupName }}</v-list-item-subtitle>
                </v-list-item>
              </v-list>
            </v-col>
            
            <!-- 第三列：网络配置 -->
            <v-col cols="12" md="3">
              <v-list density="compact">
                <v-list-item v-if="serverProps.diskType">
                  <template v-slot:prepend>
                    <v-icon color="orange">mdi-database</v-icon>
                  </template>
                  <v-list-item-title>磁盘类型</v-list-item-title>
                  <v-list-item-subtitle>{{ serverProps.diskType }}</v-list-item-subtitle>
                </v-list-item>
                
                <v-list-item v-if="serverProps.networkSpeed">
                  <template v-slot:prepend>
                    <v-icon color="success">mdi-speedometer</v-icon>
                  </template>
                  <v-list-item-title>网络速度</v-list-item-title>
                  <v-list-item-subtitle>{{ serverProps.networkSpeed }}</v-list-item-subtitle>
                </v-list-item>
                
                <v-list-item v-if="serverProps.bandwidthMbps">
                  <template v-slot:prepend>
                    <v-icon color="info">mdi-web</v-icon>
                  </template>
                  <v-list-item-title>带宽</v-list-item-title>
                  <v-list-item-subtitle>{{ serverProps.bandwidthMbps }}Mbps</v-list-item-subtitle>
                </v-list-item>
                
                <v-list-item v-if="serverProps.sshAccess">
                  <template v-slot:prepend>
                    <v-icon color="success">mdi-console</v-icon>
                  </template>
                  <v-list-item-title>SSH访问</v-list-item-title>
                  <v-list-item-subtitle>{{ serverProps.sshAccess }}</v-list-item-subtitle>
                </v-list-item>
                
                <v-list-item v-if="serverProps.username">
                  <template v-slot:prepend>
                    <v-icon color="purple">mdi-account</v-icon>
                  </template>
                  <v-list-item-title>用户名</v-list-item-title>
                  <v-list-item-subtitle>{{ serverProps.username }}</v-list-item-subtitle>
                </v-list-item>
              </v-list>
            </v-col>
            
            <!-- 第四列：时间信息 -->
            <v-col cols="12" md="3">
              <v-list density="compact">
                <v-list-item v-if="serverProps.expiryDate">
                  <template v-slot:prepend>
                    <v-icon color="error">mdi-calendar-clock</v-icon>
                  </template>
                  <v-list-item-title>到期时间</v-list-item-title>
                  <v-list-item-subtitle :class="expiryClass">
                    {{ serverProps.expiryDate }}
                  </v-list-item-subtitle>
                </v-list-item>
                
                <v-list-item v-if="serverProps.createTime">
                  <template v-slot:prepend>
                    <v-icon color="success">mdi-calendar-plus</v-icon>
                  </template>
                  <v-list-item-title>创建时间</v-list-item-title>
                  <v-list-item-subtitle>{{ serverProps.createTime }}</v-list-item-subtitle>
                </v-list-item>
                
                <v-list-item v-if="serverProps.lastUpdate">
                  <template v-slot:prepend>
                    <v-icon color="info">mdi-update</v-icon>
                  </template>
                  <v-list-item-title>最后更新</v-list-item-title>
                  <v-list-item-subtitle>{{ serverProps.lastUpdate }}</v-list-item-subtitle>
                </v-list-item>
                
                <v-list-item v-if="monitoring?.uptime">
                  <template v-slot:prepend>
                    <v-icon color="success">mdi-clock</v-icon>
                  </template>
                  <v-list-item-title>运行时间</v-list-item-title>
                  <v-list-item-subtitle>{{ monitoring.uptime }}</v-list-item-subtitle>
                </v-list-item>
              </v-list>
            </v-col>
          </v-row>
        </v-card-text>
      </v-card>
      
      <!-- 加载状态 -->
      <v-card elevation="2" class="mb-6" v-if="loading">
        <v-card-text class="text-center py-8">
          <v-progress-circular indeterminate color="primary" size="64" class="mb-4" />
          <div class="text-h6">加载服务器信息中...</div>
        </v-card-text>
      </v-card>
      
      <!-- 错误状态 -->
      <v-card elevation="2" class="mb-6" v-if="!server && !loading">
        <v-card-text class="text-center py-8">
          <v-icon size="64" color="error" class="mb-4">mdi-server-off</v-icon>
          <div class="text-h6 mb-2">无法加载服务器信息</div>
          <div class="text-body-2 text-medium-emphasis mb-4">
            服务器信息加载失败，请检查服务器ID是否正确
          </div>
          <v-btn color="primary" @click="loadServerInfo">
            重新加载
          </v-btn>
        </v-card-text>
      </v-card>

      <!-- 服务器操作 -->
      <v-card elevation="2" class="mb-6" v-if="server && !loading">
        <v-card-title class="d-flex align-center">
          <v-icon :color="themeStore.currentColors.primary" class="me-2">mdi-cog</v-icon>
          服务器操作
        </v-card-title>
        
        <v-divider />
        
        <v-card-text class="pa-6">
          <v-row>
            <!-- 电源控制 -->
            <v-col cols="12" md="6">
              <div class="mb-4">
                <h3 class="text-h6 mb-3 d-flex align-center">
                  <v-icon :color="themeStore.currentColors.warning" class="me-2">mdi-power</v-icon>
                  电源控制
                </h3>
                
                <div class="d-flex gap-2">
                  <v-btn
                    :color="themeStore.currentColors.warning"
                    variant="outlined"
                    :loading="actionLoading.restart"
                    :disabled="(server?.status !== 'online' && order?.status !== 'ACTIVE') || isActionDisabled"
                    @click="performAction('restart')"
                  >
                    <v-icon start>mdi-restart</v-icon>
                    重启
                  </v-btn>
                </div>
              </div>
            </v-col>
            
            <!-- 密码管理 -->
            <v-col cols="12" md="6">
              <div class="mb-4">
                <h3 class="text-h6 mb-3 d-flex align-center">
                  <v-icon :color="themeStore.currentColors.info" class="me-2">mdi-key</v-icon>
                  密码管理
                </h3>
                
                <v-text-field
                  v-model="currentPassword"
                  label="当前密码"
                  :type="showPassword ? 'text' : 'password'"
                  variant="outlined"
                  density="compact"
                  readonly
                  :append-inner-icon="showPassword ? 'mdi-eye' : 'mdi-eye-off'"
                  @click:append-inner="showPassword = !showPassword"
                  class="mb-3"
                />
                
                <v-text-field
                  v-model="newPassword"
                  label="新密码"
                  :type="showPassword ? 'text' : 'password'"
                  variant="outlined"
                  density="compact"
                  :append-inner-icon="showPassword ? 'mdi-eye' : 'mdi-eye-off'"
                  @click:append-inner="showPassword = !showPassword"
                  :rules="passwordRules"
                  class="mb-3"
                />
                
                <div class="d-flex gap-2">
                  <v-btn
                    :color="themeStore.currentColors.primary"
                    variant="outlined"
                    :loading="actionLoading.resetPassword"
                    :disabled="!newPassword || !effectiveServerId"
                    @click="performAction('resetPassword')"
                  >
                    <v-icon start>mdi-key-change</v-icon>
                    重置密码
                  </v-btn>
                  
                  <v-btn
                    :color="themeStore.currentColors.secondary"
                    variant="text"
                    @click="generateRandomPassword"
                  >
                    <v-icon start>mdi-dice-6</v-icon>
                    生成密码
                  </v-btn>
                  
                  <v-btn
                    :color="themeStore.currentColors.success"
                    variant="outlined"
                    :disabled="server?.status !== 'online' && order?.status !== 'ACTIVE'"
                    @click="openSSHConnection"
                  >
                    <v-icon start>mdi-console</v-icon>
                    SSH连接
                  </v-btn>
                </div>
              </div>
            </v-col>

            <!-- 系统重装 -->
            <v-col cols="12">
              <div class="mb-4">
                <h3 class="text-h6 mb-3 d-flex align-center">
                  <v-icon :color="themeStore.currentColors.primary" class="me-2">mdi-desktop-tower</v-icon>
                  系统重装
                </h3>

                <v-row>
                  <v-col cols="12" md="3">
                    <v-select
                      :items="osNameOptions"
                      item-title="title"
                      item-value="value"
                      v-model="selectedOSName"
                      label="操作系统"
                      placeholder="请选择操作系统"
                      variant="outlined"
                      density="compact"
                    />
                  </v-col>
                  <v-col cols="12" md="3">
                    <v-select
                      :items="osVersionOptions"
                      item-title="title"
                      item-value="value"
                      v-model="selectedOSVersion"
                      :disabled="!selectedOSName"
                      label="版本"
                      placeholder="请选择版本"
                      variant="outlined"
                      density="compact"
                    />
                  </v-col>
                  <v-col cols="12" md="3">
                    <v-text-field
                      v-model="reinstallPassword"
                      :type="showPassword ? 'text' : 'password'"
                      label="新Root密码"
                      placeholder="请输入新Root密码"
                      variant="outlined"
                      density="compact"
                    >
                      <template #append-inner>
                        <v-btn
                          :icon="showPassword ? 'mdi-eye-off' : 'mdi-eye'"
                          size="small"
                          variant="text"
                          @click="showPassword = !showPassword"
                        />
                        <v-btn
                          icon="mdi-refresh"
                          size="small"
                          variant="text"
                          @click="generateReinstallPassword()"
                        />
                      </template>
                    </v-text-field>
                  </v-col>
                  <v-col cols="12" md="3">
                    <v-text-field
                      v-model.number="reinstallPort"
                      type="number"
                      label="SSH端口"
                      placeholder="默认22或当前端口"
                      variant="outlined"
                      density="compact"
                      :min="1024"
                      :max="65535"
                      :rules="reinstallPortRules"
                    >
                      <template #append-inner>
                        <v-btn
                          icon="mdi-refresh"
                          size="small"
                          variant="text"
                          @click="generateReinstallPort()"
                        />
                      </template>
                    </v-text-field>
                  </v-col>
                </v-row>

                <div class="d-flex gap-2">
                  <v-btn
                    :color="themeStore.currentColors.primary"
                    variant="outlined"
                    :loading="actionLoading.reinstall"
                    :disabled="!effectiveServerId || !selectedOSName || !selectedOSVersion"
                    @click="performAction('reinstall')"
                  >
                    <v-icon start>mdi-linux</v-icon>
                    重装系统
                  </v-btn>
                </div>
              </div>
            </v-col>
          </v-row>
        </v-card-text>
      </v-card>

      <!-- 服务器监控 -->
      <v-card elevation="2" class="mb-6" v-if="server && !loading">
        <v-card-title class="d-flex align-center">
          <v-icon :color="themeStore.currentColors.primary" class="me-2">mdi-chart-line</v-icon>
          服务器监控
          <v-spacer />
          <v-btn
            :color="themeStore.currentColors.primary"
            variant="outlined"
            size="small"
            :loading="refreshingInfo"
            @click="refreshSystemInfo"
          >
            <v-icon start>mdi-refresh</v-icon>
            刷新
          </v-btn>
        </v-card-title>
        
        <v-divider />
        
        <v-card-text class="pa-6" v-if="monitoring">
          <v-row>
            <v-col cols="12" md="4">
              <div class="text-center">
                <v-progress-circular
                  :model-value="monitoring.cpuUsage || 0"
                  :color="getUsageColor(monitoring.cpuUsage || 0)"
                  size="120"
                  width="8"
                  class="mb-3"
                >
                  <div class="text-h5 font-weight-bold">
                    {{ Math.round(monitoring.cpuUsage || 0) }}%
                  </div>
                </v-progress-circular>
                <div class="text-h6 font-weight-bold">CPU使用率</div>
              </div>
            </v-col>
            
            <v-col cols="12" md="4">
              <div class="text-center">
                <v-progress-circular
                  :model-value="monitoring.memoryUsage || 0"
                  :color="getUsageColor(monitoring.memoryUsage || 0)"
                  size="120"
                  width="8"
                  class="mb-3"
                >
                  <div class="text-h5 font-weight-bold">
                    {{ Math.round(monitoring.memoryUsage || 0) }}%
                  </div>
                </v-progress-circular>
                <div class="text-h6 font-weight-bold">内存使用率</div>
              </div>
            </v-col>
            
            <v-col cols="12" md="4">
              <div class="text-center">
                <v-progress-circular
                  :model-value="monitoring.diskUsage || 0"
                  :color="getUsageColor(monitoring.diskUsage || 0)"
                  size="120"
                  width="8"
                  class="mb-3"
                >
                  <div class="text-h5 font-weight-bold">
                    {{ Math.round(monitoring.diskUsage || 0) }}%
                  </div>
                </v-progress-circular>
                <div class="text-h6 font-weight-bold">磁盘使用率</div>
              </div>
            </v-col>
          </v-row>
        </v-card-text>
        
        <!-- 无监控数据时的显示 -->
        <v-card-text v-else class="text-center py-8">
          <v-icon size="48" color="grey-lighten-2" class="mb-2">mdi-information-outline</v-icon>
          <div class="text-body-2 text-medium-emphasis">
            暂无系统监控数据
          </div>
        </v-card-text>
      </v-card>
    </v-container>
    
    <!-- 确认对话框 -->
    <v-dialog v-model="confirmDialog" max-width="400">
      <v-card>
        <v-card-title class="d-flex align-center">
          <v-icon :color="getActionColor(pendingAction)" class="me-2">
            {{ getActionIcon(pendingAction) }}
          </v-icon>
          确认{{ pendingAction === 'start' ? '启动' : pendingAction === 'restart' ? '重启' : pendingAction === 'stop' ? '关机' : pendingAction === 'resetPassword' ? '重置密码' : pendingAction === 'reinstall' ? '重装系统' : '' }}
        </v-card-title>
        
        <v-divider />
        
        <v-card-text class="pa-6">
          <p>确认要执行此操作吗？</p>
          <div v-if="pendingAction === 'resetPassword'" class="mt-3">
            <v-alert type="warning" variant="tonal" class="mb-3">
              重置密码后，请妥善保管新密码
            </v-alert>
            <div class="text-subtitle-2 mb-1">新密码:</div>
            <div class="font-weight-bold">{{ newPassword }}</div>
          </div>
          <div v-if="pendingAction === 'reinstall'" class="mt-3">
            <v-alert type="warning" variant="tonal" class="mb-3">
              重装系统将会清空当前系统数据，请谨慎操作！
            </v-alert>
            <div class="text-subtitle-2 mb-1">目标系统:</div>
            <div class="font-weight-bold">{{ selectedOSName }} {{ selectedOSVersion }}</div>
          </div>
        </v-card-text>
        
        <v-card-actions class="px-6 pb-6">
          <v-spacer />
          <v-btn
            variant="text"
            @click="confirmDialog = false"
          >
            {{ '取消' }}
          </v-btn>
          <v-btn
            :color="getActionColor(pendingAction)"
            @click="executeAction"
          >
            {{ '确认' }}
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </HomeLayout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, defineComponent, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import { useThemeStore } from '@/stores/theme'
import HomeLayout from '@/components/HomeLayout.vue'
import { orderApi } from '@/api/order'
import { priceGroupApi } from '@/api/priceGroup'
import { serverManagementApi, type ServerMonitoringDTO } from '@/api/serverManagement'
import { getServerById } from '@/api/server'
import { useNotification } from '@/composables/useNotification'

// 启用模板类型检查
defineComponent({name: 'ServerManageView'})

// API响应类型
interface ApiResponse<T> {
  success: boolean
  message: string
  data: T
}

const route = useRoute()
const router = useRouter()
const themeStore = useThemeStore()

// 响应式数据
const order = ref<any>(null)
const server = ref<any>(null)
const monitoring = ref<ServerMonitoringDTO | null>(null)
const loading = ref(false)
const refreshing = ref(false)
const newPassword = ref('')
const currentPassword = ref('')
const showPassword = ref(false)
const confirmDialog = ref(false)
const pendingAction = ref('')
const refreshingInfo = ref(false)
// 通知
const { showNotification } = useNotification()
// 系统重装相关
const selectedOSName = ref<string | null>(null)
const selectedOSVersion = ref<string | null>(null)
const reinstallPassword = ref('')
const osOptions = ref<Array<{ name: string, versions: string[] }>>([])
const reinstallPort = ref<number | null>(null)
// 操作加载状态
const actionLoading = ref({
  start: false,
  restart: false,
  stop: false,
  resetPassword: false,
  reinstall: false
})

// 计算属性
const isActionDisabled = computed(() => {
  return Object.values(actionLoading.value).some(loading => loading)
})

// 可用的服务器ID（优先使用已加载的server.id，其次使用订单中的serverId）
const effectiveServerId = computed(() => {
  return (server.value as any)?.id ?? (order.value as any)?.serverId ?? null
})

// 密码验证规则
const passwordRules = [
  (v: string) => !!v || '此字段为必填项',
  (v: string) => v.length >= 8 || '密码长度至少需要8个字符',
  (v: string) => /[A-Z]/.test(v) || '密码必须包含大写字母',
  (v: string) => /[a-z]/.test(v) || '密码必须包含小写字母',
  (v: string) => /[0-9]/.test(v) || '密码必须包含数字'
]

// OS 选择项
const osNameOptions = computed(() => {
  return osOptions.value.map(os => ({ title: os.name, value: os.name }))
})

const osVersionOptions = computed(() => {
  if (!selectedOSName.value) return []
  const os = osOptions.value.find(o => o.name === selectedOSName.value)
  if (!os) return []
  return os.versions.map(v => ({ title: v, value: v }))
})

// 默认SSH端口：订单端口 > 服务器端口 > 22
const getDefaultSshPort = () => {
  const orderPort = (order.value as any)?.sshPort
  const serverPort = (server.value as any)?.port
  return (orderPort ?? serverPort ?? 22) as number
}

// 生成随机密码（与销售页一致的字符集与长度）
const generateReinstallPasswordString = () => {
  const chars = 'ABCDEFGHJKMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789!@#$%^&*'
  let pwd = ''
  for (let i = 0; i < 12; i++) {
    pwd += chars.charAt(Math.floor(Math.random() * chars.length))
  }
  return pwd
}

// 生成随机端口（1024-65535）
const generateReinstallPortNumber = () => {
  return Math.floor(Math.random() * (65535 - 1024 + 1)) + 1024
}

// 提供给UI的生成操作
const generateReinstallPassword = () => {
  reinstallPassword.value = generateReinstallPasswordString()
}
const generateReinstallPort = () => {
  reinstallPort.value = generateReinstallPortNumber()
}

// 端口校验规则
const reinstallPortRules = [
  (v: any) => v == null || v === '' || (Number.isInteger(v) && v >= 1024 && v <= 65535) || '端口需为1024-65535的整数'
]

// 当切换系统或版本时，重置并自动生成密码与端口
watch(selectedOSName, () => {
  selectedOSVersion.value = null
  generateReinstallPassword()
  reinstallPort.value = getDefaultSshPort()
})
watch(selectedOSVersion, () => {
  generateReinstallPassword()
  reinstallPort.value = getDefaultSshPort()
})

// 获取状态文本
const getStatusText = (status?: string) => {
  switch (status) {
    // 订单状态
    case 'ACTIVE': return '运行中'
    case 'SUSPENDED': return '已暂停'
    case 'CANCELLED': return '已取消'
    case 'EXPIRED': return '已过期'
    case 'PROCESSING': return '处理中'
    case 'PENDING': return '待处理'
    case 'PAID': return '已付款'
    // 服务器状态
    case 'online': return '在线'
    case 'offline': return '离线'
    default: return '未知状态'
  }
}

// 刷新所有数据
const refreshAllData = async () => {
  refreshing.value = true
  try {
    await Promise.all([
      loadServerInfo(),
      loadMonitoringData()
    ])
  } catch (error) {
    console.error('刷新数据失败:', error)
    showNotification('刷新数据失败', 'error')
  } finally {
    refreshing.value = false
  }
}

// 加载监控数据
const loadMonitoringData = async () => {
  if (!server.value?.id) return
  
  try {
    const response = await serverManagementApi.getMonitoring(server.value.id)
    monitoring.value = response.data
  } catch (error) {
    console.error('加载监控数据失败:', error)
    // 设置默认监控数据
    monitoring.value = {
      serverId: server.value.id,
      serverIp: server.value.ipAddress || '',
      status: 'offline',
      cpuUsage: 0,
      memoryUsage: 0,
      diskUsage: 0,
      sshConnectable: false,
      lastCheckTime: new Date().toISOString()
    }
  }
}

// 格式化日期时间
const formatDateTime = (dateString?: string) => {
  if (!dateString) return 'N/A'
  return new Date(dateString).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 辅助计算属性，安全地访问server属性
const serverProps = computed(() => {
  if (!order.value || !server.value) return {
    orderNumber: '加载中...',
    ipAddress: null,
    sshPort: null,
    osName: null,
    osVersion: null,
    cpuCores: null,
    memoryGb: null,
    storageGb: null,
    diskType: null,
    networkSpeed: null,
    bandwidthMbps: null,
    username: null,
    password: null,
    status: null,
    expiryDate: null,
    specs: null,
    sshAccess: null,
    createTime: null,
    lastUpdate: null
  }

  return {
    orderNumber: order.value.orderNumber,
    // 使用服务器的IP地址
    ipAddress: server.value.ip || order.value.serverName || null,
    sshPort: order.value.sshPort || server.value.port || null,
    osName: order.value.osName || server.value.operatingSystem || null,
    osVersion: order.value.osVersion || null,
    cpuCores: server.value.cpuCores || order.value.cpuCores || null,
    memoryGb: server.value.memory || order.value.memoryGb || null,
    storageGb: server.value.diskSpace || order.value.storageGb || null,
    diskType: server.value.diskType || null,
    networkSpeed: server.value.networkSpeed || null,
    bandwidthMbps: order.value.bandwidthMbps || null,
    username: server.value.username || 'root',
    password: server.value.password || order.value.initialPassword || null,
    status: server.value.status || null,
    groupName: server.value.groupName || null,
    expiryDate: order.value.expiresAt ? formatDate(order.value.expiresAt) : null,
    specs: (server.value.cpuCores || order.value.cpuCores) && (server.value.memory || order.value.memoryGb) && (server.value.diskSpace || order.value.storageGb) ? 
      `${server.value.cpuCores || order.value.cpuCores} / ${server.value.memory || order.value.memoryGb + 'GB'} / ${server.value.diskSpace || order.value.storageGb + 'GB'}` : null,
    sshAccess: (server.value.ip || order.value.serverName) && (order.value.sshPort || server.value.port) ? 
      `${server.value.ip || order.value.serverName}:${order.value.sshPort || server.value.port}` : null,
    createTime: server.value.createTime ? formatDateTime(server.value.createTime) : null,
    lastUpdate: server.value.lastUpdate ? formatDateTime(server.value.lastUpdate) : null
  }
})

// 计算到期时间样式
const expiryClass = computed(() => {
  if (!order.value || !order.value.expiresAt) return ''
  
  const expiryDate = new Date(order.value.expiresAt)
  const now = new Date()
  const daysUntilExpiry = Math.ceil((expiryDate.getTime() - now.getTime()) / (1000 * 60 * 60 * 24))
  
  if (daysUntilExpiry <= 0) return 'text-error'
  if (daysUntilExpiry <= 7) return 'text-warning'
  return 'text-success'
})

// 注意：如果需要显示监控数据更新时间，可以使用以下代码
// const formattedUpdateTime = computed(() => {
//   if (monitoring.value?.lastUpdateTime) {
//     return formatDateTime(monitoring.value.lastUpdateTime)
//   }
//   return 'N/A'
// })

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

// 已移除，使用expiryClass计算属性代替

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
    case 'reinstall': return themeStore.currentColors.info
    default: return themeStore.currentColors.primary
  }
}

// 获取操作图标
const getActionIcon = (action: string) => {
  switch (action) {
    case 'start': return 'mdi-play'
    case 'restart': return 'mdi-restart'
    case 'stop': return 'mdi-stop'
    case 'resetPassword': return 'mdi-lock-reset'
    case 'reinstall': return 'mdi-linux'
    default: return 'mdi-cog'
  }
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
    const serverId = (server.value && (server.value as any).id) || (order.value && (order.value as any).serverId)
    if (!serverId) {
      throw new Error('服务器ID不存在')
    }

    // 构建API请求参数
    const request: any = {
      serverId: serverId,
      action: action
    }

    // 如果是重置密码，添加新密码参数
    if (action === 'resetPassword') {
      if (!newPassword.value) {
        throw new Error('新密码不能为空')
      }
      request.newPassword = newPassword.value
    }

    // 如果是重装系统，添加系统参数
    if (action === 'reinstall') {
      if (!selectedOSName.value || !selectedOSVersion.value) {
        throw new Error('请选择操作系统及版本')
      }
      request.osName = selectedOSName.value
      request.osVersion = selectedOSVersion.value
      if (reinstallPassword.value) {
        request.rootPassword = reinstallPassword.value
      }
      if (reinstallPort.value && Number.isFinite(reinstallPort.value)) {
        request.sshPort = Number(reinstallPort.value)
      }
    }

    // 调用后端API，增加超时保护避免长时间等待
    const apiCall = serverManagementApi.executeAction(request)
    const timeoutPromise = new Promise((_, reject) => {
      setTimeout(() => reject(new Error('请求超时，请稍后重试')), 15000)
    })
    const response = await Promise.race([apiCall, timeoutPromise]) as { data: ApiResponse<string> }
    
    // 检查API响应是否成功
    if (response && response.data) {
      if (response.data.success) {
        // 成功响应
        showNotification('操作已提交', 'success')
      } else {
        // 后端返回了错误信息
        throw new Error(response.data.message || '操作失败')
      }
    } else {
      // 响应格式不正确
      throw new Error('服务器响应格式错误')
    }
    
    // 如果是重置密码成功，更新当前密码显示
    if (action === 'resetPassword') {
      currentPassword.value = newPassword.value
      showNotification('密码重置成功', 'success')
    }
    // 如果是重装系统
    if (action === 'reinstall') {
      showNotification('已提交重装系统任务', 'success')
    }
    
    // 如果是重启或停止，可能需要更新服务器状态
    if (action === 'stop') {
      if (server.value) {
        server.value.status = 'offline'
      }
    } else if (action === 'start') {
      if (server.value) {
        server.value.status = 'online'
      }
    }
    
    // 刷新监控数据
    await loadMonitoringData()
    
  } catch (error: any) {
    console.error(`Failed to execute ${action}:`, error)
    showNotification(`操作失败: ${error.message || '未知错误'}`, 'error')
  } finally {
    actionLoading.value[action as keyof typeof actionLoading.value] = false
    pendingAction.value = ''
    
    // 重置密码后清空输入框
    if (action === 'resetPassword') {
      newPassword.value = ''
    }
    // 重装系统后清空密码输入（不清空已选系统）
    if (action === 'reinstall') {
      reinstallPassword.value = ''
    }
  }
}

// 刷新系统信息
const refreshSystemInfo = async () => {
  refreshingInfo.value = true
  try {
    await loadMonitoringData()
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
    showNotification('SSH命令已复制到剪贴板', 'info')
  }
}

// 加载服务器信息
const loadServerInfo = async () => {
  const orderId = route.params.id as string
  if (!orderId) {
    router.push('/dashboard')
    return
  }
  
  loading.value = true
  try {
    // 先获取订单信息
    const orderResponse = await orderApi.getOrderById(parseInt(orderId))
    if (!orderResponse.success) {
      console.error('Failed to load order:', orderResponse.message)
      showNotification(`加载订单信息失败: ${orderResponse.message || '未知错误'}`, 'error')
      router.push('/dashboard')
      return
    }
    
    order.value = orderResponse.data
    currentPassword.value = order.value.initialPassword || ''
    // 默认重装端口（订单端口 > 服务器端口 > 22）
    reinstallPort.value = getDefaultSshPort()

    // 加载价格组中的操作系统选项（从销售页JSON字段）
    try {
      if (order.value.priceGroupId) {
        const pg = await priceGroupApi.getPriceGroupById(order.value.priceGroupId)
        const raw = (pg as any).salesPageHtml
        let data: any = null
        if (raw) {
          data = typeof raw === 'string' ? (() => { try { return JSON.parse(raw) } catch { return null } })() : raw
        }
        const os = data?.operating_system
        if (Array.isArray(os)) {
          osOptions.value = os.map((o: any) => ({ name: o.name, versions: Array.isArray(o.versions) ? o.versions : [] }))
          // 设置默认选择：优先订单里记录的系统与版本，否则第一个
          if (order.value.osName && osOptions.value.find(x => x.name === order.value.osName)) {
            selectedOSName.value = order.value.osName
            const verList = osOptions.value.find(x => x.name === order.value.osName)?.versions || []
            if (order.value.osVersion && verList.includes(order.value.osVersion)) {
              selectedOSVersion.value = order.value.osVersion
            } else if (verList.length > 0) {
              selectedOSVersion.value = verList[verList.length - 1]
            }
          } else if (osOptions.value.length > 0) {
            selectedOSName.value = osOptions.value[0].name
            selectedOSVersion.value = osOptions.value[0].versions?.[osOptions.value[0].versions.length - 1] || null
          }
        }
      }
    } catch (e) {
      console.warn('加载价格组操作系统选项失败:', e)
    }
    
    // 如果订单有关联的服务器ID，获取服务器详细信息
    if (order.value.serverId) {
      try {
        const serverResponse = await getServerById(order.value.serverId)
        // 解包后端响应，取实际的服务器数据
        server.value = (serverResponse as any).data?.data || (serverResponse as any).data
        // 回填默认端口（若当前为空）
        if (reinstallPort.value == null) {
          reinstallPort.value = getDefaultSshPort()
        }
      } catch (error) {
        console.warn('Failed to load server details:', error)
        // 如果服务器信息获取失败，使用订单中的基本信息
        server.value = {
          id: order.value.serverId,
          ipAddress: order.value.serverName,
          sshPort: order.value.sshPort,
          osName: order.value.osName,
          osVersion: order.value.osVersion,
          cpuCores: order.value.cpuCores,
          memoryGb: order.value.memoryGb,
          storageGb: order.value.storageGb,
          bandwidthMbps: order.value.bandwidthMbps
        }
        // 回填默认端口（若当前为空）
        if (reinstallPort.value == null) {
          reinstallPort.value = getDefaultSshPort()
        }
      }
    } else {
      // 如果没有服务器ID，使用订单中的信息
      server.value = {
        ipAddress: order.value.serverName,
        sshPort: order.value.sshPort,
        osName: order.value.osName,
        osVersion: order.value.osVersion,
        cpuCores: order.value.cpuCores,
        memoryGb: order.value.memoryGb,
        storageGb: order.value.storageGb,
        bandwidthMbps: order.value.bandwidthMbps
      }
    }
  } catch (error) {
    console.error('Failed to load server info:', error)
    showNotification('加载服务器信息失败，请稍后重试', 'error')
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
