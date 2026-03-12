import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import {
  Table,
  Button,
  Input,
  Select,
  Tag,
  Space,
  Typography,
  Row,
  Col,
  Card,
} from 'antd';
import { SearchOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import dayjs from 'dayjs';
import { adminMemberApi } from '../../api/adminMember';
import type { AdminMemberResponse, MemberGrade, MemberStatus } from '../../api/adminMember';

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

export default function MemberListPage() {
  const navigate = useNavigate();
  const [page, setPage] = useState(1);
  const [pageSize] = useState(20);
  const [keyword, setKeyword] = useState('');
  const [searchKeyword, setSearchKeyword] = useState('');
  const [grade, setGrade] = useState<MemberGrade | undefined>(undefined);
  const [status, setStatus] = useState<MemberStatus | undefined>(undefined);

  const { data, isLoading } = useQuery({
    queryKey: ['adminMembers', page, pageSize, searchKeyword, grade, status],
    queryFn: () =>
      adminMemberApi.getList({
        keyword: searchKeyword || undefined,
        grade,
        status,
        page: page - 1,
        size: pageSize,
      }),
  });

  const members = data?.data?.data?.content ?? [];
  const totalElements = data?.data?.data?.totalElements ?? 0;

  const handleSearch = () => {
    setSearchKeyword(keyword);
    setPage(1);
  };

  const handleGradeChange = (value: MemberGrade | undefined) => {
    setGrade(value);
    setPage(1);
  };

  const handleStatusChange = (value: MemberStatus | undefined) => {
    setStatus(value);
    setPage(1);
  };

  const columns: ColumnsType<AdminMemberResponse> = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 70,
    },
    {
      title: '이메일',
      dataIndex: 'email',
      key: 'email',
    },
    {
      title: '이름',
      dataIndex: 'name',
      key: 'name',
      width: 100,
    },
    {
      title: '등급',
      dataIndex: 'grade',
      key: 'grade',
      width: 90,
      render: (grade: MemberGrade) => (
        <Tag color={GRADE_COLORS[grade]}>{grade}</Tag>
      ),
    },
    {
      title: '포인트',
      dataIndex: 'point',
      key: 'point',
      width: 100,
      render: (v: number) => `${v.toLocaleString()}P`,
    },
    {
      title: '상태',
      dataIndex: 'status',
      key: 'status',
      width: 80,
      render: (status: MemberStatus) => (
        <Tag color={STATUS_COLORS[status]}>{STATUS_LABELS[status] ?? status}</Tag>
      ),
    },
    {
      title: '가입일',
      dataIndex: 'createdAt',
      key: 'createdAt',
      width: 120,
      render: (v: string) => dayjs(v).format('YYYY-MM-DD'),
    },
    {
      title: '',
      key: 'action',
      width: 90,
      render: (_, record) => (
        <Button size="small" onClick={() => navigate(`/members/${record.id}`)}>
          상세보기
        </Button>
      ),
    },
  ];

  return (
    <div style={{ padding: 24 }}>
      <Title level={3}>회원 관리</Title>

      <Card style={{ marginBottom: 16 }}>
        <Row gutter={16} align="middle">
          <Col flex={1}>
            <Input
              placeholder="이메일 / 이름 검색"
              value={keyword}
              onChange={(e) => setKeyword(e.target.value)}
              onPressEnter={handleSearch}
              suffix={<SearchOutlined />}
            />
          </Col>
          <Col>
            <Select
              placeholder="등급"
              allowClear
              style={{ width: 120 }}
              value={grade}
              onChange={handleGradeChange}
              options={[
                { label: 'BRONZE', value: 'BRONZE' },
                { label: 'SILVER', value: 'SILVER' },
                { label: 'GOLD', value: 'GOLD' },
                { label: 'VIP', value: 'VIP' },
              ]}
            />
          </Col>
          <Col>
            <Select
              placeholder="상태"
              allowClear
              style={{ width: 120 }}
              value={status}
              onChange={handleStatusChange}
              options={[
                { label: '활성', value: 'ACTIVE' },
                { label: '정지', value: 'INACTIVE' },
                { label: '차단', value: 'BANNED' },
              ]}
            />
          </Col>
          <Col>
            <Space>
              <Button type="primary" onClick={handleSearch}>
                검색
              </Button>
            </Space>
          </Col>
        </Row>
      </Card>

      <Table
        rowKey="id"
        columns={columns}
        dataSource={members}
        loading={isLoading}
        pagination={{
          current: page,
          pageSize,
          total: totalElements,
          onChange: (p) => setPage(p),
          showSizeChanger: false,
          showTotal: (total) => `총 ${total}명`,
        }}
      />
    </div>
  );
}
