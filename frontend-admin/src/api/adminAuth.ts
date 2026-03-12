import apiClient from './client';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  data: {
    accessToken: string;
    name: string;
    role: string;
  };
}

export const adminLogin = async (credentials: LoginRequest): Promise<LoginResponse> => {
  const response = await apiClient.post<LoginResponse>('/api/admin/auth/login', credentials);
  return response.data;
};

export const adminLogout = async (): Promise<void> => {
  await apiClient.post('/api/admin/auth/logout');
};
