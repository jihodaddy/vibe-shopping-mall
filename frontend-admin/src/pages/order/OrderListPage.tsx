import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
  Table,
  Button,
  Input,
  DatePicker,
  Tag,
  Space,
  Tabs,
  message,
  Upload,
  Typography,
  Row,
  Col,
  Card,
  Select,
} from 'antd';
import { UploadOutlined, SearchOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import type { RangePickerProps } from 'antd/es/date-picker';
import dayjs from 'dayjs';
import { adminOrderApi } from '../../api/adminOrder';
import type { AdminOrderResponse, OrderStatus } from '../../api/adminOrder';

const { RangePicker } = DatePicker;
const { Title } = Typography;

const STATUS_COLORS: Record<string, string> = {
  PENDING: 'default',
  PAID: 'blue',
  PREPARING: 'cyan',
  SHIPPED: 'geekblue',
  DELIVERED: 'green',
  CANCELLED: 'red',
  REFUND_REQUESTED: 'orange',
  REFUNDED: 'purple',
  EXCHANGED: 'gold',
};

const STATUS_LABELS: Record<string, string> = {
  PENDING: '대기',
  PAID: '결제완료',
  PREPARING: '배송준비',
  SHIPPED: '배송중',
  DELIVERED: '배송완료',
  CANCELLED: '취소',
  REFUND_REQUESTED: '반품요청',
  REFUNDED: '반품완료',
  EXCHANGED: '교환',
};

const TAB_STATUSES: Record<string, string | undefined> = {
  all: undefined,
  PAID: 'PAID',
  PREPARING: 'PREPARING',
  SHIPPED: 'SHIPPED',
  DELIVERED: 'DELIVERED',
  CANCELLED: 'CANCELLED',
  refund: 'REFUND_REQUESTED',
};

export default function OrderListPage() {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const [page, setPage] = useState(1);
  const [pageSize] = useState(20);
  const [keyword, setKeyword] = useState('');
  const [searchKeyword, setSearchKeyword] = useState('');
  const [dateRange, setDateRange] = useState<[string, string] | null>(null);
  const [activeTab, setActiveTab] = useState('all');
  const [selectedRowKeys, setSelectedRowKeys] = useState<React.Key[]>([]);
  const [bulkStatus, setBulkStatus] = useState<string>('PREPARING');

  const getStatusParam = () => TAB_STATUSES[activeTab] || undefined;

  const { data, isLoading } = useQuery({
    queryKey: ['adminOrders', page, pageSize, searchKeyword, dateRange, activeTab],
    queryFn: () =>
      adminOrderApi.getList({
        keyword: searchKeyword || undefined,
        status: getStatusParam(),
        startDate: dateRange?.[0],
        endDate: dateRange?.[1],
        page: page - 1,
        size: pageSize,
      }),
  });

  const orders = data?.data?.data?.content ?? [];
  const totalElements = data?.data?.data?.totalElements ?? 0;

  const bulkStatusMutation = useMutation({
    mutationFn: async (status: string) => {
      await Promise.all(
        (selectedRowKeys as number[]).map((id) => adminOrderApi.updateStatus(id, status))
      );
    },
    onSuccess: () => {
      message.success('상태가 변경되었습니다.');
      setSelectedRowKeys([]);
      queryClient.invalidateQueries({ queryKey: ['adminOrders'] });
    },
    onError: () => {
      message.error('상태 변경에 실패했습니다.');
    },
  });

  const handleSearch = () => {
    setSearchKeyword(keyword);
    setPage(1);
  };

  const handleDateChange: RangePickerProps['onChange'] = (_, dateStrings) => {
    if (dateStrings[0] && dateStrings[1]) {
      setDateRange([dateStrings[0], dateStrings[1]]);
    } else {
      setDateRange(null);
    }
    setPage(1);
  };

  const handleTabChange = (key: string) => {
    setActiveTab(key);
    setPage(1);
    setSelectedRowKeys([]);
  };

  const columns: ColumnsType<AdminOrderResponse> = [
    {
      title: '주문번호',
      dataIndex: 'orderNumber',
      key: 'orderNumber',
      width: 160,
    },
    {
      title: '수령인',
      dataIndex: 'receiverName',
      key: 'receiverName',
      width: 100,
    },
    {
      title: '상품',
      key: 'product',
      render: (_, record) => {
        const first = record.items?.[0];
        if (!first) return '-';
        const extra = record.items.length > 1 ? ` 외 ${record.items.length - 1}건` : '';
        return `${first.productName}${extra}`;
      },
    },
    {
      title: '금액',
      dataIndex: 'finalPrice',
      key: 'finalPrice',
      width: 120,
      render: (v: number) => `${v.toLocaleString()}원`,
    },
    {
      title: '상태',
      dataIndex: 'status',
      key: 'status',
      width: 110,
      render: (status: OrderStatus) => (
        <Tag color={STATUS_COLORS[status]}>{STATUS_LABELS[status] ?? status}</Tag>
      ),
    },
    {
      title: '주문일시',
      dataIndex: 'createdAt',
      key: 'createdAt',
      width: 160,
      render: (v: string) => dayjs(v).format('YYYY-MM-DD HH:mm'),
    },
    {
      title: '',
      key: 'action',
      width: 80,
      render: (_, record) => (
        <Button size="small" onClick={() => navigate(`/orders/${record.id}`)}>
          상세보기
        </Button>
      ),
    },
  ];

  const tabItems = [
    { key: 'all', label: '전체' },
    { key: 'PAID', label: '결제완료' },
    { key: 'PREPARING', label: '배송준비' },
    { key: 'SHIPPED', label: '배송중' },
    { key: 'DELIVERED', label: '배송완료' },
    { key: 'CANCELLED', label: '취소' },
    { key: 'refund', label: '반품' },
  ];

  return (
    <div style={{ padding: 24 }}>
      <Title level={3}>주문 관리</Title>

      <Card style={{ marginBottom: 16 }}>
        <Row gutter={16} align="middle">
          <Col>
            <RangePicker onChange={handleDateChange} placeholder={['시작일', '종료일']} />
          </Col>
          <Col flex={1}>
            <Input
              placeholder="주문번호 / 수령인 검색"
              value={keyword}
              onChange={(e) => setKeyword(e.target.value)}
              onPressEnter={handleSearch}
              suffix={<SearchOutlined />}
            />
          </Col>
          <Col>
            <Button type="primary" onClick={handleSearch}>
              검색
            </Button>
          </Col>
          <Col>
            <Upload
              accept=".xlsx"
              showUploadList={false}
              beforeUpload={(file) => {
                adminOrderApi
                  .bulkUploadShipping(file)
                  .then(() => {
                    message.success('송장 일괄 업로드가 완료되었습니다.');
                    queryClient.invalidateQueries({ queryKey: ['adminOrders'] });
                  })
                  .catch(() => {
                    message.error('업로드에 실패했습니다.');
                  });
                return false;
              }}
            >
              <Button icon={<UploadOutlined />}>Excel 송장 업로드</Button>
            </Upload>
          </Col>
        </Row>
      </Card>

      {selectedRowKeys.length > 0 && (
        <Card style={{ marginBottom: 16 }}>
          <Space>
            <span>{selectedRowKeys.length}개 선택됨</span>
            <Select
              value={bulkStatus}
              onChange={(v) => setBulkStatus(v)}
              style={{ width: 140 }}
              options={[
                { label: '배송준비', value: 'PREPARING' },
                { label: '배송중', value: 'SHIPPED' },
                { label: '배송완료', value: 'DELIVERED' },
              ]}
            />
            <Button
              type="primary"
              loading={bulkStatusMutation.isPending}
              onClick={() => bulkStatusMutation.mutate(bulkStatus)}
            >
              상태 일괄 변경
            </Button>
            <Button onClick={() => setSelectedRowKeys([])}>선택 해제</Button>
          </Space>
        </Card>
      )}

      <Tabs activeKey={activeTab} onChange={handleTabChange} items={tabItems} />

      <Table
        rowKey="id"
        columns={columns}
        dataSource={orders}
        loading={isLoading}
        rowSelection={{
          selectedRowKeys,
          onChange: (keys) => setSelectedRowKeys(keys),
        }}
        pagination={{
          current: page,
          pageSize,
          total: totalElements,
          onChange: (p) => setPage(p),
          showSizeChanger: false,
          showTotal: (total) => `총 ${total}건`,
        }}
      />
    </div>
  );
}
