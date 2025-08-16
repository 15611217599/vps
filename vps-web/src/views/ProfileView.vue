<template>
  <PageLayout :title="$t('nav.profile')">
    <v-container class="py-6">
      <v-row justify="center">
        <v-col cols="12" md="10" lg="8" xl="6">
          <!-- 用户信息卡片 -->
          <v-card elevation="3" class="mb-6" rounded="lg">
            <v-card-text class="pa-6">
              <div class="text-center">
                <v-avatar size="100" :color="themeStore.currentColors.primary" class="mb-4 elevation-2">
                  <span class="text-h3 font-weight-bold">{{ form.username?.charAt(0).toUpperCase() }}</span>
                </v-avatar>
                <h2 class="text-h5 mb-2">{{ form.username }}</h2>
                <div class="text-body-2 text-medium-emphasis">
                  <v-icon size="small" class="me-1">mdi-calendar</v-icon>
                  {{ $t('profile.memberSince') }}: {{ formatDate(authStore.user?.createdAt) }}
                </div>
              </div>
            </v-card-text>
          </v-card>

          <!-- 个人资料编辑卡片 -->
          <v-card elevation="3" rounded="lg">
            <v-card-text class="pa-6">
              <v-form @submit.prevent="handleUpdateProfile" ref="profileForm">
                
                <!-- 基本信息区域 -->
                <div class="mb-6">
                  <h3 class="text-h6 mb-4 d-flex align-center">
                    <v-icon :color="themeStore.currentColors.primary" class="me-2">mdi-account-details</v-icon>
                    基本信息
                  </h3>
                  
                  <v-row>
                    <v-col cols="12" sm="6">
                      <v-text-field
                        v-model="form.username"
                        :label="$t('profile.username')"
                        prepend-inner-icon="mdi-account"
                        variant="outlined"
                        required
                        :rules="[rules.required, rules.username]"
                        density="comfortable"
                      ></v-text-field>
                    </v-col>
                    <v-col cols="12" sm="6">
                      <v-text-field
                        v-model="form.email"
                        :label="$t('profile.email')"
                        type="email"
                        prepend-inner-icon="mdi-email"
                        variant="outlined"
                        required
                        :rules="[rules.required, rules.email]"
                        density="comfortable"
                      ></v-text-field>
                    </v-col>
                  </v-row>
                </div>
                
                <!-- 密码修改区域 -->
                <div class="mb-6">
                  <v-expansion-panels variant="accordion" class="elevation-1">
                    <v-expansion-panel>
                      <v-expansion-panel-title class="text-subtitle-1">
                        <v-icon :color="themeStore.currentColors.primary" class="me-2">mdi-lock</v-icon>
                        {{ $t('profile.changePassword') }}
                      </v-expansion-panel-title>
                      <v-expansion-panel-text class="pt-4">
                        <v-row>
                          <v-col cols="12">
                            <v-text-field
                              v-model="form.currentPassword"
                              :label="$t('profile.currentPassword')"
                              :type="showCurrentPassword ? 'text' : 'password'"
                              prepend-inner-icon="mdi-lock"
                              variant="outlined"
                              :rules="passwordChangeRules.current"
                              density="comfortable"
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
                          </v-col>
                        </v-row>
                        
                        <v-row>
                          <v-col cols="12" sm="6">
                            <v-text-field
                              v-model="form.newPassword"
                              :label="$t('profile.newPassword')"
                              :type="showNewPassword ? 'text' : 'password'"
                              prepend-inner-icon="mdi-lock-plus"
                              variant="outlined"
                              :rules="passwordChangeRules.new"
                              density="comfortable"
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
                          </v-col>
                          <v-col cols="12" sm="6">
                            <v-text-field
                              v-model="form.confirmNewPassword"
                              :label="$t('profile.confirmNewPassword')"
                              :type="showConfirmPassword ? 'text' : 'password'"
                              prepend-inner-icon="mdi-lock-check"
                              variant="outlined"
                              :rules="passwordChangeRules.confirm"
                              density="comfortable"
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
                          </v-col>
                        </v-row>
                      </v-expansion-panel-text>
                    </v-expansion-panel>
                  </v-expansion-panels>
                </div>
                
                <!-- 操作按钮 -->
                <div class="d-flex flex-wrap gap-3 justify-center justify-sm-start">
                  <v-btn
                    type="submit"
                    :color="themeStore.currentColors.primary"
                    :loading="loading"
                    prepend-icon="mdi-content-save"
                    size="large"
                    class="px-6"
                  >
                    {{ $t('profile.saveChanges') }}
                  </v-btn>
                  
                  <v-btn
                    variant="outlined"
                    @click="resetForm"
                    prepend-icon="mdi-refresh"
                    size="large"
                    class="px-6"
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

onMounted(async () => {
  // 确保用户数据已加载
  if (!authStore.user && authStore.token) {
    try {
      await authStore.getProfile()
    } catch (error) {
      console.error('Failed to fetch user profile:', error)
    }
  }
  resetForm()
})
</script>

<style scoped>
.gap-3 {
  gap: 12px;
}
</style>