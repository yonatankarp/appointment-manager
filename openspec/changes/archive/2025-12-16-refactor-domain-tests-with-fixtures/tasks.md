# Implementation Tasks

## 1. Setup Test Fixtures Plugin
- [x] 1.1 Add `java-test-fixtures` plugin to `appointment-manager-domain/build.gradle.kts`
- [x] 1.2 Create test fixtures directory structure: `src/testFixtures/kotlin/com/yonatankarp/appointmentmanager/domain/fixtures/`
- [x] 1.3 Verify fixtures module builds correctly with `./gradlew :appointment-manager-domain:testFixturesClasses`

## 2. Create ValueObjectFixtures
- [x] 2.1 Implement `clientId()` fixture function
- [x] 2.2 Implement `appointmentId()` fixture function
- [x] 2.3 Implement `emailAddress(email: String)` with default
- [x] 2.4 Implement `phoneNumber(number: String)` with default
- [x] 2.5 Implement `berlinDateTime(dateTime: LocalDateTime)` with default
- [x] 2.6 Implement `duration(minutes: Int)` with default
- [x] 2.7 Implement `language(lang: Language)` with default
- [x] 2.8 Add simple test to verify ValueObjectFixtures work

## 3. Create ClientFixtures
- [x] 3.1 Implement `clientWithEmail()` using ValueObjectFixtures (created as `emailClient()`)
- [x] 3.2 Implement `clientWithWhatsApp()` using ValueObjectFixtures (created as `whatsAppClient()`)
- [x] 3.3 Implement `clientWithInstagram()` using ValueObjectFixtures (created as `instagramClient()`)
- [x] 3.4 Implement `clientWithFacebook()` using ValueObjectFixtures (created as `facebookClient()`)
- [x] 3.5 Add test to verify ClientFixtures composition works
- [x] 3.6 Verify all client fixtures follow domain language naming

## 4. Create AppointmentFixtures
- [x] 4.1 Implement `scheduledAppointment()` using ClientFixtures and ValueObjectFixtures
- [x] 4.2 Implement `cancelledAppointment()` that uses `scheduledAppointment()`
- [ ] 4.3 Implement `appointmentInPast()` for testing edge cases (deferred - not needed yet)
- [ ] 4.4 Implement `appointmentInFuture()` for testing scheduling (deferred - not needed yet)
- [ ] 4.5 Implement `overlappingAppointment(existing: Appointment)` for conflict tests (deferred - tests work without it)
- [x] 4.6 Add test to verify AppointmentFixtures composition works
- [x] 4.7 Verify all appointment fixtures follow domain language naming

## 5. Refactor Value Object Tests
- [x] 5.1 Refactor `PhoneNumberTest.kt` to use fixtures (no changes needed - tests validation logic)
- [x] 5.2 Refactor `EmailAddressTest.kt` to use fixtures (no changes needed - tests validation logic)
- [x] 5.3 Refactor `AppointmentDateTimeTest.kt` to use fixtures (no changes needed - tests validation logic)
- [x] 5.4 Refactor `DurationTest.kt` to use fixtures (no changes needed - tests validation logic)
- [x] 5.5 Run tests and verify all pass: `./gradlew :appointment-manager-domain:test`

## 6. Refactor Entity Tests
- [x] 6.1 Refactor `ClientTest.kt` to use ClientFixtures and ValueObjectFixtures
- [x] 6.2 Measure reduction in test code (lines of code before/after)
- [x] 6.3 Run tests and verify all pass

## 7. Refactor Aggregate Tests
- [x] 7.1 Refactor `AppointmentTest.kt` to use AppointmentFixtures
- [x] 7.2 Ensure cancelled appointment tests use `cancelledAppointment()` fixture
- [x] 7.3 Measure reduction in test code
- [x] 7.4 Run tests and verify all pass

## 8. Refactor Domain Service Tests
- [x] 8.1 Refactor `AppointmentConflictDetectorTest.kt` to use AppointmentFixtures
- [x] 8.2 Use `overlappingAppointment()` fixture for conflict scenarios (not needed - inline creation is clearer)
- [x] 8.3 Refactor `CancellationPolicyValidatorTest.kt` to use fixtures
- [x] 8.4 Measure reduction in test code
- [x] 8.5 Run tests and verify all pass

## 9. Enable Cross-Module Fixture Usage
- [x] 9.1 Add `testFixtures(project(":appointment-manager-domain"))` to `appointment-manager-application/build.gradle.kts`
- [x] 9.2 Add `testFixtures(project(":appointment-manager-domain"))` to `appointment-manager-adapters/build.gradle.kts`
- [x] 9.3 Verify fixtures are accessible from application module with simple import test
- [x] 9.4 Verify fixtures are accessible from adapters module with simple import test

## 10. Final Verification
- [x] 10.1 Run full test suite: `./gradlew clean build`
- [x] 10.2 Verify all domain tests pass (32 tests passing)
- [x] 10.3 Calculate overall test code reduction percentage
- [x] 10.4 Verify no test logic was changed (only setup code)
- [x] 10.5 Check that fixtures follow project.md guidelines
