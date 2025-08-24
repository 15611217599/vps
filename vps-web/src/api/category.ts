import { apiClient } from './client'

export interface ServerCategory {
  id: number
  name: string
  description: string
  sortOrder: number
  isActive: boolean
  groupCount: number
  serverCount: number
  createTime: string
  updateTime: string
  // 国际化字段
  nameZh?: string
  nameEn?: string
  descriptionZh?: string
  descriptionEn?: string
}

export interface ApiResponse<T> {
  success: boolean
  data: T
  message?: string
  language?: string
}

export interface PaginatedResponse<T> {
  success: boolean
  data: T[]
  totalElements: number
  totalPages: number
  currentPage: number
  size: number
}

/**
 * 获取所有激活的服务器类别
 */
export const getActiveCategories = async (): Promise<ServerCategory[]> => {
  const response = await apiClient.get<ApiResponse<ServerCategory[]>>('/categories/active')
  return response.data.data
}

/**
 * 获取激活的服务器类别（别名，保持兼容性）
 */
export const getLocalizedActiveCategories = async (): Promise<ServerCategory[]> => {
  return getActiveCategories()
}

/**
 * 获取服务器类别（分页）
 */
export const getCategories = async (params: {
  page?: number
  size?: number
  search?: string
}): Promise<PaginatedResponse<ServerCategory>> => {
  const response = await apiClient.get('/categories', {
    params
  })
  return response.data
}

/**
 * 根据ID获取服务器类别
 */
export const getCategoryById = async (id: number): Promise<ServerCategory> => {
  const response = await apiClient.get<ApiResponse<ServerCategory>>(`/categories/${id}`)
  return response.data.data
}

/**
 * 创建服务器类别
 */
export const createCategory = async (category: Partial<ServerCategory>): Promise<ServerCategory> => {
  const response = await apiClient.post<ApiResponse<ServerCategory>>('/categories', category)
  return response.data.data
}

/**
 * 更新服务器类别
 */
export const updateCategory = async (id: number, category: Partial<ServerCategory>): Promise<ServerCategory> => {
  const response = await apiClient.put<ApiResponse<ServerCategory>>(`/categories/${id}`, category)
  return response.data.data
}

/**
 * 删除服务器类别
 */
export const deleteCategory = async (id: number): Promise<void> => {
  await apiClient.delete(`/categories/${id}`)
}

/**
 * 检查类别名称是否存在
 */
export const checkCategoryName = async (name: string, excludeId?: number): Promise<boolean> => {
  const params: any = { name }
  if (excludeId) {
    params.excludeId = excludeId
  }
  
  const response = await apiClient.get<ApiResponse<{ exists: boolean }>>('/categories/check-name', {
    params
  })
  return response.data.data.exists
}