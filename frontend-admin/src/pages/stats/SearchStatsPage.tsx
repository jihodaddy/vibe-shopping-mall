import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { Card, DatePicker, Table, Typography, Space, Spin, Tag } from 'antd';
import type { ColumnsType } from 'antd/es/table';
import dayjs from 'dayjs';
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
} from 'recharts';
import { adminStatsApi } from '../../api/adminStats';
import type { KeywordItem } from '../../api/adminStats';

const { RangePicker } = DatePicker;
const { Title } = Typography;

export default function SearchStatsPage() {
  const [dateRange, setDateRange] = useState<[string, string]>([
    dayjs().subtract(30, 'day').format('YYYY-MM-DD'),
    dayjs().format('YYYY-MM-DD'),
  ]);

  const { data, isLoading } = useQuery({
    queryKey: ['searchKeywords', dateRange[0], dateRange[1]],
    queryFn: () =>
      adminStatsApi.getSearchKeywords(dateRange[0], dateRange[1], 20).then((r) => r.data.data),
  });

  const handleDateChange = (_: unknown, dateStrings: [string, string]) => {
    if (dateStrings[0] && dateStrings[1]) {
      setDateRange(dateStrings);
    }
  };

  const columns: ColumnsType<KeywordItem> = [
    {
      title: '순위',
      dataIndex: 'rank',
      key: 'rank',
      width: 80,
      align: 'center',
      render: (rank: number) => {
        if (rank <= 3) return <Tag color="gold">{rank}</Tag>;
        return rank;
      },
    },
    { title: '키워드', dataIndex: 'keyword', key: 'keyword' },
    {
      title: '검색횟수',
      dataIndex: 'searchCount',
      key: 'searchCount',
      align: 'right',
      render: (v: number) => v.toLocaleString('ko-KR'),
    },
  ];

  const chartData = (data?.items ?? []).slice(0, 10);

  return (
    <div>
      <Title level={4} style={{ marginBottom: 24 }}>
        검색어 분석
      </Title>

      <Card style={{ marginBottom: 24 }}>
        <Space>
          <RangePicker
            defaultValue={[dayjs(dateRange[0]), dayjs(dateRange[1])]}
            onChange={handleDateChange}
            placeholder={['시작일', '종료일']}
          />
        </Space>
      </Card>

      {isLoading ? (
        <div style={{ textAlign: 'center', padding: 48 }}>
          <Spin size="large" />
        </div>
      ) : (
        <>
          {/* Bar Chart - Top 10 */}
          <Card title="인기 검색어 TOP 10" style={{ marginBottom: 24 }}>
            <ResponsiveContainer width="100%" height={350}>
              <BarChart
                data={chartData}
                layout="vertical"
                margin={{ top: 8, right: 32, left: 80, bottom: 0 }}
              >
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis type="number" />
                <YAxis type="category" dataKey="keyword" width={70} />
                <Tooltip formatter={(value) => [`${value}회`, '검색횟수']} />
                <Bar dataKey="searchCount" name="검색횟수" fill="#1677ff" radius={[0, 4, 4, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </Card>

          {/* Full Table */}
          <Card title="검색어 순위">
            <Table
              columns={columns}
              dataSource={data?.items ?? []}
              rowKey="rank"
              pagination={false}
              size="small"
            />
          </Card>
        </>
      )}
    </div>
  );
}
