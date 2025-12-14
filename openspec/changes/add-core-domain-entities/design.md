# Design Document: Core Domain Entities

## Context
This change establishes the foundational domain model for a tattoo appointment scheduling system. The system must support multiple communication channels (Instagram, WhatsApp, Facebook, Email), multiple languages (Hebrew, English, German), and enforce critical business rules about appointment conflicts and cancellation policies.

**Key Stakeholders:**
- Tattoo artist (primary user - sets communication channels)
- Clients (end users - receive communications)

**Technical Constraints:**
- Pure Kotlin domain layer (no framework dependencies)
- Must work with Berlin timezone (Europe/Berlin)
- Domain must not include repositories (Hexagonal Architecture principle)
- Following Test-Driven Development (TDD)

## Goals / Non-Goals

**Goals:**
- Establish type-safe value objects for all domain concepts
- Implement Client and Appointment as clean domain entities
- Enforce business rules in domain layer (no overlapping, 24-hour cancellation)
- Support domain events for future event-driven architecture
- Maintain 100% test coverage for domain logic
- Use Kotlin idioms (sealed classes, inline value classes, Result types)

**Non-Goals:**
- Persistence (repositories will be added in Phase 2)
- REST API (will be added in Phase 3)
- Spring Boot integration (will be added in Phase 3)
- Actual communication with external services
- Multi-tenancy support
- User authentication/authorization

## Decisions

### Decision 1: Aggregate Boundaries
**Choice:** Appointment as aggregate root, Client as separate aggregate

**Rationale:**
- Appointment controls all state transitions and business rules (scheduling, cancellation, completion)
- Client is referenced by ID (ClientId), not embedded
- Allows independent lifecycle management (clients can exist without appointments)
- Prevents large object graphs and maintains clear boundaries
- Follows DDD principle of small, focused aggregates

**Alternatives considered:**
- Client as aggregate root containing appointments: Rejected because it would create a large aggregate with complex consistency requirements and would make appointment operations cumbersome
- Single aggregate for both: Rejected due to different lifecycle and access patterns (clients are relatively static, appointments are frequently created/modified)

**Impact:**
- Client and Appointment can be persisted independently
- Business rules are contained within appropriate boundaries
- Clear separation of concerns

### Decision 2: Value Objects vs. Primitives
**Choice:** Extensive use of value objects (PhoneNumber, EmailAddress, Duration, etc.)

**Rationale:**
- Type safety prevents invalid state at compile time
- Encapsulates validation logic in one place
- Makes domain concepts explicit (Duration vs Int, PhoneNumber vs String)
- Kotlin inline value classes provide zero runtime overhead
- Prevents primitive obsession anti-pattern

**Alternatives considered:**
- Using String, Int, etc. directly: Rejected due to:
  - Lack of validation at construction
  - Type confusion (which String is a phone number vs email?)
  - Validation logic scattered across codebase
  - No compiler help to prevent mixing types

**Impact:**
- More boilerplate for value object definitions
- Significantly improved type safety and domain clarity
- Validation happens once at construction

### Decision 3: Communication Channel Immutability
**Choice:** Communication channel derived from ContactInformation type, cannot be changed independently

**Rationale:**
- Reflects business reality: channel is determined by how initial contact was established
- Prevents inconsistent state (ContactInformation type mismatching channel enum)
- Changing channel requires explicit business logic (create new ContactInformation)
- The tattoo artist sets the communication channel based on how the appointment was discussed

**Alternatives considered:**
- Separate mutable channel field: Rejected due to consistency concerns (channel could be Email but ContactInformation could be WhatsAppContact)
- Making ContactInformation mutable: Rejected as it violates value object immutability principles

**Impact:**
- Communication channel is always consistent with contact information type
- To change channel, must update contact information (intentional friction for business reasons)

### Decision 4: Business Rule Enforcement Without Repositories
**Choice:** Domain services receive data as parameters (list of existing appointments)

**Rationale:**
- Keeps domain layer pure and framework-agnostic (no Spring, no JPA)
- Application layer is responsible for data fetching from repositories
- Follows Hexagonal Architecture: domain has no dependencies on infrastructure
- Makes testing trivial (no mocking required, just pass test data)
- Domain logic can be reasoned about in isolation

**Alternatives considered:**
- Passing repository interface to domain services: Rejected as it couples domain to persistence concerns
- Making repositories part of domain: Rejected as it violates Hexagonal Architecture

**Impact:**
- Application layer must fetch appointments and pass to domain services
- Domain services are pure functions (no side effects)
- Easy to test without infrastructure

### Decision 5: Sealed Classes vs. Enums
**Choice:**
- Sealed class for CommunicationChannel and ContactInformation
- Enum for Language and AppointmentStatus

**Rationale:**
- **Sealed classes** when:
  - Variants need different data or behavior
  - Future extensibility anticipated
  - CommunicationChannel may need variant-specific behavior (e.g., different validation per channel)
  - ContactInformation variants have different properties (username, phone, email, userId)
- **Enums** when:
  - Variants are simple constants without additional data
  - Fixed set unlikely to change
  - Language and AppointmentStatus are simple state indicators

**Alternatives considered:**
- All enums: Rejected because ContactInformation needs variant-specific data
- All sealed classes: Considered but overkill for simple state enums

**Impact:**
- Sealed classes provide exhaustive when expressions
- Type safety for pattern matching
- Clear data model aligned with business concepts

### Decision 6: Result Types for Validation
**Choice:** Use Kotlin Result type for operations that can fail (e.g., cancel, complete)

**Rationale:**
- Makes failure cases explicit in type system
- Forces callers to handle errors (cannot ignore)
- No exception throwing in domain layer (functional approach)
- Railway-oriented programming pattern
- Idiomatic Kotlin

**Alternatives considered:**
- Throwing exceptions: Rejected because:
  - Exceptions not evident in method signatures
  - Checked exceptions don't exist in Kotlin
  - Exceptions are for exceptional conditions, not business rule violations
- Custom Result sealed class: Considered but Kotlin's built-in Result is sufficient

**Impact:**
- Callers must explicitly handle success/failure cases
- Clearer API contracts
- No hidden control flow

### Decision 7: Berlin Timezone Enforcement
**Choice:** AppointmentDateTime value object enforces Berlin timezone at construction

**Rationale:**
- Prevents timezone bugs at the domain level
- Makes timezone handling explicit and central
- All business logic operates in single timezone
- Tattoo artist operates in Berlin

**Alternatives considered:**
- Storing as UTC and converting on display: Considered but adds complexity and potential for errors
- Letting callers handle timezone: Rejected due to potential for errors and inconsistency
- LocalDateTime without timezone: Rejected as it's ambiguous (daylight saving time issues)

**Impact:**
- All appointments guaranteed to be in Berlin time
- Conversion handled at construction time only
- Business rules (24-hour cancellation) work correctly with DST

### Decision 8: Domain Events as Return Values
**Choice:** Domain events returned from methods, not published

**Rationale:**
- Domain layer has no infrastructure dependencies
- Application layer responsible for publishing (if needed)
- Keeps domain pure and testable
- Event publishing is an infrastructure concern

**Alternatives considered:**
- Domain event publisher: Rejected as it couples domain to event infrastructure
- No events: Rejected as we want to support future event-driven architecture

**Impact:**
- Methods return Pair<Appointment, DomainEvent> or Result<Pair<Appointment, DomainEvent>>
- Application layer will collect and publish events
- Domain remains infrastructure-agnostic

### Decision 9: Single File for Sealed Class Variants
**Choice:** All ContactInformation variants in ContactInformation.kt, all domain events in DomainEvents.kt

**Rationale:**
- Kotlin best practice for sealed classes (visibility and exhaustiveness)
- Related concepts grouped together
- Easier navigation and understanding
- Prevents split definitions across multiple files

**Impact:**
- Larger files but better cohesion
- Easier to see all variants at once
- Simpler imports

## Risks / Trade-offs

### Risk 1: Extensive Value Objects May Be Verbose
**Impact:** More code to write and maintain
**Mitigation:**
- Use Kotlin inline value classes for zero-cost abstraction
- Factory methods simplify creation
- Benefits of type safety and validation outweigh verbosity
**Severity:** Low

### Risk 2: 24-Hour Cancellation Policy May Be Too Strict
**Impact:** Users may want more flexibility (per-client policies, configurable hours)
**Mitigation:**
- Policy is configurable (constant in CancellationPolicyValidator)
- Can be made configurable via application layer in future phases
- Business rule is clearly separated in domain service (easy to change)
**Severity:** Low

### Risk 3: Domain Events Not Yet Consumed
**Impact:** Events are created but not published
**Mitigation:**
- Events are returned from methods (foundation in place)
- Application layer will be responsible for publishing (Phase 2)
- Foundation is in place for event-driven architecture
**Severity:** Low (intentional for Phase 1)

### Risk 4: No Persistence Layer Yet
**Impact:** Cannot save appointments or clients
**Mitigation:**
- This is intentional for Phase 1
- Focus on domain correctness first
- Persistence added in Phase 2 via ports and adapters
**Severity:** Low (intentional design)

## Data Model

### Value Objects
```
PhoneNumber (inline value class)
EmailAddress (inline value class)
Duration (inline value class)
ClientId (inline value class with UUID)
AppointmentId (inline value class with UUID)
AppointmentDateTime (inline value class with ZonedDateTime in Berlin timezone)

Language (enum): HEBREW | ENGLISH | GERMAN

AppointmentStatus (enum): SCHEDULED | COMPLETED | CANCELLED

CommunicationChannel (sealed class):
  - Instagram
  - WhatsApp
  - Facebook
  - Email

ContactInformation (sealed class):
  - InstagramContact(username: String)
  - WhatsAppContact(phoneNumber: PhoneNumber)
  - FacebookContact(userId: String)
  - EmailContact(emailAddress: EmailAddress)
```

### Entities
```
Client:
  - id: ClientId
  - name: String
  - preferredLanguage: Language
  - contactInformation: ContactInformation
  - createdAt: Instant
  - updatedAt: Instant
  - communicationChannel: CommunicationChannel (derived from contactInformation)

Appointment (Aggregate Root):
  - id: AppointmentId
  - clientId: ClientId
  - dateTime: AppointmentDateTime
  - duration: Duration
  - status: AppointmentStatus
  - serviceType: String
  - createdAt: Instant
  - updatedAt: Instant
  - cancelledAt: Instant?
  - completedAt: Instant?
```

### Domain Services
```
AppointmentConflictDetector:
  - detectConflicts(newAppointment, existingAppointments): ConflictDetectionResult

CancellationPolicyValidator:
  - validate(appointment, now): CancellationValidationResult
```

### Domain Events
```
DomainEvent (interface):
  - eventId: UUID
  - occurredAt: Instant

AppointmentScheduled:
  - appointmentId: AppointmentId
  - clientId: ClientId
  - dateTime: AppointmentDateTime

AppointmentCompleted:
  - appointmentId: AppointmentId
  - clientId: ClientId

AppointmentCancelled:
  - appointmentId: AppointmentId
  - clientId: ClientId
  - reason: String?
```

## Migration Plan

**N/A** - This is the initial implementation with no migration needed.

**Rollout:**
1. Merge domain layer implementation to main branch
2. Phase 2 will add application layer (use cases, ports)
3. Phase 3 will add Spring Boot and REST API
4. Phase 4 will add persistence layer
5. Phase 5 will add external integrations

**Rollback:**
- If issues arise, simply don't use the domain classes
- No database changes in this phase
- Low risk as it's pure business logic
- Can be removed without system impact

## Open Questions

### Q1: Should we add more contact information types?
**Options:** Telegram, Signal, SMS, Phone Call
**Decision:** Start with four channels (Instagram, WhatsApp, Facebook, Email), add more as needed
**Rationale:** YAGNI principle, can extend sealed class later

### Q2: Should cancellation policy be configurable per client?
**Options:**
- Uniform 24-hour policy for all clients
- Per-client configurable policy
- Tiered policy (VIP clients get different rules)
**Decision:** Uniform policy for Phase 1, can be enhanced later
**Rationale:** Simpler to implement and test, business hasn't requested per-client policies

### Q3: How to handle appointment rescheduling?
**Options:**
- Dedicated reschedule operation
- Cancel + create new appointment
- Update appointment time with validation
**Decision:** For Phase 1, reschedule = cancel + create new appointment
**Rationale:** Simpler, maintains audit trail
**Future:** Add dedicated reschedule operation with history tracking

### Q4: Should we track appointment modification history?
**Options:**
- Event sourcing for full history
- Simple audit log
- No history tracking
**Decision:** Not in Phase 1, rely on domain events for future implementation
**Rationale:** Can be added later via event store, YAGNI for now

### Q5: How to handle appointment duration edge cases?
**Options:**
- Fixed durations (1hr, 2hr, 4hr)
- Flexible durations with min/max
**Decision:** Flexible with max 8 hours, minimum 1 minute
**Rationale:** Provides flexibility while preventing absurd values

## Success Metrics

**Implementation Success:**
- [ ] All value objects implemented with validation
- [ ] All entities implemented with business logic
- [ ] All domain services working correctly
- [ ] All domain events generated properly
- [ ] 100% test coverage for domain layer
- [ ] All tests passing
- [ ] Zero framework dependencies in domain layer

**Code Quality:**
- [ ] KDoc documentation on all public APIs
- [ ] SOLID principles followed
- [ ] No code smells (primitive obsession, feature envy, etc.)
- [ ] Immutability enforced where appropriate

**Validation:**
- [ ] OpenSpec validation passes with `--strict` flag
- [ ] All requirements in specs satisfied
- [ ] Design decisions documented and justified
