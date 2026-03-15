import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import {
  Card,
  Col,
  Row,
  Radio,
  DatePicker,
  Table,
  Button,
  Statistic,
  Typography,
  Space,
  Spin,
} from 'antd';
import {
  DollarOutlined,
  ShoppingCartOutlined,
  UserAddOutlined,
  RollbackOutlined,
  DownloadOutlined,
} from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import dayjs from 'dayjs';
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  Legend,
} from 'recharts';
import { adminStatsApi } from '../../api/adminStats';
import type { SalesStatsItem, StatsPeriod } from '../../api/adminStats';

const { RangePicker } = DatePicker;
const { Title } = Typography;

const formatKRW = (value: number) => `\u20A9${value.toLocaleString('ko-KR')}`;

export default function SalesStatsPage() {
  const [dateRange, setDateRange] = useState<[string, string]>([
    dayjs().subtract(30, 'day').format('YYYY-MM-DD'),
    dayjs().format('YYYY-MM-DD'),
  ]);
  const [period, setPeriod] = useState<StatsPeriod>('DAILY');

  const { data: statsData, isLoading: statsLoading } = useQuery({
    queryKey: ['salesStats', dateRange[0], dateRange[1], period],
    queryFn: () => adminStatsApi.getSalesStats(dateRange[0], dateRange[1], period).then((r) => r.data.data),
  });

  const { data: summaryData, isLoading: summaryLoading } = useQuery({
    queryKey: ['statsSummary', dateRange[0], dateRange[1]],
    queryFn: () => adminStatsApi.getSummary(dateRange[0], dateRange[1]).then((r) => r.data.data),
  });

  const handleDateChange = (_: unknown, dateStrings: [string, string]) => {
    if (dateStrings[0] && dateStrings[1]) {
      setDateRange(dateStrings);
    }
  };

  const handleExportCsv = () => {
    if (!statsData?.items?.length) return;

    const headers = ['기간', '주문수', '매출액', '환불건수', '환불액', '신규회원'];
    const rows = statsData.items.map((item) => [
      item.label,
      item.orderCount,
      item.salesAmount,
      item.refundCount,
      item.refundAmount,
      item.newMemberCount,
    ]);

    const bom = '\uFEFF';
    const csv = bom + [headers, ...rows].map((row) => row.join(',')).join('\n');
    const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `sales_stats_${dateRange[0]}_${dateRange[1]}.csv`;
    link.click();
    URL.revokeObjectURL(url);
  };

  const columns: ColumnsType<SalesStatsItem> = [
    { title: '기간', dataIndex: 'label', key: 'label' },
    { title: '주문수', dataIndex: 'orderCount', key: 'orderCount', align: 'right' },
    {
      title: '매출액',
      dataIndex: 'salesAmount',
      key: 'salesAmount',
      align: 'right',
      render: (v: number) => formatKRW(v),
    },
    { title: '환불건수', dataIndex: 'refundCount', key: 'refundCount', align: 'right' },
    {
      title: '환불액',
      dataIndex: 'refundAmount',
      key: 'refundAmount',
      align: 'right',
      render: (v: number) => formatKRW(v),
    },
    { title: '신규회원', dataIndex: 'newMemberCount', key: 'newMemberCount', align: 'right' },
  ];

  const isLoading = statsLoading || summaryLoading;

  return (
    <div>
      <Title level={4} style={{ marginBottom: 24 }}>
        매출 통계
      </Title>

      {/* Filters */}
      <Card style={{ marginBottom: 24 }}>
        <Space wrap size="middle">
          <RangePicker
            defaultValue={[dayjs(dateRange[0]), dayjs(dateRange[1])]}
            onChange={handleDateChange}
            placeholder={['시작일', '종료일']}
          />
          <Radio.Group value={period} onChange={(e) => setPeriod(e.target.value)}>
            <Radio.Button value="DAILY">일별</Radio.Button>
            <Radio.Button value="WEEKLY">주별</Radio.Button>
            <Radio.Button value="MONTHLY">월별</Radio.Button>
          </Radio.Group>
          <Button icon={<DownloadOutlined />} onClick={handleExportCsv}>
            CSV 다운로드
          </Button>
        </Space>
      </Card>

      {/* Summary Cards */}
      {isLoading ? (
        <div style={{ textAlign: 'center', padding: 48 }}>
          <Spin size="large" />
        </div>
      ) : (
        <>
          <Row gutter={[16, 16]} style={{ marginBottom: 24 }}>
            <Col xs={24} sm={12} lg={6}>
              <Card style={{ background: '#e6f4ff', border: 'none' }}>
                <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                  <Statistic
                    title="총 매출"
                    value={summaryData?.totalSalesAmount ?? 0}
                    prefix={<span style={{ fontSize: 16 }}>{'\u20A9'}</span>}
                    valueStyle={{ fontSize: 24, fontWeight: 600 }}
                  />
                  <DollarOutlined style={{ fontSize: 36, color: '#1677ff' }} />
                </div>
              </Card>
            </Col>
            <Col xs={24} sm={12} lg={6}>
              <Card style={{ background: '#f6ffed', border: 'none' }}>
                <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                  <Statistic
                    title="주문수"
                    value={summaryData?.totalOrderCount ?? 0}
                    suffix="건"
                    valueStyle={{ fontSize: 24, fontWeight: 600 }}
                  />
                  <ShoppingCartOutlined style={{ fontSize: 36, color: '#52c41a' }} />
                </div>
              </Card>
            </Col>
            <Col xs={24} sm={12} lg={6}>
              <Card style={{ background: '#fff2e8', border: 'none' }}>
                <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                  <Statistic
                    title="환불액"
                    value={summaryData?.totalRefundAmount ?? 0}
                    prefix={<span style={{ fontSize: 16 }}>{'\u20A9'}</span>}
                    valueStyle={{ fontSize: 24, fontWeight: 600 }}
                  />
                  <RollbackOutlined style={{ fontSize: 36, color: '#fa541c' }} />
                </div>
              </Card>
            </Col>
            <Col xs={24} sm={12} lg={6}>
              <Card style={{ background: '#f9f0ff', border: 'none' }}>
                <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                  <Statistic
                    title="신규회원"
                    value={summaryData?.totalNewMemberCount ?? 0}
                    suffix="명"
                    valueStyle={{ fontSize: 24, fontWeight: 600 }}
                  />
                  <UserAddOutlined style={{ fontSize: 36, color: '#722ed1' }} />
                </div>
              </Card>
            </Col>
          </Row>

          {/* Line Chart */}
          <Card title="매출 추이" style={{ marginBottom: 24 }}>
            <ResponsiveContainer width="100%" height={350}>
              <LineChart data={statsData?.items ?? []} margin={{ top: 8, right: 16, left: 16, bottom: 0 }}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="label" />
                <YAxis yAxisId="left" tickFormatter={(v) => `${(v / 10000).toFixed(0)}만`} />
                <YAxis yAxisId="right" orientation="right" unit="건" />
                <Tooltip
                  formatter={(value, name) => {
                    if (name === '매출액') return formatKRW(value as number);
                    if (name === '환불액') return formatKRW(value as number);
                    return `${value}`;
                  }}
                />
                <Legend />
                <Line yAxisId="left" type="monotone" dataKey="salesAmount" name="매출액" stroke="#1677ff" strokeWidth={2} dot={{ r: 3 }} />
                <Line yAxisId="right" type="monotone" dataKey="orderCount" name="주문수" stroke="#52c41a" strokeWidth={2} dot={{ r: 3 }} />
                <Line yAxisId="left" type="monotone" dataKey="refundAmount" name="환불액" stroke="#fa541c" strokeWidth={2} dot={{ r: 3 }} strokeDasharray="5 5" />
              </LineChart>
            </ResponsiveContainer>
          </Card>

          {/* Data Table */}
          <Card title="상세 데이터">
            <Table
              columns={columns}
              dataSource={statsData?.items ?? []}
              rowKey="label"
              pagination={false}
              size="small"
            />
          </Card>
        </>
      )}
    </div>
  );
}
