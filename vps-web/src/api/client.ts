import axios from 'axios'

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

    // 设置默认语言为中文
    config.headers['Accept-Language'] = 'zh-CN'

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
    
    // 提取后端错误信息
    if (error.response?.data?.message) {
      error.message = error.response.data.message
    }
    
    return Promise.reject(error)
  }
)

export { apiClient }
export default apiClient