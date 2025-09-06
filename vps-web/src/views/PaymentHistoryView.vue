<template>
  <HomeLayout>
    <UnifiedPageHeader 
      title="支付记录" 
      subtitle="查看您的充值和支付历史记录"
    />

    <v-container>
      <v-card>
        <v-card-title>
          <v-icon class="me-2">mdi-history</v-icon>
          支付历史
        </v-card-title>
        
        <v-card-text>
          <UnifiedDataTable
            :items="paymentOrders"
            :headers="headers"
            :loading="loading"
            :total-items="totalItems"
            :items-per-page="itemsPerPage"
            @update:page="handlePageChange"
            @update:items-per-page="handleItemsPerPageChange"
          >
            <template #item.money="{ item }">
              <span class="font-weight-bold text-success">¥{{ item.money }}</span>
            </template>
            
            <template #item.type="{ item }">
              <v-chip :color="getPaymentMethodColor(item.type)" size="small">
                {{ getPaymentMethodName(item.type) }}
              </v-chip>
            </template>
            
            <template #item.status="{ item }">
              <StatusChip :status="item.status" :status-map="statusMap" />
            </template>
            
            <template #item.createdAt="{ item }">
              {{ formatDate(item.createdAt) }}
            </template>
            
            <template #item.actions="{ item }">
              <v-btn
                v-if="item.status === 'PENDING'"
                size="small"
                color="primary"
                variant="outlined"
                @click="checkPaymentStatus(item.outTradeNo)"
              >
                检查状态
              </v-btn>
              <v-btn
                size="small"
                color="info"
                variant="text"
                @click="viewDetails(item)"
              >
                详情
              </v-btn>
            </template>
          </UnifiedDataTable>
        </v-card-text>
      </v-card>
    </v-container>

    <!-- 详情弹窗 -->
    <v-dialog v-model="detailDialog" max-width="600">
      <v-card v-if="selectedOrder">
        <v-card-title>
          <v-icon class="me-2">mdi-receipt</v-icon>
          支付详情
        </v-card-title>
        <v-card-text>
          <v-row>
            <v-col cols="6">
              <div class="text-caption text-medium-emphasis">订单号</div>
              <div class="text-body-1">{{ selectedOrder.outTradeNo }}</div>
            </v-col>
            <v-col cols="6">
              <div class="text-caption text-medium-emphasis">支付金额</div>
              <div class="text-h6 text-success">¥{{ selectedOrder.money }}</div>
            </v-col>
            <v-col cols="6">
              <div class="text-caption text-medium-emphasis">支付方式</div>
              <div class="text-body-1">{{ getPaymentMethodName(selectedOrder.type) }}</div>
            </v-col>
            <v-col cols="6">
              <div class="text-caption text-medium-emphasis">支付状态</div>
              <StatusChip :status="selectedOrder.status" :status-map="statusMap" />
            </v-col>
            <v-col cols="6">
              <div class="text-caption text-medium-emphasis">创建时间</div>
              <div class="text-body-1">{{ formatDate(selectedOrder.createdAt) }}</div>
            </v-col>
            <v-col cols="6" v-if="selectedOrder.completedAt">
              <div class="text-caption text-medium-emphasis">完成时间</div>
              <div class="text-body-1">{{ formatDate(selectedOrder.completedAt) }}</div>
            </v-col>
            <v-col cols="12" v-if="selectedOrder.tradeNo">
              <div class="text-caption text-medium-emphasis">第三方订单号</div>
              <div class="text-body-1">{{ selectedOrder.tradeNo }}</div>
            </v-col>
            <v-col cols="12">
              <div class="text-caption text-medium-emphasis">商品名称</div>
              <div class="text-body-1">{{ selectedOrder.name }}</div>
            </v-col>
          </v-row>
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn variant="text" @click="detailDialog = false">关闭</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </HomeLayout>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import HomeLayout from '@/components/HomeLayout.vue'
import UnifiedPageHeader from '@/components/UnifiedPageHeader.vue'
import UnifiedDataTable from '@/components/UnifiedDataTable.vue'
import StatusChip from '@/components/StatusChip.vue'
import { paymentApi, type PaymentOrder } from '@/api/payment'
import { useNotification } from '@/composables/useNotification'

const { showSuccess, showError } = useNotification()

const paymentOrders = ref<PaymentOrder[]>([])
const loading = ref(false)
const totalItems = ref(0)
const itemsPerPage = ref(10)
const currentPage = ref(1)
const detailDialog = ref(false)
const selectedOrder = ref<PaymentOrder | null>(null)

const headers = [
  { title: '订单号', key: 'outTradeNo', sortable: false },
  { title: '金额', key: 'money', sortable: false },
  { title: '支付方式', key: 'type', sortable: false },
  { title: '状态', key: 'status', sortable: false },
  { title: '创建时间', key: 'createdAt', sortable: false },
  { title: '操作', key: 'actions', sortable: false }
]

const statusMap = {
  PENDING: { color: 'warning', text: '待支付' },
  SUCCESS: { color: 'success', text: '支付成功' },
  FAILED: { color: 'error', text: '支付失败' },
  CANCELLED: { color: 'grey', text: '已取消' }
}

const paymentMethods = paymentApi.getPaymentMethods()

const getPaymentMethodName = (code: string) => {
  const method = paymentMethods.find(m => m.code === code)
  return method?.name || code
}

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

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleString()
}

const loadPaymentOrders = async () => {
  try {
    loading.value = true
    const response = await paymentApi.getUserPaymentOrders(currentPage.value - 1, itemsPerPage.value)
    paymentOrders.value = response.data.content
    totalItems.value = response.data.totalElements
  } catch (error: any) {
    showError('加载支付记录失败')
  } finally {
    loading.value = false
  }
}

const handlePageChange = (page: number) => {
  currentPage.value = page
  loadPaymentOrders()
}

const handleItemsPerPageChange = (items: number) => {
  itemsPerPage.value = items
  currentPage.value = 1
  loadPaymentOrders()
}

const checkPaymentStatus = async (outTradeNo: string) => {
  try {
    const response = await paymentApi.getPaymentOrder(outTradeNo)
    const order = paymentOrders.value.find(o => o.outTradeNo === outTradeNo)
    if (order) {
      order.status = response.data.status
      if (response.data.status === 'SUCCESS') {
        showSuccess('支付成功！')
      } else if (response.data.status === 'FAILED') {
        showError('支付失败')
      } else {
        showError('支付尚未完成')
      }
    }
  } catch (error: any) {
    showError('查询支付状态失败')
  }
}

const viewDetails = (order: PaymentOrder) => {
  selectedOrder.value = order
  detailDialog.value = true
}

onMounted(() => {
  loadPaymentOrders()
})
</script>