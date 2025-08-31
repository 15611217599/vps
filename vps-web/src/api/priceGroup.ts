import { apiClient } from './client'

export interface PriceGroup {
  id: number
  name: string
  description: string
  hourlyPrice: number
  dailyPrice: number
  monthlyPrice: number
  quarterlyPrice: number
  semiAnnualPrice: number
  annualPrice: number
  sortOrder: number
  isActive: boolean
  createTime: string
  lastUpdate: string
  // 折扣相关字段
  hasDiscount?: boolean
  discountPercentage?: number
  discountStartTime?: string
  discountEndTime?: string
}

export interface ApiResponse<T> {
  success: boolean
  data: T
  message?: string
}

export interface PaginatedResponse<T> {
  success: boolean
  data: T[]
  totalElements: number
  totalPages: number
  currentPage: number
  size: number
  hasNext: boolean
  hasPrevious: boolean
}

/**
 * 获取所有激活的价格组
 */
export const getActivePriceGroups = async (): Promise<PriceGroup[]> => {
  const response = await apiClient.get<ApiResponse<PriceGroup[]>>('/price-groups/active')
  return response.data.data
}

/**
 * 分页获取价格组列表
 */
export const getPriceGroups = async (params: {
  page?: number
  size?: number
  search?: string
  active?: boolean
}): Promise<PaginatedResponse<PriceGroup>> => {
  const response = await apiClient.get<PaginatedResponse<PriceGroup>>('/price-groups', { params })
  return response.data
}

/**
 * 根据ID获取价格组
 */
export const getPriceGroupById = async (id: number): Promise<PriceGroup> => {
  const response = await apiClient.get<ApiResponse<PriceGroup>>(`/price-groups/${id}`)
  return response.data.data
}

/**
 * 创建价格组
 */
export const createPriceGroup = async (data: Partial<PriceGroup>): Promise<ApiResponse<PriceGroup>> => {
  const response = await apiClient.post<ApiResponse<PriceGroup>>('/price-groups', data)
  return response.data
}

/**
 * 更新价格组
 */
export const updatePriceGroup = async (id: number, data: Partial<PriceGroup>): Promise<ApiResponse<PriceGroup>> => {
  const response = await apiClient.put<ApiResponse<PriceGroup>>(`/price-groups/${id}`, data)
  return response.data
}

/**
 * 删除价格组
 */
export const deletePriceGroup = async (id: number): Promise<ApiResponse<void>> => {
  const response = await apiClient.delete<ApiResponse<void>>(`/price-groups/${id}`)
  return response.data
}

/**
 * 检查价格组名称是否存在
 */
export const checkNameExists = async (name: string, excludeId?: number): Promise<boolean> => {
  const params: any = { name }
  if (excludeId) {
    params.excludeId = excludeId
  }
  const response = await apiClient.get<ApiResponse<{ exists: boolean }>>('/price-groups/check-name', { params })
  return response.data.data.exists
}

/**
 * 应用折扣
 */
export const applyDiscount = async (
  id: number, 
  discountPercentage: number,
  startTime?: string,
  endTime?: string
): Promise<ApiResponse<PriceGroup>> => {
  const response = await apiClient.post<ApiResponse<PriceGroup>>(`/price-groups/${id}/apply-discount`, {
    discountPercentage,
    startTime,
    endTime
  })
  return response.data
}

/**
 * 恢复原价
 */
export const restoreOriginalPrices = async (id: number): Promise<ApiResponse<PriceGroup>> => {
  const response = await apiClient.post<ApiResponse<PriceGroup>>(`/price-groups/${id}/restore-original-prices`)
  return response.data
}

// 导出所有API方法
export const priceGroupApi = {
  getActivePriceGroups,
  getPriceGroups,
  getPriceGroupById,
  createPriceGroup,
  updatePriceGroup,
  deletePriceGroup,
  checkNameExists,
  applyDiscount,
  restoreOriginalPrices
}
