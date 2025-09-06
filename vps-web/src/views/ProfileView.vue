<template>
  <HomeLayout>
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
                  {{ TEXTS.profile.memberSince }}: {{ formatDate((authStore.user as any)?.createdAt) }}
                </div>
              </div>
            </v-card-text>
          </v-card>

          <!-- 钱包卡片 -->
          <WalletCard ref="walletCard" class="mb-6" />

          <!-- 充值卡片 -->
          <RechargeCard class="mb-6" @recharge-success="handleRechargeSuccess" />

          <!-- 个人资料编辑卡片 -->
          <v-card elevation="3" rounded="lg">
            <v-card-text class="pa-6">
              <v-form @submit.prevent="handleUpdateProfile" ref="profileForm">
                
                <!-- 基本信息区域 -->
                <div class="mb-6">
                  <h3 class="text-h6 mb-4 d-flex align-center">
                    <v-icon :color="themeStore.currentColors.primary" class="me-2">mdi-account-details</v-icon>
                    {{ TEXTS.profile.basicInfo }}
                  </h3>
                  
                  <v-row>
                    <v-col cols="12" sm="6">
                      <v-text-field
                        v-model="form.username"
                        :label="TEXTS.profile.username"
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
                        :label="TEXTS.profile.email"
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
                        {{ TEXTS.profile.changePassword }}
                      </v-expansion-panel-title>
                      <v-expansion-panel-text class="pt-4">
                        <v-row>
                          <v-col cols="12">
                            <v-text-field
                              v-model="form.currentPassword"
                              :label="TEXTS.profile.currentPassword"
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
                              :label="TEXTS.profile.newPassword"
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
                              :label="TEXTS.profile.confirmNewPassword"
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
                <div class="d-flex flex-wrap ga-3 justify-center justify-sm-start">
                  <v-btn
                    type="submit"
                    :color="themeStore.currentColors.primary"
                    :loading="loading"
                    prepend-icon="mdi-content-save"
                    size="large"
                    class="px-6"
                  >
                    {{ TEXTS.profile.saveChanges }}
                  </v-btn>
                  
                  <v-btn
                    variant="outlined"
                    @click="resetForm"
                    prepend-icon="mdi-refresh"
                    size="large"
                    class="px-6"
                  >
                    {{ TEXTS.common.reset }}
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
  </HomeLayout>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useThemeStore } from '@/stores/theme'
import { TEXTS } from '@/constants/texts'
import HomeLayout from '@/components/HomeLayout.vue'
import WalletCard from '@/components/WalletCard.vue'
import RechargeCard from '@/components/RechargeCard.vue'

// 移除国际化
const authStore = useAuthStore()
const themeStore = useThemeStore()

// 简单的消息提示函数
const showSnackbar = (message: string, type: 'success' | 'error') => {
  console.log(`${type.toUpperCase()}: ${message}`)
  // 这里可以集成实际的消息提示组件
}

const profileForm = ref()
const walletCard = ref()
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
  required: (value: string) => !!value || '此字段为必填项',
  username: (value: string) => {
    if (!value) return '此字段为必填项'
    if (value.length < 3) return `最少需要 3 个字符`
    if (value.length > 20) return `最多允许 20 个字符`
    if (!/^[a-zA-Z0-9_]+$/.test(value)) return '用户名只能包含字母、数字和下划线'
    return true
  },
  email: (value: string) => {
    const pattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    return pattern.test(value) || '请输入有效的邮箱地址'
  }
}

// 密码修改验证规则
const passwordChangeRules = computed(() => ({
  current: form.newPassword ? [rules.required] : [],
  new: form.currentPassword ? [
    rules.required,
    (value: string) => {
      if (value.length < 8) return `最少需要 8 个字符`
      return true
    }
  ] : [],
  confirm: form.newPassword ? [
    rules.required,
    (value: string) => value === form.newPassword || '两次输入的密码不一致'
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
    error.value = '请修复表单中的错误'
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
      showSnackbar('个人资料更新成功', 'success')
      // 清空密码字段
      form.currentPassword = ''
      form.newPassword = ''
      form.confirmNewPassword = ''
    }
  } catch (err: any) {
    error.value = err.message
    showSnackbar('个人资料更新失败', 'error')
  } finally {
    loading.value = false
  }
}

// 处理充值成功事件
const handleRechargeSuccess = () => {
  // 刷新钱包余额
  if (walletCard.value?.loadWallet) {
    walletCard.value.loadWallet()
  }
  showSnackbar('充值成功，余额已更新', 'success')
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

<!-- 使用 Vuetify ga-3 utility class 替代自定义样式 -->