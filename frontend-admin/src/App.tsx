import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import AdminLayout from './components/layout/AdminLayout';
import AdminLoginPage from './pages/auth/AdminLoginPage';
import DashboardPage from './pages/dashboard/DashboardPage';
import SalesStatsPage from './pages/stats/SalesStatsPage';
import SearchStatsPage from './pages/stats/SearchStatsPage';
import CouponListPage from './pages/coupon/CouponListPage';
import BannerListPage from './pages/banner/BannerListPage';
import MemberListPage from './pages/member/MemberListPage';
import MemberDetailPage from './pages/member/MemberDetailPage';
import OrderListPage from './pages/order/OrderListPage';
import OrderDetailPage from './pages/order/OrderDetailPage';
import ProductListPage from './pages/product/ProductListPage';
import ProductFormPage from './pages/product/ProductFormPage';
import CategoryPage from './pages/product/CategoryPage';
import InquiryListPage from './pages/cs/InquiryListPage';
import InquiryDetailPage from './pages/cs/InquiryDetailPage';
import NoticeListPage from './pages/cs/NoticeListPage';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: 1,
      staleTime: 5 * 60 * 1000,
    },
  },
});

export default function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <Routes>
          {/* Public routes */}
          <Route path="/login" element={<AdminLoginPage />} />

          {/* Redirect root to dashboard */}
          <Route path="/" element={<Navigate to="/dashboard" replace />} />

          {/* Protected routes with AdminLayout */}
          <Route element={<AdminLayout />}>
            <Route path="/dashboard" element={<DashboardPage />} />
            <Route path="/products" element={<ProductListPage />} />
            <Route path="/products/new" element={<ProductFormPage />} />
            <Route path="/products/:id" element={<ProductFormPage />} />
            <Route path="/categories" element={<CategoryPage />} />
            <Route path="/orders" element={<OrderListPage />} />
            <Route path="/orders/:id" element={<OrderDetailPage />} />
            <Route path="/members" element={<MemberListPage />} />
            <Route path="/members/:id" element={<MemberDetailPage />} />
            <Route path="/coupons" element={<CouponListPage />} />
            <Route path="/banners" element={<BannerListPage />} />
            <Route path="/cs/inquiries" element={<InquiryListPage />} />
            <Route path="/cs/inquiries/:id" element={<InquiryDetailPage />} />
            <Route path="/cs/notices" element={<NoticeListPage />} />
            <Route path="/stats/sales" element={<SalesStatsPage />} />
            <Route path="/stats/search" element={<SearchStatsPage />} />
          </Route>
        </Routes>
      </BrowserRouter>
    </QueryClientProvider>
  );
}
