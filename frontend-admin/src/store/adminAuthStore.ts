import { create } from 'zustand';
import { persist } from 'zustand/middleware';

interface AdminUser {
  name: string;
  role: string;
  token: string;
}

interface AdminAuthState {
  admin: AdminUser | null;
  login: (data: AdminUser) => void;
  logout: () => void;
}

export const useAdminAuthStore = create<AdminAuthState>()(
  persist(
    (set) => ({
      admin: null,
      login: (data: AdminUser) => set({ admin: data }),
      logout: () => set({ admin: null }),
    }),
    {
      name: 'admin-auth-storage',
    }
  )
);
