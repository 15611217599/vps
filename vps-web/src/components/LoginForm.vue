<template>
  <v-container fluid class="fill-height login-bg pa-4">
    <v-row justify="center" align="center" class="fill-height ma-0">
      <v-col cols="12" sm="8" md="6" lg="4" class="pa-2">
        <v-card elevation="8" class="pa-6 login-card">
          <v-card-title class="text-center mb-4">
            <v-avatar size="64" class="login-avatar mb-3" :color="themeStore.currentColors.primary">
              <v-icon icon="mdi-account-circle" size="48" color="white"></v-icon>
            </v-avatar>
            <h2 class="text-h4 font-weight-bold">{{ $t('auth.login.title') }}</h2>
            <p class="text-body-1 text-medium-emphasis mt-1">{{ $t('auth.login.subtitle') }}</p>
          </v-card-title>
          
          <v-card-text>
            <v-form @submit.prevent="handleLogin" ref="loginForm">
              <v-text-field
                v-model="form.username"
                :label="$t('auth.login.username')"
                prepend-inner-icon="mdi-account"
                variant="outlined"
                required
                :rules="[rules.required]"
                class="mb-4"
              ></v-text-field>
              
              <v-text-field
                v-model="form.password"
                :label="$t('auth.login.password')"
                :type="showPassword ? 'text' : 'password'"
                prepend-inner-icon="mdi-lock"
                variant="outlined"
                required
                :rules="[rules.required]"
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
              

              
              <v-btn
                type="submit"
                size="large"
                block
                :loading="authStore.loading"
                class="mb-4"
                :color="themeStore.currentColors.primary"
              >
                {{ authStore.loading ? $t('auth.login.loginButtonLoading') : $t('auth.login.loginButton') }}
              </v-btn>
              
              <!-- 忘记密码链接 -->
              <div class="text-center mb-4">
                <v-btn
                  variant="text"
                  size="small"
                  :color="themeStore.currentColors.primary"
                  @click="$router.push('/forgot-password')"
                >
                  {{ $t('auth.login.forgotPassword') }}
                </v-btn>
              </div>
              
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
              <span class="text-body-1 text-medium-emphasis">{{ $t('auth.login.noAccount') }}</span>
              <v-btn
                variant="text"
                :color="themeStore.currentColors.primary"
                class="ml-1"
                size="small"
                @click="$emit('switch-mode')"
              >
                {{ $t('auth.login.registerLink') }}
              </v-btn>
            </div>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useAuthStore } from '../stores/auth'
import { useThemeStore } from '@/stores/theme'
import { useRouter } from 'vue-router'

const emit = defineEmits(['switch-mode'])
const { t } = useI18n()
const authStore = useAuthStore()
const themeStore = useThemeStore()
const router = useRouter()

const form = reactive({
  username: '',
  password: ''
})

const showPassword = ref(false)
const loginForm = ref()

const rules = {
  required: (value: string) => !!value || t('validation.required')
}

const handleLogin = async () => {
  // 先验证表单
  const { valid } = await loginForm.value.validate()
  
  if (!valid) {
    authStore.error = t('auth.errors.pleaseFixErrors')
    return
  }
  
  const success = await authStore.login({
    ...form
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

.login-bg {
  background: transparent;
  transition: background 0.3s ease;
  /* 让背景覆盖整个容器 */
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-card {
  border-radius: 12px;
  backdrop-filter: blur(10px);
  background: transparent;
  transition: background 0.3s ease;
}

/* 深色主题下的卡片样式 */
:deep(.v-theme--dark) .login-card {
  background: transparent !important;
}

.login-avatar {
  transition: background 0.3s ease;
}

/* 登录表单特定样式 */
:deep(.v-card) {
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

:deep(.v-theme--dark .v-card) {
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
}
</style>