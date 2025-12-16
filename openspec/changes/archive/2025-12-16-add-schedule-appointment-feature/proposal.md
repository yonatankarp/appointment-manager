# Change: Add Schedule Appointment Use Case

## Why
Implements the core business logic for scheduling appointments. This use case coordinates domain entities, enforces business rules (conflict detection, 24-hour policy), and defines the ports needed for persistence and events. Starting with just the use case and mocked dependencies lets us validate the application logic before building infrastructure.

## What Changes
- **Use Case**: `ScheduleAppointmentUseCase` that:
  - Validates client exists
  - Detects time conflicts using domain service
  - Creates appointment using domain aggregate
  - Publishes domain event
  - Validates appointment is in the future

- **Output Ports** (interfaces only, no implementations):
  - `SaveAppointmentPort` - Will persist appointment
  - `FindClientByIdPort` - Will verify client exists
  - `FindAppointmentsInTimeRangePort` - Will get appointments for conflict detection
  - `PublishDomainEventPort` - Will publish AppointmentScheduled event

- **Application Configuration**:
  - Timezone injection (from configuration)

- **Tests**: Unit tests with mocked ports

## Impact
- **Affected specs**: Creates 1 new capability spec
  - `schedule-appointment` (new) - Use case and its port contracts

- **Affected code**: Creates application module structure
  - `appointment-manager-application/src/main/kotlin/com/appointmentmanager/application/`
    - `usecases/ScheduleAppointmentUseCase.kt`
    - `ports/output/SaveAppointmentPort.kt`
    - `ports/output/FindClientByIdPort.kt`
    - `ports/output/FindAppointmentsInTimeRangePort.kt`
    - `ports/output/PublishDomainEventPort.kt`
  - `appointment-manager-application/src/test/` - Unit tests with MockK

- **Dependencies added**: None (MockK already available)

- **Breaking changes**: None - new functionality

- **Future work**: Subsequent features will implement the port adapters (persistence, REST API)
