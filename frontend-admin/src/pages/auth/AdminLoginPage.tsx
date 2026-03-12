import { useState } from 'react';
import { Form, Input, Button, Card, Typography, Alert } from 'antd';
import { UserOutlined, LockOutlined } from '@ant-design/icons';
import { useNavigate, Navigate } from 'react-router-dom';
import { useAdminAuthStore } from '../../store/adminAuthStore';
import { adminLogin } from '../../api/adminAuth';

const { Title, Text } = Typography;

interface LoginFormValues {
  email: string;
  password: string;
}

export default function AdminLoginPage() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();
  const { admin, login } = useAdminAuthStore();

  // Already logged in
  if (admin) {
    return <Navigate to="/dashboard" replace />;
  }

  const handleSubmit = async (values: LoginFormValues) => {
    setLoading(true);
    setError(null);
    try {
      const response = await adminLogin(values);
      const { accessToken, name, role } = response.data;
      login({ token: accessToken, name, role });
      navigate('/dashboard', { replace: true });
    } catch (err: unknown) {
      const axiosError = err as { response?: { data?: { message?: string } } };
      setError(axiosError.response?.data?.message ?? '로그인에 실패했습니다. 이메일과 비밀번호를 확인해주세요.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div
      style={{
        minHeight: '100vh',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        background: '#f0f2f5',
      }}
    >
      <Card style={{ width: 400, boxShadow: '0 4px 12px rgba(0,0,0,0.1)' }}>
        <div style={{ textAlign: 'center', marginBottom: 32 }}>
          <Title level={3} style={{ marginBottom: 4 }}>
            쇼핑몰 어드민
          </Title>
          <Text type="secondary">관리자 로그인</Text>
        </div>

        {error && (
          <Alert
            message={error}
            type="error"
            showIcon
            style={{ marginBottom: 16 }}
            closable
            onClose={() => setError(null)}
          />
        )}

        <Form
          name="admin-login"
          onFinish={handleSubmit}
          layout="vertical"
          size="large"
        >
          <Form.Item
            name="email"
            rules={[
              { required: true, message: '이메일을 입력해주세요.' },
              { type: 'email', message: '올바른 이메일 형식이 아닙니다.' },
            ]}
          >
            <Input
              prefix={<UserOutlined />}
              placeholder="이메일"
              autoComplete="email"
            />
          </Form.Item>

          <Form.Item
            name="password"
            rules={[{ required: true, message: '비밀번호를 입력해주세요.' }]}
          >
            <Input.Password
              prefix={<LockOutlined />}
              placeholder="비밀번호"
              autoComplete="current-password"
            />
          </Form.Item>

          <Form.Item style={{ marginBottom: 0 }}>
            <Button
              type="primary"
              htmlType="submit"
              loading={loading}
              block
            >
              로그인
            </Button>
          </Form.Item>
        </Form>
      </Card>
    </div>
  );
}
