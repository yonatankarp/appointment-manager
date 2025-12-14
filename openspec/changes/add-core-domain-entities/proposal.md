# Change: Add Core Domain Entities

## Why
Establish the foundational domain model for the appointment scheduling system following DDD principles. The tattoo artist needs to manage clients and appointments with specific business rules around overlapping bookings and cancellation policies.

This change creates the core domain layer that will serve as the foundation for all future business logic, ensuring type safety, validation, and adherence to business rules at the domain level.

## What Changes
- Add value objects: CommunicationChannel, Language, AppointmentStatus, ContactInformation, PhoneNumber, EmailAddress, AppointmentDateTime, Duration, ClientId, AppointmentId
- Add entities: Client, Appointment
- Add domain services: AppointmentConflictDetector, CancellationPolicyValidator
- Add domain events: AppointmentScheduled, AppointmentCompleted, AppointmentCancelled
- Implement business rules:
  - No overlapping appointments
  - 24-hour cancellation notice requirement
  - Communication channel immutability (determined by ContactInformation)
  - Berlin timezone enforcement for all appointments

## Impact
- **Affected specs**: NEW - `client-domain`, `appointment-domain`
- **Affected code**:
  - Creates new domain layer structure under `app/src/main/kotlin/org/example/domain/`
  - No changes to existing code (App.kt remains untouched for now)
- **Dependencies**: No new external dependencies required
- **Testing**: Comprehensive unit tests for all domain logic (TDD approach)
- **Breaking changes**: None (new functionality)
