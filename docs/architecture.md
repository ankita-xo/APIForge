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

## 3. Backend Module Structure

The initial APIForge backend will be a modular monolith built with Spring Boot.

The modules will have clearly defined responsibilities.

```text
com.apiforge
│
├── request
│   ├── controller
│   ├── service
│   ├── dto
│   └── repository
│
├── execution
│   ├── controller
│   ├── service
│   ├── dto
│   └── client
│
├── collection
│   ├── controller
│   ├── service
│   └── repository
│
├── environment
│   ├── controller
│   ├── service
│   └── resolver
│
├── history
│   ├── controller
│   ├── service
│   └── repository
│
├── persistence
│   ├── postgres
│   └── inmemory
│
├── ai
│   ├── service
│   └── provider
│
└── common
    ├── exception
    ├── configuration
    └── logging
```

### 3.1 Request Module

Responsible for managing API requests saved by the user.

Responsibilities include:

* Create a request
* Retrieve a request
* Update a request
* Delete a request
* Store HTTP method
* Store URL
* Store headers
* Store query parameters
* Store request body

The request module does not execute the API.

Execution belongs to the Execution module.

---

### 3.2 Execution Module

The Execution module is the core of APIForge.

It is responsible for:

* Receiving an API execution request
* Resolving environment variables
* Building the HTTP request
* Sending the request to the external API
* Measuring response time
* Capturing response status
* Capturing response headers
* Capturing response body
* Handling execution errors
* Returning the result to the frontend

Example:

```text
POST /api/executions

Request
{
    "method": "GET",
    "url": "https://example.com/users",
    "headers": {},
    "queryParams": {}
}
```

The Execution module sends the request and returns information such as:

```text
Status: 200
Response Time: 142 ms
Headers: ...
Body: ...
```

---

### 3.3 Collection Module

Responsible for organizing saved API requests.

Users should eventually be able to create structures such as:

```text
My APIs
│
├── User Service
│   ├── Get User
│   ├── Create User
│   └── Delete User
│
└── Payment Service
    ├── Create Payment
    └── Payment Status
```

The Collection module manages this organization.

---

### 3.4 Environment Module

Responsible for environment-specific variables.

Example environments:

```text
Development
    BASE_URL = http://localhost:8080
    USER_ID = 101

QA
    BASE_URL = https://qa.example.com
    USER_ID = 202

Production
    BASE_URL = https://api.example.com
    USER_ID = 303
```

The environment resolver converts:

```text
{{BASE_URL}}/users/{{USER_ID}}
```

into:

```text
https://qa.example.com/users/202
```

The resolved value is then provided to the Execution module.

---

### 3.5 History Module

Responsible for storing information about previous API executions.

A history record may contain:

* Request name
* HTTP method
* URL
* Status code
* Response time
* Timestamp
* Response body

History is useful for debugging and understanding previous API behavior.

---

### 3.6 Persistence Module

The Persistence module abstracts storage from the rest of the application.

The application should interact with a storage abstraction rather than directly depending on PostgreSQL.

Conceptually:

```text
             ┌───────────────────┐
             │ Storage Interface │
             └─────────┬─────────┘
                       │
              ┌────────┴────────┐
              │                 │
              ▼                 ▼
       PostgreSQL          In-Memory
        Storage             Storage
```

This design allows APIForge to use PostgreSQL when available and fall back to in-memory storage when required.

---

### 3.7 AI Module

AI functionality will be introduced after the core API client is working.

The AI module will provide an abstraction over AI providers.

Potential capabilities include:

* Generate API test cases
* Analyze API responses
* Explain API errors
* Generate API documentation
* Suggest debugging steps

The rest of APIForge should communicate with an AI abstraction rather than directly depending on a specific AI provider.

Conceptually:

```text
APIForge
   │
   ▼
AIProvider
   │
   ├── Provider A
   │
   └── Provider B
```

This reduces provider lock-in and allows the implementation to change later.

---

### 3.8 Common Module

Contains functionality shared across multiple modules.

Examples:

* Global exception handling
* Common response models
* Configuration
* Logging
* Validation utilities

Common code should remain limited to genuinely shared functionality.

Modules should not put unrelated business logic into the common package.
