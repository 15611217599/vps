<template>
  <HomeLayout>
    <UnifiedPageHeader 
      title="账户充值" 
      subtitle="为您的账户充值余额，支持多种支付方式"
    />

    <v-container>
      <v-row>
        <!-- 充值表单 -->
        <v-col cols="12" md="8">
          <v-card>
            <v-card-title>
              <v-icon class="me-2">mdi-credit-card</v-icon>
              充值信息
            </v-card-title>
            <v-card-text>
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
                />

                <!-- 快捷金额 -->
                <div class="mb-4">
                  <v-chip-group v-model="selectedAmount" @update:model-value="selectAmount">
                    <v-chip
                      v-for="amount in quickAmounts"
                      :key="amount"
                      :value="amount"
                      variant="outlined"
                      filter
                    >
                      ¥{{ amount }}
                    </v-chip>
                  </v-chip-group>
                </div>

                <!-- 支付方式 -->
                <div class="mb-4">
                  <v-label class="text-subtitle-2 mb-2">选择支付方式</v-label>
                  <v-radio-group v-model="rechargeForm.type" :rules="typeRules">
                    <v-row>
                      <v-col
                        v-for="method in paymentMethods"
                        :key="method.code"
                        cols="6"
                        sm="4"
                        md="3"
                      >
                        <v-card
                          :class="{ 'border-primary': rechargeForm.type === method.code }"
                          variant="outlined"
                          @click="rechargeForm.type = method.code"
                          style="cursor: pointer"
                        >
                          <v-card-text class="text-center pa-3">
                            <v-icon :icon="method.icon" size="32" class="mb-2" />
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
                />

                <!-- 提交按钮 -->
                <v-btn
                  :loading="loading"
                  :disabled="!valid"
                  color="primary"
                  size="large"
                  block
                  @click="submitRecharge"
                >
                  <v-icon class="me-2">mdi-credit-card-plus</v-icon>
                  立即充值
                </v-btn>
              </v-form>
            </v-card-text>
          </v-card>
        </v-col>

        <!-- 侧边栏信息 -->
        <v-col cols="12" md="4">
          <!-- 当前余额 -->
          <WalletCard class="mb-4" />

          <!-- 充值说明 -->
          <v-card>
            <v-card-title>
              <v-icon class="me-2">mdi-information</v-icon>
              充值说明
            </v-card-title>
            <v-card-text>
              <v-list density="compact">
                <v-list-item>
                  <v-list-item-title>
                    <v-icon class="me-2" size="small">mdi-check-circle</v-icon>
                    支持多种支付方式
                  </v-list-item-title>
                </v-list-item>
                <v-list-item>
                  <v-list-item-title>
                    <v-icon class="me-2" size="small">mdi-check-circle</v-icon>
                    充值金额实时到账
                  </v-list-item-title>
                </v-list-item>
                <v-list-item>
                  <v-list-item-title>
                    <v-icon class="me-2" size="small">mdi-check-circle</v-icon>
                    最低充值金额 ¥1
                  </v-list-item-title>
                </v-list-item>
                <v-list-item>
                  <v-list-item-title>
                    <v-icon class="me-2" size="small">mdi-check-circle</v-icon>
                    单次最高充值 ¥10,000
                  </v-list-item-title>
                </v-list-item>
              </v-list>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>
    </v-container>

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
  </HomeLayout>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import HomeLayout from '@/components/HomeLayout.vue'
import UnifiedPageHeader from '@/components/UnifiedPageHeader.vue'
import WalletCard from '@/components/WalletCard.vue'
import { paymentApi, type CreatePaymentRequest, type PaymentResponse } from '@/api/payment'
import { useNotification } from '@/composables/useNotification'
import QRCode from 'qrcode'

const router = useRouter()
const { showSuccess, showError } = useNotification()

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
      // 可以刷新钱包余额或跳转到其他页面
      router.push('/profile')
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

onMounted(() => {
  // 页面加载时可以检查是否有未完成的支付订单
})
</script>

<style scoped>
.border-primary {
  border-color: rgb(var(--v-theme-primary)) !important;
  border-width: 2px !important;
}
</style>