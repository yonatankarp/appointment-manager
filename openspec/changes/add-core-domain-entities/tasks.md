# Implementation Tasks

## 1. Setup and Value Objects
- [ ] 1.1 Create domain package structure
- [ ] 1.2 Implement Language enum (HEBREW, ENGLISH, GERMAN)
- [ ] 1.3 Implement AppointmentStatus enum (SCHEDULED, COMPLETED, CANCELLED)
- [ ] 1.4 Implement CommunicationChannel sealed class with tests
- [ ] 1.5 Implement ClientId value object (UUID-based inline class)
- [ ] 1.6 Implement AppointmentId value object (UUID-based inline class)
- [ ] 1.7 Implement PhoneNumber value object with E.164 validation and tests
- [ ] 1.8 Implement EmailAddress value object with RFC 5322 validation and tests
- [ ] 1.9 Implement ContactInformation sealed class hierarchy (all variants in one file) and tests
- [ ] 1.10 Implement AppointmentDateTime with Berlin timezone enforcement and tests
- [ ] 1.11 Implement Duration value object with validation and tests

## 2. Client Entity
- [ ] 2.1 Implement Client entity with factory method (TDD)
- [ ] 2.2 Test Client creation with valid data
- [ ] 2.3 Test Client validation (blank name)
- [ ] 2.4 Test communication channel derivation from ContactInformation
- [ ] 2.5 Test Client immutability

## 3. Appointment Entity
- [ ] 3.1 Implement Appointment entity with factory method (TDD)
- [ ] 3.2 Test Appointment.schedule() factory method
- [ ] 3.3 Implement Appointment.overlaps() method
- [ ] 3.4 Test overlap detection with various scenarios
- [ ] 3.5 Implement Appointment.cancel() with 24-hour validation
- [ ] 3.6 Test cancellation success and failure scenarios
- [ ] 3.7 Implement Appointment.complete() method
- [ ] 3.8 Test completion success and failure scenarios
- [ ] 3.9 Test status transition validations
- [ ] 3.10 Test timestamp tracking (cancelledAt, completedAt)

## 4. Domain Services
- [ ] 4.1 Implement AppointmentConflictDetector (TDD)
- [ ] 4.2 Test conflict detection with no conflicts
- [ ] 4.3 Test conflict detection with single conflict
- [ ] 4.4 Test conflict detection with multiple conflicts
- [ ] 4.5 Test conflict detection with adjacent appointments (no conflict)
- [ ] 4.6 Test conflict detection ignores non-scheduled appointments
- [ ] 4.7 Implement CancellationPolicyValidator (TDD)
- [ ] 4.8 Test 24-hour notice validation - success case
- [ ] 4.9 Test 24-hour notice validation - failure case
- [ ] 4.10 Test validation rejects non-scheduled appointments
- [ ] 4.11 Test edge cases (exactly 24 hours, past appointments)

## 5. Domain Events
- [ ] 5.1 Implement DomainEvent interface in DomainEvents.kt
- [ ] 5.2 Implement AppointmentScheduled event
- [ ] 5.3 Implement AppointmentCompleted event
- [ ] 5.4 Implement AppointmentCancelled event
- [ ] 5.5 Test event creation and immutability

## 6. Integration and Documentation
- [ ] 6.1 Run all tests and ensure 100% pass rate
- [ ] 6.2 Add KDoc documentation to all public APIs
- [ ] 6.3 Validate OpenSpec requirements are met
- [ ] 6.4 Review code for SOLID principles adherence
- [ ] 6.5 Final code review
