<template>
  <v-card elevation="3" rounded="lg">
    <v-card-title class="d-flex align-center">
      <v-icon class="me-2" color="success">mdi-credit-card-plus</v-icon>
      账户充值
    </v-card-title>
    
    <v-card-text class="pa-6">
      <v-form ref="form" v-model="valid">
        <!-- 充值金额 -->
        <v-text-field
          v-model="rechargeForm.money"
          label="充值金额"
          type="number"
          step="0.01"
          min="1"
          max="10000"
          prefix="¥"
          :rules="moneyRules"
          required
          variant="outlined"
          class="mb-4"
          density="comfortable"
        />

        <!-- 快捷金额 -->
        <div class="mb-4">
          <v-label class="text-subtitle-2 mb-2">快捷金额</v-label>
          <v-chip-group v-model="selectedAmount" @update:model-value="selectAmount">
            <v-chip
              v-for="amount in quickAmounts"
              :key="amount"
              :value="amount"
              variant="outlined"
              filter
              size="small"
            >
              ¥{{ amount }}
            </v-chip>
          </v-chip-group>
        </div>

        <!-- 支付方式 -->
        <div class="mb-4">
          <v-label class="text-subtitle-2 mb-2">选择支付方式</v-label>
          <v-radio-group v-model="rechargeForm.type" :rules="typeRules" density="compact">
            <v-row>
              <v-col
                v-for="method in paymentMethods"
                :key="method.code"
                cols="6"
                sm="4"
              >
                <v-card
                  :class="{ 'border-primary': rechargeForm.type === method.code }"
                  variant="outlined"
                  @click="rechargeForm.type = method.code"
                  style="cursor: pointer"
                  density="compact"
                >
                  <v-card-text class="text-center pa-2">
                    <v-icon :icon="method.icon" size="24" class="mb-1" />
                    <div class="text-caption">{{ method.name }}</div>
                    <v-radio
                      :value="method.code"
                      class="d-none"
                    />
                  </v-card-text>
                </v-card>
              </v-col>
            </v-row>
          </v-radio-group>
        </div>

        <!-- 充值说明 -->
        <v-textarea
          v-model="rechargeForm.name"
          label="充值说明（可选）"
          placeholder="请输入充值说明"
          variant="outlined"
          rows="2"
          class="mb-4"
          density="comfortable"
        />

        <!-- 提交按钮 -->
        <v-btn
          :loading="loading"
          :disabled="!valid"
          color="success"
          size="large"
          block
          @click="submitRecharge"
        >
          <v-icon class="me-2">mdi-credit-card-plus</v-icon>
          立即充值
        </v-btn>
      </v-form>
    </v-card-text>

    <!-- 支付弹窗 -->
    <v-dialog v-model="paymentDialog" max-width="500" persistent>
      <v-card>
        <v-card-title class="text-center">
          <v-icon class="me-2">mdi-qrcode</v-icon>
          扫码支付
        </v-card-title>
        <v-card-text class="text-center">
          <div v-if="paymentResponse">
            <!-- 二维码支付 -->
            <div v-if="paymentResponse.qrCode" class="mb-4">
              <div ref="qrCodeContainer" class="d-flex justify-center mb-4"></div>
              <p class="text-body-2 text-medium-emphasis">
                请使用{{ getPaymentMethodName(rechargeForm.type) }}扫描二维码完成支付
              </p>
            </div>
            
            <!-- 跳转支付 -->
            <div v-else-if="paymentResponse.payUrl" class="mb-4">
              <v-btn
                :href="paymentResponse.payUrl"
                target="_blank"
                color="primary"
                size="large"
                block
              >
                <v-icon class="me-2">mdi-open-in-new</v-icon>
                前往支付
              </v-btn>
              <p class="text-body-2 text-medium-emphasis mt-2">
                点击按钮将在新窗口打开支付页面
              </p>
            </div>

            <v-divider class="my-4" />
            
            <div class="text-body-2">
              <p><strong>订单号：</strong>{{ paymentResponse.outTradeNo }}</p>
              <p><strong>支付金额：</strong>¥{{ rechargeForm.money }}</p>
              <p><strong>支付方式：</strong>{{ getPaymentMethodName(rechargeForm.type) }}</p>
            </div>
          </div>
        </v-card-text>
        <v-card-actions>
          <v-btn variant="text" @click="closePaymentDialog">取消支付</v-btn>
          <v-spacer />
          <v-btn color="primary" @click="checkPaymentStatus">检查支付状态</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-card>
</template>

<script setup lang="ts">
import { ref, reactive, nextTick } from 'vue'
import { paymentApi, type CreatePaymentRequest, type PaymentResponse } from '@/api/payment'
import { useNotification } from '@/composables/useNotification'
import QRCode from 'qrcode'

const { showSuccess, showError } = useNotification()

// 定义事件
const emit = defineEmits<{
  rechargeSuccess: []
}>()

// 表单数据
const form = ref()
const valid = ref(false)
const loading = ref(false)
const paymentDialog = ref(false)
const qrCodeContainer = ref<HTMLElement>()

const rechargeForm = reactive<CreatePaymentRequest>({
  money: 10,
  type: 'alipay',
  name: '账户充值',
  device: 'pc'
})

const paymentResponse = ref<PaymentResponse | null>(null)
const selectedAmount = ref<number | null>(null)

// 快捷金额选项
const quickAmounts = [10, 50, 100, 200, 500, 1000]

// 支付方式
const paymentMethods = paymentApi.getPaymentMethods()

// 表单验证规则
const moneyRules = [
  (v: any) => !!v || '请输入充值金额',
  (v: any) => v >= 1 || '最低充值金额为 ¥1',
  (v: any) => v <= 10000 || '单次最高充值金额为 ¥10,000'
]

const typeRules = [
  (v: any) => !!v || '请选择支付方式'
]

// 选择快捷金额
const selectAmount = (amount: number | null) => {
  if (amount) {
    rechargeForm.money = amount
  }
}

// 获取支付方式名称
const getPaymentMethodName = (code: string) => {
  const method = paymentMethods.find(m => m.code === code)
  return method?.name || code
}

// 提交充值
const submitRecharge = async () => {
  if (!form.value?.validate()) return

  loading.value = true
  try {
    const response = await paymentApi.createPayment(rechargeForm)
    
    if (response.data.code === 1) {
      paymentResponse.value = response.data
      paymentDialog.value = true
      
      // 如果有二维码，生成二维码图片
      if (response.data.qrCode) {
        await nextTick()
        await generateQRCode(response.data.qrCode)
      }
      
      showSuccess('支付订单创建成功，请完成支付')
    } else {
      showError(response.data.msg || '创建支付订单失败')
    }
  } catch (error: any) {
    showError(error.response?.data?.message || '创建支付订单失败')
  } finally {
    loading.value = false
  }
}

// 生成二维码
const generateQRCode = async (text: string) => {
  if (!qrCodeContainer.value) return
  
  try {
    const canvas = document.createElement('canvas')
    await QRCode.toCanvas(canvas, text, {
      width: 200,
      margin: 2,
      color: {
        dark: '#000000',
        light: '#FFFFFF'
      }
    })
    
    qrCodeContainer.value.innerHTML = ''
    qrCodeContainer.value.appendChild(canvas)
  } catch (error) {
    console.error('生成二维码失败:', error)
  }
}

// 检查支付状态
const checkPaymentStatus = async () => {
  if (!paymentResponse.value) return
  
  try {
    const response = await paymentApi.getPaymentOrder(paymentResponse.value.outTradeNo)
    
    if (response.data.status === 'SUCCESS') {
      showSuccess('支付成功！余额已到账')
      closePaymentDialog()
      emit('rechargeSuccess')
    } else if (response.data.status === 'FAILED') {
      showError('支付失败，请重新尝试')
      closePaymentDialog()
    } else {
      showError('支付尚未完成，请继续支付')
    }
  } catch (error: any) {
    showError('查询支付状态失败')
  }
}

// 关闭支付弹窗
const closePaymentDialog = () => {
  paymentDialog.value = false
  paymentResponse.value = null
  if (qrCodeContainer.value) {
    qrCodeContainer.value.innerHTML = ''
  }
}
</script>

<style scoped>
.border-primary {
  border-color: rgb(var(--v-theme-primary)) !important;
  border-width: 2px !important;
}
</style>