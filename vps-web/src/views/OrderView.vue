<template>
  <PageLayout :title="t('order.title')">
    <v-container fluid>
      <!-- 搜索和筛选 -->

      <!-- 搜索和筛选 -->
      <v-row class="mb-4">
        <v-col cols="12" md="4">
          <v-text-field
            v-model="searchQuery"
            :label="t('order.searchPlaceholder')"
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
            :label="t('order.status')"
            variant="outlined"
            density="compact"
            clearable
            @update:model-value="loadOrders"
          />
        </v-col>
        <v-col cols="12" md="3">
          <v-select
            v-model="billingPeriodFilter"
            :items="billingPeriodOptions"
            :label="t('order.billingPeriod')"
            variant="outlined"
            density="compact"
            clearable
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
            {{ t('common.reset') }}
          </v-btn>
        </v-col>
      </v-row>

      <!-- 订单列表 -->
      <v-card>
        <v-card-title class="d-flex align-center justify-space-between">
          <span>{{ t('order.list') }}</span>
          <v-chip
            :color="orders.length > 0 ? 'primary' : 'default'"
            variant="tonal"
          >
            {{ t('common.total') }}: {{ totalItems }}
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
              <p class="text-h6 text-disabled">{{ t('order.noOrders') }}</p>
              <p class="text-body-2 text-disabled">{{ t('order.noOrdersDesc') }}</p>
            </div>
          </template>
        </v-data-table-server>
      </v-card>

      <!-- 订单详情对话框 -->
      <v-dialog v-model="detailDialog" max-width="800">
        <v-card v-if="selectedOrder">
          <v-card-title class="d-flex align-center">
            <v-icon icon="mdi-receipt" class="me-2" />
            {{ t('order.orderDetails') }}
          </v-card-title>
          
          <v-card-text>
            <v-row>
              <v-col cols="12" md="6">
                <v-list density="compact">
                  <v-list-item>
                    <v-list-item-title>{{ t('order.orderNumber') }}</v-list-item-title>
                    <v-list-item-subtitle>{{ selectedOrder.orderNumber }}</v-list-item-subtitle>
                  </v-list-item>
                  <v-list-item>
                    <v-list-item-title>{{ t('order.status') }}</v-list-item-title>
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
                    <v-list-item-title>{{ t('order.amount') }}</v-list-item-title>
                    <v-list-item-subtitle>¥{{ selectedOrder.amount }}</v-list-item-subtitle>
                  </v-list-item>
                  <v-list-item>
                    <v-list-item-title>{{ t('order.billingPeriod') }}</v-list-item-title>
                    <v-list-item-subtitle>{{ getBillingPeriodText(selectedOrder.billingPeriod) }}</v-list-item-subtitle>
                  </v-list-item>
                </v-list>
              </v-col>
              <v-col cols="12" md="6">
                <v-list density="compact">
                  <v-list-item>
                    <v-list-item-title>{{ t('order.serverName') }}</v-list-item-title>
                    <v-list-item-subtitle>{{ selectedOrder.serverName || 'N/A' }}</v-list-item-subtitle>
                  </v-list-item>
                  <v-list-item>
                    <v-list-item-title>{{ t('order.createdAt') }}</v-list-item-title>
                    <v-list-item-subtitle>{{ formatDateTime(selectedOrder.createdAt) }}</v-list-item-subtitle>
                  </v-list-item>
                  <v-list-item>
                    <v-list-item-title>{{ t('order.expiresAt') }}</v-list-item-title>
                    <v-list-item-subtitle>{{ formatDateTime(selectedOrder.expiresAt) }}</v-list-item-subtitle>
                  </v-list-item>
                </v-list>
              </v-col>
            </v-row>

            <!-- 服务器配置信息 -->
            <v-divider class="my-4" />
            <h3 class="text-h6 mb-3">{{ t('order.serverSpecs') }}</h3>
            <v-row>
              <v-col cols="6" md="3">
                <v-card variant="tonal" class="pa-3">
                  <div class="text-center">
                    <v-icon icon="mdi-cpu-64-bit" size="24" class="mb-2" />
                    <div class="text-body-2 text-medium-emphasis">CPU</div>
                    <div class="text-h6">{{ selectedOrder.cpuCores }}{{ t('order.cores') }}</div>
                  </div>
                </v-card>
              </v-col>
              <v-col cols="6" md="3">
                <v-card variant="tonal" class="pa-3">
                  <div class="text-center">
                    <v-icon icon="mdi-memory" size="24" class="mb-2" />
                    <div class="text-body-2 text-medium-emphasis">{{ t('order.memory') }}</div>
                    <div class="text-h6">{{ selectedOrder.memoryGb }}GB</div>
                  </div>
                </v-card>
              </v-col>
              <v-col cols="6" md="3">
                <v-card variant="tonal" class="pa-3">
                  <div class="text-center">
                    <v-icon icon="mdi-harddisk" size="24" class="mb-2" />
                    <div class="text-body-2 text-medium-emphasis">{{ t('order.storage') }}</div>
                    <div class="text-h6">{{ selectedOrder.storageGb }}GB</div>
                  </div>
                </v-card>
              </v-col>
              <v-col cols="6" md="3">
                <v-card variant="tonal" class="pa-3">
                  <div class="text-center">
                    <v-icon icon="mdi-speedometer" size="24" class="mb-2" />
                    <div class="text-body-2 text-medium-emphasis">{{ t('order.bandwidth') }}</div>
                    <div class="text-h6">{{ selectedOrder.bandwidthMbps }}Mbps</div>
                  </div>
                </v-card>
              </v-col>
            </v-row>
          </v-card-text>

          <v-card-actions>
            <v-spacer />
            <v-btn
              color="primary"
              variant="text"
              @click="detailDialog = false"
            >
              {{ t('common.close') }}
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
    </v-container>
  </PageLayout>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { orderApi } from '@/api/order'
import { useNotification } from '@/composables/useNotification'
import PageLayout from '@/components/PageLayout.vue'

// 简单的防抖函数实现
const debounce = (func: Function, delay: number) => {
  let timeoutId: ReturnType<typeof setTimeout>
  return (...args: any[]) => {
    clearTimeout(timeoutId)
    timeoutId = setTimeout(() => func.apply(null, args), delay)
  }
}

const { t } = useI18n()
const { showNotification } = useNotification()

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
  { title: t('order.orderNumber'), key: 'orderNumber', sortable: false },
  { title: t('order.serverName'), key: 'serverName', sortable: false },
  { title: t('order.amount'), key: 'amount', sortable: true },
  { title: t('order.status'), key: 'status', sortable: false },
  { title: t('order.billingPeriod'), key: 'billingPeriod', sortable: false },
  { title: t('order.createdAt'), key: 'createdAt', sortable: true },
  { title: t('order.expiresAt'), key: 'expiresAt', sortable: true },
  { title: t('common.actions'), key: 'actions', sortable: false, width: 120 }
])

// 状态选项
const statusOptions = computed(() => [
  { title: t('order.statusPending'), value: 'PENDING' },
  { title: t('order.statusPaid'), value: 'PAID' },
  { title: t('order.statusProcessing'), value: 'PROCESSING' },
  { title: t('order.statusActive'), value: 'ACTIVE' },
  { title: t('order.statusSuspended'), value: 'SUSPENDED' },
  { title: t('order.statusCancelled'), value: 'CANCELLED' },
  { title: t('order.statusExpired'), value: 'EXPIRED' }
])

// 计费周期选项
const billingPeriodOptions = computed(() => [
  { title: t('order.hourly'), value: 'hourly' },
  { title: t('order.daily'), value: 'daily' },
  { title: t('order.monthly'), value: 'monthly' },
  { title: t('order.quarterly'), value: 'quarterly' },
  { title: t('order.semiAnnual'), value: 'semi_annual' },
  { title: t('order.annual'), value: 'annual' }
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
      showNotification(response.message || t('order.loadFailed'), 'error')
    }
  } catch (error: any) {
    console.error('加载订单失败:', error)
    showNotification(error.message || t('order.loadFailed'), 'error')
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
      showNotification(t('order.paySuccess'), 'success')
      loadOrders()
    } else {
      showNotification(response.message || t('order.payFailed'), 'error')
    }
  } catch (error: any) {
    console.error('支付订单失败:', error)
    showNotification(error.message || t('order.payFailed'), 'error')
  }
}

// 取消订单
const cancelOrder = async (order: any) => {
  try {
    const response = await orderApi.cancelOrder(order.id)
    if (response.success) {
      showNotification(t('order.cancelSuccess'), 'success')
      loadOrders()
    } else {
      showNotification(response.message || t('order.cancelFailed'), 'error')
    }
  } catch (error: any) {
    console.error('取消订单失败:', error)
    showNotification(error.message || t('order.cancelFailed'), 'error')
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
    PENDING: t('order.statusPending'),
    PAID: t('order.statusPaid'),
    PROCESSING: t('order.statusProcessing'),
    ACTIVE: t('order.statusActive'),
    SUSPENDED: t('order.statusSuspended'),
    CANCELLED: t('order.statusCancelled'),
    EXPIRED: t('order.statusExpired')
  }
  return statusMap[status] || status
}

// 获取计费周期文本
const getBillingPeriodText = (period: string) => {
  const periodMap: Record<string, string> = {
    hourly: t('order.hourly'),
    daily: t('order.daily'),
    monthly: t('order.monthly'),
    quarterly: t('order.quarterly'),
    semi_annual: t('order.semiAnnual'),
    annual: t('order.annual')
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
