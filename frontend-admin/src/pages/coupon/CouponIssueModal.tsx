import { useState } from 'react';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import {
  Modal,
  Radio,
  Input,
  message,
  Typography,
  Space,
} from 'antd';
import { adminCouponApi } from '../../api/adminCoupon';

const { Text } = Typography;
const { TextArea } = Input;

interface Props {
  open: boolean;
  couponId: number | null;
  onClose: () => void;
}

export default function CouponIssueModal({ open, couponId, onClose }: Props) {
  const queryClient = useQueryClient();
  const [issueType, setIssueType] = useState<'all' | 'specific'>('all');
  const [memberIdsText, setMemberIdsText] = useState('');

  const issueMutation = useMutation({
    mutationFn: () => {
      const memberIds =
        issueType === 'specific'
          ? memberIdsText
              .split(/[,\n\s]+/)
              .filter((s) => s.trim())
              .map(Number)
              .filter((n) => !isNaN(n))
          : null;

      return adminCouponApi.issue({
        couponId: couponId!,
        memberIds,
      });
    },
    onSuccess: (res) => {
      const count = res.data.data;
      message.success(`${count}명에게 쿠폰이 발급되었습니다.`);
      queryClient.invalidateQueries({ queryKey: ['adminCoupons'] });
      handleClose();
    },
    onError: () => {
      message.error('쿠폰 발급에 실패했습니다.');
    },
  });

  const handleClose = () => {
    setIssueType('all');
    setMemberIdsText('');
    onClose();
  };

  return (
    <Modal
      title="쿠폰 발급"
      open={open}
      onOk={() => issueMutation.mutate()}
      onCancel={handleClose}
      okText="발급"
      cancelText="취소"
      confirmLoading={issueMutation.isPending}
      destroyOnClose
    >
      <Space direction="vertical" style={{ width: '100%', marginTop: 16 }}>
        <Text>발급 대상을 선택하세요.</Text>
        <Radio.Group
          value={issueType}
          onChange={(e) => setIssueType(e.target.value)}
        >
          <Radio value="all">전체 활성 회원에게 발급</Radio>
          <Radio value="specific">특정 회원에게 발급</Radio>
        </Radio.Group>

        {issueType === 'specific' && (
          <>
            <Text type="secondary">
              회원 ID를 쉼표, 공백 또는 줄바꿈으로 구분하여 입력하세요.
            </Text>
            <TextArea
              rows={4}
              placeholder="예: 1, 2, 3"
              value={memberIdsText}
              onChange={(e) => setMemberIdsText(e.target.value)}
            />
          </>
        )}

        {issueType === 'all' && (
          <Text type="warning">
            전체 활성 회원에게 쿠폰이 발급됩니다. 이미 발급받은 회원은 자동으로 건너뜁니다.
          </Text>
        )}
      </Space>
    </Modal>
  );
}
