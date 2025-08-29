import { apiClient } from './client'

export interface ServerInstallRequest {
  serverId: number
  osName: string
  osVersion?: string
  rootPassword?: string
  sshKey?: string
  sshPort?: number
  webPort?: number
  customImageUrl?: string
  installType?: string
  minimal?: boolean
}

export interface ServerInstallStatus {
  id: number
  ip: string
  port: number
  operatingSystem: string
  installStatus: 'NONE' | 'INSTALLING' | 'COMPLETED' | 'FAILED'
  installProgress: number
  installStep?: string
  installLog?: string
  installError?: string
  installStartedAt?: string
}

export const installApi = {
  // 开始安装系统
  startInstall: (data: ServerInstallRequest) =>
    apiClient.post<string>('/server-install/start', data),

  // 获取服务器安装状态
  getInstallStatus: (serverId: number) =>
    apiClient.get<ServerInstallStatus>(`/server-install/status/${serverId}`)
}