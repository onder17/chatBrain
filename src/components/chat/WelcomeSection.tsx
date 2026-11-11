import type { FC } from 'react';
import { AnimatedChatBrainLogo } from './AnimatedChatBrainLogo';

export const WelcomeSection: FC = () => {
  return (
    <div
      style={{
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        textAlign: 'center',
        padding: '24px 16px 12px 16px',
        animation: 'fadeIn 0.35s ease-out',
      }}
    >
      <div style={{ marginBottom: '18px' }}>
        <AnimatedChatBrainLogo size={76} />
      </div>

      <h2
        style={{
          fontSize: '26px',
          fontWeight: 700,
          color: '#ffffff',
          marginBottom: '10px',
          letterSpacing: '-0.02em',
        }}
      >
        Merhaba! 👋
      </h2>

      <p
        style={{
          fontSize: '15px',
          color: '#cbd5e1',
          maxWidth: '460px',
          lineHeight: '1.6',
          margin: '0 auto',
        }}
      >
        Ben <strong style={{ color: '#f97316', fontWeight: 600 }}>ChatBrain</strong>.
        Banka hesapların, kredi kartların, harcamaların ve faturaların hakkında sorular sorabilirsin.
      </p>
    </div>
  );
};
