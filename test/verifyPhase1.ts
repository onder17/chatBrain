import { mockChatService, INITIAL_SUGGESTIONS } from '../src/services/mockChatService';
import { formatMoney, formatPercent, signedMoney } from '../src/utils/format';

async function runTests() {
  console.log('--- STARTING CHATBRAIN PHASE 1 AUTOMATED TESTS ---\n');

  let passed = 0;
  let failed = 0;

  function assert(condition: boolean, testName: string, detail?: string) {
    if (condition) {
      console.log(`[PASS] ${testName}`);
      passed++;
    } else {
      console.error(`[FAIL] ${testName}${detail ? ` -> ${detail}` : ''}`);
      failed++;
    }
  }

  // 1. Initial Suggestions Test
  assert(INITIAL_SUGGESTIONS.length === 6, 'Suggestions count is exactly 6');
  assert(
    INITIAL_SUGGESTIONS[0].text === 'Toplam banka bakiyem ne kadar?',
    'Suggestion 1 matches spec'
  );
  assert(
    INITIAL_SUGGESTIONS[1].text === 'Kredi kartı borcum ne kadar?',
    'Suggestion 2 matches spec'
  );
  assert(
    INITIAL_SUGGESTIONS[2].text === 'Bu ay toplam ne kadar harcadım?',
    'Suggestion 3 matches spec'
  );
  assert(
    INITIAL_SUGGESTIONS[3].text === 'Yaklaşan faturalarım neler?',
    'Suggestion 4 matches spec'
  );
  assert(
    INITIAL_SUGGESTIONS[4].text === 'En çok hangi kategoride harcama yaptım?',
    'Suggestion 5 matches spec'
  );
  assert(
    INITIAL_SUGGESTIONS[5].text === 'Son işlemlerimi göster.',
    'Suggestion 6 matches spec'
  );

  // 2. Question 1: Bank Balances
  const res1 = await mockChatService.sendMessage('s1', INITIAL_SUGGESTIONS[0].text);
  assert(res1.assistantMessage.intent === 'ACCOUNT_TOTAL_BALANCE', 'Question 1 intent is ACCOUNT_TOTAL_BALANCE');
  assert(res1.assistantMessage.data?.type === 'ACCOUNT_BALANCE', 'Question 1 returns AccountBalanceData');
  assert(
    res1.assistantMessage.data?.type === 'ACCOUNT_BALANCE' &&
    res1.assistantMessage.data.totalBalance === '145650.75',
    'Question 1 total balance is 145650.75'
  );
  assert(
    res1.assistantMessage.data?.type === 'ACCOUNT_BALANCE' &&
    res1.assistantMessage.data.accounts.length === 3,
    'Question 1 returns 3 bank accounts'
  );

  // 3. Question 2: Credit Card Debt
  const res2 = await mockChatService.sendMessage('s1', INITIAL_SUGGESTIONS[1].text);
  assert(res2.assistantMessage.intent === 'CARD_TOTAL_DEBT', 'Question 2 intent is CARD_TOTAL_DEBT');
  assert(res2.assistantMessage.data?.type === 'CARD_DEBT', 'Question 2 returns CardDebtData');
  assert(
    res2.assistantMessage.data?.type === 'CARD_DEBT' &&
    res2.assistantMessage.data.totalDebt === '20500.75',
    'Question 2 total debt is 20500.75'
  );
  assert(
    res2.assistantMessage.data?.type === 'CARD_DEBT' &&
    res2.assistantMessage.data.totalLimit === '125000.00',
    'Question 2 total limit is 125000.00'
  );

  // 4. Question 3: Monthly Expenses
  const res3 = await mockChatService.sendMessage('s1', INITIAL_SUGGESTIONS[2].text);
  assert(res3.assistantMessage.intent === 'MONTHLY_EXPENSE_TOTAL', 'Question 3 intent is MONTHLY_EXPENSE_TOTAL');
  assert(res3.assistantMessage.data?.type === 'PERIOD_SUMMARY', 'Question 3 returns PeriodSummaryData');
  assert(
    res3.assistantMessage.data?.type === 'PERIOD_SUMMARY' &&
    res3.assistantMessage.data.totalExpense === '21245.49',
    'Question 3 total expense is 21245.49'
  );

  // 5. Question 4: Invoices
  const res4 = await mockChatService.sendMessage('s1', INITIAL_SUGGESTIONS[3].text);
  assert(res4.assistantMessage.intent === 'UPCOMING_INVOICES', 'Question 4 intent is UPCOMING_INVOICES');
  assert(res4.assistantMessage.data?.type === 'INVOICE_LIST', 'Question 4 returns InvoiceListData');
  assert(
    res4.assistantMessage.data?.type === 'INVOICE_LIST' &&
    res4.assistantMessage.data.invoices.length === 4,
    'Question 4 returns 4 invoices'
  );

  // 6. Question 5: Top Categories
  const res5 = await mockChatService.sendMessage('s1', INITIAL_SUGGESTIONS[4].text);
  assert(res5.assistantMessage.intent === 'TOP_SPENDING_CATEGORIES', 'Question 5 intent is TOP_SPENDING_CATEGORIES');
  assert(res5.assistantMessage.data?.type === 'SPENDING_BY_CATEGORY', 'Question 5 returns SpendingByCategoryData');

  // 7. Question 6: Recent Transactions
  const res6 = await mockChatService.sendMessage('s1', INITIAL_SUGGESTIONS[5].text);
  assert(res6.assistantMessage.intent === 'RECENT_TRANSACTIONS', 'Question 6 intent is RECENT_TRANSACTIONS');
  assert(res6.assistantMessage.data?.type === 'TRANSACTION_LIST', 'Question 6 returns TransactionListData');
  assert(
    res6.assistantMessage.data?.type === 'TRANSACTION_LIST' &&
    res6.assistantMessage.data.transactions.length === 5,
    'Question 6 returns 5 transactions'
  );

  // 8. Fallback Question
  const resFallback = await mockChatService.sendMessage('s1', 'Yarın İstanbul hava durumu nasıl?');
  assert(resFallback.assistantMessage.intent === 'DEMO_FALLBACK', 'Unknown question receives DEMO_FALLBACK');
  assert(resFallback.assistantMessage.data === null, 'Fallback has null data');
  assert(resFallback.assistantMessage.content.includes('ChatBrain Faz 1'), 'Fallback contains informative phase context');

  // 9. Latency check
  assert(
    (res1.assistantMessage.meta?.latencyMs ?? 0) >= 600,
    'Simulated delay is >= 600ms to allow loading indicator inspection'
  );

  // 10. Formatters
  assert(formatMoney('145650.75').includes('145.650,75'), 'formatMoney formats TR thousands and decimals');
  assert(formatPercent('53.3').includes('53,3') && formatPercent('53.3').startsWith('%'), 'formatPercent formats correctly with %');
  assert(signedMoney('275.00', 'EXPENSE').startsWith('−'), 'signedMoney prefixes expense with −');
  assert(signedMoney('42000.00', 'INCOME').startsWith('+'), 'signedMoney prefixes income with +');

  console.log(`\nTEST SUMMARY: ${passed} Passed, ${failed} Failed`);
  if (failed > 0) {
    process.exit(1);
  }
}

runTests().catch((err) => {
  console.error(err);
  process.exit(1);
});
