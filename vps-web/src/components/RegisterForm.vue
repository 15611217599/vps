<template>
  <v-container fluid class="fill-height register-bg pa-4">
    <v-row justify="center" align="center" class="fill-height ma-0">
      <v-col cols="12" sm="8" md="6" lg="4" class="pa-2">
        <v-card elevation="8" class="pa-6 register-card">
          <v-card-title class="text-center mb-4">
            <v-avatar size="64" class="register-avatar mb-3" :color="themeStore.currentColors.primary">
              <v-icon icon="mdi-account-plus" size="48" color="white"></v-icon>
            </v-avatar>
            <h2 class="text-h4 font-weight-bold">加入我们</h2>
            <p class="text-body-1 text-medium-emphasis mt-1">创建您的新账户</p>
          </v-card-title>
          
          <v-card-text>
            <v-form @submit.prevent="handleRegister" ref="registerForm">
              <v-text-field
                v-model="form.username"
                label="用户名"
                prepend-inner-icon="mdi-account"
                variant="outlined"
                required
                :rules="[rules.required]"
                class="mb-4"
              ></v-text-field>
              
              <v-text-field
                v-model="form.email"
                label="邮箱"
                type="email"
                prepend-inner-icon="mdi-email"
                variant="outlined"
                required
                :rules="[rules.required, rules.email]"
                class="mb-4"
              ></v-text-field>
              
              <v-text-field
                v-model="form.password"
                label="密码"
                :type="showPassword ? 'text' : 'password'"
                prepend-inner-icon="mdi-lock"
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
                    @click="showPassword = !showPassword"
                  >
                    <v-icon>
                      {{ showPassword ? 'mdi-eye-off' : 'mdi-eye' }}
                    </v-icon>
                  </v-btn>
                </template>
              </v-text-field>
              
              <v-text-field
                v-model="form.confirmPassword"
                label="确认密码"
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
              
              <div v-if="form.password" class="mb-4">
                <div class="text-caption text-medium-emphasis mb-2">密码强度</div>
                <v-progress-linear
                  :model-value="passwordStrength.score * 25"
                  :color="passwordStrength.color"
                  height="6"
                  rounded
                ></v-progress-linear>
                <div class="text-caption mt-1" :class="`text-${passwordStrength.color}`">
                  {{ passwordStrength.text }}
                </div>
              </div>
              
              <v-btn
                type="submit"
                size="large"
                block
                :loading="authStore.loading"
                :disabled="!!passwordError"
                class="mb-4"
                :color="themeStore.currentColors.primary"
              >
                {{ authStore.loading ? '注册中...' : '立即注册' }}
              </v-btn>
              
              <v-alert
                v-if="authStore.error"
                type="error"
                variant="tonal"
                class="mb-4"
                closable
                @click:close="authStore.error = null"
              >
                {{ authStore.error }}
              </v-alert>
            </v-form>
          </v-card-text>
          
          <v-card-actions class="justify-center pt-4">
            <div class="d-flex align-center justify-center">
              <span class="text-body-1 text-medium-emphasis">已有账号？</span>
              <v-btn
                variant="text"
                :color="themeStore.currentColors.primary"
                class="ml-1"
                size="small"
                @click="$emit('switch-mode')"
              >
                立即登录
              </v-btn>
            </div>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { reactive, computed, ref, onMounted } from 'vue'
import { useAuthStore } from '../stores/auth'
import { useThemeStore } from '@/stores/theme'
import { useRouter } from 'vue-router'

const emit = defineEmits(['switch-mode'])
const authStore = useAuthStore()
const themeStore = useThemeStore()
const router = useRouter()

const form = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: ''
})

const showPassword = ref(false)
const showConfirmPassword = ref(false)
const registerForm = ref()

const passwordError = computed(() => {
  if (form.password && form.confirmPassword && form.password !== form.confirmPassword) {
    return '两次输入的密码不一致'
  }
  return null
})

const passwordStrength = computed(() => {
  const password = form.password
  if (!password) return { score: 0, text: '弱', color: 'grey' }
  
  let score = 0
  let text = '弱'
  let color = 'error'
  
  if (password.length >= 8) score++
  if (password.length >= 12) score++
  if (/[a-z]/.test(password)) score++
  if (/[A-Z]/.test(password)) score++
  if (/[0-9]/.test(password)) score++
  if (/[^A-Za-z0-9]/.test(password)) score++
  
  if (score <= 2) {
    text = '弱'
    color = 'error'
  } else if (score <= 4) {
    text = '中等'
    color = 'warning'
  } else {
    text = '强'
    color = 'success'
  }
  
  return { score: Math.min(score, 4), text, color }
})

const rules = {
  required: (value: string) => !!value || '此字段为必填项',
  email: (value: string) => {
    const pattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    return pattern.test(value) || '请输入有效的邮箱地址'
  },
  password: (value: string) => {
    if (!value) return '此字段为必填项'
    if (value.length < 6) return '最少需要 6 个字符'
    return true
  },
  passwordMatch: (value: string) => {
    return value === form.password || '两次输入的密码不一致'
  }
}

const handleRegister = async () => {
  // 先验证表单
  const { valid } = await registerForm.value.validate()
  
  if (!valid) {
    authStore.error = '请修正表单中的错误后再提交'
    return
  }
  
  // 额外检查密码匹配
  if (passwordError.value) {
    authStore.error = passwordError.value
    return
  }
  
  const success = await authStore.register({
    username: form.username.trim(),
    email: form.email.trim(),
    password: form.password
  })
  
  if (success) {
    router.push('/dashboard')
  }
}

onMounted(() => {
  authStore.error = null
})
</script>

<style scoped>
.fill-height {
  /* 完全移除固定高度，让内容自然撑开 */
  padding: 40px 0; /* 只保留上下间距 */
}

.register-bg {
  background: transparent;
  transition: background 0.3s ease;
  /* 让背景覆盖整个容器 */
  display: flex;
  align-items: center;
  justify-content: center;
}

.register-card {
  border-radius: 12px;
  backdrop-filter: blur(10px);
  background: transparent;
  transition: background 0.3s ease;
}

/* 深色主题下的卡片样式 */
:deep(.v-theme--dark) .register-card {
  background: transparent !important;
}

.register-avatar {
  transition: background 0.3s ease;
}

/* 注册表单特定样式 */
:deep(.v-card) {
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

:deep(.v-theme--dark .v-card) {
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
}
</style>