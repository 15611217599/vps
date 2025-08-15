import axios from 'axios'
import { getAcceptLanguageHeader } from '@/utils/language'

const API_BASE_URL = 'http://localhost:8080/api'

// 创建axios实例
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器，添加token和语言头
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }

    // 添加语言头 - 使用标准化函数
    const language = getAcceptLanguageHeader()
    config.headers['Accept-Language'] = language

    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器，处理通用错误
apiClient.interceptors.response.use(
  (response) => {
    // 保留原始数据，不自动解析多语言，让组件自行决定何时解析
    return response
  },
  (error) => {
    if (error.response?.status === 401) {
      // Token过期或无效，清除本地存储并跳转到登录页
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      window.location.href = '/auth'
    }
    return Promise.reject(error)
  }
)

export { apiClient }
export default apiClient