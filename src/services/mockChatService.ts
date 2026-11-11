import type {
  ChatMessage,
  ChatSession,
  FinancialData,
  MessageMeta,
  SendMessageResponse,
  SuggestedQuestion,
} from '../types/chat';
import { financialApi } from './financialApiService';

/**
 * Predefined suggested questions (matching Prompt & Reference visual)
 */
export const INITIAL_SUGGESTIONS: SuggestedQuestion[] = [
  { id: '1', text: 'Toplam banka bakiyem ne kadar?' },
  { id: '2', text: 'Kredi kartı borcum ne kadar?' },
  { id: '3', text: 'Bu ay toplam ne kadar harcadım?' },
  { id: '4', text: 'Yaklaşan faturalarım neler?' },
  { id: '5', text: 'En çok hangi kategoride harcama yaptım?' },
  { id: '6', text: 'Son işlemlerimi göster.' },
];

/**
 * Deterministic Demo Financial Data Models (conforming to technical documentation §7 Seed data)
 */
const mockAccountData: FinancialData = {
  type: 'ACCOUNT_BALANCE',
  totalBalance: '145650.75',
  currency: 'TRY',
  accountCount: 3,
  bankFilter: null,
  accounts: [
    {
      id: 1,
      bankName: 'İş Bankası',
      accountName: 'Birikim Hesabı',
      ibanLast4: '0953',
      accountType: 'SAVINGS',
      balance: '85000.00',
    },
    {
      id: 2,
      bankName: 'Ziraat Bankası',
      accountName: 'Vadesiz TL',
      ibanLast4: '4821',
      accountType: 'CHECKING',
      balance: '48250.75',
    },
    {
      id: 3,
      bankName: 'Garanti BBVA',
      accountName: 'Vadesiz TL',
      ibanLast4: '7710',
      accountType: 'CHECKING',
      balance: '12400.00',
    },
  ],
  banks: [
    { bankName: 'İş Bankası', accountCount: 1, total: '85000.00' },
    { bankName: 'Ziraat Bankası', accountCount: 1, total: '48250.75' },
    { bankName: 'Garanti BBVA', accountCount: 1, total: '12400.00' },
  ],
};

const mockCardDebtData: FinancialData = {
  type: 'CARD_DEBT',
  totalDebt: '20500.75',
  totalLimit: '125000.00',
  totalAvailableLimit: '104499.25',
  totalStatementBalance: '13970.00',
  currency: 'TRY',
  cardCount: 3,
  cardFilter: null,
  cards: [
    {
      id: 1,
      bankName: 'Garanti BBVA',
      cardName: 'Bonus Platinum',
      cardLast4: '1234',
      creditLimit: '60000.00',
      currentDebt: '14320.50',
      availableLimit: '45679.50',
      statementBalance: '9870.00',
      nextDueDate: '2026-11-20',
      utilizationPercent: '23.9',
    },
    {
      id: 2,
      bankName: 'Yapı Kredi',
      cardName: 'World Gold',
      cardLast4: '5678',
      creditLimit: '40000.00',
      currentDebt: '6180.25',
      availableLimit: '33819.75',
      statementBalance: '4100.00',
      nextDueDate: '2026-11-23',
      utilizationPercent: '15.5',
    },
    {
      id: 3,
      bankName: 'Akbank',
      cardName: 'Axess Free',
      cardLast4: '9012',
      creditLimit: '25000.00',
      currentDebt: '0.00',
      availableLimit: '25000.00',
      statementBalance: '0.00',
      nextDueDate: null,
      utilizationPercent: '0.0',
    },
  ],
};

const mockPeriodSummaryData: FinancialData = {
  type: 'PERIOD_SUMMARY',
  period: {
    from: '2026-11-01',
    to: '2026-11-30',
    type: 'THIS_MONTH',
    label: 'Kasım 2026',
  },
  totalIncome: '45500.00',
  totalExpense: '21245.49',
  net: '24254.51',
  savingsRatePercent: '53.3',
  transactionCount: 13,
  topExpenseCategories: [
    { category: 'RENT', categoryName: 'Kira', total: '15000.00', count: 1, percentage: '70.6' },
    { category: 'SHOPPING', categoryName: 'Alışveriş', total: '2150.00', count: 2, percentage: '10.1' },
    { category: 'MARKET', categoryName: 'Market', total: '1730.50', count: 3, percentage: '8.1' },
    { category: 'TRANSPORT', categoryName: 'Ulaşım', total: '875.00', count: 3, percentage: '4.1' },
    { category: 'RESTAURANT', categoryName: 'Yeme-İçme', total: '860.00', count: 2, percentage: '4.0' },
  ],
};

const mockInvoiceData: FinancialData = {
  type: 'INVOICE_LIST',
  filter: 'UPCOMING',
  period: null,
  count: 4,
  totalAmount: '3428.05',
  overdueCount: 1,
  overdueAmount: '649.90',
  invoices: [
    {
      id: 1,
      providerName: 'Türk Telekom',
      category: 'INTERNET',
      amount: '649.90',
      dueDate: '2026-11-12',
      status: 'UNPAID',
      overdue: true,
      daysUntilDue: -3,
      paidAt: null,
    },
    {
      id: 2,
      providerName: 'BEDAŞ',
      category: 'ELECTRICITY',
      amount: '912.75',
      dueDate: '2026-11-18',
      status: 'UNPAID',
      overdue: false,
      daysUntilDue: 3,
      paidAt: null,
    },
    {
      id: 3,
      providerName: 'İSKİ',
      category: 'WATER',
      amount: '385.40',
      dueDate: '2026-11-20',
      status: 'UNPAID',
      overdue: false,
      daysUntilDue: 5,
      paidAt: null,
    },
    {
      id: 4,
      providerName: 'İGDAŞ',
      category: 'NATURAL_GAS',
      amount: '1480.00',
      dueDate: '2026-11-28',
      status: 'UNPAID',
      overdue: false,
      daysUntilDue: 13,
      paidAt: null,
    },
  ],
};

const mockCategorySpendingData: FinancialData = {
  type: 'SPENDING_BY_CATEGORY',
  period: {
    from: '2026-11-01',
    to: '2026-11-30',
    type: 'THIS_MONTH',
    label: 'Kasım 2026',
  },
  totalExpense: '21245.49',
  focusCategory: 'RENT',
  categories: [
    { category: 'RENT', categoryName: 'Kira', total: '15000.00', count: 1, percentage: '70.6' },
    { category: 'SHOPPING', categoryName: 'Alışveriş', total: '2150.00', count: 2, percentage: '10.1' },
    { category: 'MARKET', categoryName: 'Market', total: '1730.50', count: 3, percentage: '8.1' },
    { category: 'TRANSPORT', categoryName: 'Ulaşım', total: '875.00', count: 3, percentage: '4.1' },
    { category: 'RESTAURANT', categoryName: 'Yeme-İçme', total: '860.00', count: 2, percentage: '4.0' },
    { category: 'ENTERTAINMENT', categoryName: 'Eğlence', total: '400.00', count: 1, percentage: '1.9' },
    { category: 'SUBSCRIPTION', categoryName: 'Abonelik', total: '229.99', count: 1, percentage: '1.2' },
  ],
};

const mockTransactionListData: FinancialData = {
  type: 'TRANSACTION_LIST',
  period: {
    from: '2026-11-01',
    to: '2026-11-15',
    type: 'CUSTOM',
    label: 'Son İşlemler',
  },
  typeFilter: 'EXPENSE',
  categoryFilter: null,
  totalCount: 5,
  returnedCount: 5,
  truncated: false,
  totalIncome: '0.00',
  totalExpense: '2055.50',
  transactions: [
    {
      id: 1,
      date: '2026-11-12',
      type: 'EXPENSE',
      category: 'TRANSPORT',
      categoryName: 'Ulaşım',
      merchant: 'Uber',
      description: 'Havalimanı transferi',
      amount: '275.00',
    },
    {
      id: 2,
      date: '2026-11-11',
      type: 'EXPENSE',
      category: 'RESTAURANT',
      categoryName: 'Yeme-İçme',
      merchant: 'Yemeksepeti',
      description: 'Akşam yemeği',
      amount: '540.00',
    },
    {
      id: 3,
      date: '2026-11-10',
      type: 'EXPENSE',
      category: 'ENTERTAINMENT',
      categoryName: 'Eğlence',
      merchant: 'Paribu Cineverse',
      description: 'Sinema bileti',
      amount: '400.00',
    },
    {
      id: 4,
      date: '2026-11-09',
      type: 'EXPENSE',
      category: 'MARKET',
      categoryName: 'Market',
      merchant: 'BİM',
      description: 'Haftalık mutfak alışverişi',
      amount: '480.50',
    },
    {
      id: 5,
      date: '2026-11-07',
      type: 'EXPENSE',
      category: 'SHOPPING',
      categoryName: 'Alışveriş',
      merchant: 'Trendyol',
      description: 'Kışlık giyim',
      amount: '2150.00',
    },
  ],
};

/**
 * Text normalizer for matching questions
 */
function normalizeInput(text: string): string {
  return text
    .toLowerCase()
    .replace(/[ıİ]/g, 'i')
    .replace(/[şŞ]/g, 's')
    .replace(/[ğĞ]/g, 'g')
    .replace(/[üÜ]/g, 'u')
    .replace(/[öÖ]/g, 'o')
    .replace(/[çÇ]/g, 'c')
    .replace(/[^a-z0-9 ]/g, ' ')
    .replace(/\s+/g, ' ')
    .trim();
}

interface MockResponseMatch {
  intent: string;
  content: string;
  data: FinancialData | null;
}

function resolveMockResponse(userInput: string): MockResponseMatch {
  const norm = normalizeInput(userInput);

  // 1. Bank balance
  if (
    norm.includes('toplam banka bakiye') ||
    norm.includes('toplam param') ||
    norm.includes('hesap bakiye') ||
    (norm.includes('bakiye') && !norm.includes('kart')) ||
    norm.includes('kac param var')
  ) {
    return {
      intent: 'ACCOUNT_TOTAL_BALANCE',
      content:
        'Demo verilerine göre tüm banka hesaplarındaki toplam bakiye 145.650,75 ₺. 3 aktif banka hesabın bulunuyor: Garanti BBVA (12.400,00 ₺), Ziraat Bankası (48.250,75 ₺) ve İş Bankası Birikim (85.000,00 ₺).',
      data: mockAccountData,
    };
  }

  // 2. Card debt
  if (
    norm.includes('kredi kart') ||
    norm.includes('kart borc') ||
    norm.includes('kartimin borcu') ||
    norm.includes('kart limit')
  ) {
    return {
      intent: 'CARD_TOTAL_DEBT',
      content:
        'Demo verilerine göre kredi kartlarındaki güncel toplam borcun 20.500,75 ₺. Toplam kart limitin 125.000,00 ₺ olduğundan kullanılabilir limitin 104.499,25 ₺. Son hesap kesim borcun ise 13.970,00 ₺.',
      data: mockCardDebtData,
    };
  }

  // 3. Monthly expense total
  if (
    norm.includes('bu ay toplam ne kadar harcadim') ||
    norm.includes('bu ayki harcamam') ||
    norm.includes('bu ay harcama') ||
    norm.includes('bu ay gider') ||
    norm.includes('gelir gider')
  ) {
    return {
      intent: 'MONTHLY_EXPENSE_TOTAL',
      content:
        'Kasım 2026 döneminde toplam 21.245,49 ₺ harcama yaptın. Toplam gelirin 45.500,00 ₺ olup net tasarruf dengen +24.254,51 ₺ (%53,3 tasarruf oranı).',
      data: mockPeriodSummaryData,
    };
  }

  // 4. Invoices
  if (
    norm.includes('fatura') ||
    norm.includes('yaklasan fatura') ||
    norm.includes('odenmemis fatura')
  ) {
    return {
      intent: 'UPCOMING_INVOICES',
      content:
        'Önümüzdeki günlerde vadesi gelen 3 faturan bulunuyor (toplam 2.778,15 ₺). BEDAŞ Elektrik (912,75 ₺), İSKİ Su (385,40 ₺) ve İGDAŞ Doğalgaz (1.480,00 ₺). Ayrıca vadesi 3 gün gecikmiş 1 faturan (Türk Telekom 649,90 ₺) yer almaktadır.',
      data: mockInvoiceData,
    };
  }

  // 5. Category spending
  if (
    norm.includes('kategori') ||
    norm.includes('en cok neye') ||
    norm.includes('nereye harcadim')
  ) {
    return {
      intent: 'TOP_SPENDING_CATEGORIES',
      content:
        'Bu ay en yüksek harcama yaptığın kategori Kira (15.000,00 ₺, %70,6). Ardından Alışveriş (2.150,00 ₺, %10,1) ve Market (1.730,50 ₺, %8,1) geliyor.',
      data: mockCategorySpendingData,
    };
  }

  // 6. Recent transactions
  if (
    norm.includes('son islem') ||
    norm.includes('son harcama') ||
    norm.includes('harcamalarim') ||
    norm.includes('islemlerim')
  ) {
    return {
      intent: 'RECENT_TRANSACTIONS',
      content:
        'Son gerçekleştirdiğin 5 harcama işlemi listeleniyor. Toplam 2.055,50 ₺ harcama detayı aşağıda gösterilmiştir.',
      data: mockTransactionListData,
    };
  }

  // Fallback for custom questions
  return {
    intent: 'DEMO_FALLBACK',
    content:
      'Şu anda ChatBrain Faz 1 (Arayüz ve Altyapı) aşamasındayız. Bu demo sürümünde banka bakiyesi, kredi kartı borcu, aylık harcama, yaklaşan faturalar, kategori dağılımı ve son işlemler gibi temel finansal sorguları test edebilirsin.\n\nDoğal dil işleme, Spring Boot backend ve yerel Ollama (Qwen3 8B) yapay zekâ motoru sonraki fazlarda devreye alınacaktır. Önerilen sorulardan birini seçerek arayüzü deneyebilirsin.',
    data: null,
  };
}

/**
 * Mock Chat Service providing simulated responses with artificial latency
 */
export const mockChatService = {
  async sendMessage(
    sessionId: string,
    content: string
  ): Promise<SendMessageResponse> {
    const startedAt = Date.now();

    const match = resolveMockResponse(content);

    // If matching a financial intent, attempt to retrieve live data from backend
    let enrichedData: FinancialData | null = match.data;
    let liveSource = false;

    if (match.intent === 'ACCOUNT_TOTAL_BALANCE') {
      try {
        const live = await financialApi.getAccountBalances();
        if (live && live.type === 'ACCOUNT_BALANCE') {
          enrichedData = live;
          liveSource = true;
        }
      } catch {
        // Fall back to deterministic local mock data
      }
    } else if (match.intent === 'CARD_TOTAL_DEBT') {
      try {
        const live = await financialApi.getCreditCards();
        if (live && live.type === 'CARD_DEBT') {
          enrichedData = live;
          liveSource = true;
        }
      } catch {
        // Fall back
      }
    } else if (match.intent === 'UPCOMING_INVOICES') {
      try {
        const live = await financialApi.getBills('UPCOMING');
        if (live && live.type === 'INVOICE_LIST') {
          enrichedData = live;
          liveSource = true;
        }
      } catch {
        // Fall back
      }
    } else if (match.intent === 'MONTHLY_EXPENSE_TOTAL') {
      try {
        const live = await financialApi.getFinancialSummary();
        if (live && live.type === 'PERIOD_SUMMARY') {
          enrichedData = live;
          liveSource = true;
        }
      } catch {
        // Fall back
      }
    } else if (match.intent === 'TOP_SPENDING_CATEGORIES') {
      try {
        const live = await financialApi.getSpendingCategories();
        if (live && live.type === 'SPENDING_BY_CATEGORY') {
          enrichedData = live;
          liveSource = true;
        }
      } catch {
        // Fall back
      }
    } else if (match.intent === 'RECENT_TRANSACTIONS') {
      try {
        const live = await financialApi.getTransactions({ limit: 5 });
        if (live && live.type === 'TRANSACTION_LIST') {
          enrichedData = live;
          liveSource = true;
        }
      } catch {
        // Fall back
      }
    }

    // Artificial response delay between 600ms and 850ms to test loading indicators
    const elapsed = Date.now() - startedAt;
    const remainingDelay = Math.max(0, 650 - elapsed);
    if (remainingDelay > 0) {
      await new Promise((resolve) => setTimeout(resolve, remainingDelay));
    }

    const nowIso = new Date().toISOString();

    const userMessage: ChatMessage = {
      id: `user-${Date.now()}-${Math.random().toString(36).substring(2, 7)}`,
      sessionId,
      role: 'USER',
      content,
      intent: null,
      data: null,
      meta: null,
      status: 'OK',
      createdAt: nowIso,
    };

    const meta: MessageMeta = {
      intentSource: liveSource ? 'LLM' : (match.data ? 'RULES' : 'CONTEXT'),
      answerSource: liveSource ? 'LLM' : (match.data ? 'TEMPLATE' : 'STATIC'),
      degraded: false,
      model: liveSource ? 'chatbrain-backend-v1' : 'chatbrain-mock-v1',
      latencyMs: Date.now() - startedAt,
      confidence: match.data ? 0.98 : 0.85,
    };

    const assistantMessage: ChatMessage = {
      id: `asst-${Date.now()}-${Math.random().toString(36).substring(2, 7)}`,
      sessionId,
      role: 'ASSISTANT',
      content: match.content,
      intent: match.intent,
      data: enrichedData,
      meta,
      status: 'OK',
      createdAt: new Date().toISOString(),
    };

    const session: ChatSession = {
      id: sessionId,
      title: content.length > 40 ? `${content.substring(0, 38)}…` : content,
      createdAt: nowIso,
      updatedAt: nowIso,
    };

    return {
      userMessage,
      assistantMessage,
      session,
    };
  },

  async getSuggestions(): Promise<SuggestedQuestion[]> {
    return INITIAL_SUGGESTIONS;
  },
};
