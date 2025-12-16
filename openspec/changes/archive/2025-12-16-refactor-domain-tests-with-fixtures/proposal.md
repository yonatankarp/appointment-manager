# Change: Refactor Domain Tests with Fixtures

## Why
Current domain tests contain significant code duplication in test setup, making them harder to read and maintain. Each test manually constructs domain objects (appointments, clients, value objects) with repetitive boilerplate code.

This change introduces the Java test fixtures plugin to create reusable, composable, domain-language fixture functions that:
- Reduce test duplication and improve readability
- Provide consistent test data across all tests
- Enable fixture reuse in application and adapter layer tests
- Follow established domain language patterns (as documented in project.md)
- Support composability: higher-level fixtures use lower-level fixtures (e.g., appointment fixtures use client fixtures)

## What Changes
- Add `java-test-fixtures` plugin to `appointment-manager-domain` module
- Create composable fixture hierarchy in `src/testFixtures/kotlin/`:
  - **ValueObjectFixtures** - Base fixtures for value objects (IDs, emails, phone numbers, etc.)
  - **ClientFixtures** - Client factory functions that use ValueObjectFixtures
  - **AppointmentFixtures** - Appointment factory functions that use ClientFixtures and ValueObjectFixtures
- Refactor all existing domain tests to use fixtures
- Enable other modules (`appointment-manager-application`, `appointment-manager-adapters`) to consume domain test fixtures
- Ensure fixtures follow domain language naming (e.g., `scheduledAppointment()`, `clientWithEmail()`)

## Impact
- **Affected specs**: MODIFIED - `client-domain`, `appointment-domain` (add fixture usage examples)
- **Affected code**:
  - Modifies `appointment-manager-domain/build.gradle.kts` to add test fixtures plugin
  - Creates new test fixtures in `appointment-manager-domain/src/testFixtures/`
  - Refactors 8 test files in `appointment-manager-domain/src/test/`
  - Updates `appointment-manager-application` and `appointment-manager-adapters` build files to consume fixtures
- **Dependencies**: No new external dependencies (test fixtures is a Gradle built-in plugin)
- **Testing**: All existing 30 tests must continue to pass after refactoring
- **Breaking changes**: None (internal test refactoring only)
