import { Card, Col, Row, Statistic, Typography } from 'antd';
import {
  ShoppingCartOutlined,
  DollarOutlined,
  UserAddOutlined,
  ExclamationCircleOutlined,
} from '@ant-design/icons';
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  Legend,
} from 'recharts';

const { Title } = Typography;

// Placeholder summary data (real stats API comes in Task 10)
const summaryData = [
  {
    title: '오늘 매출',
    value: 1250000,
    prefix: '₩',
    icon: <DollarOutlined style={{ color: '#1677ff' }} />,
    color: '#e6f4ff',
  },
  {
    title: '주문수',
    value: 48,
    suffix: '건',
    icon: <ShoppingCartOutlined style={{ color: '#52c41a' }} />,
    color: '#f6ffed',
  },
  {
    title: '신규회원',
    value: 12,
    suffix: '명',
    icon: <UserAddOutlined style={{ color: '#722ed1' }} />,
    color: '#f9f0ff',
  },
  {
    title: '미처리 주문',
    value: 7,
    suffix: '건',
    icon: <ExclamationCircleOutlined style={{ color: '#fa8c16' }} />,
    color: '#fff7e6',
  },
];

// Placeholder weekly sales data
const weeklyData = [
  { day: '월', sales: 820000, orders: 32 },
  { day: '화', sales: 1100000, orders: 44 },
  { day: '수', sales: 950000, orders: 38 },
  { day: '목', sales: 1350000, orders: 52 },
  { day: '금', sales: 1800000, orders: 68 },
  { day: '토', sales: 2200000, orders: 85 },
  { day: '일', sales: 1600000, orders: 61 },
];

const formatKRW = (value: number) => `₩${value.toLocaleString('ko-KR')}`;

export default function DashboardPage() {
  return (
    <div>
      <Title level={4} style={{ marginBottom: 24 }}>
        대시보드
      </Title>

      {/* Summary Cards */}
      <Row gutter={[16, 16]} style={{ marginBottom: 32 }}>
        {summaryData.map((item) => (
          <Col xs={24} sm={12} lg={6} key={item.title}>
            <Card style={{ background: item.color, border: 'none' }}>
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <Statistic
                  title={item.title}
                  value={item.value}
                  prefix={item.prefix}
                  suffix={item.suffix}
                  valueStyle={{ fontSize: 24, fontWeight: 600 }}
                />
                <div style={{ fontSize: 36 }}>{item.icon}</div>
              </div>
            </Card>
          </Col>
        ))}
      </Row>

      {/* Weekly Sales Chart */}
      <Card title="주간 매출 현황" style={{ marginBottom: 24 }}>
        <ResponsiveContainer width="100%" height={300}>
          <BarChart data={weeklyData} margin={{ top: 8, right: 16, left: 16, bottom: 0 }}>
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis dataKey="day" />
            <YAxis yAxisId="left" tickFormatter={(v) => `${(v / 10000).toFixed(0)}만`} />
            <YAxis yAxisId="right" orientation="right" unit="건" />
            <Tooltip
              formatter={(value, name) => {
                if (name === '매출') return formatKRW(value as number);
                return `${value}건`;
              }}
            />
            <Legend />
            <Bar yAxisId="left" dataKey="sales" name="매출" fill="#1677ff" radius={[4, 4, 0, 0]} />
            <Bar yAxisId="right" dataKey="orders" name="주문수" fill="#52c41a" radius={[4, 4, 0, 0]} />
          </BarChart>
        </ResponsiveContainer>
      </Card>
    </div>
  );
}
