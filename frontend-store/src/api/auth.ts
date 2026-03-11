import client from './client'

export interface SignupRequest {
  email: string
  password: string
  name: string
  phone?: string
}

export interface LoginRequest {
  email: string
  password: string
}

export const authApi = {
  signup: (data: SignupRequest) => client.post('/api/v1/auth/signup', data),
  login: (data: LoginRequest) => client.post<{ data: { accessToken: string } }>('/api/v1/auth/login', data),
  logout: () => client.post('/api/v1/auth/logout'),
  refresh: () => client.post<{ data: { accessToken: string } }>('/api/v1/auth/refresh'),
}
