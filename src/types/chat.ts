export type Money = string; // e.g. "20500.75"
export type Iso = string;

export type MessageRole = 'USER' | 'ASSISTANT';
export type MessageStatus = 'OK' | 'FAILED';

export interface DateRange {
  from: string;
  to: string;
  type: string;
  label: string;
}

// 1. Account Balance
export interface AccountItem {
  id: number;
  bankName: string;
  accountName: string;
  ibanLast4: string;
  accountType: 'CHECKING' | 'SAVINGS' | 'DEPOSIT';
  balance: Money;
}

export interface BankTotal {
  bankName: string;
  accountCount: number;
  total: Money;
}

export interface AccountBalanceData {
  type: 'ACCOUNT_BALANCE';
  totalBalance: Money;
  currency: 'TRY';
  accountCount: number;
  bankFilter: string | null;
  accounts: AccountItem[];
  banks: BankTotal[];
}

// 2. Credit Card Debt
export interface CardItem {
  id: number;
  bankName: string;
  cardName: string;
  cardLast4: string;
  creditLimit: Money;
  currentDebt: Money;
  availableLimit: Money;
  statementBalance: Money;
  nextDueDate: string | null;
  utilizationPercent: string | null;
}

export interface CardDebtData {
  type: 'CARD_DEBT';
  totalDebt: Money;
  totalLimit: Money;
  totalAvailableLimit: Money;
  totalStatementBalance: Money;
  currency: 'TRY';
  cardCount: number;
  cardFilter: string | null;
  cards: CardItem[];
}

// 3. Transactions
export interface TransactionItem {
  id: number;
  date: string;
  type: 'INCOME' | 'EXPENSE' | 'TRANSFER';
  category: string;
  categoryName: string;
  merchant: string | null;
  description: string | null;
  amount: Money;
}

export interface TransactionListData {
  type: 'TRANSACTION_LIST';
  period: DateRange | null;
  typeFilter: string | null;
  categoryFilter: string | null;
  totalCount: number;
  returnedCount: number;
  truncated: boolean;
  totalIncome: Money;
  totalExpense: Money;
  transactions: TransactionItem[];
}

// 4. Spending by Category
export interface CategorySpending {
  category: string;
  categoryName: string;
  total: Money;
  count: number;
  percentage: string | null;
}

export interface SpendingByCategoryData {
  type: 'SPENDING_BY_CATEGORY';
  period: DateRange;
  totalExpense: Money;
  focusCategory: string | null;
  categories: CategorySpending[];
}

// 5. Period Summary
export interface PeriodSummaryData {
  type: 'PERIOD_SUMMARY';
  period: DateRange;
  totalIncome: Money;
  totalExpense: Money;
  net: Money;
  savingsRatePercent: string | null;
  transactionCount: number;
  topExpenseCategories: CategorySpending[];
}

// 6. Period Comparison
export interface CategoryChange {
  category: string;
  categoryName: string;
  current: Money;
  previous: Money;
  change: Money;
  changePercent: string | null;
}

export interface PeriodComparisonData {
  type: 'PERIOD_COMPARISON';
  current: PeriodSummaryData;
  previous: PeriodSummaryData;
  expenseChange: Money;
  expenseChangePercent: string | null;
  incomeChange: Money;
  incomeChangePercent: string | null;
  netChange: Money;
  categoryChanges: CategoryChange[];
}

// 7. Invoices
export interface InvoiceItem {
  id: number;
  providerName: string;
  category: string;
  amount: Money;
  dueDate: string;
  status: 'UNPAID' | 'PAID';
  overdue: boolean;
  daysUntilDue: number | null;
  paidAt: Iso | null;
}

export interface InvoiceListData {
  type: 'INVOICE_LIST';
  filter: string;
  period: DateRange | null;
  count: number;
  totalAmount: Money;
  overdueCount: number;
  overdueAmount: Money;
  invoices: InvoiceItem[];
}

// Union of all structured financial data payloads
export type FinancialData =
  | AccountBalanceData
  | CardDebtData
  | TransactionListData
  | SpendingByCategoryData
  | PeriodSummaryData
  | PeriodComparisonData
  | InvoiceListData;

export interface MessageMeta {
  intentSource: 'LLM' | 'RULES' | 'CONTEXT';
  answerSource: 'LLM' | 'TEMPLATE' | 'STATIC';
  degraded: boolean;
  model: string;
  latencyMs: number;
  confidence: number;
  parameters?: Record<string, unknown>;
}

export interface ChatMessage {
  id: string;
  sessionId: string;
  role: MessageRole;
  content: string;
  intent: string | null;
  data: FinancialData | null;
  meta: MessageMeta | null;
  status: MessageStatus;
  createdAt: Iso;
}

export interface ChatSession {
  id: string;
  title: string;
  createdAt: Iso;
  updatedAt: Iso;
}

export interface SendMessageRequest {
  content: string;
}

export interface SendMessageResponse {
  userMessage: ChatMessage;
  assistantMessage: ChatMessage;
  session: ChatSession;
}

export interface ApiError {
  timestamp: Iso;
  status: number;
  code: string;
  message: string;
  path: string;
  traceId: string | null;
  fieldErrors?: { field: string; message: string }[];
}

export interface SuggestedQuestion {
  id: string;
  text: string;
}
