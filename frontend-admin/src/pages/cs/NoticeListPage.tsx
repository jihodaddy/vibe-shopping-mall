import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
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
  Typography,
  Popconfirm,
  message,
} from 'antd';
import { PlusOutlined, DeleteOutlined, PushpinOutlined } from '@ant-design/icons';
import type { TableProps } from 'antd';
import { adminNoticeApi } from '../../api/adminNotice';
import type { AdminNoticeResponse } from '../../api/adminNotice';
import NoticeFormModal from './NoticeFormModal';

const { Title } = Typography;

export default function NoticeListPage() {
  const queryClient = useQueryClient();
  const [form] = Form.useForm();
  const [searchParams, setSearchParams] = useState<{
    keyword?: string;
    isActive?: boolean;
    page: number;
    size: number;
  }>({ page: 0, size: 20 });

  const [formModalOpen, setFormModalOpen] = useState(false);
  const [editingNoticeId, setEditingNoticeId] = useState<number | null>(null);

  const { data, isLoading } = useQuery({
    queryKey: ['adminNotices', searchParams],
    queryFn: () => adminNoticeApi.getList(searchParams).then((r) => r.data.data),
  });

  const deleteMutation = useMutation({
    mutationFn: (id: number) => adminNoticeApi.delete(id),
    onSuccess: () => {
      message.success('공지사항이 삭제되었습니다.');
      queryClient.invalidateQueries({ queryKey: ['adminNotices'] });
    },
    onError: () => {
      message.error('삭제에 실패했습니다.');
    },
  });

  const handleSearch = (values: { keyword?: string; isActive?: boolean }) => {
    setSearchParams({ ...values, page: 0, size: 20 });
  };

  const handleReset = () => {
    form.resetFields();
    setSearchParams({ page: 0, size: 20 });
  };

  const openCreateModal = () => {
    setEditingNoticeId(null);
    setFormModalOpen(true);
  };

  const openEditModal = (id: number) => {
    setEditingNoticeId(id);
    setFormModalOpen(true);
  };

  const columns: TableProps<AdminNoticeResponse>['columns'] = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 70,
    },
    {
      title: '제목',
      dataIndex: 'title',
      key: 'title',
      render: (title: string, record: AdminNoticeResponse) => (
        <Space>
          {record.pinned && <PushpinOutlined style={{ color: '#1677ff' }} />}
          <span
            style={{ cursor: 'pointer', color: '#1677ff' }}
            onClick={() => openEditModal(record.id)}
          >
            {title}
          </span>
        </Space>
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
      title: '고정',
      dataIndex: 'pinned',
      key: 'pinned',
      width: 80,
      render: (pinned: boolean) =>
        pinned ? <Tag color="blue">고정</Tag> : null,
    },
    {
      title: '조회수',
      dataIndex: 'viewCount',
      key: 'viewCount',
      width: 80,
    },
    {
      title: '등록일',
      dataIndex: 'createdAt',
      key: 'createdAt',
      width: 160,
      render: (date: string) => new Date(date).toLocaleString('ko-KR'),
    },
    {
      title: '관리',
      key: 'actions',
      width: 100,
      render: (_: unknown, record: AdminNoticeResponse) => (
        <Popconfirm
          title="공지사항을 삭제하시겠습니까?"
          onConfirm={() => deleteMutation.mutate(record.id)}
          okText="확인"
          cancelText="취소"
        >
          <Button
            size="small"
            danger
            icon={<DeleteOutlined />}
            disabled={!record.active}
          >
            삭제
          </Button>
        </Popconfirm>
      ),
    },
  ];

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 16 }}>
        <Title level={4} style={{ margin: 0 }}>
          공지사항 관리
        </Title>
        <Button type="primary" icon={<PlusOutlined />} onClick={openCreateModal}>
          공지 등록
        </Button>
      </div>

      <Form form={form} onFinish={handleSearch} layout="inline" style={{ marginBottom: 16 }}>
        <Row gutter={[8, 8]} style={{ width: '100%' }}>
          <Col>
            <Form.Item name="keyword">
              <Input placeholder="제목 검색" allowClear style={{ width: 200 }} />
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

      <Table<AdminNoticeResponse>
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

      <NoticeFormModal
        open={formModalOpen}
        noticeId={editingNoticeId}
        onClose={() => setFormModalOpen(false)}
      />
    </div>
  );
}
