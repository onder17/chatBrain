import { useState } from 'react';
import { QueryClient, QueryClientProvider, useQuery } from '@tanstack/react-query';
import { useChat } from './hooks/useChat';
import { ChatHeader } from './components/chat/ChatHeader';
import { MessageList } from './components/chat/MessageList';
import { MessageInput } from './components/chat/MessageInput';
import { financialApi } from './services/financialApiService';
import { Sparkles } from 'lucide-react';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      refetchOnWindowFocus: false,
      retry: 1,
    },
  },
});

function ChatBrainApp() {
  const {
    messages,
    isLoading,
    error,
    suggestions,
    sendMessage,
    startNewChat,
    hasMessages,
  } = useChat();

  const [isOpen, setIsOpen] = useState(true);

  // Live Backend connectivity check
  const { isSuccess: isBackendConnected } = useQuery({
    queryKey: ['backend-health'],
    queryFn: () => financialApi.checkHealth(),
    retry: 1,
    refetchInterval: 10000,
  });

  if (!isOpen) {
    return (
      <div
        style={{
          height: '100vh',
          width: '100vw',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          background: '#0a0e17',
          padding: '20px',
        }}
      >
        <div style={{ textAlign: 'center' }}>
          <button
            onClick={() => setIsOpen(true)}
            style={{
              display: 'inline-flex',
              alignItems: 'center',
              gap: '10px',
              padding: '14px 28px',
              borderRadius: '999px',
              background: 'linear-gradient(135deg, #f97316 0%, #ea580c 100%)',
              color: '#ffffff',
              fontSize: '16px',
              fontWeight: 600,
              boxShadow: '0 8px 25px rgba(249, 115, 22, 0.4)',
              transition: 'transform 0.2s ease, box-shadow 0.2s ease',
            }}
            onMouseEnter={(e) => {
              e.currentTarget.style.transform = 'translateY(-2px)';
              e.currentTarget.style.boxShadow = '0 12px 30px rgba(249, 115, 22, 0.5)';
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.transform = 'none';
              e.currentTarget.style.boxShadow = '0 8px 25px rgba(249, 115, 22, 0.4)';
            }}
          >
            <Sparkles size={20} />
            <span>ChatBrain'i Başlat</span>
          </button>
          <p style={{ marginTop: '14px', fontSize: '13px', color: '#64748b' }}>
            Panel simge durumuna küçültüldü. Yeniden açmak için tıklayın.
          </p>
        </div>
      </div>
    );
  }

  return (
    <div
      style={{
        display: 'flex',
        flexDirection: 'column',
        height: '100vh',
        width: '100%',
        backgroundColor: '#0a0e17',
        backgroundImage: 'radial-gradient(ellipse at 50% 0%, #151d30 0%, #0a0e17 75%)',
        position: 'relative',
        overflow: 'hidden',
      }}
    >
      {/* Top Header */}
      <ChatHeader
        onReset={startNewChat}
        onClose={() => setIsOpen(false)}
        hasMessages={hasMessages}
        backendConnected={isBackendConnected}
      />

      {/* Main Conversation or Empty Welcome Area */}
      <main
        style={{
          flex: 1,
          display: 'flex',
          flexDirection: 'column',
          overflow: 'hidden',
          position: 'relative',
        }}
      >
        <MessageList
          messages={messages}
          isLoading={isLoading}
          error={error}
          suggestions={suggestions}
          onSelectSuggestion={sendMessage}
          onRetry={sendMessage}
        />
      </main>

      {/* Fixed Message Input & Disclaimer Footer */}
      <MessageInput
        onSend={sendMessage}
        disabled={isLoading}
      />
    </div>
  );
}

export function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <ChatBrainApp />
    </QueryClientProvider>
  );
}

export default App;
