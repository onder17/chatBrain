import { useState, useRef, useEffect, type FC, type KeyboardEvent } from 'react';
import { Send } from 'lucide-react';

interface MessageInputProps {
  onSend: (message: string) => void;
  disabled?: boolean;
  placeholder?: string;
}

const MAX_CHAR_COUNT = 500;
const WARNING_CHAR_THRESHOLD = 400;

export const MessageInput: FC<MessageInputProps> = ({
  onSend,
  disabled = false,
  placeholder = 'Finansal bir soru sorun... (örn: Toplam bakiyem ne kadar?)',
}) => {
  const [content, setContent] = useState('');
  const textareaRef = useRef<HTMLTextAreaElement>(null);

  // Auto-resize textarea height
  useEffect(() => {
    if (textareaRef.current) {
      textareaRef.current.style.height = 'auto';
      const scrollHeight = textareaRef.current.scrollHeight;
      textareaRef.current.style.height = `${Math.min(scrollHeight, 130)}px`;
    }
  }, [content]);

  // Keep focus on input unless explicitly blurred
  useEffect(() => {
    if (!disabled && textareaRef.current) {
      textareaRef.current.focus();
    }
  }, [disabled]);

  const handleSend = () => {
    const trimmed = content.trim();
    if (!trimmed || disabled || trimmed.length > MAX_CHAR_COUNT) return;
    onSend(trimmed);
    setContent('');
    if (textareaRef.current) {
      textareaRef.current.style.height = 'auto';
    }
  };

  const handleKeyDown = (e: KeyboardEvent<HTMLTextAreaElement>) => {
    // Prevent submission while typing IME characters
    if (e.nativeEvent.isComposing) return;

    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSend();
    }
  };

  const isOverLimit = content.length > MAX_CHAR_COUNT;
  const isSendDisabled = disabled || !content.trim() || isOverLimit;
  const showCounter = content.length >= WARNING_CHAR_THRESHOLD;

  return (
    <footer
      style={{
        padding: '12px 20px 16px 20px',
        background: 'rgba(10, 14, 23, 0.95)',
        borderTop: '1px solid rgba(255, 255, 255, 0.08)',
        backdropFilter: 'blur(12px)',
        position: 'sticky',
        bottom: 0,
        zIndex: 20,
      }}
    >
      <div
        style={{
          width: '100%',
          maxWidth: '760px',
          margin: '0 auto',
        }}
      >
        {/* Input Pill Container */}
        <div
          style={{
            display: 'flex',
            alignItems: 'flex-end',
            gap: '8px',
            padding: '8px 12px',
            background: '#101626',
            borderRadius: '16px',
            border: '1px solid rgba(255, 255, 255, 0.1)',
            boxShadow: '0 4px 16px rgba(0, 0, 0, 0.35)',
            transition: 'border-color 0.2s ease, box-shadow 0.2s ease',
          }}
          onFocusCapture={(e) => {
            e.currentTarget.style.borderColor = 'rgba(249, 115, 22, 0.5)';
            e.currentTarget.style.boxShadow = '0 0 0 2px rgba(249, 115, 22, 0.15), 0 4px 16px rgba(0, 0, 0, 0.4)';
          }}
          onBlurCapture={(e) => {
            e.currentTarget.style.borderColor = 'rgba(255, 255, 255, 0.1)';
            e.currentTarget.style.boxShadow = '0 4px 16px rgba(0, 0, 0, 0.35)';
          }}
        >
          {/* Text Area */}
          <textarea
            ref={textareaRef}
            rows={1}
            value={content}
            disabled={disabled}
            onChange={(e) => setContent(e.target.value)}
            onKeyDown={handleKeyDown}
            placeholder={placeholder}
            maxLength={MAX_CHAR_COUNT}
            aria-label="Finansal soru girin"
            style={{
              flex: 1,
              background: 'transparent',
              color: '#f8fafc',
              fontSize: '14px',
              lineHeight: '1.5',
              padding: '6px 4px',
              resize: 'none',
              maxHeight: '130px',
              outline: 'none',
              border: 'none',
            }}
          />

          {/* Character counter (if > 400 chars) */}
          {showCounter && (
            <span
              className="num"
              style={{
                fontSize: '11px',
                color: isOverLimit ? '#ef4444' : '#f97316',
                paddingBottom: '8px',
                fontWeight: 600,
              }}
            >
              {content.length}/{MAX_CHAR_COUNT}
            </span>
          )}

          {/* Send Button */}
          <button
            onClick={handleSend}
            disabled={isSendDisabled}
            title="Gönder"
            aria-label="Gönder"
            style={{
              width: '38px',
              height: '38px',
              borderRadius: '10px',
              background: isSendDisabled
                ? 'rgba(255, 255, 255, 0.08)'
                : 'linear-gradient(135deg, #f97316 0%, #ea580c 100%)',
              color: isSendDisabled ? '#64748b' : '#ffffff',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              flexShrink: 0,
              transition: 'all 0.2s cubic-bezier(0.4, 0, 0.2, 1)',
              boxShadow: isSendDisabled
                ? 'none'
                : '0 2px 10px rgba(249, 115, 22, 0.35)',
              transform: isSendDisabled ? 'none' : 'scale(1)',
            }}
            onMouseDown={(e) => {
              if (!isSendDisabled) e.currentTarget.style.transform = 'scale(0.95)';
            }}
            onMouseUp={(e) => {
              if (!isSendDisabled) e.currentTarget.style.transform = 'scale(1)';
            }}
          >
            <Send size={16} />
          </button>
        </div>

        {/* Disclaimer Footer */}
        <div
          style={{
            marginTop: '8px',
            textAlign: 'center',
            fontSize: '11px',
            color: '#64748b',
            letterSpacing: '0.01em',
          }}
        >
          ChatBrain finansal verilerini analiz eder; ödeme veya para transferi gerçekleştirmez.
        </div>
      </div>
    </footer>
  );
};
