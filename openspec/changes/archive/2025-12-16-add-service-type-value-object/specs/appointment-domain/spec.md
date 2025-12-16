## ADDED Requirements

### Requirement: Service Type Enumeration
The system SHALL define a fixed set of valid service types for appointments.

#### Scenario: Valid service types are defined
- **GIVEN** the system requires service type for appointments
- **WHEN** creating an appointment
- **THEN** service type must be one of: CONSULTATION, TATTOO, or RETOUCH

#### Scenario: Service type is type-safe
- **GIVEN** the ServiceType value object
- **WHEN** used in domain operations
- **THEN** only predefined service types can be used
- **AND** invalid service types are rejected at compile time

## MODIFIED Requirements

### Requirement: Appointment Information
The system SHALL store appointment client reference, date/time, duration, service type (enum), and status.

#### Scenario: Appointment scheduled with valid information
- **GIVEN** valid appointment details with a ServiceType enum value
- **WHEN** scheduling an appointment
- **THEN** the appointment is created with SCHEDULED status
- **AND** all information is stored correctly
- **AND** an AppointmentScheduled event is generated
