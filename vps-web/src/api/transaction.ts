import type { ApiResponse } from '@/types/api'

export interface TransactionDTO {
  id: number
  userId: number
  username: string
  transactionNumber: string
  type: 'RECHARGE' | 'ORDER_PAYMENT' | 'REFUND' | 'WITHDRAWAL' | 'ADJUSTMENT'
  status: 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'FAILED' | 'CANCELLED'
  amount: number
  currency: string
  balanceBefore?: number
  balanceAfter?: number
  description: string
  orderId?: number
  orderNumber?: string
  paymentMethod?: string
  externalTransactionId?: string
  createdAt: string
  updatedAt: string
  completedAt?: string
}

export interface CreateRechargeRequest {
  amount: number
  paymentMethod: string
  description?: string
}

export const transactionApi = {
  // 获取用户交易记录
  getUserTransactions: async (): Promise<ApiResponse<TransactionDTO[]>> => {
    const response = await fetch('/api/transactions/user', {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    })
    
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`)
    }
    
    const contentType = response.headers.get('content-type')
    if (!contentType || !contentType.includes('application/json')) {
      const text = await response.text()
      console.error('Expected JSON but received:', text)
      throw new Error('Server returned non-JSON response')
    }
    
    return response.json()
  },

  // 根据类型获取用户交易记录
  getUserTransactionsByType: async (type: string): Promise<ApiResponse<TransactionDTO[]>> => {
    const response = await fetch(`/api/transactions/user/type/${type}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    })
    return response.json()
  },

  // 根据状态获取用户交易记录
  getUserTransactionsByStatus: async (status: string): Promise<ApiResponse<TransactionDTO[]>> => {
    const response = await fetch(`/api/transactions/user/status/${status}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    })
    return response.json()
  },

  // 根据日期范围获取用户交易记录
  getUserTransactionsByDateRange: async (startDate: string, endDate: string): Promise<ApiResponse<TransactionDTO[]>> => {
    const response = await fetch(`/api/transactions/user/date-range?startDate=${startDate}&endDate=${endDate}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    })
    return response.json()
  },

  // 根据交易号获取交易记录
  getTransactionByNumber: async (transactionNumber: string): Promise<ApiResponse<TransactionDTO>> => {
    const response = await fetch(`/api/transactions/number/${transactionNumber}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    })
    return response.json()
  },

  // 根据订单获取交易记录
  getTransactionsByOrder: async (orderId: number): Promise<ApiResponse<TransactionDTO[]>> => {
    const response = await fetch(`/api/transactions/order/${orderId}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      }
    })
    return response.json()
  },

  // 创建充值交易
  createRecharge: async (request: CreateRechargeRequest): Promise<ApiResponse<TransactionDTO>> => {
    const response = await fetch('/api/transactions/recharge', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      },
      body: JSON.stringify(request)
    })
    return response.json()
  }
}
