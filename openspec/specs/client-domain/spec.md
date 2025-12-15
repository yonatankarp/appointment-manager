# client-domain Specification

## Purpose
TBD - created by archiving change add-core-domain-entities. Update Purpose after archive.
## Requirements
### Requirement: Client Identity
The system SHALL assign a unique identifier to each client upon creation.

#### Scenario: Client creation generates unique ID
- **WHEN** a new client is created
- **THEN** the client is assigned a unique ClientId
- **AND** the ClientId is a UUID

### Requirement: Client Information
The system SHALL store client name, preferred language, and contact information.

#### Scenario: Client created with valid information
- **GIVEN** valid client details (name, language, contact)
- **WHEN** creating a client
- **THEN** the client is created successfully
- **AND** all information is stored correctly

#### Scenario: Client name cannot be blank
- **GIVEN** a blank or empty name
- **WHEN** attempting to create a client
- **THEN** creation fails with validation error

### Requirement: Communication Channel
The system SHALL derive the communication channel from the client's contact information type.

#### Scenario: Instagram contact implies Instagram channel
- **GIVEN** a client with Instagram contact information
- **WHEN** querying the communication channel
- **THEN** the channel is Instagram

#### Scenario: WhatsApp contact implies WhatsApp channel
- **GIVEN** a client with WhatsApp contact information
- **WHEN** querying the communication channel
- **THEN** the channel is WhatsApp

#### Scenario: Email contact implies Email channel
- **GIVEN** a client with Email contact information
- **WHEN** querying the communication channel
- **THEN** the channel is Email

#### Scenario: Facebook contact implies Facebook channel
- **GIVEN** a client with Facebook contact information
- **WHEN** querying the communication channel
- **THEN** the channel is Facebook

### Requirement: Communication Channel Immutability
The system SHALL NOT allow changing the communication channel independently of contact information.

#### Scenario: Channel change requires new contact information
- **GIVEN** a client with a specific contact information type
- **WHEN** attempting to change the communication channel
- **THEN** the operation requires providing new ContactInformation
- **AND** the channel is automatically derived from the new contact type

### Requirement: Language Support
The system SHALL support Hebrew, English, and German languages.

#### Scenario: Client can have Hebrew language preference
- **WHEN** creating a client with Hebrew preference
- **THEN** the client's preferred language is Hebrew

#### Scenario: Client can have English language preference
- **WHEN** creating a client with English preference
- **THEN** the client's preferred language is English

#### Scenario: Client can have German language preference
- **WHEN** creating a client with German preference
- **THEN** the client's preferred language is German

### Requirement: Contact Information Validation
The system SHALL validate contact information based on type.

#### Scenario: Phone number validation for WhatsApp
- **GIVEN** WhatsApp contact type
- **WHEN** providing a phone number
- **THEN** the phone number is validated against E.164 format
- **AND** invalid phone numbers are rejected

#### Scenario: Email validation for Email contact
- **GIVEN** Email contact type
- **WHEN** providing an email address
- **THEN** the email is validated against RFC 5322 format
- **AND** invalid emails are rejected

### Requirement: Client Timestamps
The system SHALL track when clients are created and updated.

#### Scenario: Client creation records timestamp
- **WHEN** a client is created
- **THEN** createdAt timestamp is set to current time
- **AND** updatedAt timestamp is set to current time

### Requirement: Client Immutability
The system SHALL treat clients as immutable entities.

#### Scenario: Client modifications create new instances
- **GIVEN** an existing client
- **WHEN** modifying client information (via data class copy)
- **THEN** a new client instance is created
- **AND** the original client remains unchanged

**Note**: Client is implemented as a Kotlin data class, providing immutability through the generated `copy()` method. Explicit update methods will be added in future iterations when update business logic is required.

