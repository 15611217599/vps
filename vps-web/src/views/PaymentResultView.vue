<template>
  <HomeLayout>
    <v-container class="d-flex justify-center align-center" style="min-height: 60vh;">
      <v-card max-width="500" class="text-center">
        <v-card-text class="pa-8">
          <!-- 支付成功 -->
          <div v-if="isSuccess">
            <v-icon size="80" color="success" class="mb-4">mdi-check-circle</v-icon>
            <h2 class="text-h4 mb-4 text-success">支付成功</h2>
            <p class="text-body-1 mb-4">恭喜您，充值已完成！</p>
            
            <div v-if="orderInfo" class="text-left mb-4">
              <v-divider class="mb-4" />
              <div class="mb-2"><strong>订单号：</strong>{{ orderInfo.outTradeNo }}</div>
              <div class="mb-2"><strong>支付金额：</strong>¥{{ orderInfo.money }}</div>
              <div class="mb-2"><strong>支付方式：</strong>{{ getPaymentMethodName(orderInfo.type) }}</div>
              <div class="mb-2"><strong>完成时间：</strong>{{ formatDate(orderInfo.completedAt) }}</div>
            </div>
          </div>
          
          <!-- 支付失败 -->
          <div v-else>
            <v-icon size="80" color="error" class="mb-4">mdi-close-circle</v-icon>
            <h2 class="text-h4 mb-4 text-error">支付失败</h2>
            <p class="text-body-1 mb-4">很抱歉，支付未能完成，请重试。</p>
            
            <div v-if="orderInfo" class="text-left mb-4">
              <v-divider class="mb-4" />
              <div class="mb-2"><strong>订单号：</strong>{{ orderInfo.outTradeNo }}</div>
              <div class="mb-2"><strong>支付金额：</strong>¥{{ orderInfo.money }}</div>
              <div class="mb-2"><strong>支付方式：</strong>{{ getPaymentMethodName(orderInfo.type) }}</div>
            </div>
          </div>
          
          <div class="d-flex gap-3 justify-center">
            <v-btn
              v-if="!isSuccess"
              color="primary"
              @click="goToRecharge"
            >
              重新充值
            </v-btn>
            <v-btn
              color="primary"
              variant="outlined"
              @click="goToProfile"
            >
              查看账户
            </v-btn>
            <v-btn
              variant="text"
              @click="goToHome"
            >
              返回首页
            </v-btn>
          </div>
        </v-card-text>
      </v-card>
    </v-container>
  </HomeLayout>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import HomeLayout from '@/components/HomeLayout.vue'
import { paymentApi, type PaymentOrder } from '@/api/payment'

const route = useRoute()
const router = useRouter()

const orderInfo = ref<PaymentOrder | null>(null)
const loading = ref(false)

const isSuccess = computed(() => {
  return route.name === 'PaymentSuccess'
})

const paymentMethods = paymentApi.getPaymentMethods()

const getPaymentMethodName = (code: string) => {
  const method = paymentMethods.find(m => m.code === code)
  return method?.name || code
}

const formatDate = (dateString?: string) => {
  if (!dateString) return ''
  return new Date(dateString).toLocaleString()
}

const loadOrderInfo = async () => {
  const orderNo = route.query.order as string
  if (!orderNo) return

  try {
    loading.value = true
    const response = await paymentApi.getPaymentOrder(orderNo)
    orderInfo.value = response.data
  } catch (error) {
    console.error('Failed to load order info:', error)
  } finally {
    loading.value = false
  }
}

const goToRecharge = () => {
  router.push('/recharge')
}

const goToProfile = () => {
  router.push('/profile')
}

const goToHome = () => {
  router.push('/')
}

onMounted(() => {
  loadOrderInfo()
})
</script>