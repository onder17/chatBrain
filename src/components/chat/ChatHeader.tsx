import type { FC } from 'react';
import { RotateCcw, X } from 'lucide-react';
import { AnimatedChatBrainLogo } from './AnimatedChatBrainLogo';

interface ChatHeaderProps {
  onReset?: () => void;
  onClose?: () => void;
  hasMessages: boolean;
  backendConnected?: boolean;
}

export const ChatHeader: FC<ChatHeaderProps> = ({
  onReset,
  onClose,
  hasMessages,
  backendConnected,
}) => (
  <header
    style={{
      padding: '14px 20px',
      borderBottom: '1px solid rgba(255, 255, 255, 0.08)',
      background: 'rgba(10, 14, 23, 0.92)',
      backdropFilter: 'blur(12px)',
      position: 'sticky',
      top: 0,
      zIndex: 20,
    }}
  >
    <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
      <div style={{ display: 'flex', alignItems: 'center', gap: '12px', minWidth: 0 }}>
        <AnimatedChatBrainLogo size={64} />

        <div style={{ minWidth: 0 }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px', flexWrap: 'wrap' }}>
            <h1
              style={{
                fontSize: '17px',
                fontWeight: 700,
                letterSpacing: '-0.01em',
                color: '#ffffff',
                display: 'flex',
                alignItems: 'center',
                gap: '2px',
              }}
            >
              <span>chat</span>
              <span style={{ color: '#f97316' }}>Brain</span>
            </h1>

            <div
              style={{
                display: 'flex',
                alignItems: 'center',
                gap: '5px',
                padding: '2px 8px',
                borderRadius: '999px',
                background: 'rgba(34, 197, 94, 0.12)',
              }}
            >
              <span
                style={{
                  width: '7px',
                  height: '7px',
                  borderRadius: '50%',
                  backgroundColor: '#22c55e',
                  boxShadow: '0 0 8px #22c55e',
                  display: 'inline-block',
                }}
              />
              <span style={{ fontSize: '11px', fontWeight: 600, color: '#4ade80' }}>
                Çevrimiçi
              </span>
            </div>

            {backendConnected !== undefined && (
              <div
                title={
                  backendConnected
                    ? 'Spring Boot Backend (:8090) bağlı ve çalışıyor'
                    : 'Backend bekleniyor / Mock mod devrede'
                }
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: '4px',
                  padding: '2px 8px',
                  borderRadius: '999px',
                  background: backendConnected
                    ? 'rgba(34, 197, 94, 0.1)'
                    : 'rgba(234, 179, 8, 0.1)',
                  border: `1px solid ${
                    backendConnected
                      ? 'rgba(34, 197, 94, 0.25)'
                      : 'rgba(234, 179, 8, 0.25)'
                  }`,
                  fontSize: '10px',
                  color: backendConnected ? '#86efac' : '#fde047',
                  fontWeight: 500,
                }}
              >
                <span>Backend {backendConnected ? 'Bağlı' : 'Mock'}</span>
              </div>
            )}
          </div>

          <p style={{ fontSize: '12px', color: '#94a3b8', marginTop: '2px' }}>
            Finansal durumunu anlamana ve yönetmene yardımcı olan akıllı asistan.
          </p>
        </div>
      </div>

      <div style={{ display: 'flex', alignItems: 'center', gap: '8px', flexShrink: 0 }}>
        {hasMessages && onReset && (
          <button
            onClick={onReset}
            title="Yeni Sohbet Başlat"
            style={{
              display: 'flex',
              alignItems: 'center',
              gap: '6px',
              padding: '6px 12px',
              borderRadius: '8px',
              background: 'rgba(255, 255, 255, 0.05)',
              border: '1px solid rgba(255, 255, 255, 0.1)',
              color: '#cbd5e1',
              fontSize: '12px',
              fontWeight: 500,
              transition: 'all 0.2s ease',
            }}
            onMouseEnter={(event) => {
              event.currentTarget.style.background = 'rgba(249, 115, 22, 0.15)';
              event.currentTarget.style.color = '#f97316';
              event.currentTarget.style.borderColor = 'rgba(249, 115, 22, 0.3)';
            }}
            onMouseLeave={(event) => {
              event.currentTarget.style.background = 'rgba(255, 255, 255, 0.05)';
              event.currentTarget.style.color = '#cbd5e1';
              event.currentTarget.style.borderColor = 'rgba(255, 255, 255, 0.1)';
            }}
          >
            <RotateCcw size={13} />
            <span>Yeni Sohbet</span>
          </button>
        )}

        {onClose && (
          <button
            onClick={onClose}
            title="Kapat"
            style={{
              width: '32px',
              height: '32px',
              borderRadius: '8px',
              background: 'transparent',
              color: '#94a3b8',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              transition: 'all 0.15s ease',
            }}
            onMouseEnter={(event) => {
              event.currentTarget.style.background = 'rgba(255, 255, 255, 0.08)';
              event.currentTarget.style.color = '#ffffff';
            }}
            onMouseLeave={(event) => {
              event.currentTarget.style.background = 'transparent';
              event.currentTarget.style.color = '#94a3b8';
            }}
          >
            <X size={18} />
          </button>
        )}
      </div>
    </div>
  </header>
);
