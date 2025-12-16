# Design: Schedule Appointment Use Case

## Context
Implements the core application logic for scheduling appointments. Domain entities and services exist; this use case coordinates them and defines the port contracts for future adapter implementations. Starting with just the use case (with mocked ports) validates business logic before building infrastructure.

## Goals / Non-Goals

### Goals
- Implement ScheduleAppointmentUseCase business logic
- Define clean port interfaces (SRP)
- Use case works directly with domain objects
- Comprehensive unit tests with mocked ports
- Timezone injected from configuration

### Non-Goals
- Port implementations (future: separate adapter features)
- Database persistence (future)
- REST API endpoints (future)
- Event infrastructure (future)
- Querying/viewing appointments (separate feature)
- Cancellation (separate feature)

## Decisions

### Decision 1: Use Case Uses Domain Directly
**What**: `ScheduleAppointmentUseCase` works with domain objects (`Appointment`, `Client`, `Duration`, `ServiceType`), not application-layer DTOs.

**Why**:
- Application layer is part of the application, can use domain
- Simpler - no mapping needed in use case
- Adapters (future) handle conversion to/from their protocols
- Domain objects cross application boundary

**How**:
```kotlin
class ScheduleAppointmentUseCase(
    private val saveAppointment: SaveAppointmentPort,
    private val findClient: FindClientByIdPort,
    private val findAppointmentsInRange: FindAppointmentsInTimeRangePort,
    private val publishEvent: PublishDomainEventPort,
    private val timezone: ZoneId,
) {
    fun execute(
        clientId: ClientId,
        localDateTime: LocalDateTime,
        duration: Duration,
        serviceType: ServiceType,
    ): Result<Appointment> {
        // 1. Validate future time
        // 2. Find client (via port)
        // 3. Create AppointmentDateTime with timezone
        // 4. Find conflicting appointments (via port)
        // 5. Use domain service to detect conflicts
        // 6. Schedule appointment (domain aggregate)
        // 7. Save appointment (via port)
        // 8. Publish event (via port)
        // 9. Return appointment
    }
}
```

### Decision 2: One Port Per Responsibility
**What**: Define focused port interfaces, each with single responsibility.

**Ports**:
- `SaveAppointmentPort` - Persist appointment
- `FindClientByIdPort` - Retrieve client
- `FindAppointmentsInTimeRangePort` - Query appointments for conflict detection
- `PublishDomainEventPort` - Publish domain events

**Why**:
- SRP: each port has one reason to change
- Easy to mock in tests
- Clear contracts for future adapters
- Each adapter (future) implements one port

**Example**:
```kotlin
interface SaveAppointmentPort {
    fun save(appointment: Appointment): Appointment
}

interface FindClientByIdPort {
    fun findById(clientId: ClientId): Client?
}

interface FindAppointmentsInTimeRangePort {
    fun findInRange(start: ZonedDateTime, end: ZonedDateTime): List<Appointment>
}

interface PublishDomainEventPort {
    fun publish(event: DomainEvent)
}
```

### Decision 3: Timezone Configuration
**What**: Use case receives timezone as constructor parameter (from application configuration).

**Why**:
- Domain stays pure (no default timezones)
- Application layer controls configuration
- Easy to test with different timezones
- Single source of truth

**How**:
```kotlin
@Configuration
class AppointmentConfig {
    @Value("\${appointment.timezone}")
    private lateinit var timezoneId: String

    @Bean
    fun scheduleAppointmentUseCase(...): ScheduleAppointmentUseCase {
        return ScheduleAppointmentUseCase(
            ...,
            timezone = ZoneId.of(timezoneId)
        )
    }
}
```

### Decision 4: Validate Future Appointments Only
**What**: Use case validates that appointment time is in the future.

**Why**:
- Business rule: can't book appointments in the past
- Catch data entry errors
- Prevents confusion with completed appointments

**How**:
```kotlin
fun execute(...): Result<Appointment> {
    val appointmentDateTime = AppointmentDateTime.of(localDateTime, timezone).getOrElse {
        return Result.failure(it)
    }

    if (appointmentDateTime.value.isBefore(ZonedDateTime.now(timezone))) {
        return Result.failure(IllegalArgumentException("Cannot schedule appointment in the past"))
    }
    // ...
}
```

### Decision 5: Client Must Exist
**What**: Use case fails if client doesn't exist (doesn't create client).

**Why**:
- Clear separation of concerns
- Client creation is separate use case
- Prevents accidental partial data

**How**:
```kotlin
val client = findClient.findById(clientId)
    ?: return Result.failure(IllegalArgumentException("Client not found: $clientId"))
```

## Risks / Trade-offs

### Risk: No Real Infrastructure Yet
**Mitigation**: Unit tests with mocks validate logic; adapters come in future features

### Risk: Timezone Handling Complexity
**Mitigation**: Leverage domain's AppointmentDateTime value object; well-tested

## Testing Strategy

### Unit Tests (with MockK)
- ✅ Valid scheduling succeeds
- ✅ Client not found fails
- ✅ Past appointment rejected
- ✅ Overlapping appointment fails (conflict detected)
- ✅ Adjacent appointments succeed (no conflict)
- ✅ Event published on success
- ✅ Appointment saved via port
- ✅ Timezone applied correctly

### Test Structure
```kotlin
class ScheduleAppointmentUseCaseTest {
    private lateinit var useCase: ScheduleAppointmentUseCase
    private lateinit var saveAppointment: SaveAppointmentPort
    private lateinit var findClient: FindClientByIdPort
    private lateinit var findAppointmentsInRange: FindAppointmentsInTimeRangePort
    private lateinit var publishEvent: PublishDomainEventPort

    @BeforeEach
    fun setup() {
        saveAppointment = mockk()
        findClient = mockk()
        findAppointmentsInRange = mockk()
        publishEvent = mockk()

        useCase = ScheduleAppointmentUseCase(
            saveAppointment = saveAppointment,
            findClient = findClient,
            findAppointmentsInRange = findAppointmentsInRange,
            publishEvent = publishEvent,
            timezone = ZoneId.of("Europe/Berlin"),
        )
    }

    @Test
    fun `should schedule appointment successfully`() {
        // Given
        // When
        // Then
    }
}
```

## Open Questions
- **Return saved appointment?**: Should use case return the appointment from `save()` or the domain result? → **Return saved appointment (includes generated ID from persistence)**
- **Conflict detection scope**: Check all appointments or just scheduled ones? → **Domain already handles this (overlaps() ignores non-SCHEDULED)**
