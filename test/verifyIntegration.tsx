import assert from 'node:assert/strict';
import axios, { AxiosError, type AxiosAdapter } from 'axios';
import React from 'react';
import { renderToStaticMarkup } from 'react-dom/server';
import { ChatServiceImpl } from '../src/services/chatService';
import { InvoiceListCard } from '../src/components/chat/cards/InvoiceListCard';
import { runChatRequest } from '../src/hooks/chatRequest';
import type { SendMessageResponse } from '../src/types/chat';

const invoicePayload: SendMessageResponse = {
  userMessage: { id: 'u1', sessionId: 's1', role: 'USER', content: 'Faturalarım', intent: null, data: null, meta: null, status: 'OK', createdAt: '2026-11-11T00:00:00Z' },
  assistantMessage: {
    id: 'a1', sessionId: 's1', role: 'ASSISTANT', content: '3 fatura', intent: 'UPCOMING_BILLS', status: 'OK', createdAt: '2026-11-11T00:00:01Z', meta: null,
    data: { type: 'INVOICE_LIST', filter: 'UPCOMING', period: null, count: 1, totalAmount: '912.75', overdueCount: 0, overdueAmount: '0.00', invoices: [
      { id: 2, providerName: 'BEDAŞ', category: 'ELECTRICITY', amount: '912.75', dueDate: '2026-11-18', status: 'UNPAID', overdue: false, daysUntilDue: 7, paidAt: null },
    ] },
  },
  session: { id: 's1', title: 'Faturalarım', createdAt: '2026-11-11T00:00:00Z', updatedAt: '2026-11-11T00:00:01Z' },
};

function serviceFor(status: number, data: unknown) {
  const adapter: AxiosAdapter = async (config) => {
    if (status >= 400) throw new AxiosError(`HTTP ${status}`, AxiosError.ERR_BAD_RESPONSE, config, undefined, {
      data, status, statusText: 'Error', headers: {}, config,
    });
    return { data, status, statusText: 'OK', headers: {}, config };
  };
  return new ChatServiceImpl(axios.create({ baseURL: '/api/v1/chat', adapter }));
}

async function main() {
  const productionService = serviceFor(200, invoicePayload);
  const received = await productionService.sendMessage('s1', { content: 'Faturalarım' });
  assert.equal(received.assistantMessage.data?.type, 'INVOICE_LIST');
  assert.equal(received.assistantMessage.data?.type === 'INVOICE_LIST' && received.assistantMessage.data.invoices.length, 1);

  const markup = renderToStaticMarkup(React.createElement(InvoiceListCard, { data: received.assistantMessage.data as Extract<NonNullable<typeof received.assistantMessage.data>, {type:'INVOICE_LIST'}> }));
  assert.match(markup, /BEDA/);
  assert.match(markup, /912,75/);
  const emptyMarkup = renderToStaticMarkup(React.createElement(InvoiceListCard, { data: { ...received.assistantMessage.data as Extract<NonNullable<typeof received.assistantMessage.data>, {type:'INVOICE_LIST'}>, count: 0, invoices: [] } }));
  assert.match(emptyMarkup, /Toplam/);
  assert.match(emptyMarkup, /Fatura bulunamadı/);
  assert.doesNotMatch(emptyMarkup, /BEDA/);

  const intents = ['ACCOUNT_BALANCE', 'CARD_DEBT', 'MONTHLY_SUMMARY', 'SPENDING_CATEGORIES', 'UPCOMING_BILLS', 'TRANSACTIONS'];
  for (const intent of intents) {
    const response = structuredClone(invoicePayload);
    response.assistantMessage.intent = intent;
    const result = await serviceFor(200, response).sendMessage('s1', { content: intent });
    assert.equal(result.assistantMessage.intent, intent);
  }

  await assert.rejects(() => serviceFor(400, { code: 'BAD_REQUEST' }).sendMessage('s1', { content: 'x' }));
  await assert.rejects(() => serviceFor(401, { code: 'UNAUTHORIZED' }).sendMessage('s1', { content: 'x' }));
  await assert.rejects(() => serviceFor(403, { code: 'FORBIDDEN' }).sendMessage('s1', { content: 'x' }));
  await assert.rejects(() => serviceFor(500, { code: 'INTERNAL_ERROR' }).sendMessage('s1', { content: 'x' }));
  await assert.rejects(() => serviceFor(200, { ...invoicePayload, assistantMessage: { ...invoicePayload.assistantMessage, data: { type: 'INVOICE_LIST', bills: [] } } }).sendMessage('s1', { content: 'x' }), /invoices/);
  await assert.rejects(() => serviceFor(200, null).sendMessage('s1', { content: 'x' }), /geçersiz sohbet yanıtı/);

  let isLoading = false;
  let failure: unknown;
  let resolvePending: ((value: string) => void) | undefined;
  const pending = runChatRequest(() => new Promise<string>((resolve) => { resolvePending = resolve; }), {
    onLoadingChange: (value) => { isLoading = value; }, onSuccess: () => {}, onFailure: (error) => { failure = error; },
  });
  assert.equal(isLoading, true, 'loading starts before the network request resolves');
  resolvePending?.('ok');
  await pending;
  assert.equal(isLoading, false, 'loading clears after success');
  assert.equal(failure, undefined);

  let attempts = 0;
  let failedMessageMarked = false;
  const retry = async () => runChatRequest(async () => {
    attempts++;
    if (attempts === 1) throw new Error('network down');
    return 'recovered';
  }, {
    onLoadingChange: (value) => { isLoading = value; },
    onSuccess: (value) => { if (value === 'recovered') failedMessageMarked = false; },
    onFailure: () => { failedMessageMarked = true; },
  });
  await retry();
  assert.equal(failedMessageMarked, true, 'failed request is marked for retry');
  await retry();
  assert.equal(attempts, 2, 'retry sends the request again');
  assert.equal(failedMessageMarked, false, 'successful retry clears failed state');

  await assert.rejects(() => serviceFor(0, null).sendMessage('s1', { content: 'network failure' }));

  console.log('PASS production chatService HTTP contract, six financial intents, HTTP/network failures, malformed/empty invoice handling, rendering, loading, and retry lifecycle');
}

main().catch((error) => { console.error(error); process.exitCode = 1; });
