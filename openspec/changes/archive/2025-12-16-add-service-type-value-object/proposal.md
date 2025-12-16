# Change: Add ServiceType Value Object and Improve Test Infrastructure

## Why
Currently, the Appointment aggregate uses a plain `String` for `serviceType`, which lacks type safety and doesn't enforce valid service types. This can lead to inconsistent data (e.g., "Tattoo", "tattoo", "Tattoo Session") and makes the domain model weaker than it should be.

Additionally, the test fixture organization doesn't mirror the domain structure, making it harder to navigate and maintain as the codebase grows. Some spec scenarios also lack corresponding test coverage.

## What Changes
1. **Add ServiceType Value Object (enum)**: Convert `serviceType` from `String` to a proper value object enum with predefined values: CONSULTATION, TATTOO, and RETOUCH
2. **Reorganize Test Fixtures**: Restructure test fixtures to mirror the domain package hierarchy (valueobjects/, entities/, aggregates/) for better discoverability
3. **Add Missing Test Coverage**: Implement tests for all scenarios defined in the appointment-domain spec that currently lack coverage

## Impact
- **Affected specs**: appointment-domain (MODIFIED requirement for Appointment Information)
- **Affected code**:
  - `domain/aggregates/Appointment.kt` - update serviceType field type
  - `domain/valueobjects/ServiceType.kt` - new value object enum
  - `testFixtures/` - reorganize all fixture files into subdirectories
  - `test/` - add missing test cases for spec scenarios
  - All tests using `Appointment` or `AppointmentFixtures` - update to use ServiceType enum
- **Breaking change**: None (internal domain model refinement, no external API changes)
- **Migration**: All existing code using String serviceType must be updated to use ServiceType enum
