import { useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
  Button,
  Tag,
  Descriptions,
  Space,
  Typography,
  message,
  Card,
  Spin,
  Modal,
  Select,
  InputNumber,
  Input,
  Form,
} from 'antd';
import { ArrowLeftOutlined } from '@ant-design/icons';
import dayjs from 'dayjs';
import { adminMemberApi } from '../../api/adminMember';
import type { MemberGrade, MemberStatus } from '../../api/adminMember';

const { Title } = Typography;

const GRADE_COLORS: Record<MemberGrade, string> = {
  BRONZE: 'orange',
  SILVER: 'default',
  GOLD: 'gold',
  VIP: 'purple',
};

const STATUS_COLORS: Record<MemberStatus, string> = {
  ACTIVE: 'green',
  INACTIVE: 'orange',
  BANNED: 'red',
  WITHDRAWN: 'default',
};

const STATUS_LABELS: Record<MemberStatus, string> = {
  ACTIVE: '활성',
  INACTIVE: '정지',
  BANNED: '차단',
  WITHDRAWN: '탈퇴',
};

export default function MemberDetailPage() {
  const navigate = useNavigate();
  const { id } = useParams<{ id: string }>();
  const memberId = Number(id);
  const queryClient = useQueryClient();

  const [gradeModalOpen, setGradeModalOpen] = useState(false);
  const [pointModalOpen, setPointModalOpen] = useState(false);
  const [selectedGrade, setSelectedGrade] = useState<MemberGrade>('BRONZE');
  const [pointAmount, setPointAmount] = useState<number>(0);
  const [pointReason, setPointReason] = useState('');

  const { data, isLoading } = useQuery({
    queryKey: ['adminMember', memberId],
    queryFn: () => adminMemberApi.getDetail(memberId),
    enabled: !!memberId,
  });

  const member = data?.data?.data;

  const gradeMutation = useMutation({
    mutationFn: (grade: MemberGrade) => adminMemberApi.changeGrade(memberId, grade),
    onSuccess: () => {
      message.success('등급이 변경되었습니다.');
      setGradeModalOpen(false);
      queryClient.invalidateQueries({ queryKey: ['adminMember', memberId] });
    },
    onError: () => {
      message.error('등급 변경에 실패했습니다.');
    },
  });

  const pointMutation = useMutation({
    mutationFn: () => adminMemberApi.addPoint(memberId, { amount: pointAmount, reason: pointReason }),
    onSuccess: () => {
      message.success('포인트가 지급되었습니다.');
      setPointModalOpen(false);
      setPointAmount(0);
      setPointReason('');
      queryClient.invalidateQueries({ queryKey: ['adminMember', memberId] });
    },
    onError: () => {
      message.error('포인트 지급에 실패했습니다.');
    },
  });

  const statusMutation = useMutation({
    mutationFn: (status: MemberStatus) => adminMemberApi.updateStatus(memberId, status),
    onSuccess: () => {
      message.success('회원 상태가 변경되었습니다.');
      queryClient.invalidateQueries({ queryKey: ['adminMember', memberId] });
    },
    onError: () => {
      message.error('상태 변경에 실패했습니다.');
    },
  });

  const handleStatusToggle = () => {
    if (!member) return;
    const newStatus: MemberStatus = member.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE';
    statusMutation.mutate(newStatus);
  };

  if (isLoading) {
    return (
      <div style={{ padding: 24, textAlign: 'center' }}>
        <Spin size="large" />
      </div>
    );
  }

  if (!member) {
    return (
      <div style={{ padding: 24 }}>
        <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/members')}>
          목록으로
        </Button>
        <p style={{ marginTop: 16 }}>회원 정보를 불러올 수 없습니다.</p>
      </div>
    );
  }

  return (
    <div style={{ padding: 24 }}>
      <Space style={{ marginBottom: 16 }}>
        <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/members')}>
          목록으로
        </Button>
        <Title level={4} style={{ margin: 0 }}>
          회원 상세 - {member.name}
        </Title>
      </Space>

      <Card
        title="회원 기본 정보"
        style={{ marginBottom: 16 }}
        extra={
          <Space>
            <Button onClick={() => { setSelectedGrade(member.grade); setGradeModalOpen(true); }}>
              등급 변경
            </Button>
            <Button onClick={() => setPointModalOpen(true)}>
              포인트 지급
            </Button>
            <Button
              danger={member.status === 'ACTIVE'}
              type={member.status === 'ACTIVE' ? 'primary' : 'default'}
              loading={statusMutation.isPending}
              onClick={handleStatusToggle}
              disabled={member.status === 'BANNED' || member.status === 'WITHDRAWN'}
            >
              {member.status === 'ACTIVE' ? '회원 정지' : member.status === 'BANNED' ? '차단 상태' : member.status === 'WITHDRAWN' ? '탈퇴 회원' : '회원 활성화'}
            </Button>
          </Space>
        }
      >
        <Descriptions bordered column={2}>
          <Descriptions.Item label="이메일">{member.email}</Descriptions.Item>
          <Descriptions.Item label="이름">{member.name}</Descriptions.Item>
          <Descriptions.Item label="연락처">{member.phone ?? '-'}</Descriptions.Item>
          <Descriptions.Item label="등급">
            <Tag color={GRADE_COLORS[member.grade]}>{member.grade}</Tag>
          </Descriptions.Item>
          <Descriptions.Item label="포인트">{member.point.toLocaleString()}P</Descriptions.Item>
          <Descriptions.Item label="상태">
            <Tag color={STATUS_COLORS[member.status]}>{STATUS_LABELS[member.status] ?? member.status}</Tag>
          </Descriptions.Item>
          <Descriptions.Item label="가입일" span={2}>
            {dayjs(member.createdAt).format('YYYY-MM-DD HH:mm:ss')}
          </Descriptions.Item>
        </Descriptions>
      </Card>

      {/* 등급 변경 Modal */}
      <Modal
        title="등급 변경"
        open={gradeModalOpen}
        onOk={() => gradeMutation.mutate(selectedGrade)}
        onCancel={() => setGradeModalOpen(false)}
        confirmLoading={gradeMutation.isPending}
        okText="변경"
        cancelText="취소"
      >
        <Form layout="vertical" style={{ marginTop: 16 }}>
          <Form.Item label="등급 선택">
            <Select
              value={selectedGrade}
              onChange={(v) => setSelectedGrade(v)}
              style={{ width: '100%' }}
              options={[
                { label: 'BRONZE', value: 'BRONZE' },
                { label: 'SILVER', value: 'SILVER' },
                { label: 'GOLD', value: 'GOLD' },
                { label: 'VIP', value: 'VIP' },
              ]}
            />
          </Form.Item>
        </Form>
      </Modal>

      {/* 포인트 지급 Modal */}
      <Modal
        title="포인트 지급"
        open={pointModalOpen}
        onOk={() => pointMutation.mutate()}
        onCancel={() => { setPointModalOpen(false); setPointAmount(0); setPointReason(''); }}
        confirmLoading={pointMutation.isPending}
        okText="지급"
        cancelText="취소"
        okButtonProps={{ disabled: pointAmount <= 0 || !pointReason.trim() }}
      >
        <Form layout="vertical" style={{ marginTop: 16 }}>
          <Form.Item label="포인트 수량">
            <InputNumber
              min={1}
              value={pointAmount}
              onChange={(v) => setPointAmount(v ?? 0)}
              style={{ width: '100%' }}
              placeholder="지급할 포인트 수량"
            />
          </Form.Item>
          <Form.Item label="사유">
            <Input
              value={pointReason}
              onChange={(e) => setPointReason(e.target.value)}
              placeholder="포인트 지급 사유"
            />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}
