import { apiClient } from './client'

export interface CreateOrderRequest {
  priceGroupId: number
  billingPeriod: string
  cpuCores: number
  memoryGb: number
  storageGb: number
  bandwidthMbps: number
  ipCount: number
  osName: string
  osVersion: string
  initialPassword: string
  sshPort: number
}

export interface OrderDTO {
  id: number
  userId: number
  username: string
  priceGroupId: number
  priceGroupName: string
  serverId?: number
  serverName?: string
  orderNumber: string
  status: 'PENDING' | 'PAID' | 'PROCESSING' | 'ACTIVE' | 'SUSPENDED' | 'CANCELLED' | 'EXPIRED'
  amount: number
  currency: string
  billingPeriod: string
  cpuCores: number
  memoryGb: number
  storageGb: number
  bandwidthMbps: number
  ipCount: number
  osName: string
  osVersion: string
  initialPassword: string
  sshPort: number
  createdAt: string
  updatedAt: string
  expiresAt: string
}

export const orderApi = {
  // 创建订单
  createOrder: async (data: CreateOrderRequest) => {
    const response = await apiClient.post('/orders', data)
    return response.data
  },

  // 获取用户订单列表
  getUserOrders: async () => {
    const response = await apiClient.get('/orders')
    return response.data
  },

  // 根据ID获取订单
  getOrderById: async (id: number) => {
    const response = await apiClient.get(`/orders/${id}`)
    return response.data
  },

  // 根据订单号获取订单
  getOrderByNumber: async (orderNumber: string) => {
    const response = await apiClient.get(`/orders/number/${orderNumber}`)
    return response.data
  },

  // 更新订单状态
  updateOrderStatus: async (id: number, status: string) => {
    const response = await apiClient.put(`/orders/${id}/status?status=${status}`)
    return response.data
  },

  // 取消订单
  cancelOrder: async (id: number) => {
    const response = await apiClient.put(`/orders/${id}/cancel`)
    return response.data
  },

  // 处理订单支付
  processOrderPayment: async (id: number) => {
    const response = await apiClient.post(`/orders/${id}/pay`)
    return response.data
  }
}
