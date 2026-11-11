import type { FC } from 'react';
import type { AccountBalanceData } from '../../../types/chat';
import { formatMoney } from '../../../utils/format';
import { Building2, Wallet } from 'lucide-react';

interface BalanceCardProps {
  data: AccountBalanceData;
}

export const BalanceCard: FC<BalanceCardProps> = ({ data }) => {
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
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '12px' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
          <div
            style={{
              padding: '6px',
              borderRadius: '8px',
              background: 'rgba(249, 115, 22, 0.15)',
              color: '#f97316',
              display: 'flex',
            }}
          >
            <Wallet size={16} />
          </div>
          <span style={{ fontSize: '13px', color: '#94a3b8', fontWeight: 500 }}>Toplam Banka Bakiyesi</span>
        </div>
        <span
          className="num"
          style={{
            fontSize: '11px',
            color: '#64748b',
            background: 'rgba(255, 255, 255, 0.05)',
            padding: '2px 8px',
            borderRadius: '999px',
          }}
        >
          {data.accountCount} Hesap
        </span>
      </div>

      <div
        className="num"
        style={{
          fontSize: '22px',
          fontWeight: 700,
          color: '#f8fafc',
          marginBottom: '16px',
          letterSpacing: '-0.02em',
        }}
      >
        {formatMoney(data.totalBalance)}
      </div>

      <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
        {data.accounts.map((acc) => (
          <div
            key={acc.id}
            style={{
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'space-between',
              padding: '8px 12px',
              background: 'rgba(255, 255, 255, 0.03)',
              borderRadius: '8px',
              border: '1px solid rgba(255, 255, 255, 0.04)',
            }}
          >
            <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
              <Building2 size={14} color="#94a3b8" />
              <div>
                <div style={{ fontSize: '13px', fontWeight: 500, color: '#f1f5f9' }}>
                  {acc.bankName}
                </div>
                <div style={{ fontSize: '11px', color: '#64748b' }}>
                  {acc.accountName} • •••• {acc.ibanLast4}
                </div>
              </div>
            </div>
            <div className="num" style={{ fontSize: '14px', fontWeight: 600, color: '#e2e8f0' }}>
              {formatMoney(acc.balance)}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
