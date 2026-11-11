/**
 * Formatting utilities conforming to ChatBrain Technical Documentation §8.2
 */

export const formatMoney = (v: string | number | null | undefined): string => {
  if (v == null || v === '') return '0,00 ₺';
  const num = typeof v === 'number' ? v : Number(v);
  if (isNaN(num)) return `${v} ₺`;
  return (
    num.toLocaleString('tr-TR', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    }) + ' ₺'
  );
};

export const formatPercent = (v: string | number | null | undefined): string => {
  if (v == null || v === '') return '—';
  const num = typeof v === 'number' ? v : Number(v);
  if (isNaN(num)) return '—';
  return `%${num.toLocaleString('tr-TR', { minimumFractionDigits: 1 })}`;
};

export const formatDate = (iso: string): string => {
  if (!iso) return '';
  try {
    const d = new Date(iso.includes('T') ? iso : `${iso}T00:00:00`);
    return d.toLocaleDateString('tr-TR', {
      day: 'numeric',
      month: 'short',
      year: 'numeric',
    });
  } catch {
    return iso;
  }
};

export const formatTime = (iso: string): string => {
  if (!iso) return '';
  try {
    const d = new Date(iso);
    return d.toLocaleTimeString('tr-TR', {
      hour: '2-digit',
      minute: '2-digit',
    });
  } catch {
    return '';
  }
};

export const signedMoney = (
  v: string,
  type: 'INCOME' | 'EXPENSE' | 'TRANSFER'
): string => {
  const prefix = type === 'INCOME' ? '+ ' : type === 'EXPENSE' ? '− ' : '';
  return prefix + formatMoney(v);
};
