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
- Do not add comments unless explicitly asked for
- Naming conventions:
  - Classes: PascalCase
  - Functions/variables: camelCase
  - Constants: UPPER_SNAKE_CASE
  - Test classes: `<ClassName>Test`
  - Test functions: descriptive names with backticks for readability

### Build Configuration
**Gradle Conventions**:
- Use multi-line includes in `settings.gradle.kts`:
  ```kotlin
  include(
      "appointment-manager-domain",
      "appointment-manager-application",
      "appointment-manager-adapters",
  )
  ```
- All dependencies managed via version catalog (`gradle/libs.versions.toml`)
- Use `alias(libs.plugins.*)` for plugins, never hardcode versions
- Trailing commas in multi-line collections
- Module naming: `appointment-manager-{layer}` pattern

### Architecture Patterns
This project follows **Domain-Driven Design (DDD)** with **Hexagonal Architecture** (Ports & Adapters):

**Module Structure**:
```
appointment-manager-domain/
  ├── entities/
  ├── valueobjects/
  ├── aggregates/
  ├── events/
  └── services/

appointment-manager-application/
  ├── ports/
  │   ├── input/          # Inbound ports (use case interfaces)
  │   └── output/         # Outbound ports (repository/service interfaces)
  └── usecases/           # Business use cases implementing input ports

appointment-manager-adapters/
  ├── input/
  │   ├── rest/           # REST API controllers (inbound)
  │   ├── cli/            # Command-line interface (if needed)
  │   └── scheduler/      # Cron job handlers
  └── output/
      ├── rest/           # HTTP REST clients for external services
      └── db/
          └── postgres/   # PostgreSQL database implementations
```

**Key Principles**:
- **SOLID principles** must be followed strictly
- **Single Responsibility Principle (SRP)**: Each class should have one reason to change
- **Dependency Inversion**: Domain depends on nothing; all dependencies point inward
- Domain layer must remain framework-agnostic
- Application layer defines ports (interfaces), adapters implement them
- Output adapters are organized by technology using hierarchical naming:
  - Use pattern: `output/{category}/{technology}`
  - Database adapters: `output/db/postgres/`, `output/db/redis/`
  - External API adapters: `output/rest/google-calendar/`, `output/rest/instagram/`
  - NOT flat naming: ~~`output/postgres/`~~, ~~`output/persistence/`~~
  - Principle: Name by specific technology, not abstract concepts

### Testing Strategy
This project follows **Acceptance Test-Driven Development (ATDD)** with **Test-Driven Development (TDD)**:

1. **Red**: Write a failing test first
2. **Green**: Write minimal code to make the test pass
3. **Refactor**: Improve code while keeping tests green

**Test Types**:
- **Acceptance Tests (ATDD/BDD)**: Written in BDD style using Kotlin DSL with Given-When-Then structure
  - Use Kotlin's backtick syntax for readable step function names
  - Organize tests with Given/When/Then blocks using Kotlin DSL
  - Focus on business scenarios and user acceptance criteria
  - Written before implementation to capture requirements
  - Example test structure:
    ```kotlin
    @Test
    fun `should send care instructions after an appointment`() {
        Given {
            `an artist scheduled an appointment`()
            `the appointment is due today`()
        }

        When {
            `the appointment has passed`()
        }

        Then {
            `we expect take care instructions to be sent to the client`()
        }
    }
    ```
  - Use Kotest's BDD DSL or create custom Given/When/Then DSL functions
  - Step functions use backtick syntax for natural language readability
  - Test end-to-end user journeys and business workflows
  - Tests serve as executable specifications

- **Unit Tests**: Test individual classes/functions in isolation with mocks
  - Focus on domain logic and business rules
  - Mock external dependencies
  - Use Given/When/Then structure with backtick-named helper functions
  - Example unit test structure:
    ```kotlin
    @Test
    fun `should calculate appointment reminder time correctly`() {
        Given {
            `an appointment scheduled for tomorrow at 2pm`()
        }

        When {
            `calculating the reminder time`()
        }

        Then {
            `reminder time should be today at 2pm`()
        }
    }
    ```

- **Integration Tests**: Test components working together
  - Database integration tests
  - API endpoint tests
  - External service integration tests
  - Use BDD-style naming to describe integration scenarios

**Test Coverage**: Aim for high coverage especially in domain layer (business-critical logic)

**Testing Tools**:
- Kotest for BDD-style DSL and expressive matchers
- JUnit Jupiter for additional test framework support
- MockK for mocking (Kotlin-friendly)
- Kotest matchers for fluent assertions

**Assertion Style**:
- Prefer Kotest infix matchers for readability:
  - Use: `result.isSuccess shouldBe true`
  - Avoid: ~~`assertTrue(result.isSuccess)`~~
- Common Kotest matchers: `shouldBe`, `shouldNotBe`, `shouldContain`, `shouldBeEmpty`, etc.
- Infix notation reads like natural language

**ATDD/BDD Requirements**:
- All tests (acceptance, unit, integration) must use Given-When-Then structure
- **Given/When/Then Implementation**:
  - For straightforward unit tests: use simple comments
    ```kotlin
    @Test
    fun `should validate phone number`() {
        // Given
        val phoneNumber = "+491234567890"

        // When
        val result = PhoneNumber.of(phoneNumber)

        // Then
        result.isSuccess shouldBe true
    }
    ```
  - Extract test data to variables in the Given block (avoid mixing Given and When)
  - For complex acceptance tests: use Kotest BDD DSL or custom DSL with step functions
- Step functions within blocks must use Kotlin's backtick syntax for readability
- Function names should read like natural language and describe business behavior
- Acceptance tests should be written BEFORE implementation (outside-in TDD)
- Tests should serve as living documentation of system behavior
- Avoid technical jargon; use business domain language in test names
- Each Given/When/Then block can contain multiple step functions
- Step functions should be reusable across different test scenarios

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
