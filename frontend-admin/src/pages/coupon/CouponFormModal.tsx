import { useEffect } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
  Modal,
  Form,
  Input,
  InputNumber,
  Select,
  DatePicker,
  message,
  Spin,
} from 'antd';
import dayjs from 'dayjs';
import { adminCouponApi } from '../../api/adminCoupon';
import type { CouponCreateRequest, CouponUpdateRequest } from '../../api/adminCoupon';

interface Props {
  open: boolean;
  couponId: number | null;
  onClose: () => void;
}

export default function CouponFormModal({ open, couponId, onClose }: Props) {
  const queryClient = useQueryClient();
  const [form] = Form.useForm();
  const isEdit = couponId != null;

  const { data: couponData, isLoading } = useQuery({
    queryKey: ['adminCouponDetail', couponId],
    queryFn: () => adminCouponApi.getDetail(couponId!).then((r) => r.data.data),
    enabled: isEdit && open,
  });

  useEffect(() => {
    if (open && couponData && isEdit) {
      form.setFieldsValue({
        ...couponData,
        startAt: dayjs(couponData.startAt),
        endAt: dayjs(couponData.endAt),
      });
    } else if (open && !isEdit) {
      form.resetFields();
      form.setFieldsValue({ type: 'FIXED', target: 'ALL', minOrderPrice: 0 });
    }
  }, [open, couponData, isEdit, form]);

  const createMutation = useMutation({
    mutationFn: (data: CouponCreateRequest) => adminCouponApi.create(data),
    onSuccess: () => {
      message.success('쿠폰이 등록되었습니다.');
      queryClient.invalidateQueries({ queryKey: ['adminCoupons'] });
      onClose();
    },
    onError: () => {
      message.error('쿠폰 등록에 실패했습니다.');
    },
  });

  const updateMutation = useMutation({
    mutationFn: (data: CouponUpdateRequest) => adminCouponApi.update(couponId!, data),
    onSuccess: () => {
      message.success('쿠폰이 수정되었습니다.');
      queryClient.invalidateQueries({ queryKey: ['adminCoupons'] });
      queryClient.invalidateQueries({ queryKey: ['adminCouponDetail', couponId] });
      onClose();
    },
    onError: () => {
      message.error('쿠폰 수정에 실패했습니다.');
    },
  });

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      const payload = {
        ...values,
        startAt: values.startAt.toISOString(),
        endAt: values.endAt.toISOString(),
      };

      if (isEdit) {
        const { code, ...updateData } = payload;
        updateMutation.mutate(updateData);
      } else {
        createMutation.mutate(payload);
      }
    } catch {
      // validation error
    }
  };

  return (
    <Modal
      title={isEdit ? '쿠폰 수정' : '쿠폰 등록'}
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
            name="code"
            label="쿠폰 코드"
            rules={[{ required: true, message: '쿠폰 코드를 입력하세요' }]}
          >
            <Input placeholder="예: WELCOME2024" disabled={isEdit} />
          </Form.Item>

          <Form.Item
            name="name"
            label="쿠폰명"
            rules={[{ required: true, message: '쿠폰명을 입력하세요' }]}
          >
            <Input placeholder="예: 신규 회원 5,000원 할인" />
          </Form.Item>

          <Form.Item
            name="type"
            label="할인 타입"
            rules={[{ required: true, message: '할인 타입을 선택하세요' }]}
          >
            <Select
              options={[
                { value: 'FIXED', label: '정액 할인 (원)' },
                { value: 'RATE', label: '비율 할인 (%)' },
              ]}
            />
          </Form.Item>

          <Form.Item
            name="value"
            label="할인 값"
            rules={[{ required: true, message: '할인 값을 입력하세요' }]}
          >
            <InputNumber min={1} style={{ width: '100%' }} />
          </Form.Item>

          <Form.Item name="minOrderPrice" label="최소 주문 금액">
            <InputNumber min={0} style={{ width: '100%' }} addonAfter="원" />
          </Form.Item>

          <Form.Item name="maxDiscountPrice" label="최대 할인 금액">
            <InputNumber min={0} style={{ width: '100%' }} addonAfter="원" placeholder="미입력 시 무제한" />
          </Form.Item>

          <Form.Item name="target" label="적용 대상">
            <Select
              options={[
                { value: 'ALL', label: '전체 상품' },
                { value: 'CATEGORY', label: '특정 카테고리' },
                { value: 'PRODUCT', label: '특정 상품' },
              ]}
            />
          </Form.Item>

          <Form.Item
            noStyle
            shouldUpdate={(prev, cur) => prev.target !== cur.target}
          >
            {({ getFieldValue }) =>
              getFieldValue('target') !== 'ALL' && (
                <Form.Item name="targetId" label="대상 ID">
                  <InputNumber style={{ width: '100%' }} placeholder="카테고리/상품 ID" />
                </Form.Item>
              )
            }
          </Form.Item>

          <Form.Item
            name="startAt"
            label="시작일"
            rules={[{ required: true, message: '시작일을 선택하세요' }]}
          >
            <DatePicker showTime style={{ width: '100%' }} />
          </Form.Item>

          <Form.Item
            name="endAt"
            label="종료일"
            rules={[{ required: true, message: '종료일을 선택하세요' }]}
          >
            <DatePicker showTime style={{ width: '100%' }} />
          </Form.Item>

          <Form.Item name="totalQty" label="총 수량">
            <InputNumber min={1} style={{ width: '100%' }} placeholder="미입력 시 무제한" />
          </Form.Item>
        </Form>
      </Spin>
    </Modal>
  );
}
