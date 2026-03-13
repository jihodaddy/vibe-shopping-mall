import { useEffect, useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
  Modal,
  Form,
  Input,
  InputNumber,
  Select,
  DatePicker,
  Upload,
  message,
  Spin,
  Image,
  Button,
} from 'antd';
import { UploadOutlined } from '@ant-design/icons';
import dayjs from 'dayjs';
import { adminBannerApi } from '../../api/adminBanner';
import type { BannerCreateRequest, BannerUpdateRequest } from '../../api/adminBanner';

interface Props {
  open: boolean;
  bannerId: number | null;
  onClose: () => void;
}

export default function BannerFormModal({ open, bannerId, onClose }: Props) {
  const queryClient = useQueryClient();
  const [form] = Form.useForm();
  const isEdit = bannerId != null;
  const [uploading, setUploading] = useState(false);
  const [previewUrl, setPreviewUrl] = useState<string | null>(null);

  const { data: bannerData, isLoading } = useQuery({
    queryKey: ['adminBannerDetail', bannerId],
    queryFn: () => adminBannerApi.getDetail(bannerId!).then((r) => r.data.data),
    enabled: isEdit && open,
  });

  useEffect(() => {
    if (open && bannerData && isEdit) {
      form.setFieldsValue({
        ...bannerData,
        startAt: bannerData.startAt ? dayjs(bannerData.startAt) : null,
        endAt: bannerData.endAt ? dayjs(bannerData.endAt) : null,
      });
      setPreviewUrl(bannerData.imageUrl);
    } else if (open && !isEdit) {
      form.resetFields();
      form.setFieldsValue({ position: 'MAIN_TOP', sortOrder: 0 });
      setPreviewUrl(null);
    }
  }, [open, bannerData, isEdit, form]);

  const createMutation = useMutation({
    mutationFn: (data: BannerCreateRequest) => adminBannerApi.create(data),
    onSuccess: () => {
      message.success('배너가 등록되었습니다.');
      queryClient.invalidateQueries({ queryKey: ['adminBanners'] });
      onClose();
    },
    onError: () => {
      message.error('배너 등록에 실패했습니다.');
    },
  });

  const updateMutation = useMutation({
    mutationFn: (data: BannerUpdateRequest) => adminBannerApi.update(bannerId!, data),
    onSuccess: () => {
      message.success('배너가 수정되었습니다.');
      queryClient.invalidateQueries({ queryKey: ['adminBanners'] });
      queryClient.invalidateQueries({ queryKey: ['adminBannerDetail', bannerId] });
      onClose();
    },
    onError: () => {
      message.error('배너 수정에 실패했습니다.');
    },
  });

  const handleUpload = async (file: File) => {
    try {
      setUploading(true);
      const { data: presignedData } = await adminBannerApi.getPresignedUrl(
        file.name,
        file.type
      );
      const { presignedUrl, fileUrl } = presignedData.data;

      await fetch(presignedUrl, {
        method: 'PUT',
        body: file,
        headers: { 'Content-Type': file.type },
      });

      form.setFieldsValue({ imageUrl: fileUrl });
      setPreviewUrl(fileUrl);
      message.success('이미지가 업로드되었습니다.');
    } catch {
      message.error('이미지 업로드에 실패했습니다.');
    } finally {
      setUploading(false);
    }
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      const payload = {
        ...values,
        startAt: values.startAt ? values.startAt.toISOString() : null,
        endAt: values.endAt ? values.endAt.toISOString() : null,
      };

      if (isEdit) {
        updateMutation.mutate(payload);
      } else {
        createMutation.mutate(payload);
      }
    } catch {
      // validation error
    }
  };

  return (
    <Modal
      title={isEdit ? '배너 수정' : '배너 등록'}
      open={open}
      onOk={handleSubmit}
      onCancel={onClose}
      okText={isEdit ? '수정' : '등록'}
      cancelText="취소"
      width={600}
      confirmLoading={createMutation.isPending || updateMutation.isPending}
      destroyOnClose
    >
      <Spin spinning={isEdit && isLoading}>
        <Form form={form} layout="vertical" style={{ marginTop: 16 }}>
          <Form.Item
            name="title"
            label="배너 제목"
            rules={[{ required: true, message: '배너 제목을 입력하세요' }]}
          >
            <Input placeholder="배너 제목" />
          </Form.Item>

          <Form.Item label="배너 이미지">
            <Upload
              accept="image/*"
              showUploadList={false}
              beforeUpload={(file) => {
                handleUpload(file);
                return false;
              }}
            >
              <Button icon={<UploadOutlined />} loading={uploading}>
                이미지 업로드
              </Button>
            </Upload>
            {previewUrl && (
              <div style={{ marginTop: 8 }}>
                <Image src={previewUrl} width={300} style={{ objectFit: 'cover' }} />
              </div>
            )}
          </Form.Item>

          <Form.Item
            name="imageUrl"
            label="이미지 URL"
            rules={[{ required: true, message: '이미지를 업로드하거나 URL을 입력하세요' }]}
          >
            <Input
              placeholder="이미지 URL (직접 입력 또는 업로드)"
              onChange={(e) => setPreviewUrl(e.target.value)}
            />
          </Form.Item>

          <Form.Item name="linkUrl" label="링크 URL">
            <Input placeholder="클릭 시 이동할 URL" />
          </Form.Item>

          <Form.Item
            name="position"
            label="배너 위치"
            rules={[{ required: true, message: '위치를 선택하세요' }]}
          >
            <Select
              options={[
                { value: 'MAIN_TOP', label: '메인 상단' },
                { value: 'MAIN_MIDDLE', label: '메인 중단' },
                { value: 'POPUP', label: '팝업' },
              ]}
            />
          </Form.Item>

          <Form.Item name="sortOrder" label="정렬 순서">
            <InputNumber min={0} style={{ width: '100%' }} />
          </Form.Item>

          <Form.Item name="startAt" label="시작일">
            <DatePicker showTime style={{ width: '100%' }} placeholder="미설정 시 즉시 노출" />
          </Form.Item>

          <Form.Item name="endAt" label="종료일">
            <DatePicker showTime style={{ width: '100%' }} placeholder="미설정 시 상시 노출" />
          </Form.Item>
        </Form>
      </Spin>
    </Modal>
  );
}
