import { useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
  Button,
  Tag,
  Descriptions,
  Table,
  Input,
  Select,
  Space,
  Typography,
  message,
  Checkbox,
  Card,
  Divider,
  Spin,
} from 'antd';
import { ArrowLeftOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import dayjs from 'dayjs';
import { adminOrderApi } from '../../api/adminOrder';
import type { AdminOrderItemResponse } from '../../api/adminOrder';

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

const NEXT_STATUSES: Record<string, { label: string; value: string }[]> = {
  PAID: [
    { label: '배송준비', value: 'PREPARING' },
    { label: '취소', value: 'CANCELLED' },
  ],
  PREPARING: [
    { label: '배송중', value: 'SHIPPED' },
    { label: '취소', value: 'CANCELLED' },
  ],
  SHIPPED: [{ label: '배송완료', value: 'DELIVERED' }],
};

export default function OrderDetailPage() {
  const navigate = useNavigate();
  const { id } = useParams<{ id: string }>();
  const orderId = Number(id);
  const queryClient = useQueryClient();

  const [nextStatus, setNextStatus] = useState<string>('');
  const [courier, setCourier] = useState('');
  const [trackingNumber, setTrackingNumber] = useState('');
  const [selectedItemIds, setSelectedItemIds] = useState<number[]>([]);

  const { data, isLoading } = useQuery({
    queryKey: ['adminOrder', orderId],
    queryFn: () => adminOrderApi.getDetail(orderId),
    enabled: !!orderId,
  });

  const order = data?.data?.data;

  const statusMutation = useMutation({
    mutationFn: (status: string) => adminOrderApi.updateStatus(orderId, status),
    onSuccess: () => {
      message.success('상태가 변경되었습니다.');
      setNextStatus('');
      queryClient.invalidateQueries({ queryKey: ['adminOrder', orderId] });
    },
    onError: () => {
      message.error('상태 변경에 실패했습니다.');
    },
  });

  const shippingMutation = useMutation({
    mutationFn: () => adminOrderApi.updateShipping(orderId, { courier, trackingNumber }),
    onSuccess: () => {
      message.success('배송 처리가 완료되었습니다.');
      setCourier('');
      setTrackingNumber('');
      queryClient.invalidateQueries({ queryKey: ['adminOrder', orderId] });
    },
    onError: () => {
      message.error('배송 처리에 실패했습니다.');
    },
  });

  const refundMutation = useMutation({
    mutationFn: () => adminOrderApi.processRefund(orderId, selectedItemIds),
    onSuccess: () => {
      message.success('반품 처리가 완료되었습니다.');
      setSelectedItemIds([]);
      queryClient.invalidateQueries({ queryKey: ['adminOrder', orderId] });
    },
    onError: () => {
      message.error('반품 처리에 실패했습니다.');
    },
  });

  const itemColumns: ColumnsType<AdminOrderItemResponse> = [
    {
      title: '상품명',
      dataIndex: 'productName',
      key: 'productName',
    },
    {
      title: '옵션',
      dataIndex: 'optionInfo',
      key: 'optionInfo',
      render: (v: string | undefined) => v ?? '-',
    },
    {
      title: '수량',
      dataIndex: 'qty',
      key: 'qty',
      width: 80,
    },
    {
      title: '금액',
      dataIndex: 'price',
      key: 'price',
      width: 120,
      render: (v: number) => `${v.toLocaleString()}원`,
    },
    {
      title: '상태',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (v: string) => <Tag>{v}</Tag>,
    },
  ];

  if (isLoading) {
    return (
      <div style={{ padding: 24, textAlign: 'center' }}>
        <Spin size="large" />
      </div>
    );
  }

  if (!order) {
    return (
      <div style={{ padding: 24 }}>
        <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/orders')}>
          목록으로
        </Button>
        <p style={{ marginTop: 16 }}>주문 정보를 불러올 수 없습니다.</p>
      </div>
    );
  }

  const nextOptions = NEXT_STATUSES[order.status as string] ?? [];

  return (
    <div style={{ padding: 24 }}>
      <Space style={{ marginBottom: 16 }}>
        <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/orders')}>
          목록으로
        </Button>
        <Title level={4} style={{ margin: 0 }}>
          주문 상세 - {order.orderNumber}
        </Title>
        <Tag color={STATUS_COLORS[order.status]}>{STATUS_LABELS[order.status] ?? order.status}</Tag>
      </Space>

      {/* 주문 정보 */}
      <Card title="주문 정보" style={{ marginBottom: 16 }}>
        <Descriptions bordered column={2}>
          <Descriptions.Item label="주문번호">{order.orderNumber}</Descriptions.Item>
          <Descriptions.Item label="주문일시">
            {dayjs(order.createdAt).format('YYYY-MM-DD HH:mm:ss')}
          </Descriptions.Item>
          <Descriptions.Item label="수령인">{order.receiverName}</Descriptions.Item>
          <Descriptions.Item label="연락처">{order.receiverPhone}</Descriptions.Item>
          <Descriptions.Item label="주소" span={2}>
            {order.receiverAddress ?? '-'}
          </Descriptions.Item>
          <Descriptions.Item label="배송 메모" span={2}>
            {order.deliveryMemo ?? '-'}
          </Descriptions.Item>
        </Descriptions>
      </Card>

      {/* 결제 정보 */}
      <Card title="결제 정보" style={{ marginBottom: 16 }}>
        <Descriptions bordered style={{ marginBottom: 16 }}>
          <Descriptions.Item label="최종 결제금액">
            <strong>{order.finalPrice.toLocaleString()}원</strong>
          </Descriptions.Item>
        </Descriptions>
        <Table
          rowKey="id"
          columns={itemColumns}
          dataSource={order.items}
          pagination={false}
          size="small"
        />
      </Card>

      {/* 상태 변경 */}
      {nextOptions.length > 0 && (
        <Card title="상태 변경" style={{ marginBottom: 16 }}>
          <Space>
            <Select
              placeholder="변경할 상태를 선택하세요"
              style={{ width: 200 }}
              value={nextStatus || undefined}
              onChange={(v) => setNextStatus(v)}
              options={nextOptions}
            />
            <Button
              type="primary"
              disabled={!nextStatus}
              loading={statusMutation.isPending}
              onClick={() => {
                if (nextStatus) statusMutation.mutate(nextStatus);
              }}
            >
              변경
            </Button>
          </Space>
        </Card>
      )}

      {/* 배송 처리 (PREPARING 상태일 때만) */}
      {order.status === 'PREPARING' && (
        <Card title="배송 처리" style={{ marginBottom: 16 }}>
          <Space direction="vertical" style={{ width: '100%' }}>
            <Space>
              <Input
                placeholder="택배사"
                value={courier}
                onChange={(e) => setCourier(e.target.value)}
                style={{ width: 200 }}
              />
              <Input
                placeholder="송장번호"
                value={trackingNumber}
                onChange={(e) => setTrackingNumber(e.target.value)}
                style={{ width: 200 }}
              />
              <Button
                type="primary"
                loading={shippingMutation.isPending}
                disabled={!courier || !trackingNumber}
                onClick={() => shippingMutation.mutate()}
              >
                배송처리
              </Button>
            </Space>
          </Space>
        </Card>
      )}

      {/* 반품 처리 (REFUND_REQUESTED 상태일 때만) */}
      {order.status === 'REFUND_REQUESTED' && (
        <Card title="반품 처리" style={{ marginBottom: 16 }}>
          <Space direction="vertical" style={{ width: '100%' }}>
            {order.items.map((item) => (
              <Checkbox
                key={item.id}
                checked={selectedItemIds.includes(item.id)}
                onChange={(e) => {
                  if (e.target.checked) {
                    setSelectedItemIds((prev) => [...prev, item.id]);
                  } else {
                    setSelectedItemIds((prev) => prev.filter((i) => i !== item.id));
                  }
                }}
              >
                {item.productName}
                {item.optionInfo ? ` (${item.optionInfo})` : ''} x {item.qty}
              </Checkbox>
            ))}
            <Divider />
            <Button
              type="primary"
              danger
              loading={refundMutation.isPending}
              disabled={selectedItemIds.length === 0}
              onClick={() => refundMutation.mutate()}
            >
              반품 처리
            </Button>
          </Space>
        </Card>
      )}
    </div>
  );
}
