import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import {
  Table,
  Tag,
  Space,
  Form,
  Input,
  Select,
  Row,
  Col,
  Button,
  Tabs,
  Typography,
} from 'antd';
import { useNavigate } from 'react-router-dom';
import type { TableProps } from 'antd';
import { adminCsApi } from '../../api/adminCs';
import type { AdminInquiryResponse, InquiryStatus, InquiryType } from '../../api/adminCs';

const { Title } = Typography;

const STATUS_LABELS: Record<InquiryStatus, string> = {
  PENDING: '미답변',
  ANSWERED: '답변완료',
  CLOSED: '종료',
};

const STATUS_COLORS: Record<InquiryStatus, string> = {
  PENDING: 'orange',
  ANSWERED: 'green',
  CLOSED: 'default',
};

const TYPE_LABELS: Record<InquiryType, string> = {
  PRODUCT: '상품',
  ORDER: '주문',
  DELIVERY: '배송',
  CANCEL: '취소/반품',
  ETC: '기타',
};

export default function InquiryListPage() {
  const navigate = useNavigate();
  const [form] = Form.useForm();
  const [searchParams, setSearchParams] = useState<{
    status?: InquiryStatus;
    type?: InquiryType;
    keyword?: string;
    page: number;
    size: number;
  }>({ page: 0, size: 20 });

  const { data, isLoading } = useQuery({
    queryKey: ['adminInquiries', searchParams],
    queryFn: () => adminCsApi.getInquiryList(searchParams).then((r) => r.data.data),
  });

  const handleSearch = (values: { keyword?: string; type?: InquiryType }) => {
    setSearchParams((prev) => ({ ...prev, ...values, page: 0 }));
  };

  const handleReset = () => {
    form.resetFields();
    setSearchParams((prev) => ({ ...prev, keyword: undefined, type: undefined, page: 0 }));
  };

  const handleTabChange = (key: string) => {
    const status = key === 'ALL' ? undefined : (key as InquiryStatus);
    setSearchParams((prev) => ({ ...prev, status, page: 0 }));
  };

  const columns: TableProps<AdminInquiryResponse>['columns'] = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 70,
    },
    {
      title: '유형',
      dataIndex: 'type',
      key: 'type',
      width: 100,
      render: (type: InquiryType) => <Tag>{TYPE_LABELS[type]}</Tag>,
    },
    {
      title: '제목',
      dataIndex: 'title',
      key: 'title',
      render: (title: string, record: AdminInquiryResponse) => (
        <span
          style={{ cursor: 'pointer', color: '#1677ff' }}
          onClick={() => navigate(`/cs/inquiries/${record.id}`)}
        >
          {title}
          {record.secret && (
            <Tag color="red" style={{ marginLeft: 4 }}>
              비공개
            </Tag>
          )}
        </span>
      ),
    },
    {
      title: '작성자',
      dataIndex: 'memberName',
      key: 'memberName',
      width: 120,
    },
    {
      title: '상태',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status: InquiryStatus) => (
        <Tag color={STATUS_COLORS[status]}>{STATUS_LABELS[status]}</Tag>
      ),
    },
    {
      title: '등록일',
      dataIndex: 'createdAt',
      key: 'createdAt',
      width: 160,
      render: (date: string) => new Date(date).toLocaleString('ko-KR'),
    },
  ];

  const tabItems = [
    { key: 'ALL', label: '전체' },
    { key: 'PENDING', label: '미답변' },
    { key: 'ANSWERED', label: '답변완료' },
    { key: 'CLOSED', label: '종료' },
  ];

  return (
    <div>
      <Title level={4} style={{ marginBottom: 16 }}>
        문의 관리
      </Title>

      <Tabs
        activeKey={searchParams.status ?? 'ALL'}
        onChange={handleTabChange}
        items={tabItems}
        style={{ marginBottom: 16 }}
      />

      <Form form={form} onFinish={handleSearch} layout="inline" style={{ marginBottom: 16 }}>
        <Row gutter={[8, 8]} style={{ width: '100%' }}>
          <Col>
            <Form.Item name="keyword">
              <Input placeholder="제목/내용 검색" allowClear style={{ width: 200 }} />
            </Form.Item>
          </Col>
          <Col>
            <Form.Item name="type">
              <Select
                placeholder="문의 유형"
                allowClear
                style={{ width: 130 }}
                options={Object.entries(TYPE_LABELS).map(([value, label]) => ({
                  value,
                  label,
                }))}
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

      <Table<AdminInquiryResponse>
        rowKey="id"
        columns={columns}
        dataSource={data?.content ?? []}
        loading={isLoading}
        pagination={{
          current: (searchParams.page ?? 0) + 1,
          pageSize: searchParams.size ?? 20,
          total: data?.totalElements ?? 0,
          showSizeChanger: true,
          showTotal: (total) => `총 ${total}건`,
          onChange: (page, pageSize) =>
            setSearchParams((prev) => ({ ...prev, page: page - 1, size: pageSize })),
        }}
      />
    </div>
  );
}
