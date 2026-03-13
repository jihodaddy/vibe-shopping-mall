import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
  Button,
  Table,
  Tag,
  Space,
  Form,
  Input,
  Select,
  Row,
  Col,
  message,
  Popconfirm,
  Typography,
  Modal,
} from 'antd';
import { PlusOutlined, DeleteOutlined, SendOutlined } from '@ant-design/icons';
import type { TableProps } from 'antd';
import { adminCouponApi } from '../../api/adminCoupon';
import type { AdminCouponResponse, CouponType } from '../../api/adminCoupon';
import CouponFormModal from './CouponFormModal';
import CouponIssueModal from './CouponIssueModal';

const { Title } = Typography;

const TYPE_LABELS: Record<CouponType, string> = {
  RATE: '비율 할인',
  FIXED: '정액 할인',
};

const TYPE_COLORS: Record<CouponType, string> = {
  RATE: 'blue',
  FIXED: 'green',
};

export default function CouponListPage() {
  const queryClient = useQueryClient();
  const [form] = Form.useForm();
  const [searchParams, setSearchParams] = useState<{
    keyword?: string;
    type?: CouponType;
    isActive?: boolean;
    page: number;
    size: number;
  }>({ page: 0, size: 20 });

  const [formModalOpen, setFormModalOpen] = useState(false);
  const [editingCouponId, setEditingCouponId] = useState<number | null>(null);
  const [issueModalOpen, setIssueModalOpen] = useState(false);
  const [issuingCouponId, setIssuingCouponId] = useState<number | null>(null);

  const { data, isLoading } = useQuery({
    queryKey: ['adminCoupons', searchParams],
    queryFn: () => adminCouponApi.getList(searchParams).then((r) => r.data.data),
  });

  const deactivateMutation = useMutation({
    mutationFn: (id: number) => adminCouponApi.deactivate(id),
    onSuccess: () => {
      message.success('쿠폰이 비활성화되었습니다.');
      queryClient.invalidateQueries({ queryKey: ['adminCoupons'] });
    },
    onError: () => {
      message.error('비활성화에 실패했습니다.');
    },
  });

  const handleSearch = (values: { keyword?: string; type?: CouponType; isActive?: boolean }) => {
    setSearchParams({ ...values, page: 0, size: 20 });
  };

  const handleReset = () => {
    form.resetFields();
    setSearchParams({ page: 0, size: 20 });
  };

  const openCreateModal = () => {
    setEditingCouponId(null);
    setFormModalOpen(true);
  };

  const openEditModal = (id: number) => {
    setEditingCouponId(id);
    setFormModalOpen(true);
  };

  const openIssueModal = (id: number) => {
    setIssuingCouponId(id);
    setIssueModalOpen(true);
  };

  const isExpired = (endAt: string) => new Date(endAt) < new Date();

  const columns: TableProps<AdminCouponResponse>['columns'] = [
    {
      title: '쿠폰코드',
      dataIndex: 'code',
      key: 'code',
      width: 140,
      render: (code: string) => <Tag>{code}</Tag>,
    },
    {
      title: '쿠폰명',
      dataIndex: 'name',
      key: 'name',
      render: (name: string, record: AdminCouponResponse) => (
        <span
          style={{ cursor: 'pointer', color: '#1677ff' }}
          onClick={() => openEditModal(record.id)}
        >
          {name}
        </span>
      ),
    },
    {
      title: '타입',
      dataIndex: 'type',
      key: 'type',
      width: 100,
      render: (type: CouponType) => (
        <Tag color={TYPE_COLORS[type]}>{TYPE_LABELS[type]}</Tag>
      ),
    },
    {
      title: '할인',
      key: 'discount',
      width: 120,
      render: (_: unknown, record: AdminCouponResponse) =>
        record.type === 'RATE'
          ? `${record.value}%`
          : `${record.value.toLocaleString()}원`,
    },
    {
      title: '최소주문',
      dataIndex: 'minOrderPrice',
      key: 'minOrderPrice',
      width: 120,
      render: (v: number) => `${v.toLocaleString()}원`,
    },
    {
      title: '수량',
      key: 'qty',
      width: 100,
      render: (_: unknown, record: AdminCouponResponse) =>
        record.totalQty != null
          ? `${record.usedQty}/${record.totalQty}`
          : '무제한',
    },
    {
      title: '발급수',
      dataIndex: 'issuedCount',
      key: 'issuedCount',
      width: 80,
    },
    {
      title: '기간',
      key: 'period',
      width: 200,
      render: (_: unknown, record: AdminCouponResponse) => (
        <span>
          {new Date(record.startAt).toLocaleDateString('ko-KR')} ~{' '}
          {new Date(record.endAt).toLocaleDateString('ko-KR')}
          {isExpired(record.endAt) && (
            <Tag color="red" style={{ marginLeft: 4 }}>
              만료
            </Tag>
          )}
        </span>
      ),
    },
    {
      title: '상태',
      dataIndex: 'active',
      key: 'active',
      width: 80,
      render: (active: boolean) => (
        <Tag color={active ? 'green' : 'default'}>{active ? '활성' : '비활성'}</Tag>
      ),
    },
    {
      title: '관리',
      key: 'actions',
      width: 180,
      render: (_: unknown, record: AdminCouponResponse) => (
        <Space>
          <Button
            size="small"
            icon={<SendOutlined />}
            onClick={() => openIssueModal(record.id)}
            disabled={!record.active || isExpired(record.endAt)}
          >
            발급
          </Button>
          <Popconfirm
            title="쿠폰을 비활성화하시겠습니까?"
            onConfirm={() => deactivateMutation.mutate(record.id)}
            okText="확인"
            cancelText="취소"
          >
            <Button size="small" danger icon={<DeleteOutlined />} disabled={!record.active}>
              비활성화
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 16 }}>
        <Title level={4} style={{ margin: 0 }}>
          쿠폰 관리
        </Title>
        <Button type="primary" icon={<PlusOutlined />} onClick={openCreateModal}>
          쿠폰 등록
        </Button>
      </div>

      <Form form={form} onFinish={handleSearch} layout="inline" style={{ marginBottom: 16 }}>
        <Row gutter={[8, 8]} style={{ width: '100%' }}>
          <Col>
            <Form.Item name="keyword">
              <Input placeholder="쿠폰명/코드 검색" allowClear style={{ width: 200 }} />
            </Form.Item>
          </Col>
          <Col>
            <Form.Item name="type">
              <Select
                placeholder="쿠폰 타입"
                allowClear
                style={{ width: 130 }}
                options={[
                  { value: 'RATE', label: '비율 할인' },
                  { value: 'FIXED', label: '정액 할인' },
                ]}
              />
            </Form.Item>
          </Col>
          <Col>
            <Form.Item name="isActive">
              <Select
                placeholder="상태"
                allowClear
                style={{ width: 100 }}
                options={[
                  { value: true, label: '활성' },
                  { value: false, label: '비활성' },
                ]}
              />
            </Form.Item>
          </Col>
          <Col>
            <Space>
              <Button type="primary" htmlType="submit">
                검색
              </Button>
              <Button onClick={handleReset}>초기화</Button>
            </Space>
          </Col>
        </Row>
      </Form>

      <Table<AdminCouponResponse>
        rowKey="id"
        columns={columns}
        dataSource={data?.content ?? []}
        loading={isLoading}
        pagination={{
          current: (searchParams.page ?? 0) + 1,
          pageSize: searchParams.size ?? 20,
          total: data?.totalElements ?? 0,
          showSizeChanger: true,
          showTotal: (total) => `총 ${total}개`,
          onChange: (page, pageSize) =>
            setSearchParams((prev) => ({ ...prev, page: page - 1, size: pageSize })),
        }}
      />

      <CouponFormModal
        open={formModalOpen}
        couponId={editingCouponId}
        onClose={() => setFormModalOpen(false)}
      />

      <CouponIssueModal
        open={issueModalOpen}
        couponId={issuingCouponId}
        onClose={() => setIssueModalOpen(false)}
      />
    </div>
  );
}
