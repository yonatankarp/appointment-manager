# Appointment Domain Capability Specification

## MODIFIED Requirements

### Requirement: Test Fixtures for Appointment Domain
The system SHALL provide reusable test fixtures for creating appointments in test scenarios.

#### Scenario: Basic scheduled appointment fixture
- **GIVEN** a need to create a test appointment
- **WHEN** using the `scheduledAppointment()` fixture
- **THEN** a valid scheduled appointment is created with sensible defaults
- **AND** the fixture uses ClientFixtures for client creation
- **AND** the fixture uses ValueObjectFixtures for date/time and duration
- **AND** defaults can be overridden via parameters

#### Scenario: Cancelled appointment fixture
- **GIVEN** a need to create a test cancelled appointment
- **WHEN** using the `cancelledAppointment()` fixture
- **THEN** a valid cancelled appointment is created
- **AND** the fixture uses `scheduledAppointment()` and then cancels it
- **AND** cancellation reason can be customized

#### Scenario: Appointment in past fixture
- **GIVEN** a test requiring a past appointment
- **WHEN** using the `appointmentInPast()` fixture
- **THEN** an appointment with dateTime in the past is created
- **AND** the appointment is in a valid state for testing completion logic

#### Scenario: Appointment in future fixture
- **GIVEN** a test requiring a future appointment
- **WHEN** using the `appointmentInFuture()` fixture
- **THEN** an appointment with dateTime in the future is created
- **AND** the appointment is suitable for testing scheduling logic

#### Scenario: Overlapping appointment fixture
- **GIVEN** an existing appointment and a need to test conflicts
- **WHEN** using `overlappingAppointment(existing)` fixture
- **THEN** a new appointment is created that overlaps with the existing one
- **AND** the overlap is guaranteed for conflict testing

### Requirement: Fixture Composability for Appointments
The system SHALL ensure appointment fixtures compose with client and value object fixtures.

#### Scenario: Appointment fixture uses client fixture
- **GIVEN** `scheduledAppointment()` fixture with no clientId parameter
- **WHEN** the fixture creates an appointment
- **THEN** it uses `clientId()` fixture from ValueObjectFixtures
- **AND** a unique client ID is generated

#### Scenario: Appointment fixture uses custom client
- **GIVEN** a test requiring a specific client
- **WHEN** providing a clientId to `scheduledAppointment(clientId = ...)`
- **THEN** the appointment uses the specified client ID
- **AND** the fixture doesn't create a new client

#### Scenario: Appointment fixture uses datetime fixture
- **GIVEN** `scheduledAppointment()` fixture
- **WHEN** no dateTime is specified
- **THEN** it uses `berlinDateTime()` fixture with sensible default (e.g., 7 days from now)
- **AND** the timezone is correctly set to Europe/Berlin

#### Scenario: Appointment fixture uses duration fixture
- **GIVEN** `scheduledAppointment()` fixture
- **WHEN** no duration is specified
- **THEN** it uses `duration()` fixture with default (e.g., 120 minutes)
- **AND** the duration is validated

#### Scenario: Cancelled appointment composes with scheduled
- **GIVEN** `cancelledAppointment()` fixture
- **WHEN** the fixture creates a cancelled appointment
- **THEN** it first calls `scheduledAppointment()` fixture
- **AND** then applies cancellation to the result
- **AND** demonstrates composability of state transitions

### Requirement: Value Object Test Fixtures
The system SHALL provide basic value object fixtures for common test scenarios.

#### Scenario: ClientId fixture generates unique IDs
- **GIVEN** multiple calls to `clientId()` fixture
- **WHEN** creating test clients
- **THEN** each call generates a unique UUID
- **AND** IDs are valid ClientId value objects

#### Scenario: AppointmentId fixture generates unique IDs
- **GIVEN** multiple calls to `appointmentId()` fixture
- **WHEN** creating test appointments
- **THEN** each call generates a unique UUID
- **AND** IDs are valid AppointmentId value objects

#### Scenario: Email fixture with default
- **GIVEN** `emailAddress()` fixture called with no parameters
- **WHEN** creating a test email
- **THEN** a valid default email is provided (e.g., "test@example.com")
- **AND** the email passes validation

#### Scenario: Email fixture with custom value
- **GIVEN** `emailAddress(email = "custom@example.com")` fixture
- **WHEN** creating a test email
- **THEN** the custom email is used
- **AND** the email passes validation

#### Scenario: Phone number fixture with default
- **GIVEN** `phoneNumber()` fixture called with no parameters
- **WHEN** creating a test phone number
- **THEN** a valid default E.164 phone number is provided
- **AND** the number passes validation

#### Scenario: Berlin datetime fixture
- **GIVEN** `berlinDateTime()` fixture with LocalDateTime
- **WHEN** creating a test appointment datetime
- **THEN** the datetime is converted to Europe/Berlin timezone
- **AND** timezone is preserved correctly

#### Scenario: Duration fixture with default
- **GIVEN** `duration()` fixture called with no parameters
- **WHEN** creating a test duration
- **THEN** a sensible default (e.g., 120 minutes) is provided
- **AND** the duration passes validation

### Requirement: Fixture Availability Across Modules
The system SHALL make appointment fixtures available to all test modules.

#### Scenario: Application module uses appointment fixtures
- **GIVEN** application layer use case tests
- **WHEN** importing appointment fixtures
- **THEN** all appointment fixture functions are available
- **AND** fixtures work identically to domain tests

#### Scenario: Adapters module uses appointment fixtures
- **GIVEN** adapter layer integration tests
- **WHEN** importing appointment fixtures
- **THEN** all appointment fixture functions are available
- **AND** fixtures can be used for database integration tests

#### Scenario: Fixtures reduce test setup code
- **GIVEN** a test that previously manually constructed appointments
- **WHEN** refactored to use fixtures
- **THEN** test setup code is reduced by at least 30%
- **AND** test readability improves
- **AND** no test logic changes
