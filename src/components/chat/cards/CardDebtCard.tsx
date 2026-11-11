import type { FC } from 'react';
import type { CardDebtData } from '../../../types/chat';
import { formatMoney, formatPercent } from '../../../utils/format';
import { CreditCard, Calendar } from 'lucide-react';

interface CardDebtCardProps {
  data: CardDebtData;
}

export const CardDebtCard: FC<CardDebtCardProps> = ({ data }) => {
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
      {/* 3 Summary metrics */}
      <div
        style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(3, 1fr)',
          gap: '8px',
          marginBottom: '16px',
          paddingBottom: '14px',
          borderBottom: '1px solid rgba(255, 255, 255, 0.06)',
        }}
      >
        <div>
          <span style={{ fontSize: '11px', color: '#94a3b8', display: 'block' }}>Toplam Borç</span>
          <span className="num" style={{ fontSize: '15px', fontWeight: 700, color: '#f87171' }}>
            {formatMoney(data.totalDebt)}
          </span>
        </div>
        <div>
          <span style={{ fontSize: '11px', color: '#94a3b8', display: 'block' }}>Kullanılabilir Limit</span>
          <span className="num" style={{ fontSize: '15px', fontWeight: 700, color: '#4ade80' }}>
            {formatMoney(data.totalAvailableLimit)}
          </span>
        </div>
        <div>
          <span style={{ fontSize: '11px', color: '#94a3b8', display: 'block' }}>Toplam Limit</span>
          <span className="num" style={{ fontSize: '15px', fontWeight: 600, color: '#cbd5e1' }}>
            {formatMoney(data.totalLimit)}
          </span>
        </div>
      </div>

      {/* Cards list */}
      <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
        {data.cards.map((card) => {
          const utilNum = card.utilizationPercent ? parseFloat(card.utilizationPercent) : 0;
          const isHighUtil = utilNum > 70;

          return (
            <div
              key={card.id}
              style={{
                padding: '10px 12px',
                background: 'rgba(255, 255, 255, 0.03)',
                borderRadius: '8px',
                border: '1px solid rgba(255, 255, 255, 0.04)',
              }}
            >
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '6px' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                  <CreditCard size={14} color="#f97316" />
                  <span style={{ fontSize: '13px', fontWeight: 600, color: '#f1f5f9' }}>
                    {card.bankName} {card.cardName}
                  </span>
                  <span style={{ fontSize: '11px', color: '#64748b' }}>•••• {card.cardLast4}</span>
                </div>
                <span className="num" style={{ fontSize: '13px', fontWeight: 700, color: '#f87171' }}>
                  {formatMoney(card.currentDebt)}
                </span>
              </div>

              {/* Progress bar */}
              <div
                style={{
                  height: '4px',
                  background: 'rgba(255, 255, 255, 0.08)',
                  borderRadius: '2px',
                  overflow: 'hidden',
                  margin: '6px 0',
                }}
              >
                <div
                  style={{
                    height: '100%',
                    width: `${Math.min(utilNum, 100)}%`,
                    backgroundColor: isHighUtil ? '#f87171' : '#f97316',
                    borderRadius: '2px',
                  }}
                />
              </div>

              <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '11px', color: '#64748b' }}>
                <span>Limit Doluluk: <strong className="num" style={{ color: '#94a3b8' }}>{formatPercent(card.utilizationPercent)}</strong></span>
                {card.nextDueDate && (
                  <span style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
                    <Calendar size={11} /> Son Ödeme: {card.nextDueDate}
                  </span>
                )}
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};
