import type { FC } from 'react';
import type { FinancialData } from '../../../types/chat';
import { BalanceCard } from './BalanceCard';
import { CardDebtCard } from './CardDebtCard';
import { TransactionsTable } from './TransactionsTable';
import { CategoryBreakdown } from './CategoryBreakdown';
import { SummaryCard } from './SummaryCard';
import { InvoiceListCard } from './InvoiceListCard';

interface FinancialCardProps {
  data: FinancialData;
}

export const FinancialCard: FC<FinancialCardProps> = ({ data }) => {
  switch (data.type) {
    case 'ACCOUNT_BALANCE':
      return <BalanceCard data={data} />;
    case 'CARD_DEBT':
      return <CardDebtCard data={data} />;
    case 'TRANSACTION_LIST':
      return <TransactionsTable data={data} />;
    case 'SPENDING_BY_CATEGORY':
      return <CategoryBreakdown data={data} />;
    case 'PERIOD_SUMMARY':
      return <SummaryCard data={data} />;
    case 'INVOICE_LIST':
      return <InvoiceListCard data={data} />;
    default:
      return null;
  }
};
