import apiClient from './client'

export interface Server {
  id: number
  ip: string
  port?: number
  status: 'online' | 'offline'
  cpuCores?: string
  memory?: string
  diskSpace?: string
  diskType?: string
  networkSpeed?: string
  username?: string
  password?: string
  groupId?: number
  groupName?: string
  createTime?: string
  lastUpdate?: string
}

export interface ServerRequest {
  ip: string
  port?: number | undefined
  status: string
  cpuCores?: string
  memory?: string
  diskSpace?: string
  diskType?: string
  networkSpeed?: string
  username?: string
  password?: string
  groupId?: number | undefined
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
}

// 获取服务器列表（分页）
export const getServers = async (params?: {
  page?: number
  size?: number
  search?: string
  status?: string
  groupId?: number
}) => {
  const response = await apiClient.get('/servers', { params })
  if (response.data.success) {
    return response.data
  }
  throw new Error(response.data.message || '获取服务器列表失败')
}

// 获取所有服务器
export const getAllServers = async () => {
  const response = await apiClient.get('/servers/all')
  if (response.data.success) {
    return response
  }
  throw new Error(response.data.message || '获取服务器列表失败')
}

// 根据ID获取服务器
export const getServerById = async (id: number) => {
  const response = await apiClient.get(`/servers/${id}`)
  if (response.data.success) {
    return response
  }
  throw new Error(response.data.message || '获取服务器详情失败')
}

// 创建服务器
export const createServer = async (data: ServerRequest) => {
  const response = await apiClient.post('/servers', data)
  if (response.data.success) {
    return response
  }
  throw new Error(response.data.message || '创建服务器失败')
}

// 更新服务器
export const updateServer = async (id: number, data: ServerRequest) => {
  const response = await apiClient.put(`/servers/${id}`, data)
  if (response.data.success) {
    return response
  }
  throw new Error(response.data.message || '更新服务器失败')
}

// 删除服务器
export const deleteServer = async (id: number) => {
  const response = await apiClient.delete(`/servers/${id}`)
  if (!response.data.success) {
    throw new Error(response.data.message || '删除服务器失败')
  }
}

// 根据状态获取服务器
export const getServersByStatus = async (status: string) => {
  const response = await apiClient.get(`/servers/status/${status}`)
  if (response.data.success) {
    return response
  }
  throw new Error(response.data.message || '获取服务器列表失败')
}

// 根据分组获取服务器
export const getServersByGroupId = async (groupId: number) => {
  const response = await apiClient.get(`/servers/group/${groupId}`)
  if (response.data.success) {
    return response
  }
  throw new Error(response.data.message || '获取服务器列表失败')
}

// 搜索服务器
export const searchServers = async (keyword: string, params?: {
  page?: number
  size?: number
}) => {
  const response = await apiClient.get('/servers/search', {
    params: { keyword, ...params }
  })
  if (response.data.success) {
    return response
  }
  throw new Error(response.data.message || '搜索服务器失败')
}

// 获取服务器统计信息
export const getServerStats = async () => {
  const response = await apiClient.get('/servers/stats')
  if (response.data.success) {
    return response
  }
  throw new Error(response.data.message || '获取服务器统计失败')
}

// 检查IP是否存在
export const checkIpExists = async (ip: string, excludeId?: number) => {
  const response = await apiClient.get('/servers/check-ip', {
    params: { ip, excludeId }
  })
  if (response.data.success) {
    return response
  }
  throw new Error(response.data.message || '检查IP失败')
}

// 添加获取分组列表的函数
export const getGroups = async () => {
  const response = await apiClient.get('/groups')
  if (response.data.success) {
    return response
  }
  throw new Error(response.data.message || '获取分组列表失败')
}
