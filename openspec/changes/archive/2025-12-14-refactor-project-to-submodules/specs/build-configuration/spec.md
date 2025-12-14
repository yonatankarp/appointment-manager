# Build Configuration Specification

## ADDED Requirements

### Requirement: Multi-Module Gradle Structure
The project SHALL be organized into three separate Gradle submodules to enforce hexagonal architecture boundaries at compile time.

#### Scenario: Domain module isolation
- **WHEN** the domain module is compiled
- **THEN** it SHALL NOT have dependencies on any framework or infrastructure code
- **AND** it SHALL only contain pure Kotlin code and domain logic

#### Scenario: Application module depends on domain only
- **WHEN** the application module is compiled
- **THEN** it SHALL depend only on the domain module
- **AND** it SHALL NOT have dependencies on Spring Boot or other framework code

#### Scenario: Adapters module composition
- **WHEN** the adapters module is compiled
- **THEN** it SHALL depend on both application and domain modules
- **AND** it SHALL contain all framework dependencies (Spring Boot, PostgreSQL driver, etc.)
- **AND** it SHALL contain the application entry point

#### Scenario: Dependency constraint enforcement
- **WHEN** attempting to add an invalid dependency (e.g., Spring Boot in domain module)
- **THEN** the build SHALL fail with a clear error message

### Requirement: Package Naming Convention
All packages SHALL use the `com.yonatankarp` group identifier.

#### Scenario: Domain package naming
- **WHEN** domain code is created
- **THEN** it SHALL be under the `com.yonatankarp.domain` package

#### Scenario: Application package naming
- **WHEN** application code is created
- **THEN** it SHALL be under the `com.yonatankarp.application` package

#### Scenario: Adapters package naming
- **WHEN** adapter code is created
- **THEN** it SHALL be under the `com.yonatankarp.adapters` package

### Requirement: Technology-Based Output Adapter Organization
Output adapters SHALL be organized by the specific technology they use, not by abstract concepts.

#### Scenario: Database adapter organization
- **WHEN** database adapter code is created
- **THEN** it SHALL be placed under `adapters/output/postgres/` package
- **AND** it SHALL NOT be placed under generic "persistence" or "database" packages

#### Scenario: External REST adapter organization
- **WHEN** external HTTP client adapter code is created
- **THEN** it SHALL be placed under `adapters/output/rest/` package
- **AND** it SHALL be named after the specific service (e.g., GoogleCalendarAdapter, InstagramAdapter)

### Requirement: Module Directory Structure
Each submodule SHALL follow standard Gradle/Maven directory structure with Kotlin source files.

#### Scenario: Domain module structure
- **WHEN** the domain module is created
- **THEN** it SHALL have the directory structure:
  - `appointment-manager-domain/src/main/kotlin/com/yonatankarp/domain/`
  - `appointment-manager-domain/src/test/kotlin/com/yonatankarp/domain/`
  - `appointment-manager-domain/build.gradle.kts`

#### Scenario: Application module structure
- **WHEN** the application module is created
- **THEN** it SHALL have the directory structure:
  - `appointment-manager-application/src/main/kotlin/com/yonatankarp/application/`
  - `appointment-manager-application/src/test/kotlin/com/yonatankarp/application/`
  - `appointment-manager-application/build.gradle.kts`

#### Scenario: Adapters module structure
- **WHEN** the adapters module is created
- **THEN** it SHALL have the directory structure:
  - `appointment-manager-adapters/src/main/kotlin/com/yonatankarp/adapters/`
  - `appointment-manager-adapters/src/test/kotlin/com/yonatankarp/adapters/`
  - `appointment-manager-adapters/build.gradle.kts`
