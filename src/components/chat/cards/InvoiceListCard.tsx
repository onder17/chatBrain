import type { FC } from 'react';
import type { InvoiceListData } from '../../../types/chat';
import { formatMoney } from '../../../utils/format';
import { FileText, AlertTriangle, CheckCircle2 } from 'lucide-react';

interface InvoiceListCardProps {
  data: InvoiceListData;
}

export const InvoiceListCard: FC<InvoiceListCardProps> = ({ data }) => {
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
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
          <FileText size={15} color="#f97316" />
          <span style={{ fontSize: '13px', fontWeight: 600, color: '#f1f5f9' }}>
            Fatura Durumu
          </span>
        </div>
        <div style={{ display: 'flex', gap: '8px' }}>
          {data.overdueCount > 0 && (
            <span
              style={{
                fontSize: '11px',
                padding: '2px 8px',
                borderRadius: '999px',
                background: 'rgba(239, 68, 68, 0.15)',
                color: '#f87171',
                fontWeight: 600,
                display: 'flex',
                alignItems: 'center',
                gap: '4px',
              }}
            >
              <AlertTriangle size={11} /> {data.overdueCount} Gecikmiş
            </span>
          )}
          <span className="num" style={{ fontSize: '12px', fontWeight: 700, color: '#f8fafc' }}>
            Toplam: {formatMoney(data.totalAmount)}
          </span>
        </div>
      </div>

      <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
        {data.invoices.length === 0 && (
          <div style={{ padding: '10px 12px', color: '#94a3b8', fontSize: '12px' }}>
            Fatura bulunamadı.
          </div>
        )}
        {data.invoices.map((inv) => {
          const isOverdue = inv.overdue;
          const isPaid = inv.status === 'PAID';

          return (
            <div
              key={inv.id}
              style={{
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between',
                padding: '8px 12px',
                background: isOverdue ? 'rgba(239, 68, 68, 0.06)' : 'rgba(255, 255, 255, 0.03)',
                borderRadius: '8px',
                border: isOverdue
                  ? '1px solid rgba(239, 68, 68, 0.25)'
                  : '1px solid rgba(255, 255, 255, 0.04)',
              }}
            >
              <div>
                <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                  <span style={{ fontSize: '13px', fontWeight: 600, color: '#f1f5f9' }}>
                    {inv.providerName}
                  </span>
                  {isOverdue && (
                    <span style={{ fontSize: '10px', color: '#f87171', fontWeight: 600 }}>
                      (Vadesi Geçti!)
                    </span>
                  )}
                  {isPaid && (
                    <span style={{ fontSize: '10px', color: '#4ade80', display: 'flex', alignItems: 'center', gap: '2px' }}>
                      <CheckCircle2 size={10} /> Ödendi
                    </span>
                  )}
                </div>
                <div style={{ fontSize: '11px', color: '#64748b' }}>
                  Son Ödeme: {inv.dueDate} {inv.daysUntilDue != null && !isOverdue && !isPaid ? `(${inv.daysUntilDue} gün kaldı)` : ''}
                </div>
              </div>

              <div
                className="num"
                style={{
                  fontSize: '13px',
                  fontWeight: 700,
                  color: isOverdue ? '#f87171' : '#f8fafc',
                }}
              >
                {formatMoney(inv.amount)}
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};
