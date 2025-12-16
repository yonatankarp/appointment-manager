# schedule-appointment Specification

## Purpose
TBD - created by archiving change add-schedule-appointment-feature. Update Purpose after archive.
## Requirements
### Requirement: Schedule Appointment Input Port
The system SHALL define an input port interface that represents the schedule appointment use case contract for inbound adapters.

#### Scenario: Input port defines use case contract
- **GIVEN** an inbound adapter needs to schedule appointments
- **WHEN** the adapter depends on ScheduleAppointmentPort
- **THEN** the port interface defines the execute() method signature
- **AND** the method accepts domain objects (ClientId, LocalDateTime, Duration, ServiceType)
- **AND** the method returns Result<Appointment>

### Requirement: Schedule Appointment Use Case Implementation
The system SHALL provide a use case implementation that schedules appointments by coordinating domain logic and enforcing business rules.

#### Scenario: Valid appointment scheduling succeeds
- **GIVEN** a valid client ID
- **AND** a future date/time
- **AND** a valid duration
- **AND** a valid service type
- **AND** no conflicting appointments exist
- **WHEN** executing the schedule appointment use case
- **THEN** an appointment is created using domain logic
- **AND** the appointment is saved via SaveAppointmentPort
- **AND** an AppointmentScheduled event is published via PublishDomainEventPort
- **AND** the scheduled appointment is returned

#### Scenario: Scheduling fails when client not found
- **GIVEN** a client ID that does not exist
- **WHEN** executing the schedule appointment use case
- **THEN** the use case fails with "Client not found" error
- **AND** no appointment is created
- **AND** no event is published

#### Scenario: Scheduling fails for past appointment
- **GIVEN** a date/time in the past
- **WHEN** executing the schedule appointment use case
- **THEN** the use case fails with "Cannot schedule appointment in the past" error
- **AND** no appointment is created

#### Scenario: Scheduling fails when appointments overlap
- **GIVEN** an existing scheduled appointment from 10:00-11:00
- **AND** a new appointment request from 10:30-11:30
- **WHEN** executing the schedule appointment use case
- **THEN** the use case fails with conflict error
- **AND** no appointment is created

#### Scenario: Scheduling succeeds for adjacent appointments
- **GIVEN** an existing appointment from 10:00-11:00
- **AND** a new appointment request from 11:00-12:00
- **WHEN** executing the schedule appointment use case
- **THEN** the appointment is scheduled successfully
- **AND** no conflict is detected

#### Scenario: Timezone is applied correctly
- **GIVEN** the use case is configured with Europe/Berlin timezone
- **AND** a local date/time 2025-12-20T14:00:00
- **WHEN** executing the schedule appointment use case
- **THEN** the appointment datetime is stored with Europe/Berlin timezone
- **AND** the resulting ZonedDateTime reflects the correct timezone offset

### Requirement: Output Port Contracts
The system SHALL define output port interfaces that the use case depends on, following Single Responsibility Principle.

#### Scenario: SaveAppointmentPort persists appointment
- **GIVEN** a valid Appointment domain object
- **WHEN** calling SaveAppointmentPort.save()
- **THEN** the appointment is persisted
- **AND** the saved appointment (with any generated fields) is returned

#### Scenario: FindClientByIdPort retrieves client
- **GIVEN** a valid ClientId
- **WHEN** calling FindClientByIdPort.findById()
- **THEN** the client is returned if it exists
- **AND** null is returned if client does not exist

#### Scenario: FindAppointmentsInTimeRangePort queries appointments
- **GIVEN** a time range (start and end ZonedDateTime)
- **WHEN** calling FindAppointmentsInTimeRangePort.findInRange()
- **THEN** all appointments overlapping with the time range are returned
- **AND** appointments are returned as domain Appointment objects

#### Scenario: PublishDomainEventPort publishes events
- **GIVEN** a DomainEvent (e.g., AppointmentScheduled)
- **WHEN** calling PublishDomainEventPort.publish()
- **THEN** the event is published to the event infrastructure
- **AND** the call completes successfully

### Requirement: Use Case Uses Domain Objects
The system SHALL allow the use case to work directly with domain objects without requiring application-layer DTOs.

#### Scenario: Use case accepts domain value objects
- **GIVEN** the schedule appointment use case
- **WHEN** calling execute() with ClientId, Duration, ServiceType (domain types)
- **THEN** the use case processes them directly
- **AND** no conversion to application DTOs is required

#### Scenario: Use case returns domain aggregate
- **GIVEN** successful appointment scheduling
- **WHEN** the use case completes
- **THEN** it returns the Appointment domain aggregate
- **AND** adapters are responsible for converting to their protocol formats

### Requirement: Timezone Configuration
The system SHALL inject the configured timezone into the use case from application configuration.

#### Scenario: Timezone is injected from configuration
- **GIVEN** application.yml specifies appointment.timezone: Europe/Berlin
- **WHEN** the use case is instantiated
- **THEN** the timezone is injected as a ZoneId
- **AND** the use case uses this timezone for all appointment operations

