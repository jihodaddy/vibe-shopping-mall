import { Result } from 'antd';

interface PlaceholderPageProps {
  title: string;
}

export default function PlaceholderPage({ title }: PlaceholderPageProps) {
  return (
    <Result
      status="info"
      title={title}
      subTitle="이 기능은 곧 구현될 예정입니다."
    />
  );
}

export function ProductsPage() {
  return <PlaceholderPage title="상품관리" />;
}

export function OrdersPage() {
  return <PlaceholderPage title="주문관리" />;
}

export function MembersPage() {
  return <PlaceholderPage title="회원관리" />;
}

export function CouponsPage() {
  return <PlaceholderPage title="쿠폰/배너 관리" />;
}

export function CsPage() {
  return <PlaceholderPage title="CS 문의 관리" />;
}

export function StatsPage() {
  return <PlaceholderPage title="통계 대시보드" />;
}
