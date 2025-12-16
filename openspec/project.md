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
- **IMPORTANT: Do not add ANY comments (including KDoc, doc comments, or inline comments) unless explicitly asked for**
  - This applies to ALL code: classes, functions, properties, enums, sealed classes, etc.
  - Write self-explanatory code that doesn't require comments
  - Use descriptive names for types and functions instead of adding documentation
- Naming conventions:
  - Classes: PascalCase
  - Functions/variables: camelCase
  - Constants: UPPER_SNAKE_CASE
  - Test classes: `<ClassName>Test`
  - Test functions: descriptive names with backticks for readability
  - Factory methods that generate new instances: Use `fun new()` instead of `fun generate()` or `fun create()`
    - Example: `ClientId.new()` not `ClientId.generate()`
    - Example: `AppointmentId.new()` not `AppointmentId.generate()`
- Single-expression functions:
  - For single-line functions, use expression body without explicit return type when clear from context
  - Example: `fun of(value: String) = Result.success(PhoneNumber(value))`
  - NOT: `fun of(value: String): Result<PhoneNumber> { return Result.success(PhoneNumber(value)) }`
  - Use when return type and behavior are obvious from the expression
- Validation and assertions:
  - Use Kotlin's idiomatic `require()` for preconditions and `check()` for state validation
  - Prefer `require()` and `check()` over explicit if-statements with exceptions
  - Example: `require(value.startsWith("+")) { "Phone number must start with +" }`
  - NOT: `if (!value.startsWith("+")) { throw IllegalArgumentException("...") }`
  - For factory methods returning `Result<T>`, catch exceptions and convert to Result.failure
  - Use `runCatching { }` to wrap validation logic that uses require/check
- Infix functions for DSL-like APIs:
  - Use infix functions to create readable, fluent APIs in value objects
  - Example: `Duration ofMinutes 120` instead of `Duration.ofMinutes(120)`
  - Pattern: `companion object { infix fun ofMinutes(minutes: Int) = ... }`
  - Benefits: More natural language-like syntax, better readability in tests and business logic
- Domain layer parameter conventions:
  - **NEVER use default parameters in domain layer** - all values must be explicit
  - Default parameters couple domain to infrastructure concerns
  - Example: BAD: `fun of(localDateTime: LocalDateTime, zoneId: ZoneId = ZoneId.of("Europe/Berlin"))`
  - Example: GOOD: `fun of(localDateTime: LocalDateTime, zoneId: ZoneId)`
  - Rationale: Domain should be pure; configuration/defaults belong in application/adapter layers

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

### OpenSpec Workflow
**Implementation Completion Requirements**:

Every OpenSpec change implementation MUST be completed with the following validation steps:

1. **Build Validation**: Call `@gradle-build-runner` to ensure the build is successful
   - Verifies that `./gradlew build` completes without errors
   - Confirms all modules compile correctly
   - Ensures all tests pass

2. **Code Standards Compliance**: Call `@openspec-compliance-checker` to ensure code standards are enforced
   - Validates adherence to project conventions
   - Checks hexagonal architecture boundaries
   - Verifies test coverage and quality standards
   - Confirms OpenSpec delta requirements are met

**These validation steps are MANDATORY** and must be completed before marking any OpenSpec change as complete.

**Build Validation During Development**:
- **ALWAYS use the `gradle-build-runner` agent for ALL build-related tasks**:
  - Running tests (`./gradlew test`, `./gradlew :module:test`)
  - Building the project (`./gradlew build`)
  - Compiling code (`./gradlew compileKotlin`)
  - Any Gradle command execution
  - During TDD cycles to verify tests pass/fail
  - After making code changes to verify compilation
- **NEVER run Gradle commands directly via Bash** for build/test tasks - use the Task tool with `subagent_type: "gradle-build-runner"` instead
- The `gradle-build-runner` agent provides:
  - Proper test output formatting and error reporting
  - Build validation with clear success/failure status
  - Better error diagnostics for compilation issues
  - Consistent build verification workflow
- Exception: The gradle-build-runner agent is only available when listed in your available agents. If not available, fall back to using Bash with `./gradlew` commands

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

**Domain Layer Purity**:
- **No infrastructure concerns in domain**: Domain entities and value objects must not contain infrastructure-specific logic or configuration
  - BAD: Hardcoding timezone `ZoneId.of("Europe/Berlin")` in domain value objects
  - GOOD: Accept timezone as parameter; application layer provides the configured value from `application.yml`
  - Rationale: Infrastructure concerns (timezones, locales, external service URLs) should be configurable from adapters layer
- **Effect pattern for domain events**: Use `Effect<T, E: DomainEvent>` to combine state changes with domain events
  - Pattern: `data class Effect<out T, out E : DomainEvent>(val value: T, val event: E)`
  - Methods return `Result<Effect<Aggregate, DomainEvent>>` instead of `Result<Pair<Aggregate, DomainEvent>>`
  - Example: `fun cancel(...): Result<Effect<Appointment, AppointmentCancelled>>`
  - Benefits: Type-safe, self-documenting, clear separation of state and events
  - Helper: `fun <T, E : DomainEvent> effect(value: T, event: E) = Effect(value, event)`
- **Domain services as pure functions**: Domain services receive all necessary data as parameters
  - Services do not depend on repositories or external services
  - Application layer fetches data and passes to domain services
  - Example: `fun detectConflicts(newAppointment: Appointment, existingAppointments: List<Appointment>)`
  - Benefits: Easy to test (no mocking), follows functional programming principles, maintains hexagonal architecture
- **Time-based vs explicit state transitions**: Consider whether domain behaviors should be explicit methods or derived from time/state
  - Explicit: Operations that require business logic or validation (e.g., `cancel()` requires 24-hour notice)
  - Derived: States that are automatically determined by time or other factors (e.g., appointments complete when time passes)
  - Decision criteria: If business rules govern the transition, make it explicit; if it's deterministic based on external factors, derive it

### Testing Strategy
This project follows **Acceptance Test-Driven Development (ATDD)** with **Test-Driven Development (TDD)**:

**MANDATORY TDD WORKFLOW** - MUST be followed strictly:
1. **Red**: Write ONE failing test first (ALWAYS write a single test before ANY implementation code)
2. **Green**: Write minimal code to make THAT ONE test pass
3. **Refactor**: Improve code while keeping tests green
4. **Repeat**: Go back to step 1 for the next test case

**CRITICAL RULES**:
- NEVER write production code before writing a failing test
- NEVER write ALL tests at once - write ONE test at a time
- NEVER write implementation code without a corresponding test for behavior
- The TDD cycle is: ONE test → implementation → refactor → NEXT test
- DO NOT batch write multiple tests before implementation
- DO NOT write complete test suites upfront
- Tests must be written FIRST, but ONE AT A TIME
- DO NOT write meaningless tests (e.g., testing that an instance is an instance of its own type)
- DO NOT test simple data structures without behavior (enums without methods, sealed classes without logic, simple data holders)
- ONLY write tests for actual behavior:
  - Value objects WITH validation (PhoneNumber.of(), EmailAddress.of())
  - Entities with business logic (Appointment.cancel(), Client.updateContactInfo())
  - Domain services (AppointmentConflictDetector, CancellationPolicyValidator)
  - Methods that can fail or have conditions

**TDD PROCESS EXAMPLE**:
1. Write first test (e.g., "should create phone number with valid format") → FAILS
2. Implement minimal code to make it pass → GREEN
3. Write second test (e.g., "should reject phone without plus sign") → FAILS
4. Update implementation to handle this case → GREEN
5. Continue one test at a time until all scenarios covered

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
  - MUST use Given/When/Then structure with comments (// Given, // When, // Then)
  - Extract test data to variables in the Given block (avoid mixing Given and When)
  - Example unit test structure for simple tests:
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
  - For complex tests with multiple steps, use backtick-named helper functions:
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

**Test Fixtures**:
- **Use Gradle's `java-test-fixtures` plugin** for sharing test fixtures across modules
  - Add `java-test-fixtures` plugin to module's `build.gradle.kts`
  - Place fixtures in `src/testFixtures/kotlin/` directory
  - Other modules consume via: `testImplementation(testFixtures(project(":module-name")))`
  - Benefits: Type-safe, properly scoped, sharable across all test types

- **Fixture Organization Pattern**:
  - **One fixture file per domain type** (e.g., ClientFixtures.kt, AppointmentFixtures.kt)
  - **Wrap fixtures in objects** to avoid polluting global scope
  - Example:
    ```kotlin
    object ClientFixtures {
        fun emailClient() = Client.create(...).getOrThrow()
        fun whatsAppClient() = Client.create(...).getOrThrow()
    }
    ```
  - **Use static imports** for readability in fixture implementations:
    ```kotlin
    import com.example.fixtures.ClientIdFixtures.clientId

    fun emailClient() = Client.create(
        name = clientName(),  // Clean, no object prefix
        contactInfo = emailContact(),
    )
    ```

- **Fixture Naming Conventions**:
  - **Generic fixtures with parameters**: `fun clientName(name: String)` - flexible for custom values
  - **Specific named fixtures without parameters**: `fun johnDoe()`, `fun aliceWonder()` - common test personas
  - **Domain language naming**: `emailClient()` not `clientWithEmail()` - reads naturally
  - **Communication variants**: `emailClient()`, `whatsAppClient()`, `instagramClient()`, `facebookClient()`
  - Pattern: Provide both generic (with params) and specific (no params) variants when useful

- **Fixture Composition Hierarchy**:
  - Build fixtures in layers: ValueObjects → Entities → Aggregates
  - Higher-level fixtures use lower-level ones
  - Example:
    ```kotlin
    // ValueObjectFixtures
    fun clientId() = ClientId.new()
    fun emailAddress() = EmailAddress.of("test@example.com").getOrThrow()

    // EntityFixtures (uses ValueObjectFixtures)
    fun emailClient() = Client.create(
        name = johnDoe(),
        contactInfo = emailContact(),
    ).getOrThrow()

    // AggregateFixtures (uses EntityFixtures + ValueObjectFixtures)
    fun scheduledAppointment() = Appointment.schedule(
        clientId = clientId(),
        dateTime = berlinDateTime(),
    ).getOrThrow().value
    ```

- **When to Use Fixtures**:
  - ✅ **DO use** in entity tests, aggregate tests, service tests, integration tests
  - ❌ **DON'T use** in value object validation tests - these test the factory methods themselves
  - Example: `EmailAddressTest` tests `EmailAddress.of()` validation, so it doesn't use `emailAddress()` fixture
  - Fixtures are for reducing setup boilerplate in higher-level tests, not for testing the builders

- **Fixture Function Design**:
  - Return unwrapped values (call `.getOrThrow()` in fixture, not in tests)
  - Provide sensible defaults that work for most test cases
  - Allow parameter overrides for specific test scenarios
  - Example:
    ```kotlin
    fun scheduledAppointment(
        clientId: ClientId = clientId(),
        dateTime: AppointmentDateTime = berlinDateTime(),
        duration: Duration = duration(),
        serviceType: String = "Tattoo Session",
    ) = Appointment.schedule(clientId, dateTime, duration, serviceType).getOrThrow().value
    ```

- Benefits: Tests become more readable, setup code is reusable, domain language is consistent across all test types

**Testing Tools**:
- JUnit Jupiter for test framework (use @Test annotation and JUnit test structure)
- Kotest assertions ONLY for matchers (shouldBe, shouldNotBe, etc.) - NO Kotest test specs
- MockK for mocking (Kotlin-friendly)
- DO NOT use Kotest FunSpec, StringSpec, or any Kotest test specs - use JUnit @Test methods only

**Assertion Style**:
- Prefer Kotest infix matchers for readability:
  - Use: `result.isSuccess shouldBe true`
  - Avoid: ~~`assertTrue(result.isSuccess)`~~
- Common Kotest matchers: `shouldBe`, `shouldNotBe`, `shouldContain`, `shouldBeEmpty`, etc.
- Infix notation reads like natural language
- Null safety in tests:
  - **NEVER use not-null assertion (`!!`)** - always use safe assertions
  - Use `shouldNotBeNull()` assertion before accessing nullable values
  - Example pattern:
    ```kotlin
    val effect = result.getOrNull()
    effect.shouldNotBeNull()
    effect.value.id shouldBe expectedId
    ```
  - Alternative with safe call: `result.getOrNull()?.value shouldBe "expected"`
  - NOT: `result.getOrNull()!!.value shouldBe "expected"`
  - Rationale: `shouldNotBeNull()` provides clear test failure messages; `!!` throws NPE without context

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

### Value Object Patterns

**Factory Method Patterns**:
- Use `of()` for value objects with validation that can fail
  - Returns `Result<T>` to make failure explicit
  - Example: `PhoneNumber.of("+491234567890")` returns `Result<PhoneNumber>`
  - Use `runCatching { }` with `require()` statements for validation
- Use `new()` for ID generation that cannot fail
  - Example: `ClientId.new()` generates new UUID-based ID
  - Returns the value object directly, not wrapped in Result
- Consider infix functions for fluent, readable APIs
  - Example: `Duration ofMinutes 120` instead of `Duration.ofMinutes(120)`
  - Use when it creates more natural, domain-language-like expressions

**Inline Value Classes**:
- Use `@JvmInline value class` for zero-cost type safety
- Wraps primitives (String, Int, UUID) with domain meaning
- No runtime overhead - compiles to primitives
- Examples: `ClientId`, `AppointmentId`, `PhoneNumber`, `EmailAddress`, `Duration`

**Validation Strategies**:
- Validate once at construction time (fail-fast)
- Make invalid states unrepresentable (can't create invalid value objects)
- Place validation in factory methods, not primary constructor
- Return `Result<T>` for operations that can fail due to business rules

**Sealed Classes for Domain Concepts**:
- Use sealed classes when variants have different data or behavior
- Example: `ContactInformation` sealed class with variants for Instagram, WhatsApp, Facebook, Email
- Benefits: Exhaustive when expressions, type-safe pattern matching
- Each variant can have its own properties while sharing common interface

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
