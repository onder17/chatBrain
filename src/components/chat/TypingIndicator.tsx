import type { FC } from 'react';
import { AnimatedChatBrainLogo } from './AnimatedChatBrainLogo';

export const TypingIndicator: FC = () => {
  return (
    <div
      style={{
        display: 'flex',
        alignItems: 'flex-start',
        gap: '10px',
        maxWidth: 'min(90%, 640px)',
        margin: '12px 0',
        animation: 'fadeIn 0.25s ease-out',
      }}
    >
      {/* Assistant animated mascot avatar */}
      <AnimatedChatBrainLogo size={32} />

      {/* Typing Bubble */}
      <div
        style={{
          padding: '12px 18px',
          borderRadius: '16px',
          borderTopLeftRadius: '4px',
          background: '#131b2e',
          border: '1px solid rgba(255, 255, 255, 0.08)',
          boxShadow: '0 4px 12px rgba(0, 0, 0, 0.25)',
          display: 'flex',
          alignItems: 'center',
          gap: '10px',
        }}
      >
        {/* Pulsing Dots */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
          <span
            style={{
              width: '6px',
              height: '6px',
              borderRadius: '50%',
              backgroundColor: '#f97316',
              display: 'inline-block',
              animation: 'cbPulse 1.4s infinite ease-in-out both',
            }}
          />
          <span
            style={{
              width: '6px',
              height: '6px',
              borderRadius: '50%',
              backgroundColor: '#f97316',
              display: 'inline-block',
              animation: 'cbPulse 1.4s infinite ease-in-out both 0.2s',
            }}
          />
          <span
            style={{
              width: '6px',
              height: '6px',
              borderRadius: '50%',
              backgroundColor: '#f97316',
              display: 'inline-block',
              animation: 'cbPulse 1.4s infinite ease-in-out both 0.4s',
            }}
          />
        </div>

        <span style={{ fontSize: '13px', color: '#94a3b8', fontWeight: 500 }}>
          ChatBrain yanıt hazırlıyor...
        </span>

        <style>{`
          @keyframes cbPulse {
            0%, 80%, 100% {
              transform: scale(0.6);
              opacity: 0.35;
            }
            40% {
              transform: scale(1.1);
              opacity: 1;
            }
          }
          @keyframes fadeIn {
            from { opacity: 0; transform: translateY(4px); }
            to { opacity: 1; transform: none; }
          }
        `}</style>
      </div>
    </div>
  );
};
