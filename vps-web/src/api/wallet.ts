import { apiClient } from './client'

export interface Wallet {
  id: number
  userId: number
  balance: number
  currency: string
  createdAt: string
  updatedAt: string
}

export const walletApi = {
  // 获取钱包信息
  getWallet: async (): Promise<Wallet> => {
    const response = await apiClient.get('/wallet')
    return response.data
  },

  // 创建钱包
  createWallet: async (): Promise<Wallet> => {
    const response = await apiClient.post('/wallet')
    return response.data
  },

  // 更新余额
  updateBalance: async (amount: number): Promise<Wallet> => {
    const response = await apiClient.put('/wallet/balance', null, {
      params: { amount }
    })
    return response.data
  },


}
