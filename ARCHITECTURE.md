# Construction Manager - Architecture & Technical Foundation

## 1. Architecture Decision Record (ADR)
**Decision**: Clean Architecture with MVVM, single-module initially with strict package by feature.
**Why**: Ensures separation of concerns. Business logic is isolated from UI and framework details.
**Trade-offs**: Introduces more boilerplate (mappers, use cases) for simple features.
**Alternatives Rejected**: MVI was rejected due to unnecessary complexity for a CRUD-heavy offline app. Multi-module (Gradle modules) was rejected for the initial phase to avoid build setup overhead, though the package structure will support easy extraction later.

## 2. Project Folder Structure
```
com.example
├── core/
│   ├── arch/          # Base classes (UseCase, BaseViewModel)
│   ├── di/            # Hilt modules
│   ├── error/         # Error handling, Result wrappers
│   ├── security/      # PIN, biometric unlock, encryption
│   └── util/          # Extensions, date formatters
├── data/
│   ├── local/         # Room DB, DAOs, TypeConverters
│   ├── preferences/   # DataStore
│   └── repository/    # Repository implementations
├── domain/
│   ├── model/         # Domain entities
│   ├── repository/    # Repository interfaces
│   └── usecase/       # Business logic (e.g., CalculateProjectCostUseCase)
├── feature/           # Feature isolation
│   ├── dashboard/
│   ├── projects/
│   ├── clients/
│   ├── materials/
│   ├── labour/
│   ├── expenses/
│   ├── backup/
│   └── settings/
└── ui/
    ├── common/        # Reusable Compose components
    ├── navigation/    # NavHost, routes
    └── theme/         # Color, Typography, Shape, Theme
```

## 3. Module Structure
**Decision**: Single Module (Strictly Modularized by Package).
**Reasoning**: For an offline-first app maintained by a solo developer, a single module with strict "package-by-feature" organization provides the best balance of compilation speed and low maintenance overhead. Multi-module introduces configuration overhead that isn't justified without a large team. The boundaries will be enforced via Detekt/Lint rules.

## 4. Dependency Graph
**Data Flow (Unidirectional)**:
```
[ UI (Jetpack Compose) ] 
       ↓ (Triggers Intent/Action)
[ ViewModel ]
       ↓ (Calls)
[ Use Case (Domain) ]
       ↓ (Interface delegation)
[ Repository Interface (Domain) ]
       ↓ (Implementation injected via Hilt)
[ Repository Impl (Data) ]
       ↓ (Queries)
[ Room Database / DataStore ]
```

## 5. Feature Isolation Strategy
Every feature package (e.g., `feature/projects`) will be completely self-contained regarding its Presentation layer. 
- Features communicate by passing IDs through Navigation routes, never by passing parcelable objects.
- A feature cannot depend on another feature's ViewModel or UI state.
- Cross-feature data access happens at the Domain/Repository level (e.g., Projects feature fetching Client data via `GetClientByIdUseCase`).

## 6. Database Strategy
**Room Architecture**:
- **Entities**: Represents normalized SQLite tables.
- **Primary Keys**: UUID strings will be used exclusively to future-proof for syncing/merging.
- **Foreign Keys**: Enforce referential integrity (e.g., `ExpenseEntity` cascades delete if `ProjectEntity` is deleted).
- **Indexes**: Applied to foreign keys and frequently queried columns (e.g., `projectId`, `date`).
- **Transactions**: Used for bulk inserts/updates to guarantee atomicity.
- **Migrations**: Automated Room migrations handled meticulously for every schema change.
- **Images**: Only relative file paths (`/images/projects/uuid.jpg`) are stored in the DB, never blobs.

## 7. State Management Strategy
- **Immutable UI State**: `data class ProjectUiState(...)`
- **StateFlow**: Exposes continuous state from ViewModel to Compose (`val uiState: StateFlow<ProjectUiState>`).
- **SharedFlow / Channels**: Used for one-time events (e.g., ShowSnackbar, NavigateUp) to avoid re-triggering on configuration changes.
- **States**: Loading, Success, Error are represented via sealed interfaces.

## 8. Navigation Strategy
- **Navigation Compose**: Single Activity, pure Compose navigation.
- **Type-safe Routing**: Using Kotlin Serialization for defining nested graphs and destinations.
- **Nested Graphs**: `AuthGraph` (PIN unlock), `MainGraph` (Dashboard, Projects, etc.).
- **Back Stack**: Handled via Compose Navigation, ensuring proper pop behavior on saves/deletes.

## 9. Dependency Injection Strategy
- **Hilt**: Standardized DI.
- **Scopes**: 
  - `@Singleton` for Room DB, DataStore, and Repositories.
  - `@ViewModelScoped` for Use Cases (if stateful, though usually stateless).
- Interfaces (Domain) are bound to Implementations (Data) using `@Binds` in Hilt Modules.

## 10. Design System Strategy
- **Material 3 Expressive**: Leveraging dynamic theming where appropriate, but establishing a strict brand palette (e.g., Slate/Blue for professional construction).
- **Reusable Components**: `PrimaryButton`, `OutlinedCard`, `StatCard`, extracted to `ui/common`.
- **Adaptive**: WindowSizeClasses to support eventual tablet usage on construction sites.
- **Dark Mode**: Fully supported via M3 ColorScheme.

## 11. Testing Strategy
- **Unit**: JUnit5 + MockK for UseCases and ViewModels.
- **Database**: In-memory Room DB tests (Robolectric) to verify queries.
- **UI**: Compose UI tests for isolated components.
- **Screenshot**: Roborazzi for visual regression on complex dashboards.
- **Coverage**: Target 80% on Domain logic.

## 12. Error Handling Strategy
- **Result Wrapper**: `sealed interface Result<out T> { data class Success<T>(val data: T): Result<T>; data class Error(val exception: Throwable): Result<Nothing> }`
- Domain layer returns `Result`. ViewModel maps `Result.Error` to user-friendly UI strings.
- **Timber**: Logging errors locally to aid in debugging without exposing data.

## 13. Performance Strategy
- **Startup**: Baseline Profiles + Macrobenchmark.
- **Lists**: `LazyColumn` with stable keys to prevent recomposition.
- **Images**: Coil with disk caching, downsampling large camera photos before rendering.
- **DB Queries**: Asynchronous Flow emissions.

## 14. Security Strategy
- **App Lock**: BiometricPrompt API with PIN fallback on app resume.
- **DataStore**: EncryptedSharedPreferences for sensitive settings (e.g., Backup password).
- **Backups**: Zip files encrypted with AES-256 before exporting.

## 15. Backup & Restore Architecture
1. **Export**: 
   - Checkpoint Room DB (`pragma wal_checkpoint(TRUNCATE)`).
   - Copy DB files, `files/images`, and `files/pdfs` into a temporary staging directory.
   - Zip the directory using `java.util.zip`.
   - Encrypt the Zip.
   - Pass to Android `Intent.ACTION_CREATE_DOCUMENT` for the user to save to Google Drive or local storage.
2. **Restore**: 
   - User picks Zip. Decrypt -> Unzip -> Validate schema version -> Overwrite current DB & files -> Restart App.

## 16. Coding Standards
- **Naming**: `FeatureViewModel`, `FeatureScreen`, `FeatureRepository`.
- **Format**: Ktlint + Detekt enforced.
- **Comments**: KDoc for all UseCases and Repositories detailing *why*, not *what*.

## 17. Git Strategy
- **Branching**: Trunk-based development or GitHub Flow (`feature/project-creation`).
- **Commits**: Conventional commits (`feat: add project dashboard`, `fix: room migration`).

## 18. Scalability Strategy (Future-Proofing)
- **Cloud Sync**: By keeping the Domain layer pure and using Repository interfaces, switching from `Room` to a `SyncManager` that wraps Room + Retrofit would require zero changes to the UI or UseCases.
- **Multi-Device**: Using UUIDs for primary keys ensures that if devices are ever synced in the future, primary key collisions will not occur.

## 19. Development Roadmap (20 Phases)
1. Architecture & Project Setup (Current)
2. Core UI Design System & Theme
3. Core Database Definitions (Base Entities, TypeConverters)
4. Client Management Feature (Data -> Domain -> UI)
5. Project Management Feature
6. Dashboard Feature (Aggregating Client/Project Data)
7. Labour Tracking Feature
8. Materials Tracking Feature
9. Expense & Payment Feature
10. CameraX Integration & Offline Photo Management
11. Advanced Charts & Analytics (Compose-Charts)
12. PDF Report Generation Engine
13. Security Module (Biometrics & PIN Lock)
14. Zip Export & Manual Backup System
15. Zip Import & Restore System
16. Search & Global Filtering
17. Unit, DB, and UI Testing Implementation
18. Performance Optimization (Baseline Profiles)
19. UI Polish & Premium Animations
20. Final QA & Release Prep

## 20. Architecture Review
**Strengths**: Extremely robust, isolated layers, highly testable, fully offline capability with zero compromises.
**Weaknesses**: High boilerplate for simple CRUD operations (Mappers, Use Cases, Interfaces).
**Improvements for SaaS future**: Because UUIDs are used, migrating to SaaS just requires injecting a `RemoteDataSource` and a WorkManager `SyncWorker` to reconcile local changes with the cloud. The UI remains 100% untouched.
