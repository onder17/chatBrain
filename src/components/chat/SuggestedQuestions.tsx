import type { FC } from 'react';
import type { SuggestedQuestion } from '../../types/chat';
import { ArrowRight } from 'lucide-react';

interface SuggestedQuestionsProps {
  questions: SuggestedQuestion[];
  onSelect: (text: string) => void;
  disabled?: boolean;
}

export const SuggestedQuestions: FC<SuggestedQuestionsProps> = ({
  questions,
  onSelect,
  disabled = false,
}) => {
  return (
    <div
      style={{
        width: '100%',
        maxWidth: '560px',
        margin: '18px auto 0 auto',
        padding: '0 16px',
      }}
    >
      <div
        style={{
          fontSize: '11px',
          fontWeight: 700,
          color: '#64748b',
          letterSpacing: '0.08em',
          textTransform: 'uppercase',
          marginBottom: '10px',
          paddingLeft: '4px',
        }}
      >
        Önerilen Sorular
      </div>

      <div
        style={{
          display: 'flex',
          flexDirection: 'column',
          gap: '8px',
        }}
      >
        {questions.map((q) => (
          <button
            key={q.id}
            onClick={() => onSelect(q.text)}
            disabled={disabled}
            style={{
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'space-between',
              width: '100%',
              padding: '13px 18px',
              borderRadius: '12px',
              background: '#121a2c',
              border: '1px solid rgba(255, 255, 255, 0.07)',
              color: '#f8fafc',
              fontSize: '14px',
              fontWeight: 500,
              textAlign: 'left',
              transition: 'all 0.2s cubic-bezier(0.4, 0, 0.2, 1)',
              boxShadow: '0 2px 6px rgba(0, 0, 0, 0.25)',
            }}
            onMouseEnter={(e) => {
              if (disabled) return;
              e.currentTarget.style.background = '#18233a';
              e.currentTarget.style.borderColor = 'rgba(249, 115, 22, 0.45)';
              e.currentTarget.style.transform = 'translateY(-1px)';
              e.currentTarget.style.boxShadow = '0 4px 14px rgba(249, 115, 22, 0.12)';
              const icon = e.currentTarget.querySelector('.arrow-icon') as HTMLElement;
              if (icon) {
                icon.style.transform = 'translateX(4px)';
                icon.style.color = '#f97316';
              }
            }}
            onMouseLeave={(e) => {
              if (disabled) return;
              e.currentTarget.style.background = '#121a2c';
              e.currentTarget.style.borderColor = 'rgba(255, 255, 255, 0.07)';
              e.currentTarget.style.transform = 'none';
              e.currentTarget.style.boxShadow = '0 2px 6px rgba(0, 0, 0, 0.25)';
              const icon = e.currentTarget.querySelector('.arrow-icon') as HTMLElement;
              if (icon) {
                icon.style.transform = 'none';
                icon.style.color = '#64748b';
              }
            }}
          >
            <span style={{ paddingRight: '12px' }}>{q.text}</span>
            <span
              className="arrow-icon"
              style={{
                color: '#64748b',
                display: 'flex',
                alignItems: 'center',
                transition: 'all 0.2s ease',
              }}
            >
              <ArrowRight size={16} />
            </span>
          </button>
        ))}
      </div>
    </div>
  );
};
