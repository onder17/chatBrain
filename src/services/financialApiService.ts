import axios, { type AxiosInstance } from 'axios';
import type {
  AccountBalanceData,
  CardDebtData,
  InvoiceListData,
  PeriodSummaryData,
  SpendingByCategoryData,
  TransactionListData,
} from '../types/chat';

export interface BankDto {
  id: number;
  code: string;
  name: string;
  logoUrl: string;
}

export interface BackendHealthDto {
  status: string;
  service: string;
  version: string;
  phase: string;
  dataProvider: string;
  timestamp: string;
}

interface BackendPeriodResponse {
  periodLabel?: string | null;
}

interface BackendTransactionsResponse extends BackendPeriodResponse {
  type: 'TRANSACTION_LIST';
  typeFilter: string | null;
  categoryFilter: string | null;
  totalCount: number;
  returnedCount: number;
  totalIncome: string | number;
  totalExpense: string | number;
  transactions: TransactionListData['transactions'];
}

interface BackendBillsResponse {
  type: 'INVOICE_LIST';
  filter: string;
  count: number;
  totalAmount: string | number;
  overdueCount: number;
  overdueAmount: string | number;
  invoices: InvoiceListData['invoices'];
}

function toPeriod(label?: string | null) {
  return {
    from: '',
    to: '',
    type: 'MONTH',
    label: label || 'Seçilen dönem',
  };
}

const toMoney = (value: string | number): string => String(value);

class FinancialApiService {
  private client: AxiosInstance;

  constructor() {
    const baseURL =
      (typeof import.meta !== 'undefined' && import.meta.env && import.meta.env.VITE_API_BASE_URL) ||
      '/api/v1';
    this.client = axios.create({
      baseURL,
      timeout: 10000,
      headers: {
        'Content-Type': 'application/json',
      },
    });
  }

  /**
   * Checks backend health and connectivity
   */
  async checkHealth(): Promise<BackendHealthDto> {
    const res = await this.client.get<BackendHealthDto>('/health');
    return res.data;
  }

  /**
   * Retrieves 10 simulated partner banks
   */
  async getBanks(): Promise<BankDto[]> {
    const res = await this.client.get<BankDto[]>('/financial/banks');
    return res.data;
  }

  /**
   * Retrieves aggregated account balances
   */
  async getAccountBalances(bankFilter?: string): Promise<AccountBalanceData> {
    const res = await this.client.get<AccountBalanceData>('/financial/accounts/balances', {
      params: bankFilter ? { bank: bankFilter } : {},
    });
    return {
      ...res.data,
      totalBalance: toMoney(res.data.totalBalance),
      accounts: res.data.accounts.map((account) => ({
        ...account,
        balance: toMoney(account.balance),
      })),
      banks: res.data.banks.map((bank) => ({ ...bank, total: toMoney(bank.total) })),
    };
  }

  /**
   * Retrieves user credit card debt and limits
   */
  async getCreditCards(filter?: string): Promise<CardDebtData> {
    const res = await this.client.get<CardDebtData>('/financial/credit-cards', {
      params: filter ? { filter } : {},
    });
    return {
      ...res.data,
      totalDebt: toMoney(res.data.totalDebt),
      totalLimit: toMoney(res.data.totalLimit),
      totalAvailableLimit: toMoney(res.data.totalAvailableLimit),
      totalStatementBalance: toMoney(res.data.totalStatementBalance),
      cards: res.data.cards.map((card) => ({
        ...card,
        creditLimit: toMoney(card.creditLimit),
        currentDebt: toMoney(card.currentDebt),
        availableLimit: toMoney(card.availableLimit),
        statementBalance: toMoney(card.statementBalance),
        utilizationPercent:
          card.utilizationPercent == null ? null : toMoney(card.utilizationPercent),
      })),
    };
  }

  /**
   * Retrieves bills / invoices
   */
  async getBills(status?: string): Promise<InvoiceListData> {
    const res = await this.client.get<BackendBillsResponse>('/financial/bills', {
      params: status ? { status } : {},
    });
    const { invoices, ...summary } = res.data;
    return {
      ...summary,
      totalAmount: toMoney(summary.totalAmount),
      overdueAmount: toMoney(summary.overdueAmount),
      period: null,
      invoices: invoices.map((bill) => ({ ...bill, amount: toMoney(bill.amount) })),
    };
  }

  /**
   * Retrieves transactions
   */
  async getTransactions(params?: {
    type?: string;
    category?: string;
    startDate?: string;
    endDate?: string;
    limit?: number;
  }): Promise<TransactionListData> {
    const res = await this.client.get<BackendTransactionsResponse>('/financial/transactions', {
      params,
    });
    const { periodLabel, ...data } = res.data;
    return {
      ...data,
      totalIncome: toMoney(data.totalIncome),
      totalExpense: toMoney(data.totalExpense),
      transactions: data.transactions.map((transaction) => ({
        ...transaction,
        amount: toMoney(transaction.amount),
      })),
      period: periodLabel ? toPeriod(periodLabel) : null,
      truncated: data.returnedCount < data.totalCount,
    };
  }

  /**
   * Retrieves spending categories breakdown
   */
  async getSpendingCategories(params?: {
    startDate?: string;
    endDate?: string;
    focusCategory?: string;
  }): Promise<SpendingByCategoryData> {
    const res = await this.client.get<SpendingByCategoryData & BackendPeriodResponse>('/financial/spending-categories', {
      params,
    });
    const { periodLabel, ...data } = res.data;
    return {
      ...data,
      totalExpense: toMoney(data.totalExpense),
      categories: data.categories.map((category) => ({
        ...category,
        total: toMoney(category.total),
        percentage: category.percentage == null ? null : toMoney(category.percentage),
      })),
      period: toPeriod(periodLabel),
    };
  }

  /**
   * Retrieves monthly financial summary
   */
  async getFinancialSummary(params?: {
    startDate?: string;
    endDate?: string;
  }): Promise<PeriodSummaryData> {
    const res = await this.client.get<
      Omit<PeriodSummaryData, 'period' | 'topExpenseCategories'> &
        BackendPeriodResponse & { topCategories: PeriodSummaryData['topExpenseCategories'] }
    >('/financial/summary', {
      params,
    });
    const { periodLabel, topCategories, ...data } = res.data;
    return {
      ...data,
      totalIncome: toMoney(data.totalIncome),
      totalExpense: toMoney(data.totalExpense),
      net: toMoney(data.net),
      savingsRatePercent:
        data.savingsRatePercent == null ? null : toMoney(data.savingsRatePercent),
      period: toPeriod(periodLabel),
      topExpenseCategories: topCategories.map((category) => ({
        ...category,
        total: toMoney(category.total),
        percentage: category.percentage == null ? null : toMoney(category.percentage),
      })),
    };
  }
}

export const financialApi = new FinancialApiService();
