# ChatBrain Comprehensive Audit Progress

Updated: 2026-11-11

## Current architecture

- **Frontend:** React 18, TypeScript, Vite. The runtime `chatService` calls `/api/v1/chat`; Vite proxies `/api` to Spring Boot on port 8090. Financial responses dispatch to typed cards.
- **Backend:** Java 17, Spring Boot 3.3.5, Maven. Controllers use `FinancialService` and the `FinancialDataProvider` abstraction. Money arithmetic uses `BigDecimal`.
- **AI:** Backend-only Ollama client targets `http://127.0.0.1:11434`, default model `qwen3:8b`. Qwen3 thinking mode is disabled for structured replies. A local model outage, timeout, blank output, malformed JSON, or rejected claim leaves the verified deterministic response in place.
- **Financial data:** Standalone profile uses fixed mock fixtures and reference date `2026-11-15`. Credit card products are separate product-only records; personal cards belong to user identities.
- **Security:** Standalone demo identity defaults to user 1 and permits the configured demo IDs (1, 2). BrainApp profile expects BrainApp-provided `Principal` mapping and data-source implementations. Ollama is never called by the frontend.
- **Request path:** React `chatService` -> Vite proxy -> `ChatController` -> `ChatOrchestrator` -> `FinancialQueryEngine` -> financial service/provider -> DTO adapter -> typed React card.

## Existing functionality

- Phase 1 chat UI, mascot/animation assets, welcome state, suggestions, and financial cards.
- REST endpoints for health, chat, banks, account balances, credit cards, product catalogue, bills, transactions, spending categories, and financial summary.
- Backend-derived totals and period filtering; mock provider has deterministic October/November 2026 data.
- Local Qwen3 integration with backend deterministic fallback.
- Provider and identity boundaries for future BrainApp integration.

## Completed phases

1. **Project Foundation & Chat Interface:** Existing UI and API client retained.
2. **Backend Foundation & Mock Financial Data:** Provider abstraction, DTOs, controllers, and deterministic fixtures retained.
3. **Financial Intelligence Layer:** Intent matching, date-range handling, backend calculations, and financial response cards verified; monthly-spending intent now recognizes “harcadım”.
4. **Local AI Integration:** Real local Qwen3 generation verified; structured claim guard and deterministic fallback are covered.
5. **Identity, Security & BrainApp Integration Readiness:** Demo user isolation and product-only catalogue verified; BrainApp source/identity contracts remain ready for implementation by the host app.
6. **Testing, Optimization & Finalization:** Clean backend suite, frontend contract/lifecycle suite, production build, local health/chat smoke tests, README, `.gitignore`, and this report updated.

## Fixed issues and implementation status

- **Bill/invoice API mismatch:** `BillResponse` now serializes the canonical `invoices` field consumed by the frontend. Adapter/API tests verify populated and empty lists; malformed missing invoice arrays are rejected by the frontend service. `InvoiceListCard` renders an explicit empty state.
- **Upcoming/overdue bills:** Added distinct `UPCOMING`, `OVERDUE`, `PAID`, and `UNPAID` filters. Due today is upcoming (within the inclusive 14-day window), yesterday is overdue, and paid bills are neither. Intent handling queries the matching filter.
- **Credit card catalogue/user data:** User 1 owns only three personal cards; user 2 has no personal cards. The 121-item catalogue uses `CreditCardProduct` with only product/bank/type fields and no identity, limits, debts, suffixes, or payment dates. Per-user debt aggregation is tested.
- **Catalogue endpoint exposure:** The endpoint returns only non-personal product metadata. No separate auth system was added. Standalone financial endpoints use the existing demo identity mechanism; production authentication must be supplied by BrainApp.
- **Period labels:** Provider response labels derive from requested start/end dates, with Turkish locale and month/year boundary support. The November 2026 seed default remains a fixture default, not a fixed label for custom queries.
- **AI grounding:** Model response format is structured JSON with answer, claim codes, and entities. The guard checks intent-specific claim codes, numeric/date evidence, entity membership, links, markup, and output size. Rejected output retains the deterministic template.
- **Qwen3 blank output:** Local Qwen3 was spending its short token budget in `thinking` and returning empty `content`. Ollama requests now set `think:false`; actual generation succeeded after the fix.
- **Frontend integration tests:** Kept the original mock-only Phase 1 checks and added tests against the production `chatService` with a mocked Axios adapter, malformed/empty invoice response handling, invoice rendering, six intent labels, HTTP/network failures, loading cleanup, and retry lifecycle.
- **Financial checks:** Tests verify account/card totals, income/expense/net, bill totals, card product isolation, inclusive transaction date bounds, leap dates, and year-crossing labels.
- **Project hygiene:** Added root `.gitignore`; removed the temporary source-edit script. No existing project source or tests were removed.

## Tests and live verification

- `mvn test` using Java 17: **46 tests, 46 passed, 0 failures, 0 errors, 0 skipped** (final run).
- `mvn -q -o clean test` using Java 17: **46 tests, 46 passed, 0 failures, 0 errors, 0 skipped** (final run).
- `npm.cmd test`: original mock tests **34 passed**; production integration/lifecycle suite **26 runtime assertions passed** (21 assertion sites, including six financial-intent cases).
- `npm.cmd run build`: TypeScript and Vite production build passed (1699 modules).
- Live backend: `/api/v1/health` returned `UP` with `MockFinancialDataProvider`.
- Live backend-to-Ollama chat: balance question returned `ACCOUNT_BALANCE`, `answerSource=LLM`, `degraded=false`, and backend total `145650.75` in about 2 seconds.
- Final strict-guard smoke: Qwen3 returned a verified balance and account count with `answerSource=LLM`, `degraded=false`.
- Live Vite proxy path: health endpoint returned `UP`; upcoming-bills chat returned `UPCOMING_BILLS`, an `INVOICE_LIST` with 3 invoices, 0 overdue invoices, and a successful LLM answer.
- Ollama availability: local API reachable; `qwen3:8b` is installed and generated a non-empty response with `think:false`.
- Failure/fallback tests: mocked unavailable/timeout, malformed JSON, unsupported amount/claim, and prompt injection preserve the verified template or skip model calls.
- Intermediate test runs exposed the unrecognized `harcadım` intent and an under-specified TRY currency fixture in the guard test; both were corrected and the final clean suite passes.
- Both local development servers were stopped after smoke verification.

## Remaining limitations

- This workspace does not contain BrainApp's actual authenticated services or identity implementation. The contracts cannot prove live BrainApp authorization, tenant boundaries, or persistence integration.
- Standalone mode is a demo: `X-Demo-User-Id` is not production authentication; the product catalogue is safe/public, but deployment must mount the API behind BrainApp authentication as described in the README. The local Ollama API was reachable even though the `ollama` CLI executable was not on PATH.
- Financial fixtures are fixed demo data with a configurable reference date, not a live bank connection. The default fixture range is November 2026.
- The workspace has no `.git` metadata, so Git status/diff could not be used to review the final working tree. `.gitignore` is present and generated folders are ignored.

## Next recommended actions

1. Before BrainApp integration, implement `BrainAppFinancialDataSource` using existing authorized financial services, implement `BrainAppIdentitySource`, and configure host Spring Security/session/CORS.
2. Keep Ollama optional in deployments; validate its model availability and latency on the target host.
