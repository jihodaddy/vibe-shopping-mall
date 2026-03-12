import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
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
  Image,
  message,
  Popconfirm,
  Typography,
} from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';
import type { TableProps } from 'antd';
import { adminProductApi } from '../../api/adminProduct';
import type { AdminProductResponse, ProductStatus } from '../../api/adminProduct';

const { Title } = Typography;

const STATUS_LABELS: Record<ProductStatus, string> = {
  ON_SALE: '판매중',
  SOLD_OUT: '품절',
  HIDDEN: '숨김',
  DELETED: '삭제됨',
};

const STATUS_COLORS: Record<ProductStatus, string> = {
  ON_SALE: 'green',
  SOLD_OUT: 'orange',
  HIDDEN: 'red',
  DELETED: 'gray',
};

export default function ProductListPage() {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const [form] = Form.useForm();
  const [selectedRowKeys, setSelectedRowKeys] = useState<number[]>([]);
  const [searchParams, setSearchParams] = useState<{
    keyword?: string;
    categoryId?: number;
    status?: string;
    page: number;
    size: number;
  }>({ page: 0, size: 20 });

  const { data, isLoading } = useQuery({
    queryKey: ['adminProducts', searchParams],
    queryFn: () => adminProductApi.getList(searchParams).then((r) => r.data.data),
  });

  const { data: categoriesData } = useQuery({
    queryKey: ['adminCategories'],
    queryFn: () => adminProductApi.getCategories().then((r) => r.data.data),
  });

  const deleteMutation = useMutation({
    mutationFn: (id: number) => adminProductApi.delete(id),
    onSuccess: () => {
      message.success('상품이 삭제되었습니다.');
      queryClient.invalidateQueries({ queryKey: ['adminProducts'] });
    },
    onError: () => {
      message.error('삭제에 실패했습니다.');
    },
  });

  const bulkStatusMutation = useMutation({
    mutationFn: ({ ids, status }: { ids: number[]; status: ProductStatus }) =>
      adminProductApi.bulkUpdateStatus(ids, status),
    onSuccess: () => {
      message.success('상태가 변경되었습니다.');
      setSelectedRowKeys([]);
      queryClient.invalidateQueries({ queryKey: ['adminProducts'] });
    },
    onError: () => {
      message.error('상태 변경에 실패했습니다.');
    },
  });

  const flattenCategories = (
    cats: typeof categoriesData,
    prefix = ''
  ): { value: number; label: string }[] => {
    if (!cats) return [];
    return cats.flatMap((c) => [
      { value: c.id, label: prefix + c.name },
      ...flattenCategories(c.children, prefix + c.name + ' > '),
    ]);
  };

  const categoryOptions = flattenCategories(categoriesData ?? []);

  const handleSearch = (values: {
    keyword?: string;
    categoryId?: number;
    status?: string;
  }) => {
    setSearchParams({ ...values, page: 0, size: 20 });
  };

  const handleReset = () => {
    form.resetFields();
    setSearchParams({ page: 0, size: 20 });
  };

  const columns: TableProps<AdminProductResponse>['columns'] = [
    {
      title: '썸네일',
      dataIndex: 'imageUrls',
      key: 'thumbnail',
      width: 80,
      render: (imageUrls: Record<string, boolean>) => {
        const urls = Object.keys(imageUrls ?? {});
        return urls.length > 0 ? (
          <Image src={urls[0]} width={60} height={60} style={{ objectFit: 'cover' }} />
        ) : (
          <div
            style={{
              width: 60,
              height: 60,
              background: '#f0f0f0',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              fontSize: 12,
              color: '#999',
            }}
          >
            없음
          </div>
        );
      },
    },
    {
      title: '상품명',
      dataIndex: 'name',
      key: 'name',
      render: (name: string, record: AdminProductResponse) => (
        <span
          style={{ cursor: 'pointer', color: '#1677ff' }}
          onClick={() => navigate(`/products/${record.id}`)}
        >
          {name}
        </span>
      ),
    },
    {
      title: '카테고리',
      dataIndex: 'categoryName',
      key: 'categoryName',
    },
    {
      title: '가격',
      dataIndex: 'price',
      key: 'price',
      render: (price: number) => `${price.toLocaleString()}원`,
    },
    {
      title: '재고',
      dataIndex: 'stockQty',
      key: 'stockQty',
    },
    {
      title: '상태',
      dataIndex: 'status',
      key: 'status',
      render: (status: ProductStatus) => (
        <Tag color={STATUS_COLORS[status]}>{STATUS_LABELS[status] ?? status}</Tag>
      ),
    },
    {
      title: '등록일',
      dataIndex: 'createdAt',
      key: 'createdAt',
      render: (date: string) => new Date(date).toLocaleDateString('ko-KR'),
    },
    {
      title: '관리',
      key: 'actions',
      render: (_: unknown, record: AdminProductResponse) => (
        <Space>
          <Button
            size="small"
            icon={<EditOutlined />}
            onClick={() => navigate(`/products/${record.id}`)}
          >
            수정
          </Button>
          <Popconfirm
            title="상품을 삭제하시겠습니까?"
            onConfirm={() => deleteMutation.mutate(record.id)}
            okText="삭제"
            cancelText="취소"
          >
            <Button size="small" danger icon={<DeleteOutlined />}>
              삭제
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
          상품 관리
        </Title>
        <Button type="primary" icon={<PlusOutlined />} onClick={() => navigate('/products/new')}>
          상품 등록
        </Button>
      </div>

      <Form form={form} onFinish={handleSearch} layout="inline" style={{ marginBottom: 16 }}>
        <Row gutter={[8, 8]} style={{ width: '100%' }}>
          <Col>
            <Form.Item name="keyword">
              <Input placeholder="상품명 검색" allowClear style={{ width: 200 }} />
            </Form.Item>
          </Col>
          <Col>
            <Form.Item name="categoryId">
              <Select
                placeholder="카테고리"
                allowClear
                style={{ width: 160 }}
                options={categoryOptions}
              />
            </Form.Item>
          </Col>
          <Col>
            <Form.Item name="status">
              <Select
                placeholder="상태"
                allowClear
                style={{ width: 120 }}
                options={[
                  { value: 'ON_SALE', label: '판매중' },
                  { value: 'SOLD_OUT', label: '품절' },
                  { value: 'HIDDEN', label: '숨김' },
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

      {selectedRowKeys.length > 0 && (
        <div style={{ marginBottom: 8 }}>
          <Space>
            <span>{selectedRowKeys.length}개 선택됨</span>
            <Button
              size="small"
              onClick={() =>
                bulkStatusMutation.mutate({ ids: selectedRowKeys, status: 'ON_SALE' })
              }
            >
              일괄 판매중
            </Button>
            <Button
              size="small"
              onClick={() =>
                bulkStatusMutation.mutate({ ids: selectedRowKeys, status: 'HIDDEN' })
              }
            >
              일괄 숨김
            </Button>
          </Space>
        </div>
      )}

      <Table<AdminProductResponse>
        rowKey="id"
        columns={columns}
        dataSource={data?.content ?? []}
        loading={isLoading}
        rowSelection={{
          selectedRowKeys,
          onChange: (keys) => setSelectedRowKeys(keys as number[]),
        }}
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
    </div>
  );
}
