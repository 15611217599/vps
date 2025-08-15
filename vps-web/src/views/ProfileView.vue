<template>
  <PageLayout :title="$t('nav.profile')">
    <v-container class="py-8">
      <v-row justify="center">
        <v-col cols="12" md="8" lg="6">
          <v-card elevation="2">
            <v-card-title class="d-flex align-center">
              <v-icon :color="themeStore.currentColors.primary" class="me-2">mdi-account-edit</v-icon>
              {{ $t('profile.title') }}
            </v-card-title>
            
            <v-card-text>
              <v-form @submit.prevent="handleUpdateProfile" ref="profileForm">
                <!-- 用户头像 -->
                <div class="text-center mb-6">
                  <v-avatar size="120" :color="themeStore.currentColors.primary" class="mb-4">
                    <span class="text-h2">{{ form.username?.charAt(0).toUpperCase() }}</span>
                  </v-avatar>
                  <div class="text-h6">{{ form.username }}</div>
                  <div class="text-body-2 text-medium-emphasis">{{ $t('profile.memberSince') }}: {{ formatDate(authStore.user?.createdAt) }}</div>
                </div>
                
                <!-- 用户名 -->
                <v-text-field
                  v-model="form.username"
                  :label="$t('profile.username')"
                  prepend-inner-icon="mdi-account"
                  variant="outlined"
                  required
                  :rules="[rules.required, rules.username]"
                  class="mb-4"
                ></v-text-field>
                
                <!-- 邮箱 -->
                <v-text-field
                  v-model="form.email"
                  :label="$t('profile.email')"
                  type="email"
                  prepend-inner-icon="mdi-email"
                  variant="outlined"
                  required
                  :rules="[rules.required, rules.email]"
                  class="mb-4"
                ></v-text-field>
                
                <!-- 密码修改区域 -->
                <v-expansion-panels class="mb-4">
                  <v-expansion-panel>
                    <v-expansion-panel-title>
                      <v-icon class="me-2">mdi-lock</v-icon>
                      {{ $t('profile.changePassword') }}
                    </v-expansion-panel-title>
                    <v-expansion-panel-text>
                      <v-text-field
                        v-model="form.currentPassword"
                        :label="$t('profile.currentPassword')"
                        :type="showCurrentPassword ? 'text' : 'password'"
                        prepend-inner-icon="mdi-lock"
                        variant="outlined"
                        :rules="passwordChangeRules.current"
                        class="mb-4"
                      >
                        <template v-slot:append-inner>
                          <v-btn
                            icon
                            variant="text"
                            size="small"
                            @click="showCurrentPassword = !showCurrentPassword"
                          >
                            <v-icon>
                              {{ showCurrentPassword ? 'mdi-eye-off' : 'mdi-eye' }}
                            </v-icon>
                          </v-btn>
                        </template>
                      </v-text-field>
                      
                      <v-text-field
                        v-model="form.newPassword"
                        :label="$t('profile.newPassword')"
                        :type="showNewPassword ? 'text' : 'password'"
                        prepend-inner-icon="mdi-lock-plus"
                        variant="outlined"
                        :rules="passwordChangeRules.new"
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
                        v-model="form.confirmNewPassword"
                        :label="$t('profile.confirmNewPassword')"
                        :type="showConfirmPassword ? 'text' : 'password'"
                        prepend-inner-icon="mdi-lock-check"
                        variant="outlined"
                        :rules="passwordChangeRules.confirm"
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
                    </v-expansion-panel-text>
                  </v-expansion-panel>
                </v-expansion-panels>
                
                <!-- 操作按钮 -->
                <div class="d-flex gap-3">
                  <v-btn
                    type="submit"
                    :color="themeStore.currentColors.primary"
                    :loading="loading"
                    prepend-icon="mdi-content-save"
                  >
                    {{ $t('profile.saveChanges') }}
                  </v-btn>
                  
                  <v-btn
                    variant="outlined"
                    @click="resetForm"
                    prepend-icon="mdi-refresh"
                  >
                    {{ $t('profile.reset') }}
                  </v-btn>
                </div>
                
                <!-- 错误和成功消息 -->
                <v-alert
                  v-if="error"
                  type="error"
                  variant="tonal"
                  class="mt-4"
                  closable
                  @click:close="error = null"
                >
                  {{ error }}
                </v-alert>
                
                <v-alert
                  v-if="success"
                  type="success"
                  variant="tonal"
                  class="mt-4"
                  closable
                  @click:close="success = null"
                >
                  {{ success }}
                </v-alert>
              </v-form>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>
    </v-container>
  </PageLayout>
</template>

<script setup lang="ts">
import { reactive, ref, computed, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useAuthStore } from '../stores/auth'
import { useThemeStore } from '@/stores/theme'
import PageLayout from '@/components/PageLayout.vue'

const { t } = useI18n()
const authStore = useAuthStore()
const themeStore = useThemeStore()

const profileForm = ref()
const loading = ref(false)
const error = ref<string | null>(null)
const success = ref<string | null>(null)

const showCurrentPassword = ref(false)
const showNewPassword = ref(false)
const showConfirmPassword = ref(false)

const form = reactive({
  username: '',
  email: '',
  currentPassword: '',
  newPassword: '',
  confirmNewPassword: ''
})

// 基础验证规则
const rules = {
  required: (value: string) => !!value || t('validation.required'),
  username: (value: string) => {
    if (!value) return t('validation.required')
    if (value.length < 3) return t('validation.minLength', { min: 3 })
    if (value.length > 20) return t('validation.maxLength', { max: 20 })
    if (!/^[a-zA-Z0-9_]+$/.test(value)) return t('profile.usernameFormat')
    return true
  },
  email: (value: string) => {
    const pattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    return pattern.test(value) || t('validation.email')
  }
}

// 密码修改验证规则
const passwordChangeRules = computed(() => ({
  current: form.newPassword ? [rules.required] : [],
  new: form.currentPassword ? [
    rules.required,
    (value: string) => {
      if (value.length < 8) return t('validation.minLength', { min: 8 })
      return true
    }
  ] : [],
  confirm: form.newPassword ? [
    rules.required,
    (value: string) => value === form.newPassword || t('auth.errors.passwordMismatch')
  ] : []
}))

const formatDate = (dateString: string) => {
  if (!dateString) return ''
  return new Date(dateString).toLocaleDateString()
}

const resetForm = () => {
  form.username = authStore.user?.username || ''
  form.email = authStore.user?.email || ''
  form.currentPassword = ''
  form.newPassword = ''
  form.confirmNewPassword = ''
  error.value = null
  success.value = null
}

const handleUpdateProfile = async () => {
  // 验证表单
  const { valid } = await profileForm.value.validate()
  
  if (!valid) {
    error.value = t('auth.errors.pleaseFixErrors')
    return
  }
  
  loading.value = true
  error.value = null
  success.value = null
  
  try {
    // 准备更新数据
    const updateData: any = {
      username: form.username.trim(),
      email: form.email.trim()
    }
    
    // 如果要修改密码
    if (form.currentPassword && form.newPassword) {
      updateData.currentPassword = form.currentPassword
      updateData.newPassword = form.newPassword
    }
    
    const result = await authStore.updateProfile(updateData)
    
    if (result) {
      success.value = t('profile.updateSuccess')
      // 清空密码字段
      form.currentPassword = ''
      form.newPassword = ''
      form.confirmNewPassword = ''
    }
  } catch (err: any) {
    error.value = err.message || t('profile.updateError')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  resetForm()
})
</script>

<style scoped>
.gap-3 {
  gap: 12px;
}
</style>