# Client Domain Capability Specification

## MODIFIED Requirements

### Requirement: Test Fixtures for Client Domain
The system SHALL provide reusable test fixtures for creating clients in test scenarios.

#### Scenario: Basic client fixture with email
- **GIVEN** a need to create a test client with email contact
- **WHEN** using the `clientWithEmail()` fixture
- **THEN** a valid client is created with sensible defaults
- **AND** the fixture uses ValueObjectFixtures for email creation
- **AND** defaults can be overridden via parameters

#### Scenario: Client fixture with WhatsApp
- **GIVEN** a need to create a test client with WhatsApp contact
- **WHEN** using the `clientWithWhatsApp()` fixture
- **THEN** a valid client is created with phone number validation
- **AND** the fixture uses ValueObjectFixtures for phone number creation

#### Scenario: Client fixture with Instagram
- **GIVEN** a need to create a test client with Instagram contact
- **WHEN** using the `clientWithInstagram()` fixture
- **THEN** a valid client is created with Instagram username

#### Scenario: Client fixture with Facebook
- **GIVEN** a need to create a test client with Facebook contact
- **WHEN** using the `clientWithFacebook()` fixture
- **THEN** a valid client is created with Facebook user ID

#### Scenario: Client fixture with custom parameters
- **GIVEN** a test requiring specific client attributes
- **WHEN** providing custom parameters to any client fixture
- **THEN** the fixture creates a client with those attributes
- **AND** only specified parameters are customized
- **AND** unspecified parameters use sensible defaults

#### Scenario: Fixtures use domain language
- **GIVEN** any client fixture function
- **WHEN** reading the fixture name
- **THEN** the name uses clear domain language (e.g., `clientWithEmail`, not `createEmailClient`)
- **AND** the name is simple and readable

### Requirement: Fixture Composability
The system SHALL ensure client fixtures compose with value object fixtures.

#### Scenario: Client fixture uses email fixture
- **GIVEN** `clientWithEmail()` fixture
- **WHEN** the fixture creates a client
- **THEN** it uses `emailAddress()` fixture from ValueObjectFixtures
- **AND** the email fixture provides validation

#### Scenario: Client fixture uses phone fixture
- **GIVEN** `clientWithWhatsApp()` fixture
- **WHEN** the fixture creates a client
- **THEN** it uses `phoneNumber()` fixture from ValueObjectFixtures
- **AND** the phone fixture provides E.164 validation

#### Scenario: Client fixture uses ID fixture
- **GIVEN** any client fixture
- **WHEN** no ID is specified
- **THEN** it uses `clientId()` fixture from ValueObjectFixtures
- **AND** a unique UUID is generated

### Requirement: Cross-Module Fixture Availability
The system SHALL make client fixtures available to all test modules.

#### Scenario: Application module uses client fixtures
- **GIVEN** application layer tests
- **WHEN** importing client fixtures
- **THEN** all client fixture functions are available
- **AND** fixtures work identically to domain tests

#### Scenario: Adapters module uses client fixtures
- **GIVEN** adapter layer tests
- **WHEN** importing client fixtures
- **THEN** all client fixture functions are available
- **AND** fixtures can be used for integration tests
