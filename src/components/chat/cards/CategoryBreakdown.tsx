import type { FC } from 'react';
import type { SpendingByCategoryData } from '../../../types/chat';
import { formatMoney, formatPercent } from '../../../utils/format';
import { PieChart } from 'lucide-react';

interface CategoryBreakdownProps {
  data: SpendingByCategoryData;
}

export const CategoryBreakdown: FC<CategoryBreakdownProps> = ({ data }) => {
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
          <PieChart size={15} color="#f97316" />
          <span style={{ fontSize: '13px', fontWeight: 600, color: '#f1f5f9' }}>
            Kategoriye Göre Harcamalar
          </span>
        </div>
        <span className="num" style={{ fontSize: '12px', fontWeight: 700, color: '#f8fafc' }}>
          {formatMoney(data.totalExpense)}
        </span>
      </div>

      <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
        {data.categories.map((cat, idx) => {
          const pctNum = cat.percentage ? parseFloat(cat.percentage) : 0;
          return (
            <div key={cat.category}>
              <div
                style={{
                  display: 'flex',
                  justifyContent: 'space-between',
                  alignItems: 'center',
                  fontSize: '12px',
                  marginBottom: '4px',
                }}
              >
                <span style={{ color: '#cbd5e1', fontWeight: 500 }}>
                  {cat.categoryName}
                  <span style={{ color: '#64748b', marginLeft: '6px' }}>({cat.count} işlem)</span>
                </span>
                <span style={{ display: 'flex', gap: '8px' }}>
                  <strong className="num" style={{ color: '#f1f5f9' }}>
                    {formatMoney(cat.total)}
                  </strong>
                  <span className="num" style={{ color: '#94a3b8', width: '42px', textAlign: 'right' }}>
                    {formatPercent(cat.percentage)}
                  </span>
                </span>
              </div>
              <div
                style={{
                  height: '5px',
                  background: 'rgba(255, 255, 255, 0.08)',
                  borderRadius: '3px',
                  overflow: 'hidden',
                }}
              >
                <div
                  style={{
                    height: '100%',
                    width: `${Math.min(pctNum, 100)}%`,
                    backgroundColor: idx === 0 ? '#f97316' : idx === 1 ? '#fb923c' : '#64748b',
                    borderRadius: '3px',
                  }}
                />
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};
