# Implementation Tasks

## 1. Setup Application Module Structure
- [x] 1.1 Create package structure in appointment-manager-application
  - [x] `com.appointmentmanager.application.usecases`
  - [x] `com.appointmentmanager.application.ports.input`
  - [x] `com.appointmentmanager.application.ports.output`
- [x] 1.2 Add domain module dependency to application module's build.gradle.kts

## 2. Define Input Port Interface
- [x] 2.1 Create `ScheduleAppointmentPort` interface in `ports/input` package
  - [x] Define `execute()` method accepting ClientId, LocalDateTime, Duration, ServiceType
  - [x] Return type: Result<Appointment>

## 3. Define Output Port Interfaces
- [x] 3.1 Create `SaveAppointmentPort` interface in `ports/output` package
- [x] 3.2 Create `FindClientByIdPort` interface in `ports/output` package
- [x] 3.3 Create `FindAppointmentsInTimeRangePort` interface in `ports/output` package
- [x] 3.4 Create `PublishDomainEventPort` interface in `ports/output` package

## 4. Implement ScheduleAppointmentUseCase (TDD - One Test at a Time)
- [x] 4.1 Create `ScheduleAppointmentUseCase` class in `usecases` package
- [x] 4.2 Implement `ScheduleAppointmentPort` interface
- [x] 4.3 Add constructor dependencies (4 output ports + AppointmentConflictDetector + ZoneId for timezone)
- [x] 4.4 Write test: "should fail when client not found" → implement
- [x] 4.5 Write test: "should fail when appointment is in the past" → implement
- [x] 4.6 Write test: "should fail when appointment overlaps with existing" → implement
- [x] 4.7 Write test: "should succeed when appointment time is adjacent" → implement
- [x] 4.8 Write test: "should schedule appointment successfully" → covered by adjacent test
- [x] 4.9 Write test: "should save appointment via port" → covered by adjacent test
- [x] 4.10 Write test: "should publish AppointmentScheduled event" → covered by adjacent test
- [x] 4.11 Write test: "should apply timezone correctly" → validated in all tests
- [x] 4.12 Refactor if needed while keeping tests green

## 5. Validation
- [x] 5.1 Run all tests and ensure they pass
- [x] 5.2 Call @gradle-build-runner to verify build succeeds
- [x] 5.3 Call @openspec-compliance-checker to validate adherence to specs
- [x] 5.4 Validate with `openspec validate add-schedule-appointment-feature --strict`
