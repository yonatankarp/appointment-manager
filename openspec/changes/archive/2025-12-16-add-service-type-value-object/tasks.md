# Implementation Tasks

## 1. Add ServiceType Value Object (TDD)
- [x] 1.1 Create ServiceType enum with values: CONSULTATION, TATTOO, RETOUCH
- [x] 1.2 Create ServiceTypeFixtures for test support

## 2. Update Appointment Aggregate (TDD)
- [x] 2.1 Write failing test: schedule appointment with ServiceType enum
- [x] 2.2 Update Appointment.schedule() to accept ServiceType instead of String
- [x] 2.3 Update Appointment data class serviceType field to ServiceType type
- [x] 2.4 Remove String validation for serviceType (no longer needed)
- [x] 2.5 Verify test passes

## 3. Update Test Fixtures
- [x] 3.1 Update AppointmentFixtures to use ServiceType enum
- [x] 3.2 Update all tests importing AppointmentFixtures to use ServiceType
- [x] 3.3 Verify all existing tests still pass

## 4. Reorganize Test Fixtures Structure
- [x] 4.1 Create fixture subdirectories: valueobjects/, entities/, aggregates/
- [x] 4.2 Move value object fixtures to testFixtures/fixtures/valueobjects/
  - ClientIdFixtures.kt
  - AppointmentIdFixtures.kt
  - EmailAddressFixtures.kt
  - PhoneNumberFixtures.kt
  - ClientNameFixtures.kt
  - LanguageFixtures.kt
  - AppointmentDateTimeFixtures.kt
  - DurationFixtures.kt
  - AppointmentStatusFixtures.kt
  - CommunicationChannelFixtures.kt
  - ContactInformationFixtures.kt
  - ServiceTypeFixtures.kt
- [x] 4.3 Move entity fixtures to testFixtures/fixtures/entities/
  - ClientFixtures.kt
- [x] 4.4 Move aggregate fixtures to testFixtures/fixtures/aggregates/
  - AppointmentFixtures.kt
- [x] 4.5 Update all import statements in test files
- [x] 4.6 Update all import statements in fixture files
- [x] 4.7 Verify all tests compile and pass after reorganization

## 5. Add Missing Test Coverage
- [x] 5.1 Identify untested scenarios from appointment-domain spec
- [x] 5.2 Add test: "Zero or negative duration is rejected"
- [x] 5.3 Add test: "Excessive duration is rejected"
- [x] 5.4 Add test: "Completely overlapping appointments are detected"
- [x] 5.5 Add test: "Partially overlapping appointments are detected"
- [x] 5.6 Add test: "Adjacent appointments are allowed"
- [x] 5.7 Add test: "Cancellation with insufficient notice fails"
- [x] 5.8 Add test: "Cancellation at exactly 24 hours succeeds"
- [x] 5.9 Add test: "Only scheduled appointments can be cancelled"
- [x] 5.10 Add test: "Cancellation generates AppointmentCancelled event with all fields"
- [x] 5.11 Add test: "Cancellation updates timestamps correctly"
- [x] 5.12 Verify all new tests pass

## 6. Validation
- [x] 6.1 Run full build with gradle-build-runner agent
- [x] 6.2 Verify all tests pass
- [x] 6.3 Run openspec-compliance-checker agent
- [x] 6.4 Address any compliance issues
- [x] 6.5 Final build verification
