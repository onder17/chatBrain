import type { FC } from 'react';
import type { ChatMessage as ChatMessageType } from '../../types/chat';
import { FinancialCard } from './cards/FinancialCard';
import { formatTime } from '../../utils/format';
import { AlertCircle } from 'lucide-react';
import { AnimatedChatBrainLogo } from './AnimatedChatBrainLogo';

interface ChatMessageProps {
  message: ChatMessageType;
  onRetry?: (content: string) => void;
}

export const ChatMessage: FC<ChatMessageProps> = ({ message, onRetry }) => {
  const isUser = message.role === 'USER';
  const isFailed = message.status === 'FAILED';

  return (
    <div
      style={{
        display: 'flex',
        flexDirection: 'column',
        alignItems: isUser ? 'flex-end' : 'flex-start',
        width: '100%',
        margin: '8px 0',
        animation: 'messageAppear 0.2s ease-out both',
      }}
    >
      <div
        style={{
          display: 'flex',
          alignItems: 'flex-start',
          gap: '10px',
          maxWidth: isUser ? 'min(82%, 580px)' : 'min(90%, 680px)',
          flexDirection: isUser ? 'row-reverse' : 'row',
        }}
      >
        {/* Assistant Animated Mascot Avatar Badge */}
        {!isUser && (
          <div style={{ marginTop: '2px', flexShrink: 0 }}>
            <AnimatedChatBrainLogo size={32} />
          </div>
        )}

        {/* Message Bubble */}
        <div
          style={{
            padding: '12px 16px',
            borderRadius: '16px',
            borderTopRightRadius: isUser ? '4px' : '16px',
            borderTopLeftRadius: isUser ? '16px' : '4px',
            background: isUser ? '#1e293b' : '#131b2e',
            color: '#f8fafc',
            border: isUser
              ? '1px solid #334155'
              : '1px solid rgba(255, 255, 255, 0.08)',
            boxShadow: '0 3px 12px rgba(0, 0, 0, 0.25)',
            width: '100%',
          }}
        >
          {/* Text Content */}
          <p
            style={{
              fontSize: '14px',
              lineHeight: '1.6',
              whiteSpace: 'pre-wrap',
              wordBreak: 'break-word',
              color: isUser ? '#f1f5f9' : '#f8fafc',
            }}
          >
            {message.content}
          </p>

          {/* Render structured financial data if present */}
          {!isUser && message.data && <FinancialCard data={message.data} />}

          {/* Degraded mode note */}
          {!isUser && message.meta?.degraded && (
            <div
              style={{
                marginTop: '10px',
                fontSize: '11px',
                color: '#94a3b8',
                display: 'flex',
                alignItems: 'center',
                gap: '4px',
              }}
            >
              <span>Yapay zekâ şu an sınırlı modda; cevap hazır şablonla üretildi.</span>
            </div>
          )}

          {/* Footer timestamp & status */}
          <div
            style={{
              display: 'flex',
              justifyContent: 'flex-end',
              alignItems: 'center',
              gap: '6px',
              marginTop: '6px',
              fontSize: '10px',
              color: '#64748b',
            }}
          >
            <span>{formatTime(message.createdAt)}</span>
            {isFailed && (
              <span
                style={{
                  color: '#f87171',
                  fontWeight: 600,
                  display: 'flex',
                  alignItems: 'center',
                  gap: '2px',
                }}
              >
                <AlertCircle size={10} /> Gönderilemedi
              </span>
            )}
          </div>
        </div>
      </div>

      {/* Retry button for failed user messages */}
      {isUser && isFailed && onRetry && (
        <button
          onClick={() => onRetry(message.content)}
          style={{
            marginTop: '4px',
            fontSize: '11px',
            color: '#f97316',
            background: 'transparent',
            padding: '2px 8px',
            borderRadius: '4px',
            textDecoration: 'underline',
          }}
        >
          Tekrar dene
        </button>
      )}

      <style>{`
        @keyframes messageAppear {
          from {
            opacity: 0;
            transform: translateY(6px);
          }
          to {
            opacity: 1;
            transform: none;
          }
        }
      `}</style>
    </div>
  );
};
