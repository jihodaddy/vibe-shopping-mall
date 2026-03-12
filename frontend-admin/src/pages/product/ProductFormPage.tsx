import { useEffect, useRef, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
  Form,
  Input,
  InputNumber,
  Select,
  Button,
  Upload,
  Space,
  Spin,
  message,
  Typography,
  TreeSelect,
} from 'antd';
import { UploadOutlined } from '@ant-design/icons';
import type { UploadFile, UploadProps } from 'antd';
import axios from 'axios';
import { adminProductApi } from '../../api/adminProduct';
import type {
  AdminCategoryResponse,
  ProductStatus,
  ProductCreateRequest,
  ProductUpdateRequest,
} from '../../api/adminProduct';

const { Title } = Typography;
const { TextArea } = Input;

const STATUS_OPTIONS = [
  { value: 'ON_SALE', label: '판매중' },
  { value: 'SOLD_OUT', label: '품절' },
  { value: 'HIDDEN', label: '숨김' },
];

interface TreeSelectNode {
  title: string;
  value: number;
  disabled?: boolean;
  children?: TreeSelectNode[];
}

function buildTreeSelectData(categories: AdminCategoryResponse[]): TreeSelectNode[] {
  return categories.map((cat) => ({
    title: cat.name,
    value: cat.id,
    disabled: !cat.active,
    children: cat.children ? buildTreeSelectData(cat.children) : [],
  }));
}

interface FormValues {
  categoryId: number;
  name: string;
  price: number;
  discountRate: number;
  stockQty: number;
  status?: ProductStatus;
  description: string;
}

export default function ProductFormPage() {
  const navigate = useNavigate();
  const { id } = useParams<{ id?: string }>();
  const isEdit = Boolean(id && id !== 'new');
  const productId = isEdit ? id : null;
  const queryClient = useQueryClient();
  const [form] = Form.useForm<FormValues>();
  const [fileList, setFileList] = useState<UploadFile[]>([]);
  const [uploadedUrls, setUploadedUrls] = useState<Record<string, boolean>>({});
  const uidToUrlRef = useRef<Map<string, string>>(new Map());

  const { data: categoriesData } = useQuery({
    queryKey: ['adminCategories'],
    queryFn: () => adminProductApi.getCategories().then((r) => r.data.data),
  });

  const { data: productData, isLoading: productLoading } = useQuery({
    queryKey: ['adminProduct', 'detail', productId ?? 'new'],
    queryFn: () => adminProductApi.getDetail(Number(productId)!).then((r) => r.data.data),
    enabled: isEdit && productId !== null && !isNaN(Number(productId)),
  });

  useEffect(() => {
    if (productData) {
      form.setFieldsValue({
        categoryId: productData.categoryId,
        name: productData.name,
        price: productData.price,
        discountRate: productData.discountRate,
        stockQty: productData.stockQty,
        status: productData.status,
        description: productData.description,
      });
      setUploadedUrls(productData.imageUrls ?? {});
      const existingFiles: UploadFile[] = Object.keys(productData.imageUrls ?? {}).map(
        (fileUrl, idx) => {
          const uid = `existing-${fileUrl}`;
          uidToUrlRef.current.set(uid, fileUrl);
          return {
            uid,
            name: `image-${idx}`,
            status: 'done',
            url: fileUrl,
          };
        }
      );
      setFileList(existingFiles);
    }
  }, [productData, form]);

  const createMutation = useMutation({
    mutationFn: (data: ProductCreateRequest) => adminProductApi.create(data),
    onSuccess: () => {
      message.success('상품이 등록되었습니다.');
      queryClient.invalidateQueries({ queryKey: ['adminProducts'] });
      navigate('/products');
    },
    onError: () => {
      message.error('상품 등록에 실패했습니다.');
    },
  });

  const updateMutation = useMutation({
    mutationFn: ({ id: pid, data }: { id: number; data: ProductUpdateRequest }) =>
      adminProductApi.update(pid, data),
    onSuccess: () => {
      message.success('상품이 수정되었습니다.');
      queryClient.invalidateQueries({ queryKey: ['adminProducts'] });
      queryClient.invalidateQueries({ queryKey: ['adminProduct', 'detail', productId ?? 'new'] });
      navigate('/products');
    },
    onError: () => {
      message.error('상품 수정에 실패했습니다.');
    },
  });

  const handleUpload: UploadProps['customRequest'] = async (options) => {
    const { file, onSuccess, onError } = options;
    const uploadFile = file as File & { uid: string };
    try {
      const { data: presignedData } = await adminProductApi.getPresignedUrl(
        uploadFile.name,
        uploadFile.type
      );
      const { presignedUrl, fileUrl } = presignedData.data;

      await axios.put(presignedUrl, uploadFile, {
        headers: { 'Content-Type': uploadFile.type },
      });

      uidToUrlRef.current.set(uploadFile.uid, fileUrl);
      setUploadedUrls((prev) => ({ ...prev, [fileUrl]: true }));
      setFileList((prev) =>
        prev.map((f) =>
          f.uid === uploadFile.uid ? { ...f, url: fileUrl, status: 'done' } : f
        )
      );
      onSuccess?.({});
    } catch (err) {
      message.error('이미지 업로드에 실패했습니다.');
      onError?.(err as Error);
    }
  };

  const handleFileChange: UploadProps['onChange'] = ({ fileList: newFileList }) => {
    // Find removed files
    const removedUids = fileList
      .filter((f) => !newFileList.find((nf) => nf.uid === f.uid))
      .map((f) => f.uid);

    // Clean up uploadedUrls for removed files
    if (removedUids.length > 0) {
      setUploadedUrls((prev) => {
        const next = { ...prev };
        for (const uid of removedUids) {
          const url = uidToUrlRef.current.get(uid);
          if (url) {
            delete next[url];
            uidToUrlRef.current.delete(uid);
          }
        }
        return next;
      });
    }

    setFileList(newFileList);
  };

  const handleSubmit = (values: FormValues) => {
    const payload = {
      categoryId: values.categoryId,
      name: values.name,
      price: values.price,
      discountRate: values.discountRate ?? 0,
      stockQty: values.stockQty,
      description: values.description ?? '',
      imageUrls: uploadedUrls,
    };

    if (isEdit && productId) {
      updateMutation.mutate({
        id: Number(productId),
        data: { ...payload, status: values.status ?? 'ON_SALE' },
      });
    } else {
      createMutation.mutate(payload);
    }
  };

  const treeData = categoriesData ? buildTreeSelectData(categoriesData) : [];
  const isBusy = createMutation.isPending || updateMutation.isPending;

  if (isEdit && productLoading) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', padding: 80 }}>
        <Spin size="large" />
      </div>
    );
  }

  return (
    <div style={{ maxWidth: 720 }}>
      <Title level={4} style={{ marginBottom: 24 }}>
        {isEdit ? '상품 수정' : '상품 등록'}
      </Title>

      <Form form={form} layout="vertical" onFinish={handleSubmit}>
        <Form.Item
          label="카테고리"
          name="categoryId"
          rules={[{ required: true, message: '카테고리를 선택해주세요.' }]}
        >
          <TreeSelect
            placeholder="카테고리 선택"
            treeData={treeData}
            allowClear
            showSearch
            treeDefaultExpandAll
            style={{ width: '100%' }}
          />
        </Form.Item>

        <Form.Item
          label="상품명"
          name="name"
          rules={[{ required: true, message: '상품명을 입력해주세요.' }]}
        >
          <Input placeholder="상품명" />
        </Form.Item>

        <Form.Item
          label="가격"
          name="price"
          rules={[{ required: true, message: '가격을 입력해주세요.' }]}
        >
          <InputNumber
            min={0}
            style={{ width: '100%' }}
            formatter={(value) => `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')}
            addonAfter="원"
          />
        </Form.Item>

        <Form.Item label="할인율" name="discountRate" initialValue={0}>
          <InputNumber min={0} max={100} style={{ width: '100%' }} addonAfter="%" />
        </Form.Item>

        <Form.Item
          label="재고"
          name="stockQty"
          rules={[{ required: true, message: '재고를 입력해주세요.' }]}
        >
          <InputNumber min={0} style={{ width: '100%' }} addonAfter="개" />
        </Form.Item>

        {isEdit && (
          <Form.Item label="상품 상태" name="status">
            <Select options={STATUS_OPTIONS} placeholder="상태 선택" />
          </Form.Item>
        )}

        <Form.Item label="상품 설명" name="description">
          <TextArea rows={5} placeholder="상품 설명을 입력해주세요." />
        </Form.Item>

        <Form.Item label="이미지 업로드">
          <Upload
            fileList={fileList}
            customRequest={handleUpload}
            onChange={handleFileChange}
            listType="picture-card"
            accept="image/*"
            multiple
            beforeUpload={(file) => {
              const isImage = file.type.startsWith('image/');
              if (!isImage) {
                message.error('이미지 파일만 업로드할 수 있습니다.');
                return Upload.LIST_IGNORE;
              }
              const isLt10M = file.size / 1024 / 1024 < 10;
              if (!isLt10M) {
                message.error('이미지 크기는 10MB 이하이어야 합니다.');
                return Upload.LIST_IGNORE;
              }
              return true;
            }}
          >
            <div>
              <UploadOutlined />
              <div style={{ marginTop: 8 }}>업로드</div>
            </div>
          </Upload>
        </Form.Item>

        <Form.Item>
          <Space>
            <Button type="primary" htmlType="submit" loading={isBusy}>
              {isEdit ? '수정' : '등록'}
            </Button>
            <Button onClick={() => navigate('/products')}>취소</Button>
          </Space>
        </Form.Item>
      </Form>
    </div>
  );
}
