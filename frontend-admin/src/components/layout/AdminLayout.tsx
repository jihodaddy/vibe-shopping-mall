import { useState } from 'react';
import { Layout, Menu, Button, Typography, Space, Breadcrumb, Avatar, Dropdown } from 'antd';
import {
  DashboardOutlined,
  ShoppingOutlined,
  ShoppingCartOutlined,
  UserOutlined,
  TagsOutlined,
  PictureOutlined,
  CustomerServiceOutlined,
  BarChartOutlined,
  LogoutOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  QuestionCircleOutlined,
  NotificationOutlined,
} from '@ant-design/icons';
import { Outlet, useNavigate, useLocation, Navigate } from 'react-router-dom';
import { useAdminAuthStore } from '../../store/adminAuthStore';
import { adminLogout } from '../../api/adminAuth';

const { Sider, Header, Content } = Layout;
const { Text } = Typography;

const menuItems = [
  { key: '/dashboard', icon: <DashboardOutlined />, label: '대시보드' },
  { key: '/products', icon: <ShoppingOutlined />, label: '상품관리' },
  { key: '/orders', icon: <ShoppingCartOutlined />, label: '주문관리' },
  { key: '/members', icon: <UserOutlined />, label: '회원관리' },
  { key: '/coupons', icon: <TagsOutlined />, label: '쿠폰관리' },
  { key: '/banners', icon: <PictureOutlined />, label: '배너관리' },
  {
    key: '/cs',
    icon: <CustomerServiceOutlined />,
    label: 'CS관리',
    children: [
      { key: '/cs/inquiries', icon: <QuestionCircleOutlined />, label: '문의관리' },
      { key: '/cs/notices', icon: <NotificationOutlined />, label: '공지관리' },
    ],
  },
  {
    key: '/stats',
    icon: <BarChartOutlined />,
    label: '통계',
    children: [
      { key: '/stats/sales', label: '매출통계' },
      { key: '/stats/search', label: '검색어분석' },
    ],
  },
];

const breadcrumbNameMap: Record<string, string> = {
  '/dashboard': '대시보드',
  '/products': '상품관리',
  '/orders': '주문관리',
  '/members': '회원관리',
  '/coupons': '쿠폰관리',
  '/banners': '배너관리',
  '/cs/inquiries': '문의관리',
  '/cs/notices': '공지관리',
  '/stats/sales': '매출통계',
  '/stats/search': '검색어분석',
};

export default function AdminLayout() {
  const [collapsed, setCollapsed] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();
  const { admin, logout } = useAdminAuthStore();

  // Route guard: redirect to /login if not authenticated
  if (!admin) {
    return <Navigate to="/login" replace />;
  }

  const handleMenuClick = ({ key }: { key: string }) => {
    navigate(key);
  };

  const handleLogout = async () => {
    try {
      await adminLogout();
    } catch {
      // ignore logout API errors
    } finally {
      logout();
      navigate('/login');
    }
  };

  const currentPath = location.pathname;
  const selectedKey = Object.keys(breadcrumbNameMap)
    .filter((key) => currentPath.startsWith(key))
    .sort((a, b) => b.length - a.length)[0] ?? currentPath;
  const breadcrumbLabel = breadcrumbNameMap[selectedKey] ?? currentPath;

  const userMenuItems = [
    {
      key: 'logout',
      icon: <LogoutOutlined />,
      label: '로그아웃',
      onClick: handleLogout,
    },
  ];

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider
        collapsible
        collapsed={collapsed}
        onCollapse={setCollapsed}
        trigger={null}
        style={{ background: '#001529' }}
      >
        <div
          style={{
            height: 64,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            borderBottom: '1px solid rgba(255,255,255,0.1)',
          }}
        >
          {!collapsed && (
            <Text strong style={{ color: '#fff', fontSize: 16 }}>
              어드민
            </Text>
          )}
        </div>
        <Menu
          theme="dark"
          mode="inline"
          selectedKeys={[selectedKey]}
          defaultOpenKeys={['/cs', '/stats']}
          items={menuItems}
          onClick={handleMenuClick}
          style={{ marginTop: 8 }}
        />
      </Sider>

      <Layout>
        <Header
          style={{
            background: '#fff',
            padding: '0 24px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            boxShadow: '0 1px 4px rgba(0,21,41,0.08)',
            position: 'sticky',
            top: 0,
            zIndex: 1,
          }}
        >
          <Button
            type="text"
            icon={collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
            onClick={() => setCollapsed(!collapsed)}
            style={{ fontSize: 16, width: 40, height: 40 }}
          />

          <Space>
            <Dropdown menu={{ items: userMenuItems }} placement="bottomRight">
              <Space style={{ cursor: 'pointer' }}>
                <Avatar icon={<UserOutlined />} style={{ backgroundColor: '#1677ff' }} />
                <div style={{ lineHeight: 1.2 }}>
                  <div>
                    <Text strong style={{ fontSize: 14 }}>
                      {admin.name}
                    </Text>
                  </div>
                  <div>
                    <Text type="secondary" style={{ fontSize: 12 }}>
                      {admin.role}
                    </Text>
                  </div>
                </div>
              </Space>
            </Dropdown>
          </Space>
        </Header>

        <Content style={{ padding: 24 }}>
          <Breadcrumb
            style={{ marginBottom: 16 }}
            items={[{ title: '홈' }, { title: breadcrumbLabel }]}
          />
          <div
            style={{
              background: '#fff',
              padding: 24,
              borderRadius: 8,
              minHeight: 360,
            }}
          >
            <Outlet />
          </div>
        </Content>
      </Layout>
    </Layout>
  );
}
