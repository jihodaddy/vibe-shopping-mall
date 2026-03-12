import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
  Button,
  Tree,
  Modal,
  Form,
  Input,
  InputNumber,
  Switch,
  Space,
  Spin,
  message,
  Typography,
} from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';
import type { TreeDataNode } from 'antd';
import { adminProductApi } from '../../api/adminProduct';
import type {
  AdminCategoryResponse,
  CategoryCreateRequest,
  CategoryUpdateRequest,
} from '../../api/adminProduct';

const { Title } = Typography;

interface CategoryNode extends TreeDataNode {
  id: number;
  name: string;
  depth: number;
  sortOrder: number;
  active: boolean;
  children: CategoryNode[];
}

function buildTreeData(categories: AdminCategoryResponse[], parentId?: number): CategoryNode[] {
  return categories.map((cat) => ({
    key: cat.id,
    id: cat.id,
    name: cat.name,
    depth: cat.depth,
    sortOrder: cat.sortOrder,
    active: cat.active,
    title: cat.name,
    children: cat.children ? buildTreeData(cat.children, cat.id) : [],
    _parentId: parentId,
  }));
}

interface ModalState {
  open: boolean;
  mode: 'add' | 'edit' | 'addChild';
  node?: CategoryNode;
  parentId?: number;
}

interface FormValues {
  name: string;
  sortOrder: number;
  active: boolean;
}

export default function CategoryPage() {
  const queryClient = useQueryClient();
  const [modalState, setModalState] = useState<ModalState>({ open: false, mode: 'add' });
  const [form] = Form.useForm<FormValues>();

  const { data: categories, isLoading } = useQuery({
    queryKey: ['adminCategories'],
    queryFn: () => adminProductApi.getCategories().then((r) => r.data.data),
  });

  const createMutation = useMutation({
    mutationFn: (data: CategoryCreateRequest) => adminProductApi.createCategory(data),
    onSuccess: () => {
      message.success('카테고리가 추가되었습니다.');
      queryClient.invalidateQueries({ queryKey: ['adminCategories'] });
      setModalState({ open: false, mode: 'add' });
    },
    onError: () => {
      message.error('카테고리 추가에 실패했습니다.');
    },
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, data }: { id: number; data: CategoryUpdateRequest }) =>
      adminProductApi.updateCategory(id, data),
    onSuccess: () => {
      message.success('카테고리가 수정되었습니다.');
      queryClient.invalidateQueries({ queryKey: ['adminCategories'] });
      setModalState({ open: false, mode: 'edit' });
    },
    onError: () => {
      message.error('카테고리 수정에 실패했습니다.');
    },
  });

  const deleteMutation = useMutation({
    mutationFn: (id: number) => adminProductApi.deleteCategory(id),
    onSuccess: () => {
      message.success('카테고리가 삭제되었습니다.');
      queryClient.invalidateQueries({ queryKey: ['adminCategories'] });
    },
    onError: () => {
      message.error('카테고리 삭제에 실패했습니다.');
    },
  });

  const openAddRoot = () => {
    form.resetFields();
    form.setFieldsValue({ sortOrder: 0, active: true });
    setModalState({ open: true, mode: 'add' });
  };

  const openAddChild = (node: CategoryNode) => {
    form.resetFields();
    form.setFieldsValue({ sortOrder: 0, active: true });
    setModalState({ open: true, mode: 'addChild', parentId: node.id });
  };

  const openEdit = (node: CategoryNode) => {
    form.setFieldsValue({
      name: node.name,
      sortOrder: node.sortOrder,
      active: node.active,
    });
    setModalState({ open: true, mode: 'edit', node });
  };

  const handleDelete = (node: CategoryNode) => {
    Modal.confirm({
      title: '카테고리 삭제',
      content: `"${node.name}" 카테고리를 삭제하시겠습니까? 하위 카테고리도 함께 비활성화됩니다.`,
      okText: '삭제',
      okButtonProps: { danger: true },
      cancelText: '취소',
      onOk: () => deleteMutation.mutate(node.id),
    });
  };

  const handleModalOk = async () => {
    const values = await form.validateFields();
    if (modalState.mode === 'add') {
      createMutation.mutate({
        name: values.name,
        sortOrder: values.sortOrder ?? 0,
      });
    } else if (modalState.mode === 'addChild') {
      createMutation.mutate({
        parentId: modalState.parentId,
        name: values.name,
        sortOrder: values.sortOrder ?? 0,
      });
    } else if (modalState.mode === 'edit' && modalState.node) {
      updateMutation.mutate({
        id: modalState.node.id,
        data: {
          name: values.name,
          sortOrder: values.sortOrder ?? 0,
          active: values.active ?? true,
        },
      });
    }
  };

  const treeNodes = categories ? buildTreeData(categories) : [];

  const renderTitle = (nodeData: CategoryNode) => (
    <Space style={{ display: 'flex', justifyContent: 'space-between', minWidth: 300 }}>
      <span style={{ color: nodeData.active ? undefined : '#999' }}>
        {nodeData.name}
        {!nodeData.active && <span style={{ color: '#ccc', marginLeft: 8 }}>(비활성)</span>}
      </span>
      <Space size="small">
        <Button
          size="small"
          icon={<PlusOutlined />}
          onClick={(e) => {
            e.stopPropagation();
            openAddChild(nodeData);
          }}
          title="하위 카테고리 추가"
        />
        <Button
          size="small"
          icon={<EditOutlined />}
          onClick={(e) => {
            e.stopPropagation();
            openEdit(nodeData);
          }}
          title="수정"
        />
        <Button
          size="small"
          danger
          icon={<DeleteOutlined />}
          onClick={(e) => {
            e.stopPropagation();
            handleDelete(nodeData);
          }}
          title="삭제"
        />
      </Space>
    </Space>
  );

  const nodesWithTitle = (nodes: CategoryNode[]): CategoryNode[] =>
    nodes.map((n) => ({
      ...n,
      title: renderTitle(n),
      children: nodesWithTitle(n.children),
    }));

  const isBusy = createMutation.isPending || updateMutation.isPending;
  const modalTitle =
    modalState.mode === 'edit'
      ? '카테고리 수정'
      : modalState.mode === 'addChild'
      ? '하위 카테고리 추가'
      : '최상위 카테고리 추가';

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 16 }}>
        <Title level={4} style={{ margin: 0 }}>
          카테고리 관리
        </Title>
        <Button type="primary" icon={<PlusOutlined />} onClick={openAddRoot}>
          최상위 카테고리 추가
        </Button>
      </div>

      {isLoading ? (
        <div style={{ display: 'flex', justifyContent: 'center', padding: 40 }}>
          <Spin />
        </div>
      ) : (
        <Tree
          treeData={nodesWithTitle(treeNodes) as TreeDataNode[]}
          defaultExpandAll
          blockNode
          selectable={false}
        />
      )}

      <Modal
        title={modalTitle}
        open={modalState.open}
        onOk={handleModalOk}
        onCancel={() => setModalState({ open: false, mode: 'add' })}
        okText={modalState.mode === 'edit' ? '수정' : '추가'}
        cancelText="취소"
        confirmLoading={isBusy}
      >
        <Form form={form} layout="vertical">
          <Form.Item
            label="카테고리 이름"
            name="name"
            rules={[{ required: true, message: '이름을 입력해주세요.' }]}
          >
            <Input placeholder="카테고리 이름" />
          </Form.Item>
          <Form.Item label="정렬 순서" name="sortOrder" initialValue={0}>
            <InputNumber min={0} style={{ width: '100%' }} />
          </Form.Item>
          {modalState.mode === 'edit' && (
            <Form.Item label="활성" name="active" valuePropName="checked">
              <Switch />
            </Form.Item>
          )}
        </Form>
      </Modal>
    </div>
  );
}
