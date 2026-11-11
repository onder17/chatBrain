import type { FC } from 'react';
import type { PeriodSummaryData } from '../../../types/chat';
import { formatMoney, formatPercent } from '../../../utils/format';
import { TrendingUp, ArrowDownLeft, ArrowUpRight } from 'lucide-react';

interface SummaryCardProps {
  data: PeriodSummaryData;
}

export const SummaryCard: FC<SummaryCardProps> = ({ data }) => {
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
          marginBottom: '14px',
        }}
      >
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
          <TrendingUp size={16} color="#f97316" />
          <span style={{ fontSize: '13px', fontWeight: 600, color: '#f1f5f9' }}>
            {data.period.label} Gelir-Gider Dengesi
          </span>
        </div>
        {data.savingsRatePercent && (
          <span
            style={{
              fontSize: '11px',
              padding: '2px 8px',
              borderRadius: '999px',
              background: 'rgba(34, 197, 94, 0.15)',
              color: '#4ade80',
              fontWeight: 600,
            }}
          >
            Tasarruf Oranı: {formatPercent(data.savingsRatePercent)}
          </span>
        )}
      </div>

      <div
        style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(3, 1fr)',
          gap: '8px',
          marginBottom: '14px',
          padding: '10px',
          borderRadius: '8px',
          background: 'rgba(255, 255, 255, 0.03)',
        }}
      >
        <div>
          <span style={{ fontSize: '11px', color: '#94a3b8', display: 'flex', alignItems: 'center', gap: '3px' }}>
            <ArrowDownLeft size={11} color="#4ade80" /> Toplam Gelir
          </span>
          <span className="num" style={{ fontSize: '14px', fontWeight: 700, color: '#4ade80' }}>
            {formatMoney(data.totalIncome)}
          </span>
        </div>
        <div>
          <span style={{ fontSize: '11px', color: '#94a3b8', display: 'flex', alignItems: 'center', gap: '3px' }}>
            <ArrowUpRight size={11} color="#f97316" /> Toplam Gider
          </span>
          <span className="num" style={{ fontSize: '14px', fontWeight: 700, color: '#f87171' }}>
            {formatMoney(data.totalExpense)}
          </span>
        </div>
        <div>
          <span style={{ fontSize: '11px', color: '#94a3b8', display: 'block' }}>Net Tasarruf</span>
          <span className="num" style={{ fontSize: '14px', fontWeight: 700, color: '#38bdf8' }}>
            {formatMoney(data.net)}
          </span>
        </div>
      </div>
    </div>
  );
};
