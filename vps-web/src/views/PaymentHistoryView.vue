<template>
  <HomeLayout>
    <v-container fluid>
      <!-- 搜索和筛选 -->
      <v-row class="mb-4">
        <v-col cols="12" md="4">
          <v-text-field
            v-model="searchQuery"
            :label="TEXTS.payment.searchPlaceholder"
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
            :label="TEXTS.payment.status"
            variant="outlined"
            density="compact"
            clearable
            :menu-props="{ maxHeight: '300px', zIndex: 9999 }"
            @update:model-value="filterPayments"
          />
        </v-col>
        <v-col cols="12" md="3">
          <v-select
            v-model="typeFilter"
            :items="typeOptions"
            :label="TEXTS.payment.paymentMethod"
            variant="outlined"
            density="compact"
            clearable
            :menu-props="{ maxHeight: '300px', zIndex: 9999 }"
            @update:model-value="filterPayments"
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

      <!-- 支付统计卡片 -->
      <v-row class="mb-4">
        <v-col cols="12" md="3">
          <v-card class="text-center pa-4" color="success" variant="tonal">
            <v-icon size="32" class="mb-2">mdi-check-circle</v-icon>
            <div class="text-h6">{{ TEXTS.payment.successfulPayments }}</div>
            <div class="text-h4">¥{{ totalSuccessAmount.toFixed(2) }}</div>
          </v-card>
        </v-col>
        <v-col cols="12" md="3">
          <v-card class="text-center pa-4" color="warning" variant="tonal">
            <v-icon size="32" class="mb-2">mdi-clock-outline</v-icon>
            <div class="text-h6">{{ TEXTS.payment.pendingPayments }}</div>
            <div class="text-h4">{{ pendingCount }}</div>
          </v-card>
        </v-col>
        <v-col cols="12" md="3">
          <v-card class="text-center pa-4" color="error" variant="tonal">
            <v-icon size="32" class="mb-2">mdi-close-circle</v-icon>
            <div class="text-h6">{{ TEXTS.payment.failedPayments }}</div>
            <div class="text-h4">{{ failedCount }}</div>
          </v-card>
        </v-col>
        <v-col cols="12" md="3">
          <v-card class="text-center pa-4" color="info" variant="tonal">
            <v-icon size="32" class="mb-2">mdi-receipt</v-icon>
            <div class="text-h6">{{ TEXTS.payment.totalPayments }}</div>
            <div class="text-h4">{{ paymentOrders.length }}</div>
          </v-card>
        </v-col>
      </v-row>

      <!-- 支付记录列表 -->
      <v-card>
        <v-card-title class="d-flex align-center justify-space-between">
          <span>{{ TEXTS.payment.list }}</span>
          <v-chip
            :color="paymentOrders.length > 0 ? 'primary' : 'default'"
            variant="tonal"
          >
            {{ TEXTS.common.total }}: {{ totalItems }}
          </v-chip>
        </v-card-title>

        <v-data-table
          v-model:items-per-page="itemsPerPage"
          v-model:page="currentPage"
          :headers="headers"
          :items="paymentOrders"
          :loading="loading"
          class="elevation-0"
        >
          <!-- 订单号列 -->
          <template #item.outTradeNo="{ item }">
            <v-chip
              color="primary"
              variant="tonal"
              size="small"
              class="font-mono"
            >
              {{ item.outTradeNo }}
            </v-chip>
          </template>

          <!-- 金额列 -->
          <template #item.money="{ item }">
            <div class="d-flex align-center">
              <v-icon icon="mdi-currency-cny" size="16" class="me-1" />
              <span class="font-weight-medium text-success">¥{{ item.money }}</span>
            </div>
          </template>

          <!-- 支付方式列 -->
          <template #item.type="{ item }">
            <v-chip
              :color="getPaymentMethodColor(item.type)"
              variant="tonal"
              size="small"
            >
              <v-icon start size="14">{{ getPaymentMethodIcon(item.type) }}</v-icon>
              {{ getPaymentMethodName(item.type) }}
            </v-chip>
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

          <!-- 商品名称列 -->
          <template #item.name="{ item }">
            <div class="text-body-2">
              {{ item.name }}
            </div>
          </template>

          <!-- 创建时间列 -->
          <template #item.createdAt="{ item }">
            <div class="text-body-2">
              {{ formatDateTime(item.createdAt) }}
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
                @click="viewDetails(item)"
              />
              <v-btn
                v-if="item.status === 'PENDING'"
                icon="mdi-refresh"
                size="small"
                variant="text"
                color="info"
                @click="checkPaymentStatus(item.outTradeNo)"
              />
            </div>
          </template>

          <!-- 无数据状态 -->
          <template #no-data>
            <div class="text-center py-8">
              <v-icon icon="mdi-credit-card-outline" size="64" class="text-disabled mb-4" />
              <p class="text-h6 text-disabled">{{ TEXTS.payment.noPayments }}</p>
              <p class="text-body-2 text-disabled">{{ TEXTS.payment.noPaymentsDesc }}</p>
            </div>
          </template>
        </v-data-table>
      </v-card>

      <!-- 通知组件 -->
      <NotificationSnackbar
        v-model="notificationState.show"
        :message="notificationState.message"
        :type="notificationState.type"
      />

      <!-- 支付详情对话框 -->
      <UnifiedDialog
        v-model="detailDialog"
        :title="TEXTS.payment.paymentDetails"
        header-icon="mdi-receipt"
        max-width="700"
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
                  <v-list-item-title>{{ TEXTS.payment.orderNumber }}</v-list-item-title>
                  <v-list-item-subtitle>{{ selectedOrder.outTradeNo }}</v-list-item-subtitle>
                </v-list-item>
                <v-list-item>
                  <v-list-item-title>{{ TEXTS.payment.amount }}</v-list-item-title>
                  <v-list-item-subtitle>
                    <span class="text-h6 text-success">¥{{ selectedOrder.money }}</span>
                  </v-list-item-subtitle>
                </v-list-item>
                <v-list-item>
                  <v-list-item-title>{{ TEXTS.payment.paymentMethod }}</v-list-item-title>
                  <v-list-item-subtitle>
                    <v-chip
                      :color="getPaymentMethodColor(selectedOrder.type)"
                      variant="tonal"
                      size="small"
                    >
                      <v-icon start size="14">{{ getPaymentMethodIcon(selectedOrder.type) }}</v-icon>
                      {{ getPaymentMethodName(selectedOrder.type) }}
                    </v-chip>
                  </v-list-item-subtitle>
                </v-list-item>
                <v-list-item>
                  <v-list-item-title>{{ TEXTS.payment.status }}</v-list-item-title>
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
                  <v-list-item-title>{{ TEXTS.payment.createdAt }}</v-list-item-title>
                  <v-list-item-subtitle>{{ formatDateTime(selectedOrder.createdAt) }}</v-list-item-subtitle>
                </v-list-item>
              </v-list>
            </v-col>
            <v-col cols="12" md="6">
              <v-list density="compact">
                <v-list-item v-if="selectedOrder.tradeNo">
                  <v-list-item-title>{{ TEXTS.payment.thirdPartyOrderNo }}</v-list-item-title>
                  <v-list-item-subtitle>{{ selectedOrder.tradeNo }}</v-list-item-subtitle>
                </v-list-item>
                <v-list-item v-if="selectedOrder.completedAt">
                  <v-list-item-title>{{ TEXTS.payment.completedAt }}</v-list-item-title>
                  <v-list-item-subtitle>{{ formatDateTime(selectedOrder.completedAt) }}</v-list-item-subtitle>
                </v-list-item>
                <v-list-item>
                  <v-list-item-title>{{ TEXTS.payment.productName }}</v-list-item-title>
                  <v-list-item-subtitle>{{ selectedOrder.name }}</v-list-item-subtitle>
                </v-list-item>
                <v-list-item v-if="selectedOrder.clientIp">
                  <v-list-item-title>{{ TEXTS.payment.clientIp }}</v-list-item-title>
                  <v-list-item-subtitle>{{ selectedOrder.clientIp }}</v-list-item-subtitle>
                </v-list-item>
                <v-list-item v-if="selectedOrder.device">
                  <v-list-item-title>{{ TEXTS.payment.device }}</v-list-item-title>
                  <v-list-item-subtitle>{{ selectedOrder.device }}</v-list-item-subtitle>
                </v-list-item>
              </v-list>
            </v-col>
          </v-row>

          <!-- 操作按钮 -->
          <div class="d-flex justify-end mt-4">
            <v-btn
              v-if="selectedOrder.status === 'PENDING'"
              color="primary"
              variant="outlined"
              class="me-3"
              @click="checkPaymentStatus(selectedOrder.outTradeNo)"
            >
              <v-icon start>mdi-refresh</v-icon>
              {{ TEXTS.payment.checkStatus }}
            </v-btn>
          </div>
        </div>
      </UnifiedDialog>
    </v-container>
  </HomeLayout>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import HomeLayout from '@/components/HomeLayout.vue'
import UnifiedDialog from '@/components/UnifiedDialog.vue'
import NotificationSnackbar from '@/components/NotificationSnackbar.vue'
import { paymentApi, type PaymentOrder } from '@/api/payment'
import { useNotification } from '@/composables/useNotification'
import { TEXTS } from '@/constants/texts'

// 简单的防抖函数实现
const debounce = (func: Function, delay: number) => {
  let timeoutId: ReturnType<typeof setTimeout>
  return (...args: any[]) => {
    clearTimeout(timeoutId)
    timeoutId = setTimeout(() => func.apply(null, args), delay)
  }
}

const { notificationState, showNotification } = useNotification()

// 响应式数据
const paymentOrders = ref<PaymentOrder[]>([])
const allPaymentOrders = ref<PaymentOrder[]>([]) // 存储所有支付记录
const loading = ref(false)
const searchQuery = ref('')
const statusFilter = ref('')
const typeFilter = ref('')
const currentPage = ref(1)
const itemsPerPage = ref(10)
const totalItems = ref(0)
const detailDialog = ref(false)
const selectedOrder = ref<PaymentOrder | null>(null)

// 表格列定义
const headers = computed(() => [
  { title: TEXTS.payment.orderNumber, key: 'outTradeNo', sortable: false },
  { title: TEXTS.payment.amount, key: 'money', sortable: true },
  { title: TEXTS.payment.paymentMethod, key: 'type', sortable: false },
  { title: TEXTS.payment.status, key: 'status', sortable: false },
  { title: TEXTS.payment.productName, key: 'name', sortable: false },
  { title: TEXTS.payment.createdAt, key: 'createdAt', sortable: true },
  { title: '操作', key: 'actions', sortable: false, width: 120 }
])

// 状态选项
const statusOptions = computed(() => [
  { title: TEXTS.payment.statusPending, value: 'PENDING' },
  { title: TEXTS.payment.statusSuccess, value: 'SUCCESS' },
  { title: TEXTS.payment.statusFailed, value: 'FAILED' },
  { title: TEXTS.payment.statusCancelled, value: 'CANCELLED' }
])

// 支付方式选项
const typeOptions = computed(() => {
  const methods = paymentApi.getPaymentMethods()
  return methods.map(method => ({
    title: method.name,
    value: method.code
  }))
})

// 统计数据
const totalSuccessAmount = computed(() => {
  return paymentOrders.value
    .filter(p => p.status === 'SUCCESS')
    .reduce((sum, p) => sum + p.money, 0)
})

const pendingCount = computed(() => {
  return paymentOrders.value.filter(p => p.status === 'PENDING').length
})

const failedCount = computed(() => {
  return paymentOrders.value.filter(p => p.status === 'FAILED').length
})

// 防抖搜索
const debouncedSearch = debounce(() => {
  currentPage.value = 1
  filterPayments()
}, 500)

// 筛选支付记录
const filterPayments = () => {
  let filtered = [...allPaymentOrders.value]
  
  // 搜索筛选
  if (searchQuery.value.trim()) {
    const query = searchQuery.value.toLowerCase().trim()
    filtered = filtered.filter(payment => 
      payment.outTradeNo.toLowerCase().includes(query) ||
      payment.name.toLowerCase().includes(query) ||
      payment.money.toString().includes(query) ||
      (payment.tradeNo && payment.tradeNo.toLowerCase().includes(query))
    )
  }
  
  // 状态筛选
  if (statusFilter.value) {
    filtered = filtered.filter(payment => payment.status === statusFilter.value)
  }
  
  // 支付方式筛选
  if (typeFilter.value) {
    filtered = filtered.filter(payment => payment.type === typeFilter.value)
  }
  
  paymentOrders.value = filtered
  totalItems.value = filtered.length
}

// 加载支付记录
const loadPaymentOrders = async () => {
  loading.value = true
  try {
    const response = await paymentApi.getUserPaymentOrders(0, 1000) // 获取所有记录用于客户端筛选
    if (response.data.success) {
      allPaymentOrders.value = response.data.data.content || []
      filterPayments() // 加载完成后应用筛选
    } else {
      showNotification(response.data.message || '加载支付记录失败', 'error')
    }
  } catch (error: any) {
    console.error('加载支付记录失败:', error)
    // 如果是网络错误或服务器未启动，显示更友好的错误信息
    if (error.message?.includes('Network Error') || error.code === 'ERR_NETWORK') {
      showNotification('无法连接到服务器，请检查网络连接或服务器状态', 'error')
    } else if (error.response?.status === 404) {
      showNotification('支付记录API接口不存在，请检查后端服务', 'error')
    } else {
      showNotification(error.response?.data?.message || error.message || '加载支付记录失败', 'error')
    }
  } finally {
    loading.value = false
  }
}

// 重置筛选条件
const resetFilters = () => {
  searchQuery.value = ''
  statusFilter.value = ''
  typeFilter.value = ''
  currentPage.value = 1
  filterPayments()
}

// 检查支付状态
const checkPaymentStatus = async (outTradeNo: string) => {
  try {
    const response = await paymentApi.getPaymentOrder(outTradeNo)
    if (response.data.success) {
      const order = allPaymentOrders.value.find(o => o.outTradeNo === outTradeNo)
      if (order) {
        order.status = response.data.data.status
        if (response.data.data.completedAt) {
          order.completedAt = response.data.data.completedAt
        }
        filterPayments() // 重新筛选以更新显示
        
        if (response.data.data.status === 'SUCCESS') {
          showNotification('支付成功！', 'success')
        } else if (response.data.data.status === 'FAILED') {
          showNotification('支付失败', 'error')
        } else {
          showNotification('支付尚未完成', 'info')
        }
      }
    } else {
      showNotification(response.data.message || '查询支付状态失败', 'error')
    }
  } catch (error: any) {
    console.error('查询支付状态失败:', error)
    showNotification(error.response?.data?.message || error.message || '查询支付状态失败', 'error')
  }
}

// 查看详情
const viewDetails = (order: PaymentOrder) => {
  selectedOrder.value = order
  detailDialog.value = true
}

// 获取支付方式名称
const getPaymentMethodName = (code: string) => {
  const method = paymentApi.getPaymentMethods().find(m => m.code === code)
  return method?.name || code
}

// 获取支付方式颜色
const getPaymentMethodColor = (code: string) => {
  const colors: Record<string, string> = {
    alipay: 'blue',
    wxpay: 'green',
    qqpay: 'purple',
    bank: 'orange',
    jdpay: 'red',
    paypal: 'indigo',
    usdt: 'amber'
  }
  return colors[code] || 'grey'
}

// 获取支付方式图标
const getPaymentMethodIcon = (code: string) => {
  const icons: Record<string, string> = {
    alipay: 'mdi-alipay',
    wxpay: 'mdi-wechat',
    qqpay: 'mdi-qqchat',
    bank: 'mdi-bank',
    jdpay: 'mdi-shopping',
    paypal: 'mdi-paypal',
    usdt: 'mdi-currency-usd'
  }
  return icons[code] || 'mdi-credit-card'
}

// 获取状态颜色
const getStatusColor = (status: string) => {
  const colors: Record<string, string> = {
    PENDING: 'warning',
    SUCCESS: 'success',
    FAILED: 'error',
    CANCELLED: 'grey'
  }
  return colors[status] || 'default'
}

// 获取状态文本
const getStatusText = (status: string) => {
  const statusMap: Record<string, string> = {
    PENDING: TEXTS.payment.statusPending,
    SUCCESS: TEXTS.payment.statusSuccess,
    FAILED: TEXTS.payment.statusFailed,
    CANCELLED: TEXTS.payment.statusCancelled
  }
  return statusMap[status] || status
}

// 格式化日期时间
const formatDateTime = (dateTime: string) => {
  if (!dateTime) return 'N/A'
  return new Date(dateTime).toLocaleString()
}

// 组件挂载时加载数据
onMounted(() => {
  loadPaymentOrders()
})
</script>

<style scoped>
.font-mono {
  font-family: 'Roboto Mono', monospace;
}
</style>