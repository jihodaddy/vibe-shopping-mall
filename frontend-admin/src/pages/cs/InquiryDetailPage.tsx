import { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
  Card,
  Descriptions,
  Tag,
  Button,
  Input,
  Space,
  Typography,
  Divider,
  List,
  message,
  Popconfirm,
  Spin,
} from 'antd';
import { ArrowLeftOutlined } from '@ant-design/icons';
import { adminCsApi } from '../../api/adminCs';
import type { InquiryStatus, InquiryType } from '../../api/adminCs';

const { Title, Text } = Typography;
const { TextArea } = Input;

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

export default function InquiryDetailPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const [answerContent, setAnswerContent] = useState('');

  const { data: inquiry, isLoading } = useQuery({
    queryKey: ['adminInquiry', id],
    queryFn: () => adminCsApi.getInquiryDetail(Number(id)).then((r) => r.data.data),
    enabled: !!id,
  });

  const answerMutation = useMutation({
    mutationFn: (content: string) =>
      adminCsApi.answerInquiry(Number(id), { content }),
    onSuccess: () => {
      message.success('답변이 등록되었습니다.');
      setAnswerContent('');
      queryClient.invalidateQueries({ queryKey: ['adminInquiry', id] });
      queryClient.invalidateQueries({ queryKey: ['adminInquiries'] });
    },
    onError: () => {
      message.error('답변 등록에 실패했습니다.');
    },
  });

  const closeMutation = useMutation({
    mutationFn: () => adminCsApi.closeInquiry(Number(id)),
    onSuccess: () => {
      message.success('문의가 종료되었습니다.');
      queryClient.invalidateQueries({ queryKey: ['adminInquiry', id] });
      queryClient.invalidateQueries({ queryKey: ['adminInquiries'] });
    },
    onError: () => {
      message.error('문의 종료에 실패했습니다.');
    },
  });

  const handleSubmitAnswer = () => {
    if (!answerContent.trim()) {
      message.warning('답변 내용을 입력해주세요.');
      return;
    }
    answerMutation.mutate(answerContent);
  };

  if (isLoading) {
    return <Spin size="large" style={{ display: 'block', margin: '100px auto' }} />;
  }

  if (!inquiry) {
    return <div>문의를 찾을 수 없습니다.</div>;
  }

  const isClosed = inquiry.status === 'CLOSED';

  return (
    <div>
      <Space style={{ marginBottom: 16 }}>
        <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/cs/inquiries')}>
          목록으로
        </Button>
      </Space>

      <Card>
        <Title level={4}>문의 상세</Title>
        <Descriptions bordered column={2} style={{ marginBottom: 24 }}>
          <Descriptions.Item label="문의 ID">{inquiry.id}</Descriptions.Item>
          <Descriptions.Item label="상태">
            <Tag color={STATUS_COLORS[inquiry.status]}>
              {STATUS_LABELS[inquiry.status]}
            </Tag>
          </Descriptions.Item>
          <Descriptions.Item label="작성자">
            {inquiry.memberName} ({inquiry.memberEmail})
          </Descriptions.Item>
          <Descriptions.Item label="유형">
            <Tag>{TYPE_LABELS[inquiry.type]}</Tag>
          </Descriptions.Item>
          <Descriptions.Item label="제목" span={2}>
            {inquiry.title}
            {inquiry.secret && (
              <Tag color="red" style={{ marginLeft: 8 }}>
                비공개
              </Tag>
            )}
          </Descriptions.Item>
          <Descriptions.Item label="내용" span={2}>
            <div style={{ whiteSpace: 'pre-wrap' }}>{inquiry.content}</div>
          </Descriptions.Item>
          <Descriptions.Item label="등록일">
            {new Date(inquiry.createdAt).toLocaleString('ko-KR')}
          </Descriptions.Item>
          <Descriptions.Item label="수정일">
            {inquiry.updatedAt ? new Date(inquiry.updatedAt).toLocaleString('ko-KR') : '-'}
          </Descriptions.Item>
        </Descriptions>

        <Divider />

        <Title level={5}>답변 이력</Title>
        {inquiry.answers && inquiry.answers.length > 0 ? (
          <List
            dataSource={inquiry.answers}
            renderItem={(answer) => (
              <List.Item>
                <List.Item.Meta
                  title={
                    <Space>
                      <Text strong>{answer.adminName}</Text>
                      <Text type="secondary" style={{ fontSize: 12 }}>
                        {new Date(answer.createdAt).toLocaleString('ko-KR')}
                      </Text>
                    </Space>
                  }
                  description={
                    <div style={{ whiteSpace: 'pre-wrap', marginTop: 8 }}>
                      {answer.content}
                    </div>
                  }
                />
              </List.Item>
            )}
          />
        ) : (
          <Text type="secondary">등록된 답변이 없습니다.</Text>
        )}

        {!isClosed && (
          <>
            <Divider />
            <Title level={5}>답변 작성</Title>
            <TextArea
              rows={4}
              value={answerContent}
              onChange={(e) => setAnswerContent(e.target.value)}
              placeholder="답변 내용을 입력하세요."
              style={{ marginBottom: 12 }}
            />
            <Space>
              <Button
                type="primary"
                onClick={handleSubmitAnswer}
                loading={answerMutation.isPending}
              >
                답변 등록
              </Button>
              <Popconfirm
                title="문의를 종료하시겠습니까?"
                description="종료된 문의에는 더 이상 답변할 수 없습니다."
                onConfirm={() => closeMutation.mutate()}
                okText="확인"
                cancelText="취소"
              >
                <Button danger loading={closeMutation.isPending}>
                  문의 종료
                </Button>
              </Popconfirm>
            </Space>
          </>
        )}
      </Card>
    </div>
  );
}
