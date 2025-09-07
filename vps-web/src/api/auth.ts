import apiClient from './client'

export interface LoginRequest {
  username: string
  password: string
}

export interface RegisterRequest {
  username: string
  email: string
  password: string
}

export interface AuthResponse {
  token: string
  username: string
  email: string
  role: string
}

export interface UpdateProfileRequest {
  username: string
  email: string
  currentPassword?: string
  newPassword?: string
}

export interface ResetPasswordRequest {
  email: string
  verificationCode: string
  newPassword: string
}

export const authApi = {
  login: (data: LoginRequest) => apiClient.post<AuthResponse>('/auth/login', data),
  register: (data: RegisterRequest) => apiClient.post<AuthResponse>('/auth/register', data),
  getProfile: () => apiClient.get('/auth/profile'),
  updateProfile: (data: UpdateProfileRequest) => apiClient.put('/auth/profile', data),
  sendResetCode: (email: string) => apiClient.post('/auth/forgot-password', { email }),
  resetPassword: (data: ResetPasswordRequest) => apiClient.post('/auth/reset-password', data)
}