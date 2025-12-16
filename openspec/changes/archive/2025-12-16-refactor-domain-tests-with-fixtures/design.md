# Design Document: Test Fixtures Refactoring

## Context
The domain layer currently has 30 passing tests across 8 test files. Each test manually constructs domain objects, leading to significant duplication. For example, creating an appointment requires:
1. Creating a ClientId
2. Creating an AppointmentDateTime with timezone
3. Creating a Duration
4. Calling Appointment.schedule()
5. Unwrapping Result and Effect types

This boilerplate is repeated across multiple tests, making them harder to read and maintain.

**Key Stakeholders:**
- Development team (improved test maintainability)
- Future developers (easier to write new tests)

**Technical Constraints:**
- Must use Gradle's built-in `java-test-fixtures` plugin
- Fixtures must be shareable across all modules
- All existing tests must continue to pass
- Must follow domain language patterns from project.md

## Goals / Non-Goals

**Goals:**
- Create composable fixture hierarchy (low-level → high-level)
- Reduce test code duplication by 50%+
- Improve test readability with domain language
- Enable fixture reuse in application and adapter tests
- Maintain all existing test coverage

**Non-Goals:**
- Adding new test cases (scope is refactoring only)
- Changing domain implementation code
- Adding new domain functionality
- Modifying test assertions or test logic

## Decisions

### Decision 1: Composable Fixture Hierarchy
**Choice:** Three-tier fixture hierarchy: ValueObjects → Entities → Aggregates

**Rationale:**
- **ValueObjectFixtures** provides basic building blocks (IDs, dates, emails)
- **ClientFixtures** builds on ValueObjectFixtures to create clients
- **AppointmentFixtures** uses both ClientFixtures and ValueObjectFixtures
- Mirrors the actual domain model structure
- Enables maximum reuse and composability

**Example:**
```kotlin
// ValueObjectFixtures
fun clientId() = ClientId.new()
fun berlinDateTime(dateTime: LocalDateTime) =
    AppointmentDateTime.of(dateTime, ZoneId.of("Europe/Berlin"))

// ClientFixtures (uses ValueObjectFixtures)
fun clientWithEmail(
    id: ClientId = clientId(),
    name: String = "John Doe",
    email: String = "john@example.com"
) = Client.create(...)

// AppointmentFixtures (uses both)
fun scheduledAppointment(
    clientId: ClientId = clientId(),
    dateTime: AppointmentDateTime = berlinDateTime(...)
) = Appointment.schedule(...)
```

**Alternatives considered:**
- Flat fixture structure: Rejected because it would duplicate client creation logic in appointment fixtures
- One monolithic fixture object: Rejected because it violates SRP and makes navigation harder

**Impact:**
- Clear dependency hierarchy
- Easy to understand which fixtures use which
- Minimal duplication across fixture functions

### Decision 2: Domain Language Function Names
**Choice:** Use simple, domain-language function names (e.g., `scheduledAppointment()`, `clientWithEmail()`)

**Rationale:**
- Follows pattern established in project.md (Test Fixtures section)
- Reads naturally in test code
- Avoids technical jargon
- Makes tests self-documenting

**Alternatives considered:**
- createScheduledAppointment(): Rejected as too verbose
- newAppointment(): Rejected as too generic
- Backtick syntax (`a scheduled appointment`): Rejected as unnecessary for simple factory functions

**Impact:**
- Tests read like natural language
- Consistent with project guidelines
- Easier for non-technical stakeholders to understand test scenarios

### Decision 3: Default Parameters with Sensible Defaults
**Choice:** All fixture functions accept parameters with sensible defaults

**Rationale:**
- Tests only override what matters for that specific scenario
- Reduces test setup code significantly
- Provides flexibility when needed
- Common pattern in test fixtures

**Example:**
```kotlin
fun scheduledAppointment(
    clientId: ClientId = clientId(),
    dateTime: AppointmentDateTime = berlinDateTime(LocalDateTime.now().plusDays(7)),
    durationMinutes: Int = 120,
    serviceType: String = "Tattoo Session"
): Appointment
```

**Alternatives considered:**
- Builder pattern: Rejected as overkill for test fixtures
- No defaults (all required): Rejected as it doesn't reduce duplication enough

**Impact:**
- Most tests can use fixtures with zero parameters
- Easy to customize when needed
- Clear what the "normal" case looks like

### Decision 4: Unwrap Result and Effect Types in Fixtures
**Choice:** Fixtures return unwrapped domain objects, not Result/Effect wrappers

**Rationale:**
- Fixtures are for happy-path test setup
- Tests that need to verify failure cases can call domain methods directly
- Reduces `.getOrThrow()` noise in tests
- Makes test setup code cleaner

**Example:**
```kotlin
// Fixture returns Appointment, not Result<Effect<Appointment, Event>>
fun scheduledAppointment(...): Appointment {
    return Appointment.schedule(...).getOrThrow().value
}

// Tests use it simply
val appointment = scheduledAppointment()
```

**Alternatives considered:**
- Return Result types: Rejected because fixtures are for setup, not testing validation
- Return Effect types: Rejected because tests rarely need the event in setup

**Impact:**
- Cleaner test code
- Fixtures focused on happy paths
- Tests verifying failures still use domain methods directly

### Decision 5: Location in src/testFixtures
**Choice:** Place fixtures in `src/testFixtures/kotlin/com/yonatankarp/appointmentmanager/domain/fixtures/`

**Rationale:**
- Standard Gradle location for test fixtures plugin
- Automatically available to dependent modules
- Clear separation from actual tests
- Follows Gradle conventions

**Alternatives considered:**
- src/test/kotlin/fixtures: Rejected because it doesn't enable cross-module sharing
- Separate test-fixtures module: Rejected as overkill for this size project

**Impact:**
- Standard Gradle setup
- Fixtures available to all modules via `testFixtures(project(":appointment-manager-domain"))`
- Clear project structure

## Risks / Trade-offs

### Risk 1: Fixture Maintenance Overhead
**Impact:** Fixtures need to be updated when domain model changes
**Mitigation:**
- Fixtures are type-safe; compiler catches breaking changes
- Small, focused fixture functions are easy to update
- Clear hierarchy makes it obvious what needs updating
**Severity:** Low

### Risk 2: Hiding Important Setup Details
**Impact:** Fixtures with defaults might obscure important test setup
**Mitigation:**
- Tests can override any parameter
- Fixture function signatures are self-documenting
- Domain language names make it clear what's being created
**Severity:** Low

### Risk 3: Fixture Complexity Growth
**Impact:** Fixtures might become complex as more scenarios are added
**Mitigation:**
- Keep fixtures simple and focused
- Create new fixtures for distinct scenarios rather than adding complexity to existing ones
- Follow YAGNI principle - don't add fixtures until needed
**Severity:** Low

## Fixture Structure

### ValueObjectFixtures
```
clientId() -> ClientId
appointmentId() -> AppointmentId
emailAddress(email: String = "test@example.com") -> EmailAddress
phoneNumber(number: String = "+491234567890") -> PhoneNumber
berlinDateTime(dateTime: LocalDateTime = ...) -> AppointmentDateTime
duration(minutes: Int = 120) -> Duration
language(lang: Language = Language.ENGLISH) -> Language
```

### ClientFixtures
```
clientWithEmail(
    id: ClientId = clientId(),
    name: String = "John Doe",
    language: Language = Language.ENGLISH,
    email: String = "john@example.com"
) -> Client

clientWithWhatsApp(
    id: ClientId = clientId(),
    name: String = "Jane Smith",
    language: Language = Language.HEBREW,
    phone: String = "+491234567890"
) -> Client

clientWithInstagram(...) -> Client
clientWithFacebook(...) -> Client
```

### AppointmentFixtures
```
scheduledAppointment(
    clientId: ClientId = clientId(),
    dateTime: AppointmentDateTime = berlinDateTime(...),
    durationMinutes: Int = 120,
    serviceType: String = "Tattoo Session"
) -> Appointment

cancelledAppointment(
    baseAppointment: Appointment = scheduledAppointment(...),
    reason: String? = "Client requested"
) -> Appointment

appointmentInPast(...) -> Appointment
appointmentInFuture(...) -> Appointment
overlappingAppointment(existing: Appointment, ...) -> Appointment
```

## Migration Plan

**Rollout:**
1. Add java-test-fixtures plugin to domain module build.gradle.kts
2. Create ValueObjectFixtures with most common value objects
3. Create ClientFixtures using ValueObjectFixtures
4. Create AppointmentFixtures using ClientFixtures and ValueObjectFixtures
5. Refactor tests one file at a time (8 files total)
6. Verify all 30 tests still pass after each file refactoring
7. Update application and adapters modules to use fixtures
8. Document fixture usage patterns

**Rollback:**
- If issues arise, revert to direct domain object construction
- No production code changes, low risk
- Can refactor incrementally (one test file at a time)

## Success Metrics

**Quantitative:**
- [ ] Reduce average lines of code per test by 30%+
- [ ] All 30 existing tests pass
- [ ] Zero test logic changes (only setup refactoring)
- [ ] Fixtures available in application and adapters modules

**Qualitative:**
- [ ] Tests are easier to read and understand
- [ ] New tests are faster to write
- [ ] Test setup is consistent across all tests
- [ ] Domain language is clear in test code
