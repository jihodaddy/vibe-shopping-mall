import axios from 'axios';
import { useAdminAuthStore } from '../store/adminAuthStore';

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor: attach Authorization header from store
apiClient.interceptors.request.use(
  (config) => {
    const admin = useAdminAuthStore.getState().admin;
    if (admin?.token) {
      config.headers.Authorization = `Bearer ${admin.token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor: on 401, clear auth state and redirect to /login
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      useAdminAuthStore.getState().logout();
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default apiClient;
