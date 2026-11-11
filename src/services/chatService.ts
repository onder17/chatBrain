import axios, { type AxiosInstance } from 'axios';
import type { SendMessageRequest, SendMessageResponse, SuggestedQuestion } from '../types/chat';

const apiBaseUrl = typeof import.meta.env !== 'undefined' ? import.meta.env.VITE_API_BASE_URL : undefined;
const api = axios.create({
  baseURL: (apiBaseUrl || '/api/v1') + '/chat',
  timeout: 30000,
  headers: { 'Content-Type': 'application/json' },
});

export interface IChatService {
  sendMessage(sessionId: string, request: SendMessageRequest): Promise<SendMessageResponse>;
  getSuggestions(): Promise<SuggestedQuestion[]>;
}

export class ChatServiceImpl implements IChatService {
  constructor(private readonly client: AxiosInstance = api) {}

  async sendMessage(sessionId: string, request: SendMessageRequest): Promise<SendMessageResponse> {
    const response = await this.client.post<unknown>(
      `/sessions/${encodeURIComponent(sessionId)}/messages`,
      request,
    );
    return parseSendMessageResponse(response.data);
  }

  async getSuggestions(): Promise<SuggestedQuestion[]> {
    try {
      const response = await this.client.get<unknown>('/suggestions');
      if (Array.isArray(response.data) && response.data.every((item) =>
        isRecord(item) && typeof item.id === 'string' && typeof item.text === 'string')) {
        return response.data as SuggestedQuestion[];
      }
      throw new Error('Sunucudan geçersiz öneri yanıtı alındı.');
    } catch {
      return [
        'Toplam banka bakiyem ne kadar?',
        'Kredi kartı borcum ne kadar?',
        'Bu ay toplam ne kadar harcadım?',
        'Yaklaşan faturalarım neler?',
        'En çok hangi kategoride harcama yaptım?',
        'Son işlemlerimi göster.',
      ].map((text, index) => ({ id: String(index + 1), text }));
    }
  }
}

function isRecord(value: unknown): value is Record<string, unknown> {
  return typeof value === 'object' && value !== null && !Array.isArray(value);
}

export function parseSendMessageResponse(value: unknown): SendMessageResponse {
  if (!isRecord(value) || !isRecord(value.userMessage) || !isRecord(value.assistantMessage) || !isRecord(value.session))
    throw new Error('Sunucudan geçersiz sohbet yanıtı alındı.');
  const user = value.userMessage;
  const assistant = value.assistantMessage;
  const session = value.session;
  if (user.role !== 'USER' || assistant.role !== 'ASSISTANT' ||
      typeof user.id !== 'string' || typeof assistant.id !== 'string' ||
      typeof user.content !== 'string' || typeof assistant.content !== 'string' ||
      typeof user.sessionId !== 'string' || typeof assistant.sessionId !== 'string' ||
      typeof user.createdAt !== 'string' || typeof assistant.createdAt !== 'string' ||
      typeof session.id !== 'string' || typeof session.title !== 'string')
    throw new Error('Sunucudan geçersiz sohbet yanıtı alındı.');
  if (assistant.data != null) {
    if (!isRecord(assistant.data) || typeof assistant.data.type !== 'string')
      throw new Error('Sunucudan geçersiz finansal veri alındı.');
    if (assistant.data.type === 'INVOICE_LIST' && !Array.isArray(assistant.data.invoices))
      throw new Error('Fatura yanıtında invoices listesi bulunamadı.');
  }
  return value as unknown as SendMessageResponse;
}

export const chatService = new ChatServiceImpl();
