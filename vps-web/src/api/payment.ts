import { apiClient } from './client'

export interface CreatePaymentRequest {
  money: number
  type: string
  name?: string
  device?: string
  param?: string
}

export interface PaymentResponse {
  code: number
  msg?: string
  tradeNo?: string
  payUrl?: string
  qrCode?: string
  urlScheme?: string
  outTradeNo: string
}

export interface PaymentOrder {
  id: number
  outTradeNo: string
  tradeNo?: string
  apiTradeNo?: string
  type: string
  status: string
  money: number
  name: string
  clientIp?: string
  device?: string
  param?: string
  payUrl?: string
  qrCode?: string
  urlScheme?: string
  buyer?: string
  createdAt: string
  updatedAt: string
  completedAt?: string
}

export const paymentApi = {
  // 创建支付订单
  createPayment: (data: CreatePaymentRequest) =>
    apiClient.post<PaymentResponse>('/payment/create', data),

  // 查询支付订单
  getPaymentOrder: (outTradeNo: string) =>
    apiClient.get<PaymentOrder>(`/payment/order/${outTradeNo}`),

  // 获取用户支付订单列表
  getUserPaymentOrders: (page = 0, size = 10) =>
    apiClient.get<{ content: PaymentOrder[], totalElements: number, totalPages: number }>(`/payment/orders?page=${page}&size=${size}`),

  // 支付方式列表
  getPaymentMethods: () => [
    { code: 'alipay', name: '支付宝', icon: 'mdi-wallet' },
    { code: 'wxpay', name: '微信支付', icon: 'mdi-wechat' },
    { code: 'qqpay', name: 'QQ钱包', icon: 'mdi-bell' },
    { code: 'bank', name: '网银支付', icon: 'mdi-bank' },
    { code: 'jdpay', name: '京东支付', icon: 'mdi-shopping' },
    { code: 'paypal', name: 'PayPal', icon: 'mdi-credit-card' },
    { code: 'usdt', name: '泰达币', icon: 'mdi-bitcoin' }
  ]
}