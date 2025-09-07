# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

HateItOrRateIt is a modern Android app that helps users track products they like or dislike with photos, names, descriptions, and store information. Built with Kotlin, Jetpack Compose, and a modular architecture following clean architecture principles.

## Essential Commands

### Building and Testing

```bash
# Build the app
./gradlew build

# Run all tests
./gradlew test

# Run a specific test class
./gradlew :feature:settings:ui:testGplayDebugUnitTest --tests="*SettingsViewModelTest"

# Run instrumented tests
./gradlew connectedAndroidTest

# Generate test coverage report
./gradlew testAggregateTestReport

# Build specific variants
./gradlew assembleGplayDebug  # Google Play version
./gradlew assembleFdroidDebug # F-Droid version
```

### Code Quality

```bash
# Run linting
./gradlew ktlintCheck

# Auto-fix lint issues
./gradlew ktlintFormat

# Run static analysis
./gradlew detekt

# Generate dependency analysis
./gradlew buildHealth
```

## Architecture Overview

### Modular Structure

The app follows a feature-based modular architecture with strict separation:

- **`:app`** - Main application module with Activity, Navigation, and DI setup
- **`:feature:*:ui`** - Feature modules (home, settings, details, productmanager) containing UI, ViewModels, and navigation
- **`:data:*`** - Data layer modules split into API/implementation pairs for repositories, analytics, storage, etc.
- **`:core:*`** - Core utilities (async, app updates, app info)
- **`:utils:*`** - Utility modules for files, date/time, Android APIs
- **`:uikit`** - Shared UI components, themes, and design system
- **`:strings`** - Centralized string resources
- **`:testing:*`** - Test utilities and fakes

### Key Architectural Patterns

#### API/Implementation Split

Most data layer modules follow the pattern:

- `*-api` module: Contains interfaces and models
- `*-impl` module: Contains concrete implementations

Example: `local-datastorage-api` + `local-datastorage-impl`

#### Flavor-Based Implementations

The app supports two build flavors with different implementations:

- **`gplay`** - Google Play version with Firebase Analytics, Crashlytics, Remote Config
- **`fdroid`** - F-Droid version with no-op implementations for privacy

Flavor-specific code is located in `src/gplay/` and `src/fdroid/` directories.

#### ViewState Pattern
All screens use a consistent ViewState pattern:
```kotlin
data class FeatureViewState(
    val data: SomeData = defaultValue,
    val isLoading: Boolean = false,
    val onAction: () -> Unit = {}
)

class FeatureViewModel : ViewModel() {
    private val _viewState = MutableStateFlow(FeatureViewState())
    val viewState = _viewState.asStateFlow()
}
```

### Navigation Architecture

- Uses Jetpack Navigation with type-safe destinations
- Each feature defines its own `*NavDestination` and navigation extensions
- Main navigation is centralized in `MainNavHost.kt`

### Dependency Injection

- Uses Hilt for dependency injection
- Each module has its own DI module
- Flavor-specific implementations are provided through different DI modules

## Testing Strategy

### Test Structure

- **Unit tests**: ViewModels, repositories, utilities
- **Integration tests**: Database operations, API interactions  
- **UI tests**: Limited to complex navigation flows

### Test Conventions

- Use MockK for mocking
- Coroutine tests use `MainDispatcherRule`
- Database tests use in-memory databases
- ViewState testing focuses on state transitions and business logic

### Test Location Pattern
```
feature/
└── settings/
    └── ui/
        ├── src/main/java/...          # Implementation
        └── src/test/java/...          # Unit tests (mirrors main structure)
```

## Development Workflows

### Adding New Features

1. Create feature module under `feature/[name]/ui/`
2. Add navigation destinations and extensions
3. Implement ViewState pattern for screens
4. Add string resources to `:strings` module
5. Update `MainNavHost.kt` with new routes
6. Add comprehensive unit tests

### Working with Data Layer

1. Define interfaces in `*-api` module
2. Implement in `*-impl` module with proper DI setup
3. Consider flavor-specific implementations if needed
4. Add repository tests with fakes from `:testing:domain`

### Modifying UI Components

- Shared components go in `:uikit`
- Use Material 3 design system
- Follow existing `Plato*` naming convention for custom components
- Always provide preview functions for Compose components

## Important Notes

### Build Variants and Flavors

The project automatically detects build type:

- Gradle tasks containing "Gplay" apply Google Services plugins
- F-Droid builds exclude Google dependencies

### String Resources

- All strings centralized in `:strings` module
- Use `RString` class for type-safe string references
- Supports multiple languages (English, German, French)

### Code Quality Requirements

- All modules use KtLint, Detekt, and Compose rules
- Tests are required for ViewModels and business logic
- Coverage exclusions are defined in root `build.gradle.kts`

### Material 3 Migration

The app recently migrated to Material 3. When working with text visibility issues:

- Wrap content in `Surface` components to establish proper color context
- Use `MaterialTheme.colorScheme` for theming
- The main activity already includes a root `Surface` wrapper

### Key Dependencies

- Jetpack Compose for UI
- Hilt for dependency injection
- Room for local database
- Retrofit for networking (if applicable)
- Kotlin Coroutines + Flow for async operations
- Navigation Compose for navigation

## Code Style Guidelines

### File Formatting

- Always add a newline at the end of files when creating new files
- This ensures consistency with the project's linting rules and prevents unnecessary diff noise

### Comments

- Do not add comments in newly written code
- The code should be self-documenting through clear naming and structure
- Only add comments when explicitly requested by the user