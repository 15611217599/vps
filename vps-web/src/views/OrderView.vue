<template>
  <PageLayout>
    <v-container fluid>
      <!-- 搜索和筛选 -->

      <!-- 搜索和筛选 -->
      <v-row class="mb-4">
        <v-col cols="12" md="4">
          <v-text-field
            v-model="searchQuery"
            :label="TEXTS.order.searchPlaceholder"
            prepend-inner-icon="mdi-magnify"
            variant="outlined"
            density="compact"
            clearable
            @input="debouncedSearch"
          />
        </v-col>
        <v-col cols="12" md="3">
          <v-select
            v-model="statusFilter"
            :items="statusOptions"
            :label="TEXTS.order.status"
            variant="outlined"
            density="compact"
            clearable
            :menu-props="{ maxHeight: '300px', zIndex: 9999 }"
            @update:model-value="loadOrders"
          />
        </v-col>
        <v-col cols="12" md="3">
          <v-select
            v-model="billingPeriodFilter"
            :items="billingPeriodOptions"
            :label="TEXTS.order.billingPeriod"
            variant="outlined"
            density="compact"
            clearable
            :menu-props="{ maxHeight: '300px', zIndex: 9999 }"
            @update:model-value="loadOrders"
          />
        </v-col>
        <v-col cols="12" md="2">
          <v-btn
            color="primary"
            variant="outlined"
            block
            @click="resetFilters"
          >
            {{ TEXTS.common.reset }}
          </v-btn>
        </v-col>
      </v-row>

      <!-- 订单列表 -->
      <v-card>
        <v-card-title class="d-flex align-center justify-space-between">
          <span>{{ TEXTS.order.list }}</span>
          <v-chip
            :color="orders.length > 0 ? 'primary' : 'default'"
            variant="tonal"
          >
            {{ TEXTS.common.total }}: {{ totalItems }}
          </v-chip>
        </v-card-title>

        <v-data-table-server
          v-model:items-per-page="itemsPerPage"
          v-model:page="currentPage"
          :headers="headers"
          :items="orders"
          :items-length="totalItems"
          :loading="loading"
          :search="searchQuery"
          class="elevation-0"
          @update:options="loadOrders"
        >
          <!-- 订单号列 -->
          <template #item.orderNumber="{ item }">
            <v-chip
              color="primary"
              variant="tonal"
              size="small"
              class="font-mono"
            >
              {{ item.orderNumber }}
            </v-chip>
          </template>

          <!-- 服务器列 -->
          <template #item.serverName="{ item }">
            <div class="d-flex align-center">
              <v-icon icon="mdi-server" size="16" class="me-2" />
              {{ item.serverName || 'N/A' }}
            </div>
          </template>

          <!-- 金额列 -->
          <template #item.amount="{ item }">
            <div class="d-flex align-center">
              <v-icon icon="mdi-currency-cny" size="16" class="me-1" />
              <span class="font-weight-medium">{{ item.amount }}</span>
            </div>
          </template>

          <!-- 状态列 -->
          <template #item.status="{ item }">
            <v-chip
              :color="getStatusColor(item.status)"
              variant="tonal"
              size="small"
            >
              {{ getStatusText(item.status) }}
            </v-chip>
          </template>

          <!-- 计费周期列 -->
          <template #item.billingPeriod="{ item }">
            <v-chip
              color="info"
              variant="outlined"
              size="small"
            >
              {{ getBillingPeriodText(item.billingPeriod) }}
            </v-chip>
          </template>

          <!-- 创建时间列 -->
          <template #item.createdAt="{ item }">
            <div class="text-body-2">
              {{ formatDateTime(item.createdAt) }}
            </div>
          </template>

          <!-- 自动续费列 -->
          <template #item.autoRenewal="{ item }">
            <v-switch
              :model-value="item.autoRenewal || false"
              color="success"
              density="compact"
              hide-details
              :disabled="!['ACTIVE', 'PAID'].includes(item.status)"
              @update:model-value="(value) => toggleAutoRenewal(item, value || false)"
            ></v-switch>
          </template>

          <!-- 过期时间列 -->
          <template #item.expiresAt="{ item }">
            <div class="text-body-2" :class="getExpirationClass(item.expiresAt)">
              {{ formatDateTime(item.expiresAt) }}
            </div>
          </template>

          <!-- 操作列 -->
          <template #item.actions="{ item }">
            <div class="d-flex gap-1">
              <v-btn
                icon="mdi-eye"
                size="small"
                variant="text"
                color="primary"
                @click="viewOrder(item)"
              />
              <v-btn
                v-if="item.status === 'ACTIVE'"
                icon="mdi-cog"
                size="small"
                variant="text"
                color="info"
                @click="manageServer(item)"
              />

              <v-btn
                v-if="item.status === 'PENDING'"
                icon="mdi-credit-card"
                size="small"
                variant="text"
                color="success"
                @click="payOrder(item)"
              />
              <v-btn
                v-if="['PENDING', 'PAID'].includes(item.status)"
                icon="mdi-cancel"
                size="small"
                variant="text"
                color="error"
                @click="cancelOrder(item)"
              />
            </div>
          </template>

          <!-- 无数据状态 -->
          <template #no-data>
            <div class="text-center py-8">
              <v-icon icon="mdi-receipt-outline" size="64" class="text-disabled mb-4" />
              <p class="text-h6 text-disabled">{{ TEXTS.order.noOrders }}</p>
              <p class="text-body-2 text-disabled">{{ TEXTS.order.noOrdersDesc }}</p>
            </div>
          </template>
        </v-data-table-server>
      </v-card>

      <!-- 通知组件 -->
      <NotificationSnackbar
        v-model="notificationState.show"
        :message="notificationState.message"
        :type="notificationState.type"
      />

      <!-- 订单详情对话框 -->
      <UnifiedDialog
        v-model="detailDialog"
        :title="TEXTS.order.orderDetails"
        header-icon="mdi-receipt"
        max-width="800"
        :show-save-button="false"
        :show-cancel-button="false"
        :show-close-button="true"
        @cancel="detailDialog = false"
      >
        <div v-if="selectedOrder">
            <v-row>
              <v-col cols="12" md="6">
                <v-list density="compact">
                  <v-list-item>
                    <v-list-item-title>{{ TEXTS.order.orderNumber }}</v-list-item-title>
                    <v-list-item-subtitle>{{ selectedOrder.orderNumber }}</v-list-item-subtitle>
                  </v-list-item>
                  <v-list-item>
                    <v-list-item-title>{{ TEXTS.order.status }}</v-list-item-title>
                    <v-list-item-subtitle>
                      <v-chip
                        :color="getStatusColor(selectedOrder.status)"
                        variant="tonal"
                        size="small"
                      >
                        {{ getStatusText(selectedOrder.status) }}
                      </v-chip>
                    </v-list-item-subtitle>
                  </v-list-item>
                  <v-list-item>
                    <v-list-item-title>{{ TEXTS.order.amount }}</v-list-item-title>
                    <v-list-item-subtitle>¥{{ selectedOrder.amount }}</v-list-item-subtitle>
                  </v-list-item>
                  <v-list-item>
                    <v-list-item-title>{{ TEXTS.order.billingPeriod }}</v-list-item-title>
                    <v-list-item-subtitle>{{ getBillingPeriodText(selectedOrder.billingPeriod) }}</v-list-item-subtitle>
                  </v-list-item>
                  <v-list-item>
                    <v-list-item-title>{{ TEXTS.sales.autoRenewal }}</v-list-item-title>
                    <v-list-item-subtitle>
                      <v-chip
                        :color="selectedOrder.autoRenewal ? 'success' : 'default'"
                        variant="tonal"
                        size="small"
                      >
                        <v-icon start size="14">{{ selectedOrder.autoRenewal ? 'mdi-check-circle' : 'mdi-close-circle' }}</v-icon>
                        {{ selectedOrder.autoRenewal ? '已启用自动续费' : '已禁用自动续费' }}
                      </v-chip>
                    </v-list-item-subtitle>
                  </v-list-item>
                </v-list>
              </v-col>
              <v-col cols="12" md="6">
                <v-list density="compact">
                  <v-list-item>
                    <v-list-item-title>{{ TEXTS.order.serverName }}</v-list-item-title>
                    <v-list-item-subtitle>{{ selectedOrder.serverName || 'N/A' }}</v-list-item-subtitle>
                  </v-list-item>
                  <v-list-item>
                    <v-list-item-title>{{ TEXTS.order.createdAt }}</v-list-item-title>
                    <v-list-item-subtitle>{{ formatDateTime(selectedOrder.createdAt) }}</v-list-item-subtitle>
                  </v-list-item>
                  <v-list-item>
                    <v-list-item-title>{{ TEXTS.order.expiresAt }}</v-list-item-title>
                    <v-list-item-subtitle>{{ formatDateTime(selectedOrder.expiresAt) }}</v-list-item-subtitle>
                  </v-list-item>
                </v-list>
              </v-col>
            </v-row>

          <!-- 服务器配置信息 -->
          <v-divider class="my-4" />
          <h3 class="text-h6 mb-3">{{ TEXTS.order.serverSpecs }}</h3>
          <v-row>
            <v-col cols="6" md="3">
              <v-card variant="tonal" class="pa-3">
                <div class="text-center">
                  <v-icon icon="mdi-cpu-64-bit" size="24" class="mb-2" />
                  <div class="text-body-2 text-medium-emphasis">CPU</div>
                  <div class="text-h6">{{ selectedOrder.cpuCores }}{{ TEXTS.order.cores }}</div>
                </div>
              </v-card>
            </v-col>
            <v-col cols="6" md="3">
              <v-card variant="tonal" class="pa-3">
                <div class="text-center">
                  <v-icon icon="mdi-memory" size="24" class="mb-2" />
                  <div class="text-body-2 text-medium-emphasis">{{ TEXTS.order.memory }}</div>
                  <div class="text-h6">{{ selectedOrder.memoryGb }}GB</div>
                </div>
              </v-card>
            </v-col>
            <v-col cols="6" md="3">
              <v-card variant="tonal" class="pa-3">
                <div class="text-center">
                  <v-icon icon="mdi-harddisk" size="24" class="mb-2" />
                  <div class="text-body-2 text-medium-emphasis">{{ TEXTS.order.storage }}</div>
                  <div class="text-h6">{{ selectedOrder.storageGb }}GB</div>
                </div>
              </v-card>
            </v-col>
            <v-col cols="6" md="3">
              <v-card variant="tonal" class="pa-3">
                <div class="text-center">
                  <v-icon icon="mdi-speedometer" size="24" class="mb-2" />
                  <div class="text-body-2 text-medium-emphasis">{{ TEXTS.order.bandwidth }}</div>
                  <div class="text-h6">{{ selectedOrder.bandwidthMbps }}Mbps</div>
                </div>
              </v-card>
            </v-col>
          </v-row>

          <!-- 操作按钮 -->
          <div class="d-flex justify-end mt-4">
            <v-btn
              v-if="selectedOrder.status === 'ACTIVE'"
              color="info"
              variant="outlined"
              class="me-3"
              @click="manageServer(selectedOrder)"
            >
              <v-icon start>mdi-cog</v-icon>
              {{ TEXTS.order.manageServer }}
            </v-btn>
          </div>
        </div>
      </UnifiedDialog>
    </v-container>
  </PageLayout>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'

import { useRouter } from 'vue-router'
import { orderApi } from '@/api/order'
import { useNotification } from '@/composables/useNotification'
import PageLayout from '@/components/PageLayout.vue'
import UnifiedDialog from '@/components/UnifiedDialog.vue'
import NotificationSnackbar from '@/components/NotificationSnackbar.vue'
import { TEXTS } from '@/constants/texts'

// 简单的防抖函数实现
const debounce = (func: Function, delay: number) => {
  let timeoutId: ReturnType<typeof setTimeout>
  return (...args: any[]) => {
    clearTimeout(timeoutId)
    timeoutId = setTimeout(() => func.apply(null, args), delay)
  }
}

// 移除国际化
const router = useRouter()
const { notificationState, showNotification } = useNotification()

// 定义订单类型
interface Order {
  id: number
  orderNumber: string
  serverName?: string
  amount: number
  status: string
  billingPeriod: string
  createdAt: string
  expiresAt: string
  cpuCores: number
  memoryGb: number
  storageGb: number
  bandwidthMbps: number
  autoRenewal?: boolean
}

// 响应式数据
const orders = ref<Order[]>([])
const loading = ref(false)
const searchQuery = ref('')
const statusFilter = ref('')
const billingPeriodFilter = ref('')
const currentPage = ref(1)
const itemsPerPage = ref(10)
const totalItems = ref(0)
const detailDialog = ref(false)
const selectedOrder = ref<Order | null>(null)

// 表格列定义
const headers = computed(() => [
  { title: TEXTS.order.orderNumber, key: 'orderNumber', sortable: false },
  { title: TEXTS.order.serverName, key: 'serverName', sortable: false },
  { title: TEXTS.order.amount, key: 'amount', sortable: true },
  { title: TEXTS.order.status, key: 'status', sortable: false },
  { title: TEXTS.order.billingPeriod, key: 'billingPeriod', sortable: false },
  { title: TEXTS.sales.autoRenewal, key: 'autoRenewal', sortable: false },
  { title: TEXTS.order.createdAt, key: 'createdAt', sortable: true },
  { title: TEXTS.order.expiresAt, key: 'expiresAt', sortable: true },
  { title: '操作', key: 'actions', sortable: false, width: 120 }
])

// 状态选项
const statusOptions = computed(() => [
  { title: TEXTS.order.statusPending, value: 'PENDING' },
  { title: TEXTS.order.statusPaid, value: 'PAID' },
  { title: TEXTS.order.statusProcessing, value: 'PROCESSING' },
  { title: TEXTS.order.statusActive, value: 'ACTIVE' },
  { title: TEXTS.order.statusSuspended, value: 'SUSPENDED' },
  { title: TEXTS.order.statusCancelled, value: 'CANCELLED' },
  { title: TEXTS.order.statusExpired, value: 'EXPIRED' }
])

// 计费周期选项
const billingPeriodOptions = computed(() => [
  { title: TEXTS.order.hourly, value: 'hourly' },
  { title: TEXTS.order.daily, value: 'daily' },
  { title: TEXTS.order.monthly, value: 'monthly' },
  { title: TEXTS.order.quarterly, value: 'quarterly' },
  { title: TEXTS.order.semiAnnual, value: 'semi_annual' },
  { title: TEXTS.order.annual, value: 'annual' }
])

// 防抖搜索
const debouncedSearch = debounce(() => {
  currentPage.value = 1
  loadOrders()
}, 500)

// 加载订单列表
const loadOrders = async () => {
  loading.value = true
  try {
    const response = await orderApi.getUserOrders()
    if (response.success) {
      orders.value = response.data || []
      totalItems.value = response.data?.length || 0
    } else {
      showNotification(response.message || '加载订单失败', 'error')
    }
  } catch (error: any) {
    console.error('加载订单失败:', error)
    showNotification(error.message || '加载订单失败', 'error')
  } finally {
    loading.value = false
  }
}

// 重置筛选条件
const resetFilters = () => {
  searchQuery.value = ''
  statusFilter.value = ''
  billingPeriodFilter.value = ''
  currentPage.value = 1
  loadOrders()
}

// 查看订单详情
const viewOrder = (order: any) => {
  selectedOrder.value = order
  detailDialog.value = true
}

// 支付订单
const payOrder = async (order: any) => {
  try {
    const response = await orderApi.processOrderPayment(order.id)
    if (response.success) {
      showNotification('支付成功', 'success')
      loadOrders()
    } else {
      showNotification(response.message || '支付失败', 'error')
    }
  } catch (error: any) {
    console.error('支付订单失败:', error)
    showNotification(error.message || '支付失败', 'error')
  }
}

// 取消订单
const cancelOrder = async (order: any) => {
  try {
    const response = await orderApi.cancelOrder(order.id)
    if (response.success) {
      showNotification('取消订单成功', 'success')
      loadOrders()
    } else {
      showNotification(response.message || '取消订单失败', 'error')
    }
  } catch (error: any) {
    console.error('取消订单失败:', error)
    showNotification(error.message || '取消订单失败', 'error')
  }
}

// 获取状态颜色
const getStatusColor = (status: string) => {
  const colors: Record<string, string> = {
    PENDING: 'warning',
    PAID: 'info',
    PROCESSING: 'primary',
    ACTIVE: 'success',
    SUSPENDED: 'error',
    CANCELLED: 'error',
    EXPIRED: 'error'
  }
  return colors[status] || 'default'
}

// 获取状态文本
const getStatusText = (status: string) => {
  const statusMap: Record<string, string> = {
    PENDING: TEXTS.order.statusPending,
    PAID: TEXTS.order.statusPaid,
    PROCESSING: TEXTS.order.statusProcessing,
    ACTIVE: TEXTS.order.statusActive,
    SUSPENDED: TEXTS.order.statusSuspended,
    CANCELLED: TEXTS.order.statusCancelled,
    EXPIRED: TEXTS.order.statusExpired
  }
  return statusMap[status] || status
}

// 获取计费周期文本
const getBillingPeriodText = (period: string) => {
  const periodMap: Record<string, string> = {
    hourly: TEXTS.order.hourly,
    daily: TEXTS.order.daily,
    monthly: TEXTS.order.monthly,
    quarterly: TEXTS.order.quarterly,
    semi_annual: TEXTS.order.semiAnnual,
    annual: TEXTS.order.annual
  }
  return periodMap[period] || period
}

// 格式化日期时间
const formatDateTime = (dateTime: string) => {
  if (!dateTime) return 'N/A'
  return new Date(dateTime).toLocaleString()
}

// 获取过期时间样式
const getExpirationClass = (expiresAt: string) => {
  if (!expiresAt) return ''
  const now = new Date()
  const expiration = new Date(expiresAt)
  const diffDays = Math.ceil((expiration.getTime() - now.getTime()) / (1000 * 60 * 60 * 24))
  
  if (diffDays < 0) return 'text-error'
  if (diffDays <= 7) return 'text-warning'
  return ''
}

// 管理服务器
const manageServer = (order: any) => {
  router.push(`/server-manage/${order.id}`)
}



// 切换自动续费
const toggleAutoRenewal = async (order: any, autoRenewal: boolean) => {
  try {
    const response = await orderApi.updateAutoRenewal(order.id, autoRenewal)
    if (response.success) {
      // 更新本地订单状态
      const orderIndex = orders.value.findIndex(o => o.id === order.id)
      if (orderIndex !== -1) {
        orders.value[orderIndex].autoRenewal = autoRenewal
      }
      showNotification(
        autoRenewal ? TEXTS.sales.autoRenewal + '启用成功' : TEXTS.sales.autoRenewal + '禁用成功', 
        'success'
      )
    } else {
      showNotification(response.message || '自动续费设置更新失败', 'error')
    }
  } catch (error: any) {
    console.error('更新自动续费设置失败:', error)
    showNotification(error.message || '自动续费设置更新失败', 'error')
  }
}

// 组件挂载时加载数据
onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.font-mono {
  font-family: 'Roboto Mono', monospace;
}
</style>
