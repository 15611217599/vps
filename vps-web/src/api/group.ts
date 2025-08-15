import apiClient from './client'

// 服务器分组接口类型定义
export interface Server {
  id: number
  name: string
  ip: string
  port: number
  username: string
  password: string
  status: string
  type: string
  location: string
  provider?: string
  cpuCores?: string
  memory?: string
  diskSpace?: string
  diskType?: string
  networkSpeed?: string
  operatingSystem?: string
  groupId?: number
  groupName?: string
  createTime?: string
  lastUpdate?: string
}

export interface ServerGroup {
  id: number
  name: string
  description?: string
  region?: string
  country?: string
  city?: string
  categoryId?: number
  categoryName?: string
  servers?: Server[]
}

export interface GroupOption {
  value: number
  title: string
  description?: string
}

export interface CreateGroupRequest {
  categoryId: number
  name: string
  description?: string
  region?: string
  country?: string
  city?: string
  sortOrder?: number
}

export interface UpdateGroupRequest {
  categoryId: number
  name: string
  description?: string
  region?: string
  country?: string
  city?: string
  sortOrder?: number
}

// API方法 - 只保留分组相关的功能
export const groupAPI = {
  // 获取分组数据（分页）
  async getGroups(params?: { page?: number; size?: number; search?: string }): Promise<any> {
    const queryParams = new URLSearchParams()
    if (params?.page !== undefined) queryParams.append('page', params.page.toString())
    if (params?.size !== undefined) queryParams.append('size', params.size.toString())
    if (params?.search) queryParams.append('search', params.search)
    
    const url = `/groups${queryParams.toString() ? '?' + queryParams.toString() : ''}`
    const response = await apiClient.get(url)
    
    if (response.data.success) {
      return response.data
    }
    throw new Error(response.data.message || '获取分组失败')
  },

  // 获取分组的服务器数据
  async getGroupedServers(): Promise<ServerGroup[]> {
    const response = await apiClient.get('/servers/grouped')
    if (response.data.success) {
      return response.data.data
    }
    throw new Error(response.data.message || '获取分组服务器失败')
  },

  // 获取分组选项
  async getGroupOptions(): Promise<GroupOption[]> {
    const response = await apiClient.get('/servers/groups')
    if (response.data.success) {
      return response.data.data
    }
    throw new Error(response.data.message || '获取分组选项失败')
  },

  // 创建分组
  async createGroup(data: CreateGroupRequest): Promise<ServerGroup> {
    const response = await apiClient.post('/groups', data)
    if (response.data.success) {
      return response.data.data
    }
    throw new Error(response.data.message || '创建分组失败')
  },

  // 更新分组
  async updateGroup(id: number, data: UpdateGroupRequest): Promise<ServerGroup> {
    const response = await apiClient.put(`/groups/${id}`, data)
    if (response.data.success) {
      return response.data.data
    }
    throw new Error(response.data.message || '更新分组失败')
  },

  // 删除分组
  async deleteGroup(id: number): Promise<void> {
    const response = await apiClient.delete(`/groups/${id}`)
    if (!response.data.success) {
      throw new Error(response.data.message || '删除分组失败')
    }
  },

  // 获取所有类别
  async getCategories(): Promise<any[]> {
    const response = await apiClient.get('/categories/active')
    if (response.data.success) {
      return response.data.data
    }
    throw new Error(response.data.message || '获取类别失败')
  }
}