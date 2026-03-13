import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
  Button,
  Table,
  Tag,
  Space,
  Select,
  Image,
  message,
  Popconfirm,
  Typography,
  InputNumber,
} from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';
import type { TableProps } from 'antd';
import { adminBannerApi } from '../../api/adminBanner';
import type { AdminBannerResponse, BannerPosition } from '../../api/adminBanner';
import BannerFormModal from './BannerFormModal';

const { Title } = Typography;

const POSITION_LABELS: Record<BannerPosition, string> = {
  MAIN_TOP: '메인 상단',
  MAIN_MIDDLE: '메인 중단',
  POPUP: '팝업',
};

const POSITION_COLORS: Record<BannerPosition, string> = {
  MAIN_TOP: 'blue',
  MAIN_MIDDLE: 'green',
  POPUP: 'orange',
};

export default function BannerListPage() {
  const queryClient = useQueryClient();
  const [searchParams, setSearchParams] = useState<{
    position?: BannerPosition;
    isActive?: boolean;
    page: number;
    size: number;
  }>({ page: 0, size: 20 });

  const [formModalOpen, setFormModalOpen] = useState(false);
  const [editingBannerId, setEditingBannerId] = useState<number | null>(null);

  const { data, isLoading } = useQuery({
    queryKey: ['adminBanners', searchParams],
    queryFn: () => adminBannerApi.getList(searchParams).then((r) => r.data.data),
  });

  const deleteMutation = useMutation({
    mutationFn: (id: number) => adminBannerApi.delete(id),
    onSuccess: () => {
      message.success('배너가 비활성화되었습니다.');
      queryClient.invalidateQueries({ queryKey: ['adminBanners'] });
    },
    onError: () => {
      message.error('삭제에 실패했습니다.');
    },
  });

  const sortMutation = useMutation({
    mutationFn: (bannerIds: number[]) => adminBannerApi.updateSort(bannerIds),
    onSuccess: () => {
      message.success('정렬 순서가 변경되었습니다.');
      queryClient.invalidateQueries({ queryKey: ['adminBanners'] });
    },
    onError: () => {
      message.error('정렬 변경에 실패했습니다.');
    },
  });

  const openCreateModal = () => {
    setEditingBannerId(null);
    setFormModalOpen(true);
  };

  const openEditModal = (id: number) => {
    setEditingBannerId(id);
    setFormModalOpen(true);
  };

  const isCurrentlyActive = (record: AdminBannerResponse) => {
    if (!record.active) return false;
    const now = new Date();
    if (record.startAt && new Date(record.startAt) > now) return false;
    if (record.endAt && new Date(record.endAt) < now) return false;
    return true;
  };

  const columns: TableProps<AdminBannerResponse>['columns'] = [
    {
      title: '이미지',
      dataIndex: 'imageUrl',
      key: 'imageUrl',
      width: 120,
      render: (url: string) => (
        <Image src={url} width={100} height={60} style={{ objectFit: 'cover' }} />
      ),
    },
    {
      title: '제목',
      dataIndex: 'title',
      key: 'title',
      render: (title: string, record: AdminBannerResponse) => (
        <span
          style={{ cursor: 'pointer', color: '#1677ff' }}
          onClick={() => openEditModal(record.id)}
        >
          {title}
        </span>
      ),
    },
    {
      title: '위치',
      dataIndex: 'position',
      key: 'position',
      width: 120,
      render: (position: BannerPosition) => (
        <Tag color={POSITION_COLORS[position]}>{POSITION_LABELS[position]}</Tag>
      ),
    },
    {
      title: '정렬순서',
      dataIndex: 'sortOrder',
      key: 'sortOrder',
      width: 100,
    },
    {
      title: '기간',
      key: 'period',
      width: 220,
      render: (_: unknown, record: AdminBannerResponse) => {
        if (!record.startAt && !record.endAt) return '상시 노출';
        return (
          <span>
            {record.startAt ? new Date(record.startAt).toLocaleDateString('ko-KR') : '~'} ~{' '}
            {record.endAt ? new Date(record.endAt).toLocaleDateString('ko-KR') : '~'}
          </span>
        );
      },
    },
    {
      title: '상태',
      key: 'status',
      width: 80,
      render: (_: unknown, record: AdminBannerResponse) => (
        <Tag color={isCurrentlyActive(record) ? 'green' : 'default'}>
          {isCurrentlyActive(record) ? '노출중' : '비노출'}
        </Tag>
      ),
    },
    {
      title: '관리',
      key: 'actions',
      width: 160,
      render: (_: unknown, record: AdminBannerResponse) => (
        <Space>
          <Button
            size="small"
            icon={<EditOutlined />}
            onClick={() => openEditModal(record.id)}
          >
            수정
          </Button>
          <Popconfirm
            title="배너를 비활성화하시겠습니까?"
            onConfirm={() => deleteMutation.mutate(record.id)}
            okText="확인"
            cancelText="취소"
          >
            <Button size="small" danger icon={<DeleteOutlined />} disabled={!record.active}>
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
          배너 관리
        </Title>
        <Button type="primary" icon={<PlusOutlined />} onClick={openCreateModal}>
          배너 등록
        </Button>
      </div>

      <Space style={{ marginBottom: 16 }}>
        <Select
          placeholder="위치 필터"
          allowClear
          style={{ width: 150 }}
          value={searchParams.position}
          onChange={(v) => setSearchParams((prev) => ({ ...prev, position: v, page: 0 }))}
          options={[
            { value: 'MAIN_TOP', label: '메인 상단' },
            { value: 'MAIN_MIDDLE', label: '메인 중단' },
            { value: 'POPUP', label: '팝업' },
          ]}
        />
        <Select
          placeholder="상태"
          allowClear
          style={{ width: 120 }}
          value={searchParams.isActive}
          onChange={(v) => setSearchParams((prev) => ({ ...prev, isActive: v, page: 0 }))}
          options={[
            { value: true, label: '활성' },
            { value: false, label: '비활성' },
          ]}
        />
      </Space>

      <Table<AdminBannerResponse>
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

      <BannerFormModal
        open={formModalOpen}
        bannerId={editingBannerId}
        onClose={() => setFormModalOpen(false)}
      />
    </div>
  );
}
