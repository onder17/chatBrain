# ChatBrain

ChatBrain is a standalone Turkish personal finance assistant with a React 18/Vite client and a Java 17/Spring Boot 3.3.5 API. The standalone profile uses deterministic mock financial data. The language model is an optional local Ollama service; it never reads the financial provider directly.

## Prerequisites

- Java 17 and Maven 3.8 or later.
- Node.js 18 or later with npm (current local verification used Node 24.11.0 and npm 11.6.1).
- Ollama with `qwen3:8b` for model-generated phrasing; backend financial answers still work when Ollama is unavailable.

## Current implementation

- The existing responsive chat UI, animated mascot, message cards, welcome state, and suggested questions are retained.
- The frontend sends messages to `POST /api/v1/chat/sessions/{sessionId}/messages` and loads suggestions from `GET /api/v1/chat/suggestions`.
- The backend resolves an allowlisted intent, runs deterministic financial services, and returns structured financial data with the response.
- In-memory follow-up context is keyed by user and session and expires after 30 minutes. It stores only the last intent and date range, not message text or financial results.
- Ollama can classify questions and return structured JSON rewrites. `AnswerGuard` checks allowed claim codes, supported amounts and dates, and entity names against backend evidence. Invalid output or an Ollama outage uses the deterministic backend-generated template. Qwen3 thinking mode is disabled for short structured responses.
- The provider boundary remains `FinancialDataProvider`. Standalone uses `MockFinancialDataProvider`; the `brainapp` profile has a `BrainAppFinancialDataProvider` adapter contract.
- Standalone identity defaults to user `1`. `X-Demo-User-Id` can select only an ID in `chatbrain.security.allowed-demo-user-ids` (defaults to `1,2`). Chat requests have a per-user rate limit and request-size validation.

## Windows CMD commands

Run these in separate CMD windows from the project directory. The backend targets Java 17; the first three lines select the installed JDK and keep Maven's cache under the current Windows user.

```bat
REM Backend tests
cd /d C:\chatBrain\backend
set "JAVA_HOME=C:\Program Files\Java\jdk-17"
set "PATH=%JAVA_HOME%\bin;%PATH%"
set "MAVEN_OPTS=-Duser.home=%USERPROFILE%"
mvn test

REM Start the backend
cd /d C:\chatBrain\backend
set "JAVA_HOME=C:\Program Files\Java\jdk-17"
set "PATH=%JAVA_HOME%\bin;%PATH%"
set "MAVEN_OPTS=-Duser.home=%USERPROFILE%"
mvn spring-boot:run

REM Check backend health (in another CMD window)
curl.exe -i http://localhost:8090/api/v1/health

REM Frontend tests and production build
cd /d C:\chatBrain
npm.cmd test
npm.cmd run build

REM Start the frontend
cd /d C:\chatBrain
npm.cmd run dev
```

The Vite development server proxies `/api` to `http://localhost:8090`. Open `http://localhost:5173` after starting both services.

## Ollama / Qwen3:8b

Install Ollama for Windows, then in a CMD window run:

```bat
ollama pull qwen3:8b
ollama serve
```

Ollama should listen on `127.0.0.1:11434`. ChatBrain rejects non-loopback Ollama URLs. Defaults can be changed with:

| Variable | Default | Purpose |
| --- | --- | --- |
| `CHATBRAIN_AI_ENABLED` | `true` | Set to `false` to disable Ollama calls and always use templates. |
| `OLLAMA_BASE_URL` | `http://127.0.0.1:11434` | Local Ollama base URL; loopback only. |
| `OLLAMA_MODEL` | `qwen3:8b` | Local Ollama model tag. |
| `OLLAMA_TIMEOUT_MS` | `12000` | Bounded connect/read timeout (500–30000 ms). |

When Ollama times out or returns malformed output, the client enters a short cooldown and serves verified template answers. A live local `qwen3:8b` response and guarded backend chat request were verified during this audit. The Spring API and financial endpoints remain available when Ollama is unavailable.

## API surface

- `GET /api/v1/health`
- `GET /api/v1/chat/suggestions`
- `POST /api/v1/chat/sessions/{sessionId}/messages` with `{ "content": "..." }`
- `GET /api/v1/financial/banks`
- `GET /api/v1/financial/accounts` and `/accounts/balances`
- `GET /api/v1/financial/credit-cards` and `/credit-cards/catalogue`
- `GET /api/v1/financial/bills?status=ALL|UNPAID|PAID|UPCOMING|OVERDUE`
- `GET /api/v1/financial/transactions?type=&category=&startDate=&endDate=&limit=`
- `GET /api/v1/financial/spending-categories?startDate=&endDate=&focusCategory=`
- `GET /api/v1/financial/summary?startDate=&endDate=`

Monetary calculations use `BigDecimal`, are performed by backend services, and are serialized as decimal strings. Date ranges are inclusive and validated. The standalone mock provider uses a fixed November 2026 fixture/reference date so existing seed-data totals remain deterministic. Date questions support the fixture month, the previous month, an explicit ISO date range, and an explicit year.

## BrainApp integration boundary

The `brainapp` Spring profile selects `BrainAppFinancialDataProvider` and `BrainAppCurrentUserProvider`. BrainApp must provide:

1. A `BrainAppFinancialDataSource` bean implemented through BrainApp's existing authorized financial services (no new ChatBrain database is needed).
2. A `BrainAppIdentitySource` bean that maps BrainApp's authenticated `Principal` to its own financial-user key.
3. Spring Security/session configuration that authenticates the browser request before it reaches ChatBrain.

The BrainApp schemas and authentication mechanism were not present in this workspace, so the source and identity contracts are defined but cannot be wired to live BrainApp services here. The standalone demo header is not production authentication.

## Test and integration status

`npm.cmd run build`, `npm.cmd test`, and `mvn test` are used for verification. The frontend suite exercises the production `chatService` through an HTTP adapter and renders invoice cards. The backend suite covers arithmetic, period labels, bill filters, user isolation, prompt-injection handling, and structured answer validation. A live Ollama generation and backend chat request were verified. BrainApp integration remains unconnected until BrainApp provides its authorized data and identity services.
