# Appointment Domain Capability Specification

## ADDED Requirements

### Requirement: Appointment Identity
The system SHALL assign a unique identifier to each appointment upon creation.

#### Scenario: Appointment scheduling generates unique ID
- **WHEN** a new appointment is scheduled
- **THEN** the appointment is assigned a unique AppointmentId
- **AND** the AppointmentId is a UUID

### Requirement: Appointment Information
The system SHALL store appointment client reference, date/time, duration, service type, and status.

#### Scenario: Appointment scheduled with valid information
- **GIVEN** valid appointment details
- **WHEN** scheduling an appointment
- **THEN** the appointment is created with SCHEDULED status
- **AND** all information is stored correctly
- **AND** an AppointmentScheduled event is generated

### Requirement: Berlin Timezone Enforcement
The system SHALL store all appointment times in Berlin timezone (Europe/Berlin).

#### Scenario: Appointment time is in Berlin timezone
- **GIVEN** a local date/time for an appointment
- **WHEN** creating the appointment
- **THEN** the time is stored in Europe/Berlin timezone
- **AND** timezone conversions are handled automatically

#### Scenario: Non-Berlin timezone is rejected
- **GIVEN** a date/time with non-Berlin timezone
- **WHEN** attempting to create an appointment
- **THEN** the appointment creation fails with validation error

### Requirement: Appointment Duration Validation
The system SHALL validate appointment duration is positive and reasonable.

#### Scenario: Valid duration is accepted
- **GIVEN** a duration between 1 minute and 8 hours
- **WHEN** creating an appointment
- **THEN** the duration is accepted

#### Scenario: Zero or negative duration is rejected
- **GIVEN** a duration of 0 or negative minutes
- **WHEN** creating an appointment
- **THEN** the creation fails with validation error

#### Scenario: Excessive duration is rejected
- **GIVEN** a duration greater than 8 hours
- **WHEN** creating an appointment
- **THEN** the creation fails with validation error

### Requirement: No Overlapping Appointments
The system SHALL prevent scheduling appointments that overlap in time.

#### Scenario: Non-overlapping appointments are allowed
- **GIVEN** two appointments with different time ranges
- **WHEN** checking for conflicts
- **THEN** no conflict is detected

#### Scenario: Completely overlapping appointments are detected
- **GIVEN** two appointments with identical time ranges
- **WHEN** checking for conflicts
- **THEN** a conflict is detected

#### Scenario: Partially overlapping appointments are detected
- **GIVEN** appointment A from 10:00-11:00
- **AND** appointment B from 10:30-11:30
- **WHEN** checking for conflicts
- **THEN** a conflict is detected

#### Scenario: Adjacent appointments are allowed
- **GIVEN** appointment A from 10:00-11:00
- **AND** appointment B from 11:00-12:00
- **WHEN** checking for conflicts
- **THEN** no conflict is detected

#### Scenario: Cancelled appointments are ignored in conflict detection
- **GIVEN** a cancelled appointment in the time range
- **AND** a new appointment in the same time range
- **WHEN** checking for conflicts
- **THEN** no conflict is detected

### Requirement: 24-Hour Cancellation Policy
The system SHALL require at least 24 hours notice to cancel an appointment.

#### Scenario: Cancellation with sufficient notice succeeds
- **GIVEN** a scheduled appointment more than 24 hours in the future
- **WHEN** cancelling the appointment
- **THEN** the cancellation succeeds
- **AND** the status changes to CANCELLED
- **AND** cancelledAt timestamp is set
- **AND** an AppointmentCancelled event is generated

#### Scenario: Cancellation with insufficient notice fails
- **GIVEN** a scheduled appointment less than 24 hours in the future
- **WHEN** attempting to cancel the appointment
- **THEN** the cancellation fails
- **AND** the status remains SCHEDULED

#### Scenario: Cancellation at exactly 24 hours succeeds
- **GIVEN** a scheduled appointment exactly 24 hours in the future
- **WHEN** cancelling the appointment
- **THEN** the cancellation succeeds

### Requirement: Appointment Status Transitions
The system SHALL enforce valid status transitions.

#### Scenario: Scheduled appointment can be cancelled
- **GIVEN** an appointment with SCHEDULED status
- **WHEN** cancelling with sufficient notice
- **THEN** status transitions to CANCELLED

#### Scenario: Scheduled appointment can be completed
- **GIVEN** an appointment with SCHEDULED status
- **WHEN** marking as completed
- **THEN** status transitions to COMPLETED
- **AND** completedAt timestamp is set
- **AND** an AppointmentCompleted event is generated

#### Scenario: Cancelled appointment cannot be completed
- **GIVEN** an appointment with CANCELLED status
- **WHEN** attempting to mark as completed
- **THEN** the operation fails

#### Scenario: Completed appointment cannot be cancelled
- **GIVEN** an appointment with COMPLETED status
- **WHEN** attempting to cancel
- **THEN** the operation fails

### Requirement: Domain Events
The system SHALL generate domain events for appointment lifecycle changes.

#### Scenario: Scheduling generates AppointmentScheduled event
- **WHEN** an appointment is scheduled
- **THEN** an AppointmentScheduled event is created
- **AND** the event contains appointment ID, client ID, and date/time
- **AND** the event has a unique event ID and timestamp

#### Scenario: Completion generates AppointmentCompleted event
- **WHEN** an appointment is completed
- **THEN** an AppointmentCompleted event is created
- **AND** the event contains appointment ID and client ID

#### Scenario: Cancellation generates AppointmentCancelled event
- **WHEN** an appointment is cancelled
- **THEN** an AppointmentCancelled event is created
- **AND** the event contains appointment ID, client ID, and optional reason

### Requirement: Appointment Timestamps
The system SHALL track appointment lifecycle timestamps.

#### Scenario: Creation sets initial timestamps
- **WHEN** an appointment is scheduled
- **THEN** createdAt is set to current time
- **AND** updatedAt is set to current time
- **AND** cancelledAt is null
- **AND** completedAt is null

#### Scenario: Cancellation updates timestamps
- **WHEN** an appointment is cancelled
- **THEN** cancelledAt is set to current time
- **AND** updatedAt is updated

#### Scenario: Completion updates timestamps
- **WHEN** an appointment is completed
- **THEN** completedAt is set to current time
- **AND** updatedAt is updated
