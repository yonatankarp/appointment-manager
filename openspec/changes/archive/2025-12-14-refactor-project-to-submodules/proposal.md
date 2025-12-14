# Change: Refactor Project to Multi-Module Structure

## Why
The current single-module structure does not enforce the boundaries between hexagonal architecture layers (domain, application, adapters). Moving to separate Gradle submodules will provide compile-time enforcement of dependency rules, preventing accidental coupling between layers and ensuring the domain remains framework-agnostic.

This change aligns the physical project structure with the logical architecture defined in `openspec/project.md`, making it impossible for adapters to directly depend on each other or for the domain to accidentally depend on infrastructure code.

## What Changes
- **BREAKING**: Restructure the project from single module to multi-module Gradle build
- Create `appointment-manager-domain` submodule containing domain entities, value objects, aggregates, events, and services
- Create `appointment-manager-application` submodule containing ports (input/output interfaces) and use cases
- Create `appointment-manager-adapters` submodule containing input adapters (REST, CLI, scheduler) and output adapters (REST clients, persistence)
- Update Gradle build configuration to enforce dependency direction:
  - Domain depends on nothing (pure Kotlin)
  - Application depends on domain only
  - Adapters depend on application and domain
  - Adapters cannot depend on other adapters
- Move existing domain code from `app/src/main/kotlin/org/example/domain/` to `appointment-manager-domain/src/main/kotlin/com/yonatankarp/domain/`
- Update package names from `org.example` to `com.yonatankarp`
- Update root `settings.gradle.kts` to include all submodules
- Migrate test code to corresponding submodule test directories

## Impact
- **Affected specs**: None (no specs exist yet, this is purely structural)
- **Affected code**:
  - Root `settings.gradle.kts`: Add submodule includes
  - Root `build.gradle.kts`: Configure subproject dependencies
  - `app/` directory: Will be replaced by three submodules
  - Any existing domain code under `app/src/main/kotlin/org/example/domain/` will be moved to `appointment-manager-domain/src/main/kotlin/com/yonatankarp/domain/`
  - Any existing domain tests under `app/src/test/kotlin/org/example/domain/` will be moved to `appointment-manager-domain/src/test/kotlin/com/yonatankarp/domain/`
  - Package names updated from `org.example` to `com.yonatankarp` throughout the project
- **Dependencies**: No new external dependencies, only inter-module dependencies
- **Testing**: All existing tests must continue to pass after migration
- **Breaking changes**: **BREAKING** - Build commands, module references, and package names will change
- **Coordination**: Should be completed before or in coordination with `add-core-domain-entities` change
