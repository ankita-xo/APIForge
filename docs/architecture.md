# APIForge Architecture

## 1. Architecture Decision Rules

APIForge will be developed incrementally using a modular architecture.

The following rules will guide architectural decisions throughout the project:

### Rule 1: Build the simplest architecture that satisfies the requirement

We will avoid introducing infrastructure or technologies before they are actually required.

For example, Kafka, Redis, Kubernetes, and distributed microservices will not be introduced into the MVP unless a concrete requirement justifies them.

### Rule 2: Start as a Modular Monolith

The initial backend will be a single Spring Boot application.

The application will be organized into logical modules such as:

* Request Management
* API Execution
* Collections
* Environments
* Execution History
* Persistence
* AI Integration

Modules should have clear responsibilities and limited coupling.

The architecture should allow individual modules to evolve independently and, if required in the future, be extracted into separate services.

### Rule 3: Keep external dependencies behind abstractions

Important infrastructure dependencies should not be tightly coupled to business logic.

Examples include:

* Persistence
* AI providers
* External API execution

Where appropriate, interfaces will be used so that implementations can be replaced without changing the core business logic.

### Rule 4: API execution is a core capability

The primary purpose of APIForge is to allow users to construct and execute API requests.

Therefore, API execution should remain available even when optional infrastructure such as persistent storage is unavailable.

### Rule 5: Persistence is not a single point of failure

PostgreSQL will provide persistent storage when available.

However, the application should be able to operate using in-memory storage when PostgreSQL is unavailable.

The application should not fail to start solely because the database is unavailable.

### Rule 6: Fail gracefully

Failures should be communicated clearly to the user.

Examples:

* Invalid API URL
* External API timeout
* External API unavailable
* Invalid request configuration
* Database unavailable
* AI provider unavailable

The system should distinguish between failures in API execution and failures in optional features such as persistence or AI.

### Rule 7: Security-sensitive information should be handled carefully

APIForge may eventually handle:

* API tokens
* Authorization headers
* environment variables
* API credentials

Sensitive values should not be unnecessarily exposed in logs, error messages, or generated documentation.

AI integrations must also keep provider API keys on the backend rather than exposing them in the React frontend.

---

## 2. Persistence Fallback Boundary

APIForge separates **core API execution** from **data persistence**.

### Core capability

The following capability must continue to work without PostgreSQL:

```text
Build Request
     ↓
Execute Request
     ↓
Receive External API Response
     ↓
Display Response
```

This is the minimum functionality required for APIForge to be useful.

### Optional persistence

Persistence is used for features such as:

* Saving requests
* Saving collections
* Saving environments
* Storing execution history
* Persisting user configuration

When PostgreSQL is available:

```text
Application
     ↓
Persistence Interface
     ↓
PostgreSQL
```

When PostgreSQL is unavailable:

```text
Application
     ↓
Persistence Interface
     ↓
In-Memory Storage
```

### Fallback behavior

If PostgreSQL becomes unavailable:

1. The Spring Boot application should remain running.
2. API request execution should continue.
3. The application should switch to or use in-memory storage where supported.
4. The user should be informed that persistent storage is unavailable.
5. Data stored only in memory may be lost when the application restarts.

### Explicit boundary

The fallback applies to **persistence**, not to external API execution.

For example:

```text
PostgreSQL DOWN
       │
       ├── Send API Request ──► CONTINUES
       │
       ├── View API Response ─► CONTINUES
       │
       └── Save Request ──────► Temporary/In-Memory
```

However:

```text
External API DOWN
       │
       └── API Execution ──► FAILS WITH CLEAR ERROR
```

The system must not hide an external API failure by treating it as a database failure.

### Design principle

> Persistence is an enhancement to APIForge, not a dependency required for its core API execution capability.
