# Project Context

## Purpose
This is an appointment scheduling system designed for a tattoo artist business. The system helps manage client appointments with the following key features:

- **Google Calendar Integration**: Sync appointment times with Google Calendar
- **Multi-Channel Reminders**: Send appointment reminders via Instagram, WhatsApp, Facebook, or email
- **Automated Notifications**: Cron jobs to send reminders one day before appointments
- **Post-Appointment Care**: Send tattoo care instructions after appointments
- **Multi-Language Support**: Currently supports Hebrew, English, and German
- **Client Communication**: Use client's preferred communication channel and language

## Tech Stack
- **Language**: Kotlin (latest stable version)
- **JDK**: Java 21 (toolchain-managed)
- **Framework**: Spring Boot
- **Database**: PostgreSQL
- **Build Tool**: Gradle (Kotlin DSL)
- **Testing**: JUnit Jupiter (latest version)

## Project Conventions

### Code Style
- Follow standard Kotlin coding conventions
- Use Kotlin DSL for Gradle build scripts
- Package structure based on hexagonal architecture (see Architecture Patterns section)
- Naming conventions:
  - Classes: PascalCase
  - Functions/variables: camelCase
  - Constants: UPPER_SNAKE_CASE
  - Test classes: `<ClassName>Test`
  - Test functions: descriptive names with backticks for readability

### Architecture Patterns
This project follows **Domain-Driven Design (DDD)** with **Hexagonal Architecture** (Ports & Adapters):

**Module Structure**:
```
domain/
  ├── entities/
  ├── valueobjects/
  ├── aggregates/
  ├── events/
  └── services/

application/
  ├── ports/
  │   ├── input/          # Inbound ports (use case interfaces)
  │   └── output/         # Outbound ports (repository/service interfaces)
  └── usecases/           # Business use cases implementing input ports

adapters/
  ├── input/
  │   ├── rest/           # REST API controllers (inbound)
  │   ├── cli/            # Command-line interface (if needed)
  │   └── scheduler/      # Cron job handlers
  └── output/
      ├── rest/           # HTTP REST clients for external services
      └── persistence/    # Database implementations
```

**Key Principles**:
- **SOLID principles** must be followed strictly
- **Single Responsibility Principle (SRP)**: Each class should have one reason to change
- **Dependency Inversion**: Domain depends on nothing; all dependencies point inward
- Domain layer must remain framework-agnostic
- Application layer defines ports (interfaces), adapters implement them
- Output adapters are organized by technology (REST, persistence, etc.), not by specific external service

### Testing Strategy
This project follows **Test-Driven Development (TDD)**:

1. **Red**: Write a failing test first
2. **Green**: Write minimal code to make the test pass
3. **Refactor**: Improve code while keeping tests green

**Test Types**:
- **Unit Tests**: Test individual classes/functions in isolation with mocks
  - Focus on domain logic and business rules
  - Mock external dependencies
- **Integration Tests**: Test components working together
  - Database integration tests
  - API endpoint tests
  - External service integration tests

**Test Coverage**: Aim for high coverage especially in domain layer (business-critical logic)

**Testing Tools**:
- JUnit Jupiter for test framework
- Mockito/MockK for mocking
- Consider Kotest for more idiomatic Kotlin testing

### Git Workflow
**Trunk-Based Development**:

- **Main Branch**: `main` - always deployable
- **Feature Branches**: Short-lived branches (1-2 days max)
  - Branch naming: `feature/<short-description>` or `fix/<short-description>`
  - Examples: `feature/google-calendar-integration`, `fix/reminder-timezone`
- **Commit Conventions**: Use conventional commits
  - `feat:` - New feature
  - `fix:` - Bug fix
  - `refactor:` - Code refactoring
  - `test:` - Adding/updating tests
  - `docs:` - Documentation changes
  - Examples: `feat: add appointment reminder service`, `fix: correct timezone handling`
- **Integration**: Frequent integration to main (at least daily)
- **Pull Requests**: Required before merging to main
- **CI/CD**: Automated tests must pass before merge

## Domain Context

### Core Domain Concepts

**Appointment**: Central aggregate representing a scheduled session
- Has a client, date/time, service type (tattoo session)
- Tracks status (scheduled, completed, cancelled)
- Associated with a specific language and communication preference

**Client**: Entity representing the tattoo artist's customer
- Preferred language (Hebrew/English/German)
- Preferred communication channel (Instagram/WhatsApp/Facebook/Email)
- Contact information for each channel

**Reminder**: Value object or entity for scheduled notifications
- Sent 1 day before appointment
- Contains appointment details in client's language

**Care Instructions**: Post-appointment tattoo care information
- Multi-language support
- Sent after appointment completion
- Channel-specific formatting

### Business Rules
- Reminders must be sent exactly 1 day before the appointment
- All communication must be in the client's preferred language
- Care instructions must be sent only after appointment completion
- Each client has a preferred communication channel that should be respected
- All times are in Berlin timezone (Europe/Berlin)

### Domain Events (to consider)
- `AppointmentScheduled`
- `AppointmentCompleted`
- `AppointmentCancelled`
- `ReminderSent`
- `CareInstructionsSent`

## Important Constraints

### Technical Constraints
- Must support Hebrew (RTL language), English, and German
- All times use Berlin timezone (Europe/Berlin)
- Cron job scheduling for automated reminders and care instructions
- Rate limiting considerations for external APIs (Instagram, WhatsApp, etc.)

### Business Constraints
- Single tattoo artist operation (not multi-tenant, for now)
- Must maintain appointment history for business records
- Client privacy: sensitive contact information must be handled securely

## External Dependencies

### Critical External Services
1. **Google Calendar API**
   - Purpose: Sync appointment times
   - Authentication: OAuth 2.0
   - Integration: REST API

2. **Instagram API (Meta Graph API)**
   - Purpose: Send appointment reminders and care instructions
   - Integration: REST API

3. **WhatsApp Business API**
   - Purpose: Send messages to clients
   - Integration: REST API

4. **Facebook Messenger API**
   - Purpose: Alternative communication channel
   - Integration: REST API (Meta Graph API)

5. **Email Service**
   - Purpose: Email notifications
   - Integration: SMTP or REST API (e.g., SendGrid)

### Infrastructure Dependencies
- **PostgreSQL Database**: Primary data store
- **Cron Scheduler**: For automated reminders (Spring @Scheduled or Quartz)
- **Logging/Monitoring**: Structured logging for production
- **Secret Management**: Secure storage for API keys and credentials
