<template>
  <GuestLayout>
      <v-container fluid class="fill-height forgot-password-bg pa-4">
        <v-row justify="center" align="center" class="fill-height ma-0">
          <v-col cols="12" sm="8" md="6" lg="4" class="pa-2">
            <v-card elevation="8" class="pa-6 forgot-password-card">
              <v-card-title class="text-center mb-4">
                <v-avatar size="64" class="forgot-password-avatar mb-3" :color="themeStore.currentColors.primary">
                  <v-icon icon="mdi-lock-reset" size="48" color="white"></v-icon>
                </v-avatar>
                <h2 class="text-h4 font-weight-bold">{{ $t('forgotPassword.title') }}</h2>
                <p class="text-body-1 text-medium-emphasis mt-1">{{ $t('forgotPassword.subtitle') }}</p>
              </v-card-title>
              
              <v-card-text>
                <!-- 步骤1: 输入邮箱 -->
                <v-form v-if="step === 1" @submit.prevent="handleSendCode" ref="emailForm">
                  <v-text-field
                    v-model="form.email"
                    :label="$t('forgotPassword.email')"
                    type="email"
                    prepend-inner-icon="mdi-email"
                    variant="outlined"
                    required
                    :rules="[rules.required, rules.email]"
                    class="mb-4"
                  ></v-text-field>
                  
                  <v-btn
                    type="submit"
                    size="large"
                    block
                    :loading="loading"
                    class="mb-4"
                    :color="themeStore.currentColors.primary"
                  >
                    {{ loading ? $t('forgotPassword.sendingCode') : $t('forgotPassword.sendCode') }}
                  </v-btn>
                </v-form>
                
                <!-- 步骤2: 输入验证码和新密码 -->
                <v-form v-if="step === 2" @submit.prevent="handleResetPassword" ref="resetForm">
                  <v-alert
                    type="info"
                    variant="tonal"
                    class="mb-4"
                  >
                    {{ $t('forgotPassword.codeInfo', { email: form.email }) }}
                  </v-alert>
                  
                  <v-text-field
                    v-model="form.verificationCode"
                    :label="$t('forgotPassword.verificationCode')"
                    prepend-inner-icon="mdi-shield-key"
                    variant="outlined"
                    required
                    :rules="[rules.required, rules.verificationCode]"
                    class="mb-4"
                    maxlength="6"
                  ></v-text-field>
                  
                  <v-text-field
                    v-model="form.newPassword"
                    :label="$t('forgotPassword.newPassword')"
                    :type="showNewPassword ? 'text' : 'password'"
                    prepend-inner-icon="mdi-lock-plus"
                    variant="outlined"
                    required
                    :rules="[rules.required, rules.password]"
                    class="mb-4"
                  >
                    <template v-slot:append-inner>
                      <v-btn
                        icon
                        variant="text"
                        size="small"
                        @click="showNewPassword = !showNewPassword"
                      >
                        <v-icon>
                          {{ showNewPassword ? 'mdi-eye-off' : 'mdi-eye' }}
                        </v-icon>
                      </v-btn>
                    </template>
                  </v-text-field>
                  
                  <v-text-field
                    v-model="form.confirmPassword"
                    :label="$t('forgotPassword.confirmPassword')"
                    :type="showConfirmPassword ? 'text' : 'password'"
                    prepend-inner-icon="mdi-lock-check"
                    variant="outlined"
                    required
                    :rules="[rules.required, rules.passwordMatch]"
                    class="mb-4"
                  >
                    <template v-slot:append-inner>
                      <v-btn
                        icon
                        variant="text"
                        size="small"
                        @click="showConfirmPassword = !showConfirmPassword"
                      >
                        <v-icon>
                          {{ showConfirmPassword ? 'mdi-eye-off' : 'mdi-eye' }}
                        </v-icon>
                      </v-btn>
                    </template>
                  </v-text-field>
                  
                  <!-- 密码强度指示器 -->
                  <div v-if="form.newPassword" class="mb-4">
                    <div class="text-caption text-medium-emphasis mb-2">{{ $t('auth.register.passwordStrength') }}</div>
                    <v-progress-linear
                      :model-value="passwordStrength.score * 25"
                      :color="passwordStrength.color"
                      height="6"
                      rounded
                    ></v-progress-linear>
                    <div class="text-caption mt-1" :class="`text-${passwordStrength.color}`">
                      {{ $t(`auth.register.password${passwordStrength.text}`) }}
                    </div>
                  </div>
                  
                  <div class="d-flex gap-3">
                    <v-btn
                      variant="outlined"
                      @click="goBackToEmail"
                      prepend-icon="mdi-arrow-left"
                    >
                      {{ $t('forgotPassword.backToEmail') }}
                    </v-btn>
                    
                    <v-btn
                      type="submit"
                      :loading="loading"
                      :color="themeStore.currentColors.primary"
                      prepend-icon="mdi-check"
                      class="flex-grow-1"
                    >
                      {{ loading ? $t('forgotPassword.resetting') : $t('forgotPassword.resetPassword') }}
                    </v-btn>
                  </div>
                  
                  <!-- 重新发送验证码 -->
                  <div class="text-center mt-4">
                    <v-btn
                      variant="text"
                      size="small"
                      :disabled="resendCountdown > 0"
                      @click="handleResendCode"
                    >
                      {{ resendCountdown > 0 
                        ? $t('forgotPassword.resendCountdown', { seconds: resendCountdown })
                        : $t('forgotPassword.resendCode') 
                      }}
                    </v-btn>
                  </div>
                </v-form>
                
                <!-- 错误和成功消息 -->
                <v-alert
                  v-if="error"
                  type="error"
                  variant="tonal"
                  class="mb-4"
                  closable
                  @click:close="error = null"
                >
                  {{ error }}
                </v-alert>
                
                <v-alert
                  v-if="success"
                  type="success"
                  variant="tonal"
                  class="mb-4"
                  closable
                  @click:close="success = null"
                >
                  {{ success }}
                </v-alert>
              </v-card-text>
              
              <v-card-actions class="justify-center pt-4">
                <div class="d-flex align-center justify-center">
                  <span class="text-body-1 text-medium-emphasis">{{ $t('forgotPassword.rememberPassword') }}</span>
                  <v-btn
                    variant="text"
                    :color="themeStore.currentColors.primary"
                    class="ml-1"
                    size="small"
                    @click="$router.push('/auth')"
                  >
                    {{ $t('forgotPassword.backToLogin') }}
                  </v-btn>
                </div>
              </v-card-actions>
            </v-card>
          </v-col>
        </v-row>
      </v-container>
  </GuestLayout>
</template>

<script setup lang="ts">
import { reactive, ref, computed, onMounted, onUnmounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useThemeStore } from '@/stores/theme'
import { useRouter } from 'vue-router'
import { authApi } from '@/api/auth'
import GuestLayout from '@/components/GuestLayout.vue'

const { t } = useI18n()
const themeStore = useThemeStore()
const router = useRouter()

const step = ref(1) // 1: 输入邮箱, 2: 输入验证码和新密码
const loading = ref(false)
const error = ref<string | null>(null)
const success = ref<string | null>(null)
const resendCountdown = ref(0)
let countdownTimer: NodeJS.Timeout | null = null

const emailForm = ref()
const resetForm = ref()
const showNewPassword = ref(false)
const showConfirmPassword = ref(false)

const form = reactive({
  email: '',
  verificationCode: '',
  newPassword: '',
  confirmPassword: ''
})

// 验证规则
const rules = {
  required: (value: string) => !!value || t('validation.required'),
  email: (value: string) => {
    const pattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    return pattern.test(value) || t('validation.email')
  },
  verificationCode: (value: string) => {
    if (!value) return t('validation.required')
    if (!/^\d{6}$/.test(value)) return t('forgotPassword.codeFormat')
    return true
  },
  password: (value: string) => {
    if (!value) return t('validation.required')
    if (value.length < 6) return t('validation.minLength', { min: 6 })
    return true
  },
  passwordMatch: (value: string) => {
    return value === form.newPassword || t('auth.errors.passwordMismatch')
  }
}

// 密码强度计算
const passwordStrength = computed(() => {
  const password = form.newPassword
  if (!password) return { score: 0, text: 'Weak', color: 'grey' }
  
  let score = 0
  let text = 'Weak'
  let color = 'error'
  
  if (password.length >= 6) score++
  if (password.length >= 10) score++
  if (/[a-z]/.test(password)) score++
  if (/[A-Z]/.test(password)) score++
  if (/[0-9]/.test(password)) score++
  if (/[^A-Za-z0-9]/.test(password)) score++
  
  if (score <= 2) {
    text = 'Weak'
    color = 'error'
  } else if (score <= 4) {
    text = 'Medium'
    color = 'warning'
  } else {
    text = 'Strong'
    color = 'success'
  }
  
  return { score: Math.min(score, 4), text, color }
})

// 发送验证码
const handleSendCode = async () => {
  const { valid } = await emailForm.value.validate()
  
  if (!valid) {
    error.value = t('auth.errors.pleaseFixErrors')
    return
  }
  
  loading.value = true
  error.value = null
  success.value = null
  
  try {
    await authApi.sendResetCode(form.email.trim())
    success.value = t('forgotPassword.codeSent')
    step.value = 2
    startResendCountdown()
  } catch (err: any) {
    error.value = err.response?.data?.error || err.message || t('forgotPassword.sendCodeError')
  } finally {
    loading.value = false
  }
}

// 重置密码
const handleResetPassword = async () => {
  const { valid } = await resetForm.value.validate()
  
  if (!valid) {
    error.value = t('auth.errors.pleaseFixErrors')
    return
  }
  
  loading.value = true
  error.value = null
  success.value = null
  
  try {
    await authApi.resetPassword({
      email: form.email.trim(),
      verificationCode: form.verificationCode.trim(),
      newPassword: form.newPassword
    })
    
    success.value = t('forgotPassword.resetSuccess')
    
    // 3秒后跳转到登录页面
    setTimeout(() => {
      router.push('/auth')
    }, 3000)
  } catch (err: any) {
    error.value = err.response?.data?.error || err.message || t('forgotPassword.resetError')
  } finally {
    loading.value = false
  }
}

// 重新发送验证码
const handleResendCode = async () => {
  if (resendCountdown.value > 0) return
  
  loading.value = true
  error.value = null
  
  try {
    await authApi.sendResetCode(form.email.trim())
    success.value = t('forgotPassword.codeResent')
    startResendCountdown()
  } catch (err: any) {
    error.value = err.response?.data?.error || err.message || t('forgotPassword.sendCodeError')
  } finally {
    loading.value = false
  }
}

// 返回邮箱输入步骤
const goBackToEmail = () => {
  step.value = 1
  form.verificationCode = ''
  form.newPassword = ''
  form.confirmPassword = ''
  error.value = null
  success.value = null
  stopResendCountdown()
}

// 开始重发倒计时
const startResendCountdown = () => {
  resendCountdown.value = 60
  countdownTimer = setInterval(() => {
    resendCountdown.value--
    if (resendCountdown.value <= 0) {
      stopResendCountdown()
    }
  }, 1000)
}

// 停止重发倒计时
const stopResendCountdown = () => {
  if (countdownTimer) {
    clearInterval(countdownTimer)
    countdownTimer = null
  }
  resendCountdown.value = 0
}

onMounted(() => {
  error.value = null
  success.value = null
})

onUnmounted(() => {
  stopResendCountdown()
})
</script>

<style scoped>
.fill-height {
  /* 完全移除固定高度，让内容自然撑开 */
  padding: 40px 0; /* 只保留上下间距 */
}

.forgot-password-bg {
  background: transparent;
  transition: background 0.3s ease;
  /* 让背景覆盖整个容器 */
  display: flex;
  align-items: center;
  justify-content: center;
}

.forgot-password-card {
  border-radius: 12px;
  backdrop-filter: blur(10px);
  background: transparent;
  transition: background 0.3s ease;
}

/* 深色主题下的卡片样式 */
:deep(.v-theme--dark) .forgot-password-card {
  background: transparent !important;
}

.forgot-password-avatar {
  transition: background 0.3s ease;
}

.gap-3 {
  gap: 12px;
}

/* 卡片阴影 */
:deep(.v-card) {
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

:deep(.v-theme--dark .v-card) {
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
}
</style>