<template>
  <PageLayout>
    <v-container>
      <v-row justify="center">
        <v-col cols="12" md="8">
          <v-card>
            <v-card-title class="text-center pa-6">
              <v-icon size="48" :color="getStatusColor(server?.installStatus)" class="mb-4">
                {{ getStatusIcon(server?.installStatus) }}
              </v-icon>
              <h2>服务器安装进度</h2>
              <p class="text-subtitle-1 mt-2">{{ server?.ip }}</p>
            </v-card-title>

            <v-card-text class="pa-6">
              <div v-if="loading" class="text-center">
                <v-progress-circular indeterminate color="primary" size="64" />
                <p class="mt-4">正在加载服务器信息...</p>
              </div>

              <div v-else-if="server">
                <!-- 进度条 -->
                <div class="mb-6" v-if="server.installStatus === 'INSTALLING'">
                  <div class="d-flex justify-space-between align-center mb-2">
                    <span class="text-h6">安装进度</span>
                    <span class="text-h6">{{ server.installProgress }}%</span>
                  </div>
                  <v-progress-linear
                    :model-value="server.installProgress"
                    height="12"
                    rounded
                    color="primary"
                    striped
                    :indeterminate="server.installProgress === 0"
                  />
                </div>

                <!-- 状态信息 -->
                <v-row class="mb-4">
                  <v-col cols="12" sm="6">
                    <v-card variant="outlined">
                      <v-card-text>
                        <div class="text-caption text-grey">安装状态</div>
                        <v-chip :color="getStatusColor(server.installStatus)" size="small">
                          {{ getStatusText(server.installStatus) }}
                        </v-chip>
                      </v-card-text>
                    </v-card>
                  </v-col>
                  <v-col cols="12" sm="6">
                    <v-card variant="outlined">
                      <v-card-text>
                        <div class="text-caption text-grey">操作系统</div>
                        <div class="text-body-1">{{ server.operatingSystem || '未设置' }}</div>
                      </v-card-text>
                    </v-card>
                  </v-col>
                </v-row>

                <!-- 当前步骤 -->
                <v-card variant="outlined" class="mb-4" v-if="server.installStep">
                  <v-card-text>
                    <div class="text-caption text-grey">当前步骤</div>
                    <div class="text-body-1">{{ server.installStep }}</div>
                  </v-card-text>
                </v-card>

                <!-- 服务器信息 -->
                <v-card variant="outlined" class="mb-4">
                  <v-card-text>
                    <div class="text-caption text-grey mb-2">服务器信息</div>
                    <div class="text-body-2">
                      <strong>IP地址:</strong> {{ server.ip }}
                    </div>
                    <div class="text-body-2">
                      <strong>SSH端口:</strong> {{ server.port || 22 }}
                    </div>
                    <div class="text-body-2" v-if="server.installStartedAt">
                      <strong>开始时间:</strong> {{ formatDateTime(server.installStartedAt) }}
                    </div>
                  </v-card-text>
                </v-card>

                <!-- 错误信息 -->
                <v-alert
                  v-if="server.installStatus === 'FAILED' && server.installError"
                  type="error"
                  variant="outlined"
                  class="mb-4"
                >
                  <div class="text-caption">错误信息</div>
                  <div class="text-body-2">{{ server.installError }}</div>
                </v-alert>

                <!-- 完成信息 -->
                <v-alert
                  v-if="server.installStatus === 'COMPLETED'"
                  type="success"
                  variant="outlined"
                  class="mb-4"
                >
                  <div class="text-body-1">
                    <v-icon class="mr-2">mdi-check-circle</v-icon>
                    系统安装完成！
                  </div>
                  <div class="text-body-2 mt-2">
                    您现在可以通过 SSH 连接到服务器：
                    <code class="ml-2">ssh root@{{ server.ip }} -p {{ server.port || 22 }}</code>
                  </div>
                </v-alert>

                <!-- 安装日志 -->
                <v-card v-if="server.installLog">
                  <v-card-title class="d-flex justify-space-between align-center">
                    <span>安装日志</span>
                    <v-btn
                      size="small"
                      variant="text"
                      @click="refreshStatus"
                      prepend-icon="mdi-refresh"
                    >
                      刷新
                    </v-btn>
                  </v-card-title>
                  <v-card-text>
                    <v-textarea
                      :model-value="server.installLog"
                      readonly
                      rows="10"
                      variant="outlined"
                      class="font-monospace"
                      style="font-size: 12px;"
                    />
                  </v-card-text>
                </v-card>
              </div>

              <div v-else class="text-center">
                <v-icon size="64" color="error">mdi-alert-circle</v-icon>
                <h3 class="mt-4">服务器不存在</h3>
                <p class="text-body-1">请检查服务器ID是否正确</p>
              </div>
            </v-card-text>

            <v-card-actions class="pa-6 pt-0">
              <v-spacer />
              <v-btn
                color="primary"
                variant="outlined"
                @click="refreshStatus"
                :loading="loading"
                prepend-icon="mdi-refresh"
              >
                刷新
              </v-btn>
              <v-btn
                color="primary"
                @click="goBack"
                prepend-icon="mdi-arrow-left"
              >
                返回
              </v-btn>
            </v-card-actions>
          </v-card>
        </v-col>
      </v-row>
    </v-container>
  </PageLayout>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { installApi, type ServerInstallStatus } from '@/api/install'
import { useNotification } from '@/composables/useNotification'
import PageLayout from '@/components/PageLayout.vue'

const route = useRoute()
const router = useRouter()
const { showError } = useNotification()

// 数据
const server = ref<ServerInstallStatus | null>(null)
const loading = ref(false)
const serverId = ref<number>(0)
let refreshInterval: NodeJS.Timeout | null = null

// 方法
const loadServerStatus = async () => {
  if (!serverId.value) return

  loading.value = true
  try {
    const response = await installApi.getInstallStatus(serverId.value)
    server.value = response.data
  } catch (error) {
    showError('加载服务器信息失败')
    server.value = null
  } finally {
    loading.value = false
  }
}

const refreshStatus = () => {
  loadServerStatus()
}

const getStatusColor = (status?: string) => {
  switch (status) {
    case 'INSTALLING': return 'primary'
    case 'COMPLETED': return 'success'
    case 'FAILED': return 'error'
    default: return 'grey'
  }
}

const getStatusIcon = (status?: string) => {
  switch (status) {
    case 'INSTALLING': return 'mdi-cog-sync'
    case 'COMPLETED': return 'mdi-check-circle'
    case 'FAILED': return 'mdi-alert-circle'
    default: return 'mdi-server'
  }
}

const getStatusText = (status?: string) => {
  switch (status) {
    case 'NONE': return '未安装'
    case 'INSTALLING': return '安装中'
    case 'COMPLETED': return '安装完成'
    case 'FAILED': return '安装失败'
    default: return '未知'
  }
}

const formatDateTime = (dateTime: string) => {
  if (!dateTime) return 'N/A'
  return new Date(dateTime).toLocaleString()
}

const goBack = () => {
  router.back()
}

// 生命周期
onMounted(() => {
  serverId.value = Number(route.params.serverId)
  if (serverId.value) {
    loadServerStatus()
    
    // 如果服务器正在安装，每5秒刷新一次
    refreshInterval = setInterval(() => {
      if (server.value && server.value.installStatus === 'INSTALLING') {
        loadServerStatus()
      }
    }, 5000)
  }
})

onUnmounted(() => {
  if (refreshInterval) {
    clearInterval(refreshInterval)
  }
})
</script>

<style scoped>
.font-monospace {
  font-family: 'Courier New', monospace;
}

code {
  background-color: rgba(0, 0, 0, 0.1);
  padding: 2px 6px;
  border-radius: 4px;
  font-family: 'Courier New', monospace;
}
</style>