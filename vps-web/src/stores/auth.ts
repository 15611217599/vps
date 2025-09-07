import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi, type LoginRequest, type RegisterRequest } from '../api/auth'

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(
    localStorage.getItem('token') || sessionStorage.getItem('token')
  )
  const user = ref<{ username: string; email: string; role?: string } | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)

  const isAuthenticated = computed(() => !!token.value)
  const isAdmin = computed(() => user.value?.role === 'ADMIN')

  const login = async (credentials: LoginRequest) => {
    loading.value = true
    error.value = null
    try {
      // 前端再次验证
      if (!credentials.username?.trim() || !credentials.password?.trim()) {
        throw new Error('用户名和密码不能为空')
      }
      
      const response = await authApi.login({
        username: credentials.username.trim(),
        password: credentials.password
      })
      const { token: authToken, username, email, role } = response.data
      
      token.value = authToken
      user.value = { username, email, role }
      
      // 默认使用localStorage存储（记住用户）
      localStorage.setItem('token', authToken)
      
      return true
    } catch (err: any) {
      error.value = err.response?.data?.error || err.message || '登录失败'
      return false
    } finally {
      loading.value = false
    }
  }

  const register = async (userData: RegisterRequest) => {
    loading.value = true
    error.value = null
    try {
      // 前端再次验证
      if (!userData.username?.trim() || !userData.email?.trim() || !userData.password?.trim()) {
        throw new Error('所有字段都不能为空')
      }
      
      const response = await authApi.register({
        username: userData.username.trim(),
        email: userData.email.trim(),
        password: userData.password
      })
      const { token: authToken, username, email, role } = response.data
      
      token.value = authToken
      user.value = { username, email, role }
      localStorage.setItem('token', authToken)
      
      return true
    } catch (err: any) {
      error.value = err.response?.data?.error || err.message || '注册失败'
      return false
    } finally {
      loading.value = false
    }
  }

  const logout = () => {
    token.value = null
    user.value = null
    localStorage.removeItem('token')
    sessionStorage.removeItem('token')
  }

  const getProfile = async () => {
    if (!token.value) return
    
    try {
      const response = await authApi.getProfile()
      user.value = response.data
    } catch (err) {
      logout()
    }
  }

  const updateProfile = async (updateData: {
    username: string
    email: string
    currentPassword?: string
    newPassword?: string
  }) => {
    loading.value = true
    error.value = null
    try {
      const response = await authApi.updateProfile(updateData)
      user.value = response.data
      return true
    } catch (err: any) {
      error.value = err.response?.data?.error || err.message || '更新失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  return {
    token,
    user,
    loading,
    error,
    isAuthenticated,
    isAdmin,
    login,
    register,
    logout,
    getProfile,
    updateProfile
  }
})