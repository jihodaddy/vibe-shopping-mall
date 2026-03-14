import { useEffect } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Modal, Form, Input, Switch, message, Spin } from 'antd';
import { adminNoticeApi } from '../../api/adminNotice';

const { TextArea } = Input;

interface NoticeFormModalProps {
  open: boolean;
  noticeId: number | null;
  onClose: () => void;
}

export default function NoticeFormModal({ open, noticeId, onClose }: NoticeFormModalProps) {
  const [form] = Form.useForm();
  const queryClient = useQueryClient();
  const isEdit = noticeId !== null;

  const { data: notice, isLoading } = useQuery({
    queryKey: ['adminNotice', noticeId],
    queryFn: () => adminNoticeApi.getDetail(noticeId!).then((r) => r.data.data),
    enabled: open && isEdit,
  });

  useEffect(() => {
    if (open && isEdit && notice) {
      form.setFieldsValue({
        title: notice.title,
        content: notice.content,
        isPinned: notice.pinned,
      });
    } else if (open && !isEdit) {
      form.resetFields();
    }
  }, [open, isEdit, notice, form]);

  const createMutation = useMutation({
    mutationFn: (values: { title: string; content: string; isPinned: boolean }) =>
      adminNoticeApi.create(values),
    onSuccess: () => {
      message.success('공지사항이 등록되었습니다.');
      queryClient.invalidateQueries({ queryKey: ['adminNotices'] });
      onClose();
    },
    onError: () => {
      message.error('등록에 실패했습니다.');
    },
  });

  const updateMutation = useMutation({
    mutationFn: (values: { title: string; content: string; isPinned: boolean }) =>
      adminNoticeApi.update(noticeId!, values),
    onSuccess: () => {
      message.success('공지사항이 수정되었습니다.');
      queryClient.invalidateQueries({ queryKey: ['adminNotices'] });
      queryClient.invalidateQueries({ queryKey: ['adminNotice', noticeId] });
      onClose();
    },
    onError: () => {
      message.error('수정에 실패했습니다.');
    },
  });

  const handleOk = async () => {
    try {
      const values = await form.validateFields();
      const data = {
        title: values.title,
        content: values.content,
        isPinned: values.isPinned ?? false,
      };
      if (isEdit) {
        updateMutation.mutate(data);
      } else {
        createMutation.mutate(data);
      }
    } catch {
      // validation error
    }
  };

  const loading = createMutation.isPending || updateMutation.isPending;

  return (
    <Modal
      title={isEdit ? '공지사항 수정' : '공지사항 등록'}
      open={open}
      onOk={handleOk}
      onCancel={onClose}
      okText={isEdit ? '수정' : '등록'}
      cancelText="취소"
      confirmLoading={loading}
      width={700}
      destroyOnClose
    >
      {isEdit && isLoading ? (
        <Spin style={{ display: 'block', margin: '40px auto' }} />
      ) : (
        <Form form={form} layout="vertical" initialValues={{ isPinned: false }}>
          <Form.Item
            name="title"
            label="제목"
            rules={[{ required: true, message: '제목을 입력해주세요.' }]}
          >
            <Input placeholder="공지사항 제목" maxLength={200} />
          </Form.Item>

          <Form.Item
            name="content"
            label="내용"
            rules={[{ required: true, message: '내용을 입력해주세요.' }]}
          >
            <TextArea rows={10} placeholder="공지사항 내용을 입력하세요." />
          </Form.Item>

          <Form.Item name="isPinned" label="상단 고정" valuePropName="checked">
            <Switch />
          </Form.Item>
        </Form>
      )}
    </Modal>
  );
}
