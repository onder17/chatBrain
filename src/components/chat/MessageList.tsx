import { useEffect, useRef, type FC } from 'react';
import type { ChatMessage as ChatMessageType, SuggestedQuestion } from '../../types/chat';
import { ChatMessage } from './ChatMessage';
import { WelcomeSection } from './WelcomeSection';
import { SuggestedQuestions } from './SuggestedQuestions';
import { TypingIndicator } from './TypingIndicator';
import { AlertCircle } from 'lucide-react';

interface MessageListProps {
  messages: ChatMessageType[];
  isLoading: boolean;
  error: string | null;
  suggestions: SuggestedQuestion[];
  onSelectSuggestion: (text: string) => void;
  onRetry?: (text: string) => void;
}

export const MessageList: FC<MessageListProps> = ({
  messages,
  isLoading,
  error,
  suggestions,
  onSelectSuggestion,
  onRetry,
}) => {
  const scrollEndRef = useRef<HTMLDivElement>(null);
  const containerRef = useRef<HTMLDivElement>(null);

  // Auto-scroll to bottom on new message or loading state change
  useEffect(() => {
    scrollEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages.length, isLoading, error]);

  const isEmpty = messages.length === 0;

  return (
    <div
      ref={containerRef}
      style={{
        flex: 1,
        overflowY: 'auto',
        overflowX: 'hidden',
        padding: '16px 20px',
        display: 'flex',
        flexDirection: 'column',
      }}
    >
      <div
        style={{
          width: '100%',
          maxWidth: '760px',
          margin: '0 auto',
          display: 'flex',
          flexDirection: 'column',
          flex: 1,
          justifyContent: isEmpty ? 'center' : 'flex-start',
        }}
      >
        {isEmpty ? (
          <div>
            <WelcomeSection />
            <SuggestedQuestions
              questions={suggestions}
              onSelect={onSelectSuggestion}
              disabled={isLoading}
            />
          </div>
        ) : (
          <div style={{ display: 'flex', flexDirection: 'column' }}>
            {messages.map((m) => (
              <ChatMessage key={m.id} message={m} onRetry={onRetry} />
            ))}

            {isLoading && <TypingIndicator />}

            {error && (
              <div
                style={{
                  margin: '12px 0',
                  padding: '12px 16px',
                  borderRadius: '12px',
                  background: 'rgba(239, 68, 68, 0.1)',
                  border: '1px solid rgba(239, 68, 68, 0.25)',
                  color: '#fca5a5',
                  fontSize: '13px',
                  display: 'flex',
                  alignItems: 'center',
                  gap: '8px',
                }}
              >
                <AlertCircle size={16} color="#ef4444" />
                <span style={{ flex: 1 }}>{error}</span>
              </div>
            )}

            <div ref={scrollEndRef} style={{ height: '8px' }} />
          </div>
        )}
      </div>
    </div>
  );
};
