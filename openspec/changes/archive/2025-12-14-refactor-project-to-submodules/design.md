# Design: Multi-Module Project Structure

## Context

The appointment-manager project currently uses a single Gradle module (`app`) with packages organized by hexagonal architecture principles. While the package structure suggests layer separation, there is no compile-time enforcement preventing violations of architectural boundaries.

This refactoring establishes three separate Gradle submodules that mirror the hexagonal architecture layers defined in `openspec/project.md`, providing compile-time guarantees that:
- The domain layer remains pure and framework-agnostic
- Application layer only depends on domain
- Adapters cannot bypass the application layer
- Cross-adapter dependencies are prevented

### Constraints
- Must support Kotlin DSL for Gradle build scripts
- Must work with Java 21 toolchain
- Must integrate with existing Spring Boot setup (confined to adapters)
- Must support ATDD/TDD testing strategy across all layers
- Package naming must use `com.yonatankarp` group

## Goals / Non-Goals

### Goals
- Enforce hexagonal architecture boundaries at compile time
- Make domain layer completely framework-agnostic
- Prevent accidental coupling between adapters
- Maintain clear separation between ports (interfaces) and implementations
- Support independent testing of each layer
- Enable future extraction of modules into separate repositories if needed
- Organize output adapters by technology (postgres, rest) rather than abstract concepts

### Non-Goals
- Performance optimization (this is structural only)
- Changing business logic or domain model
- Introducing new frameworks or libraries
- Creating separate deployable artifacts (still single application)
- Modifying test strategy or coverage requirements

## Decisions

### Decision 1: Three Submodules (Not More Granular)

**Choice**: Create exactly three submodules: `appointment-manager-domain`, `appointment-manager-application`, `appointment-manager-adapters`

**Rationale**:
- Aligns directly with hexagonal architecture's three layers
- Matches the structure already defined in `openspec/project.md`
- Simpler than splitting adapters into input/output submodules
- Easier to manage dependencies and builds
- Sufficient granularity for current project scale

**Alternatives Considered**:
- **Five submodules** (domain, application, input-adapters, output-adapters, main): Rejected because it adds complexity without clear benefit at current scale. Adapters can still be organized by packages internally.
- **Keep single module**: Rejected because it provides no compile-time enforcement of architecture rules

### Decision 2: Adapters Module Contains Spring Boot Application

**Choice**: The `appointment-manager-adapters` module will contain the Spring Boot `@SpringBootApplication` class and all framework dependencies

**Rationale**:
- Keeps domain and application layers framework-agnostic
- Adapters are the "glue" that wires everything together
- Spring Boot is an implementation detail of how we expose and consume external interfaces
- Allows domain and application to be tested without Spring context

**Alternatives Considered**:
- **Separate "main" or "app" module**: Rejected because adapters already serve as the composition root
- **Spring Boot in application layer**: Rejected because it would couple application layer to framework

### Decision 3: Shared Dependency Management via Version Catalog

**Choice**: Use Gradle version catalog (`libs.versions.toml`) for centralized dependency management

**Rationale**:
- Type-safe dependency declarations in Kotlin DSL
- Central place to manage versions across all submodules
- Gradle's recommended approach for multi-module projects
- Easier to update dependencies consistently

**Alternatives Considered**:
- **buildSrc**: Rejected because version catalogs are more maintainable and have better IDE support
- **Platform/BOM dependencies**: Can be used alongside version catalog for Spring Boot BOM

### Decision 4: Package Name Change to `com.yonatankarp`

**Choice**: Change all packages from `org.example` to `com.yonatankarp`

**Rationale**:
- Proper domain ownership (`yonatankarp` domain)
- Professional package naming following Java conventions
- Reflects actual ownership and authorship
- Avoids placeholder naming

**Alternatives Considered**:
- **Keep `org.example`**: Rejected as it's a placeholder name, not suitable for real project

### Decision 5: Enforce Dependency Direction via Gradle Configuration

**Choice**: Explicitly configure dependencies in `build.gradle.kts` files and add constraints to prevent violations

**Rationale**:
- Clear, explicit dependency graph
- Fails fast at compile time if rules are violated
- Self-documenting architecture through build configuration
- No need for additional architecture testing tools (though they can be added later)

**Implementation**:
```kotlin
// domain/build.gradle.kts
dependencies {
    // No dependencies on other modules
    // Only pure Kotlin and testing libraries
}

// application/build.gradle.kts
dependencies {
    implementation(project(":appointment-manager-domain"))
    // No framework dependencies
}

// adapters/build.gradle.kts
dependencies {
    implementation(project(":appointment-manager-application"))
    implementation(project(":appointment-manager-domain"))
    // Spring Boot and other frameworks here
}
```

### Decision 6: Organize Output Adapters by Technology

**Choice**: Output adapters are organized by the specific technology they use (e.g., `postgres`, `rest`) rather than abstract concepts like "persistence"

**Rationale**:
- Hexagonal architecture principle: adapters are technology-specific implementations
- Makes it clear what technologies are being used
- Easier to find and manage technology-specific code
- Aligns with the project convention that "Output adapters are organized by technology"

**Structure**:
- `adapters/output/postgres/` - PostgreSQL database implementations
- `adapters/output/rest/` - HTTP REST clients for external services (Google Calendar, Instagram, WhatsApp, etc.)

**Alternatives Considered**:
- **Generic "persistence"**: Rejected because it's an abstract concept, not a technology
- **"database"**: Less specific than naming the actual database technology

## Module Responsibilities

### appointment-manager-domain
**Purpose**: Contains core business logic and domain model

**Contents**:
- Entities (Client, Appointment)
- Value Objects (Language, CommunicationChannel, etc.)
- Aggregates
- Domain Events (AppointmentScheduled, AppointmentCompleted, etc.)
- Domain Services (business logic that doesn't fit in entities)

**Dependencies**: None (pure Kotlin only)

**Testing**: Unit tests with mocks, no framework dependencies

### appointment-manager-application
**Purpose**: Contains use cases and ports (interfaces) defining system boundaries

**Contents**:
- Input Ports (use case interfaces)
- Output Ports (repository interfaces, external service interfaces)
- Use Case implementations
- Application services (orchestration logic)

**Dependencies**: Domain module only

**Testing**: Unit tests with mocked ports, integration tests with test doubles

### appointment-manager-adapters
**Purpose**: Implements ports and provides external interfaces to the application

**Contents**:
- Input Adapters:
  - REST Controllers (Spring Web)
  - Scheduler (Spring @Scheduled, Quartz)
  - CLI (if needed)
- Output Adapters:
  - PostgreSQL (JPA/JDBC repository implementations)
  - External REST clients (Google Calendar, Instagram, WhatsApp, etc.)
- Application entry point (`@SpringBootApplication`)
- Configuration classes

**Dependencies**: Application module, Domain module, Spring Boot, PostgreSQL driver, external SDKs

**Testing**: Integration tests, acceptance tests, Spring Boot tests

## Package Structure

### Domain Module
```
com.yonatankarp.domain/
├── entities/
│   ├── Client.kt
│   └── Appointment.kt
├── valueobjects/
│   ├── CommunicationChannel.kt
│   ├── Language.kt
│   ├── AppointmentStatus.kt
│   ├── ContactInformation.kt
│   ├── PhoneNumber.kt
│   ├── EmailAddress.kt
│   ├── AppointmentDateTime.kt
│   ├── Duration.kt
│   ├── ClientId.kt
│   └── AppointmentId.kt
├── aggregates/
├── events/
│   ├── AppointmentScheduled.kt
│   ├── AppointmentCompleted.kt
│   └── AppointmentCancelled.kt
└── services/
    ├── AppointmentConflictDetector.kt
    └── CancellationPolicyValidator.kt
```

### Application Module
```
com.yonatankarp.application/
├── ports/
│   ├── input/
│   │   ├── ScheduleAppointmentUseCase.kt
│   │   ├── CancelAppointmentUseCase.kt
│   │   └── SendReminderUseCase.kt
│   └── output/
│       ├── AppointmentRepository.kt
│       ├── ClientRepository.kt
│       ├── NotificationService.kt
│       └── CalendarService.kt
└── usecases/
    ├── ScheduleAppointmentService.kt
    ├── CancelAppointmentService.kt
    └── SendReminderService.kt
```

### Adapters Module
```
com.yonatankarp.adapters/
├── input/
│   ├── rest/
│   │   ├── AppointmentController.kt
│   │   └── ClientController.kt
│   └── scheduler/
│       └── ReminderScheduler.kt
├── output/
│   ├── postgres/
│   │   ├── PostgresAppointmentRepository.kt
│   │   └── PostgresClientRepository.kt
│   └── rest/
│       ├── GoogleCalendarAdapter.kt
│       ├── InstagramAdapter.kt
│       └── WhatsAppAdapter.kt
└── AppointmentManagerApplication.kt
```

## Risks / Trade-offs

### Risk: Migration Complexity
**Mitigation**:
- Implement incrementally following the task checklist
- Verify tests pass after each major step
- Can roll back easily since it's early in development with minimal code

### Risk: Build Time Increase
**Impact**: Multi-module builds can be slower due to inter-module dependencies
**Mitigation**:
- Use Gradle build cache and configuration cache
- Parallel builds enabled by default in Gradle
- Small project size means impact will be minimal

### Trade-off: More Build Files
**Impact**: Three `build.gradle.kts` files instead of one
**Benefit**: Clearer separation of concerns and dependencies
**Mitigation**: Use version catalog to centralize dependency declarations

### Trade-off: More Complex IDE Setup
**Impact**: IDE needs to recognize all modules
**Benefit**: Better code navigation and compile-time validation
**Mitigation**: Modern IDEs (IntelliJ IDEA) handle multi-module Gradle projects well

## Migration Plan

### Phase 1: Preparation
1. Create version catalog with all current dependencies
2. Update root `settings.gradle.kts` to include new submodules
3. Create build.gradle.kts for each submodule with proper dependencies

### Phase 2: Domain Migration
1. Create domain module directory structure
2. Move existing domain code from `app/` to `appointment-manager-domain/`
3. Update package names from `org.example.domain` to `com.yonatankarp.domain`
4. Verify domain tests pass in isolation

### Phase 3: Application Migration
1. Create application module directory structure
2. Create ports and use cases (may be empty initially)
3. Verify application module compiles with domain dependency

### Phase 4: Adapters Migration
1. Create adapters module directory structure
2. Move Spring Boot application class to adapters
3. Move any existing adapter code
4. Verify application runs from adapters module

### Phase 5: Cleanup
1. Remove old `app/` directory
2. Verify all tests pass
3. Verify application builds and runs correctly

### Rollback Strategy
If issues arise:
1. Keep old `app/` directory until verification complete
2. Git allows easy revert of file moves
3. Early stage means minimal code to migrate back

## Open Questions

None - this is a straightforward structural refactoring with clear requirements from `openspec/project.md`.
