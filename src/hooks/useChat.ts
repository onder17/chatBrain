import { useState, useCallback, useEffect } from 'react';
import type { ChatMessage, SuggestedQuestion } from '../types/chat';
import { chatService } from '../services/chatService';
import { runChatRequest } from './chatRequest';

export function useChat() {
  const [sessionId, setSessionId] = useState<string>(() => `sess-${Date.now()}`);
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [suggestions, setSuggestions] = useState<SuggestedQuestion[]>([]);

  // Load suggested questions on mount
  useEffect(() => {
    let mounted = true;
    chatService.getSuggestions().then((items) => {
      if (mounted) setSuggestions(items);
    }).catch(() => {
      // Fallback
    });
    return () => {
      mounted = false;
    };
  }, []);

  const sendMessage = useCallback(
    async (rawContent: string) => {
      const content = rawContent.trim();
      if (!content || isLoading) return;

      // Reset transient error
      setError(null);
      setIsLoading(true);

      // Optimistic user message (created instantly for fluid UX)
      const optimisticUserMsg: ChatMessage = {
        id: `temp-${Date.now()}`,
        sessionId,
        role: 'USER',
        content,
        intent: null,
        data: null,
        meta: null,
        status: 'OK',
        createdAt: new Date().toISOString(),
      };

      setMessages((prev) => [...prev, optimisticUserMsg]);

      await runChatRequest(() => chatService.sendMessage(sessionId, { content }), {
        onLoadingChange: setIsLoading,
        onSuccess: (response) => setMessages((prev) => {
          const filtered = prev.filter((m) => m.id !== optimisticUserMsg.id);
          return [...filtered, response.userMessage, response.assistantMessage];
        }),
        onFailure: (err) => {
          console.error('Failed to send message:', err);
          setError(err instanceof Error ? err.message : 'Mesaj iletilemedi. Lütfen bağlantınızı kontrol edip tekrar deneyin.');
          setMessages((prev) => prev.map((m) =>
            m.id === optimisticUserMsg.id ? { ...m, status: 'FAILED' } : m));
        },
      });
    },
    [sessionId, isLoading]
  );

  const startNewChat = useCallback(() => {
    setSessionId(`sess-${Date.now()}`);
    setMessages([]);
    setError(null);
    setIsLoading(false);
  }, []);

  return {
    sessionId,
    messages,
    isLoading,
    error,
    suggestions,
    sendMessage,
    startNewChat,
    hasMessages: messages.length > 0,
  };
}
