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
