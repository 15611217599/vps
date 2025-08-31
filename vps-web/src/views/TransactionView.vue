<template>
  <PageLayout>
    <v-container fluid>
      <!-- 搜索和筛选 -->
      <v-row class="mb-4">
        <v-col cols="12" md="3">
          <v-text-field
            v-model="searchQuery"
            :label="TEXTS.transaction.searchPlaceholder"
            prepend-inner-icon="mdi-magnify"
            variant="outlined"
            density="compact"
            clearable
            @input="debouncedSearch"
          />
        </v-col>
        <v-col cols="12" md="3">
          <v-select
            v-model="typeFilter"
            :items="typeOptions"
            :label="TEXTS.transaction.type"
            variant="outlined"
            density="compact"
            clearable
            :menu-props="{ maxHeight: '300px', zIndex: 9999 }"
            @update:model-value="loadTransactions"
          />
        </v-col>
        <v-col cols="12" md="3">
          <v-select
            v-model="statusFilter"
            :items="statusOptions"
            :label="TEXTS.transaction.status"
            variant="outlined"
            density="compact"
            clearable
            :menu-props="{ maxHeight: '300px', zIndex: 9999 }"
            @update:model-value="loadTransactions"
          />
        </v-col>
        <v-col cols="12" md="3">
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

      <!-- 交易统计卡片 -->
      <v-row class="mb-4">
        <v-col cols="12" md="3">
          <v-card class="text-center pa-4" color="success" variant="tonal">
            <v-icon size="32" class="mb-2">mdi-trending-up</v-icon>
            <div class="text-h6">{{ TEXTS.transaction.totalIncome }}</div>
            <div class="text-h4">¥{{ totalIncome.toFixed(2) }}</div>
          </v-card>
        </v-col>
        <v-col cols="12" md="3">
          <v-card class="text-center pa-4" color="error" variant="tonal">
            <v-icon size="32" class="mb-2">mdi-trending-down</v-icon>
            <div class="text-h6">{{ TEXTS.transaction.totalExpense }}</div>
            <div class="text-h4">¥{{ totalExpense.toFixed(2) }}</div>
          </v-card>
        </v-col>
        <v-col cols="12" md="3">
          <v-card class="text-center pa-4" color="info" variant="tonal">
            <v-icon size="32" class="mb-2">mdi-receipt</v-icon>
            <div class="text-h6">{{ TEXTS.transaction.totalTransactions }}</div>
            <div class="text-h4">{{ transactions.length }}</div>
          </v-card>
        </v-col>
        <v-col cols="12" md="3">
          <v-card class="text-center pa-4" color="warning" variant="tonal">
            <v-icon size="32" class="mb-2">mdi-clock-outline</v-icon>
            <div class="text-h6">{{ TEXTS.transaction.pendingTransactions }}</div>
            <div class="text-h4">{{ pendingCount }}</div>
          </v-card>
        </v-col>
      </v-row>

      <!-- 交易列表 -->
      <v-card>
        <v-card-title class="d-flex align-center justify-space-between">
          <span>{{ TEXTS.transaction.list }}</span>
          <v-chip
            :color="transactions.length > 0 ? 'primary' : 'default'"
            variant="tonal"
          >
            {{ TEXTS.common.total }}: {{ totalItems }}
          </v-chip>
        </v-card-title>

        <v-data-table-server
          v-model:items-per-page="itemsPerPage"
          v-model:page="currentPage"
          :headers="headers"
          :items="filteredTransactions"
          :items-length="totalItems"
          :loading="loading"
          :search="searchQuery"
          class="elevation-0"
          @update:options="loadTransactions"
        >
          <!-- 交易号列 -->
          <template #item.transactionNumber="{ item }">
            <v-chip
              color="primary"
              variant="tonal"
              size="small"
              class="font-mono"
            >
              {{ item.transactionNumber }}
            </v-chip>
          </template>

          <!-- 类型列 -->
          <template #item.type="{ item }">
            <v-chip
              :color="getTypeColor(item.type)"
              variant="tonal"
              size="small"
            >
              <v-icon start size="14">{{ getTypeIcon(item.type) }}</v-icon>
              {{ getTypeText(item.type) }}
            </v-chip>
          </template>

          <!-- 金额列 -->
          <template #item.amount="{ item }">
            <div class="d-flex align-center">
              <v-icon 
                :icon="item.type === 'ORDER_PAYMENT' ? 'mdi-minus' : 'mdi-plus'" 
                :color="item.type === 'ORDER_PAYMENT' ? 'error' : 'success'"
                size="16" 
                class="me-1" 
              />
              <span 
                class="font-weight-medium"
                :class="item.type === 'ORDER_PAYMENT' ? 'text-error' : 'text-success'"
              >
                ¥{{ item.amount }}
              </span>
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

          <!-- 余额变化列 -->
          <template #item.balanceChange="{ item }">
            <div v-if="item.balanceBefore !== null && item.balanceAfter !== null" class="text-body-2">
              <div>{{ TEXTS.transaction.before }}: ¥{{ item.balanceBefore }}</div>
              <div>{{ TEXTS.transaction.after }}: ¥{{ item.balanceAfter }}</div>
            </div>
            <span v-else class="text-disabled">N/A</span>
          </template>

          <!-- 关联订单列 -->
          <template #item.orderNumber="{ item }">
            <v-chip
              v-if="item.orderNumber"
              color="info"
              variant="outlined"
              size="small"
              class="font-mono"
              @click="viewOrder(item.orderId)"
            >
              {{ item.orderNumber }}
            </v-chip>
            <span v-else class="text-disabled">N/A</span>
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
                @click="viewTransaction(item)"
              />
            </div>
          </template>

          <!-- 无数据状态 -->
          <template #no-data>
            <div class="text-center py-8">
              <v-icon icon="mdi-receipt-outline" size="64" class="text-disabled mb-4" />
              <p class="text-h6 text-disabled">{{ TEXTS.transaction.noTransactions }}</p>
              <p class="text-body-2 text-disabled">{{ TEXTS.transaction.noTransactionsDesc }}</p>
            </div>
          </template>
        </v-data-table-server>
      </v-card>

      <!-- 交易详情对话框 -->
      <UnifiedDialog
        v-model="detailDialog"
        :title="TEXTS.transaction.transactionDetails"
        header-icon="mdi-receipt"
        max-width="600"
        :show-save-button="false"
        :show-cancel-button="false"
        :show-close-button="true"
        @cancel="detailDialog = false"
      >
        <div v-if="selectedTransaction">
          <v-row>
            <v-col cols="12">
              <v-list density="compact">
                <v-list-item>
                  <v-list-item-title>{{ TEXTS.transaction.transactionNumber }}</v-list-item-title>
                  <v-list-item-subtitle>{{ selectedTransaction.transactionNumber }}</v-list-item-subtitle>
                </v-list-item>
                <v-list-item>
                  <v-list-item-title>{{ TEXTS.transaction.type }}</v-list-item-title>
                  <v-list-item-subtitle>
                    <v-chip
                      :color="getTypeColor(selectedTransaction.type)"
                      variant="tonal"
                      size="small"
                    >
                      <v-icon start size="14">{{ getTypeIcon(selectedTransaction.type) }}</v-icon>
                      {{ getTypeText(selectedTransaction.type) }}
                    </v-chip>
                  </v-list-item-subtitle>
                </v-list-item>
                <v-list-item>
                  <v-list-item-title>{{ TEXTS.transaction.status }}</v-list-item-title>
                  <v-list-item-subtitle>
                    <v-chip
                      :color="getStatusColor(selectedTransaction.status)"
                      variant="tonal"
                      size="small"
                    >
                      {{ getStatusText(selectedTransaction.status) }}
                    </v-chip>
                  </v-list-item-subtitle>
                </v-list-item>
                <v-list-item>
                  <v-list-item-title>{{ TEXTS.transaction.amount }}</v-list-item-title>
                  <v-list-item-subtitle>¥{{ selectedTransaction.amount }}</v-list-item-subtitle>
                </v-list-item>
                <v-list-item v-if="selectedTransaction.balanceBefore !== null">
                  <v-list-item-title>{{ TEXTS.transaction.balanceBefore }}</v-list-item-title>
                  <v-list-item-subtitle>¥{{ selectedTransaction.balanceBefore }}</v-list-item-subtitle>
                </v-list-item>
                <v-list-item v-if="selectedTransaction.balanceAfter !== null">
                  <v-list-item-title>{{ TEXTS.transaction.balanceAfter }}</v-list-item-title>
                  <v-list-item-subtitle>¥{{ selectedTransaction.balanceAfter }}</v-list-item-subtitle>
                </v-list-item>
                <v-list-item>
                  <v-list-item-title>{{ TEXTS.transaction.description }}</v-list-item-title>
                  <v-list-item-subtitle>{{ selectedTransaction.description }}</v-list-item-subtitle>
                </v-list-item>
                <v-list-item v-if="selectedTransaction.orderNumber">
                  <v-list-item-title>{{ TEXTS.transaction.relatedOrder }}</v-list-item-title>
                  <v-list-item-subtitle>{{ selectedTransaction.orderNumber }}</v-list-item-subtitle>
                </v-list-item>
                <v-list-item v-if="selectedTransaction.paymentMethod">
                  <v-list-item-title>{{ TEXTS.transaction.paymentMethod }}</v-list-item-title>
                  <v-list-item-subtitle>{{ selectedTransaction.paymentMethod }}</v-list-item-subtitle>
                </v-list-item>
                <v-list-item>
                  <v-list-item-title>{{ TEXTS.transaction.createdAt }}</v-list-item-title>
                  <v-list-item-subtitle>{{ formatDateTime(selectedTransaction.createdAt) }}</v-list-item-subtitle>
                </v-list-item>
                <v-list-item v-if="selectedTransaction.completedAt">
                  <v-list-item-title>{{ TEXTS.transaction.completedAt }}</v-list-item-title>
                  <v-list-item-subtitle>{{ formatDateTime(selectedTransaction.completedAt) }}</v-list-item-subtitle>
                </v-list-item>
              </v-list>
            </v-col>
          </v-row>
        </div>
      </UnifiedDialog>

      <!-- 通知组件 -->
      <NotificationSnackbar
        v-model="notificationState.show"
        :message="notificationState.message"
        :type="notificationState.type"
      />
    </v-container>
  </PageLayout>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'

import { useRouter } from 'vue-router'
import { transactionApi, type TransactionDTO } from '@/api/transaction'
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

// 响应式数据
const transactions = ref<TransactionDTO[]>([])
const loading = ref(false)
const searchQuery = ref('')
const typeFilter = ref('')
const statusFilter = ref('')
const currentPage = ref(1)
const itemsPerPage = ref(10)
const totalItems = ref(0)
const detailDialog = ref(false)
const selectedTransaction = ref<TransactionDTO | null>(null)

// 表格列定义
const headers = computed(() => [
  { title: TEXTS.transaction.transactionNumber, key: 'transactionNumber', sortable: false },
  { title: TEXTS.transaction.type, key: 'type', sortable: false },
  { title: TEXTS.transaction.amount, key: 'amount', sortable: true },
  { title: TEXTS.transaction.status, key: 'status', sortable: false },
  { title: TEXTS.transaction.balanceChange, key: 'balanceChange', sortable: false },
  { title: TEXTS.transaction.relatedOrder, key: 'orderNumber', sortable: false },
  { title: TEXTS.transaction.createdAt, key: 'createdAt', sortable: true },
  { title: '操作', key: 'actions', sortable: false, width: 80 }
])

// 类型选项
const typeOptions = computed(() => [
  { title: TEXTS.transaction.typeRecharge, value: 'RECHARGE' },
  { title: TEXTS.transaction.typeOrderPayment, value: 'ORDER_PAYMENT' },
  { title: TEXTS.transaction.typeRefund, value: 'REFUND' },
  { title: TEXTS.transaction.typeWithdrawal, value: 'WITHDRAWAL' },
  { title: TEXTS.transaction.typeAdjustment, value: 'ADJUSTMENT' }
])

// 状态选项
const statusOptions = computed(() => [
  { title: TEXTS.transaction.statusPending, value: 'PENDING' },
  { title: TEXTS.transaction.statusProcessing, value: 'PROCESSING' },
  { title: TEXTS.transaction.statusCompleted, value: 'COMPLETED' },
  { title: TEXTS.transaction.statusFailed, value: 'FAILED' },
  { title: TEXTS.transaction.statusCancelled, value: 'CANCELLED' }
])

// 过滤后的交易记录
const filteredTransactions = computed(() => {
  let filtered = transactions.value

  if (typeFilter.value) {
    filtered = filtered.filter(t => t.type === typeFilter.value)
  }

  if (statusFilter.value) {
    filtered = filtered.filter(t => t.status === statusFilter.value)
  }

  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(t => 
      t.transactionNumber.toLowerCase().includes(query) ||
      t.description.toLowerCase().includes(query) ||
      (t.orderNumber && t.orderNumber.toLowerCase().includes(query))
    )
  }

  totalItems.value = filtered.length
  return filtered
})

// 统计数据
const totalIncome = computed(() => {
  return transactions.value
    .filter(t => t.type === 'RECHARGE' && t.status === 'COMPLETED')
    .reduce((sum, t) => sum + t.amount, 0)
})

const totalExpense = computed(() => {
  return transactions.value
    .filter(t => t.type === 'ORDER_PAYMENT' && t.status === 'COMPLETED')
    .reduce((sum, t) => sum + t.amount, 0)
})

const pendingCount = computed(() => {
  return transactions.value.filter(t => t.status === 'PENDING').length
})

// 防抖搜索
const debouncedSearch = debounce(() => {
  currentPage.value = 1
}, 500)

// 加载交易记录
const loadTransactions = async () => {
  loading.value = true
  try {
    const response = await transactionApi.getUserTransactions()
    if (response.success) {
      transactions.value = response.data || []
    } else {
      showNotification(response.message || '加载交易记录失败', 'error')
    }
  } catch (error: any) {
    console.error('加载交易记录失败:', error)
    // 如果是网络错误或服务器未启动，显示更友好的错误信息
    if (error.message.includes('Server returned non-JSON response')) {
      showNotification('后端服务未启动或API路径错误，请检查服务器状态', 'error')
    } else {
      showNotification(error.message || '加载交易记录失败', 'error')
    }
  } finally {
    loading.value = false
  }
}

// 重置筛选条件
const resetFilters = () => {
  searchQuery.value = ''
  typeFilter.value = ''
  statusFilter.value = ''
  currentPage.value = 1
}

// 查看交易详情
const viewTransaction = (transaction: TransactionDTO) => {
  selectedTransaction.value = transaction
  detailDialog.value = true
}

// 查看关联订单
const viewOrder = (orderId?: number) => {
  if (orderId) {
    router.push(`/orders?orderId=${orderId}`)
  }
}

// 获取类型颜色
const getTypeColor = (type: string) => {
  const colors: Record<string, string> = {
    RECHARGE: 'success',
    ORDER_PAYMENT: 'error',
    REFUND: 'info',
    WITHDRAWAL: 'warning',
    ADJUSTMENT: 'purple'
  }
  return colors[type] || 'default'
}

// 获取类型图标
const getTypeIcon = (type: string) => {
  const icons: Record<string, string> = {
    RECHARGE: 'mdi-plus-circle',
    ORDER_PAYMENT: 'mdi-minus-circle',
    REFUND: 'mdi-undo',
    WITHDRAWAL: 'mdi-bank-transfer-out',
    ADJUSTMENT: 'mdi-tune'
  }
  return icons[type] || 'mdi-help-circle'
}

// 获取类型文本
const getTypeText = (type: string) => {
  const typeMap: Record<string, string> = {
    RECHARGE: TEXTS.transaction.typeRecharge,
    ORDER_PAYMENT: TEXTS.transaction.typeOrderPayment,
    REFUND: TEXTS.transaction.typeRefund,
    WITHDRAWAL: TEXTS.transaction.typeWithdrawal,
    ADJUSTMENT: TEXTS.transaction.typeAdjustment
  }
  return typeMap[type] || type
}

// 获取状态颜色
const getStatusColor = (status: string) => {
  const colors: Record<string, string> = {
    PENDING: 'warning',
    PROCESSING: 'primary',
    COMPLETED: 'success',
    FAILED: 'error',
    CANCELLED: 'error'
  }
  return colors[status] || 'default'
}

// 获取状态文本
const getStatusText = (status: string) => {
  const statusMap: Record<string, string> = {
    PENDING: TEXTS.transaction.statusPending,
    PROCESSING: TEXTS.transaction.statusProcessing,
    COMPLETED: TEXTS.transaction.statusCompleted,
    FAILED: TEXTS.transaction.statusFailed,
    CANCELLED: TEXTS.transaction.statusCancelled
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
  loadTransactions()
})
</script>

<style scoped>
.font-mono {
  font-family: 'Roboto Mono', monospace;
}
</style>
