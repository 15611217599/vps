import { apiClient } from './client'

export interface ServerActionRequest {
  serverId: number
  action: 'start' | 'restart' | 'stop' | 'resetPassword' | 'reinstall'
  newPassword?: string
  osName?: string
  osVersion?: string
  rootPassword?: string
  sshPort?: number
  remark?: string
}

export interface ServerMonitoringDTO {
  serverId: number
  serverIp: string
  status: 'online' | 'offline' | 'maintenance'
  cpuUsage?: number
  memoryUsage?: number
  diskUsage?: number
  networkInbound?: number
  networkOutbound?: number
  uptime?: string
  processCount?: number
  loadAverage?: number
  publicIp?: string
  privateIp?: string
  sshPort?: number
  sshConnectable?: boolean
  lastUpdateTime?: string
  lastCheckTime?: string
}

export interface ServerActionLogDTO {
  id: number
  serverId: number
  serverIp: string
  action: string
  status: 'pending' | 'running' | 'success' | 'failed'
  remark?: string
  errorMessage?: string
  startTime: string
  endTime?: string
  operatorName?: string
  progress?: number
  currentStep?: string
}

export interface OsOptionDTO {
  name: string
  displayName: string
  versions: string[]
}

export const serverManagementApi = {
  // 执行服务器操作
  executeAction: (request: ServerActionRequest) =>
    apiClient.post<string>('/server-management/action', request),

  // 获取服务器监控信息
  getMonitoring: (serverId: number) =>
    apiClient.get<ServerMonitoringDTO>(`/server-management/${serverId}/monitoring`),

  // 获取服务器操作日志
  getActionLogs: (serverId: number, page = 0, size = 20) =>
    apiClient.get<ServerActionLogDTO[]>(`/server-management/${serverId}/logs`, {
      params: { page, size }
    }),

  // 测试服务器连接
  testConnection: (serverId: number) =>
    apiClient.post<boolean>(`/server-management/${serverId}/test-connection`),

  // 获取可用的操作系统列表
  getAvailableOs: () =>
    apiClient.get<OsOptionDTO[]>('/server-management/available-os')
}
