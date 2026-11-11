import type { FC } from 'react';
import type { TransactionListData } from '../../../types/chat';
import { formatMoney, signedMoney } from '../../../utils/format';
import { ArrowDownLeft, ArrowUpRight, ArrowLeftRight } from 'lucide-react';

interface TransactionsTableProps {
  data: TransactionListData;
}

export const TransactionsTable: FC<TransactionsTableProps> = ({ data }) => {
  return (
    <div
      style={{
        marginTop: '12px',
        padding: '16px',
        background: 'rgba(15, 23, 42, 0.65)',
        borderRadius: '12px',
        border: '1px solid rgba(255, 255, 255, 0.08)',
      }}
    >
      <div
        style={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          marginBottom: '12px',
        }}
      >
        <span style={{ fontSize: '13px', fontWeight: 600, color: '#f1f5f9' }}>
          {data.period?.label || 'Son İşlemler'}
        </span>
        <span className="num" style={{ fontSize: '11px', color: '#64748b' }}>
          {data.totalCount} İşlem
        </span>
      </div>

      <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
        {data.transactions.map((tx) => {
          const isExpense = tx.type === 'EXPENSE';
          const isIncome = tx.type === 'INCOME';

          return (
            <div
              key={tx.id}
              style={{
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between',
                padding: '8px 10px',
                background: 'rgba(255, 255, 255, 0.03)',
                borderRadius: '8px',
                border: '1px solid rgba(255, 255, 255, 0.04)',
              }}
            >
              <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                <div
                  style={{
                    width: '28px',
                    height: '28px',
                    borderRadius: '50%',
                    background: isIncome
                      ? 'rgba(34, 197, 94, 0.15)'
                      : isExpense
                      ? 'rgba(249, 115, 22, 0.15)'
                      : 'rgba(148, 163, 184, 0.15)',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                  }}
                >
                  {isIncome ? (
                    <ArrowDownLeft size={14} color="#4ade80" />
                  ) : isExpense ? (
                    <ArrowUpRight size={14} color="#f97316" />
                  ) : (
                    <ArrowLeftRight size={14} color="#94a3b8" />
                  )}
                </div>

                <div>
                  <div style={{ fontSize: '13px', fontWeight: 500, color: '#f8fafc' }}>
                    {tx.merchant || tx.description || tx.categoryName}
                  </div>
                  <div style={{ fontSize: '11px', color: '#64748b' }}>
                    {tx.date} • {tx.categoryName}
                  </div>
                </div>
              </div>

              <div
                className="num"
                style={{
                  fontSize: '13px',
                  fontWeight: 600,
                  color: isIncome ? '#4ade80' : '#f1f5f9',
                }}
              >
                {signedMoney(tx.amount, tx.type)}
              </div>
            </div>
          );
        })}
      </div>

      {data.totalExpense !== '0.00' && (
        <div
          style={{
            marginTop: '12px',
            paddingTop: '10px',
            borderTop: '1px solid rgba(255, 255, 255, 0.06)',
            display: 'flex',
            justifyContent: 'space-between',
            fontSize: '12px',
            color: '#94a3b8',
          }}
        >
          <span>Listelenen Toplam Harcama:</span>
          <span className="num" style={{ fontWeight: 700, color: '#f1f5f9' }}>
            {formatMoney(data.totalExpense)}
          </span>
        </div>
      )}
    </div>
  );
};
