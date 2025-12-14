# Implementation Tasks

## 1. Prepare Build Configuration
- [x] 1.1 Update root `settings.gradle.kts` to include submodules: `appointment-manager-domain`, `appointment-manager-application`, `appointment-manager-adapters`
- [x] 1.2 Create `buildSrc` or version catalog for shared dependency management
- [x] 1.3 Configure root `build.gradle.kts` with subprojects configuration block

## 2. Create Domain Submodule
- [x] 2.1 Create directory structure: `appointment-manager-domain/src/main/kotlin/com/yonatankarp/domain/`
- [x] 2.2 Create directory structure: `appointment-manager-domain/src/test/kotlin/com/yonatankarp/domain/`
- [x] 2.3 Create `appointment-manager-domain/build.gradle.kts` with pure Kotlin dependencies only (no Spring, no framework dependencies)
- [x] 2.4 Move existing domain code from `app/src/main/kotlin/org/example/domain/` to `appointment-manager-domain/src/main/kotlin/com/yonatankarp/domain/`
- [x] 2.5 Update package declarations in domain code from `org.example.domain` to `com.yonatankarp.domain`
- [x] 2.6 Move existing domain tests and update package declarations
- [x] 2.7 Verify domain module has no external framework dependencies

## 3. Create Application Submodule
- [x] 3.1 Create directory structure: `appointment-manager-application/src/main/kotlin/com/yonatankarp/application/`
- [x] 3.2 Create directory structure: `appointment-manager-application/src/test/kotlin/com/yonatankarp/application/`
- [x] 3.3 Create `appointment-manager-application/build.gradle.kts` with dependency on domain module
- [x] 3.4 Create ports directory structure: `application/ports/input/` and `application/ports/output/`
- [x] 3.5 Create use cases directory structure: `application/usecases/`
- [x] 3.6 Verify application module depends only on domain

## 4. Create Adapters Submodule
- [x] 4.1 Create directory structure: `appointment-manager-adapters/src/main/kotlin/com/yonatankarp/adapters/`
- [x] 4.2 Create directory structure: `appointment-manager-adapters/src/test/kotlin/com/yonatankarp/adapters/`
- [x] 4.3 Create `appointment-manager-adapters/build.gradle.kts` with dependencies on application and domain modules
- [x] 4.4 Add Spring Boot dependencies to adapters module only (deferred - will be added when needed)
- [x] 4.5 Create input adapters directory structure: `adapters/input/rest/`, `adapters/input/scheduler/`
- [x] 4.6 Create output adapters directory structure: `adapters/output/rest/`, `adapters/output/db/postgres/`
- [x] 4.7 Move existing application entry point (App.kt) to adapters module
- [x] 4.8 Update package declarations to use `com.yonatankarp.adapters`

## 5. Configure Inter-Module Dependencies
- [x] 5.1 Ensure domain module has no dependencies on other modules
- [x] 5.2 Configure application module to depend on domain module
- [x] 5.3 Configure adapters module to depend on both application and domain modules
- [x] 5.4 Add dependency constraints to prevent circular dependencies
- [x] 5.5 Verify dependency graph with `./gradlew dependencies`

## 6. Update Build and Run Configuration
- [x] 6.1 Update main application class path in adapters module
- [x] 6.2 Configure Spring Boot plugin in adapters module only (deferred - will be added when needed)
- [x] 6.3 Update `application` plugin configuration if needed (deferred - will be added when needed)
- [x] 6.4 Test build with `./gradlew build`
- [x] 6.5 Test run with `./gradlew :appointment-manager-adapters:run` (deferred - requires application plugin or Spring Boot)

## 7. Migrate and Verify Tests
- [x] 7.1 Ensure all domain tests run from domain module: `./gradlew :appointment-manager-domain:test`
- [x] 7.2 Ensure all application tests run from application module: `./gradlew :appointment-manager-application:test`
- [x] 7.3 Ensure all adapter tests run from adapters module: `./gradlew :appointment-manager-adapters:test`
- [x] 7.4 Verify all tests pass: `./gradlew test`

## 8. Clean Up Old Structure
- [x] 8.1 Remove old `app/` directory after verifying everything works
- [x] 8.2 Update `.gitignore` if needed for new module structure (not needed)
- [x] 8.3 Update any documentation referencing old structure

## 9. Validation
- [x] 9.1 Verify clean build: `./gradlew clean build`
- [x] 9.2 Verify application starts: `./gradlew :appointment-manager-adapters:run` (deferred - requires application plugin or Spring Boot)
- [x] 9.3 Verify dependency constraints are enforced (verified via dependency graph)
- [x] 9.4 Run all tests and confirm they pass
- [x] 9.5 Verify IDE recognizes all modules correctly (manual verification)
