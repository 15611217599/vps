import apiClient from './client'

export interface ServerGroup {
  id: number
  name: string
  description?: string
  sortOrder?: number
  isActive?: boolean
  region?: string
  country?: string
  city?: string
  categoryId?: number
  categoryName?: string
  createTime?: string
  updateTime?: string
}

export interface ServerGroupRequest {
  name: string
  description?: string
  sortOrder?: number
  isActive?: boolean
  region?: string
  country?: string
  city?: string
  categoryId?: number
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
}

// 获取服务器分组列表（分页）
export const getServerGroups = async (params?: {
  page?: number
  size?: number
  search?: string
  categoryId?: number
}) => {
  const response = await apiClient.get('/groups', { params })
  if (response.data.success) {
    return response.data
  }
  throw new Error(response.data.message || '获取服务器分组列表失败')
}

// 获取所有服务器分组（用于下拉选择）
export const getAllServerGroups = async () => {
  const response = await apiClient.get('/groups/active')
  if (response.data.success) {
    return response.data
  }
  throw new Error(response.data.message || '获取服务器分组列表失败')
}

// 根据ID获取服务器分组
export const getServerGroupById = async (id: number) => {
  const response = await apiClient.get(`/groups/${id}`)
  if (response.data.success) {
    return response.data
  }
  throw new Error(response.data.message || '获取服务器分组详情失败')
}

// 创建服务器分组
export const createServerGroup = async (data: ServerGroupRequest) => {
  const response = await apiClient.post('/groups', data)
  if (response.data.success) {
    return response.data
  }
  throw new Error(response.data.message || '创建服务器分组失败')
}

// 更新服务器分组
export const updateServerGroup = async (id: number, data: ServerGroupRequest) => {
  const response = await apiClient.put(`/groups/${id}`, data)
  if (response.data.success) {
    return response.data
  }
  throw new Error(response.data.message || '更新服务器分组失败')
}

// 删除服务器分组
export const deleteServerGroup = async (id: number) => {
  const response = await apiClient.delete(`/groups/${id}`)
  if (!response.data.success) {
    throw new Error(response.data.message || '删除服务器分组失败')
  }
  return response.data
}

export const serverGroupApi = {
  getServerGroups,
  getAllServerGroups,
  getServerGroupById,
  createServerGroup,
  updateServerGroup,
  deleteServerGroup
}
