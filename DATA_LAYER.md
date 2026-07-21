# DEYAAR CONSTRUCTIONS - Data Layer & Core Infrastructure

## 1. Room Database Design
The offline-first architecture relies on Room as the single source of truth.

**Entities Overview:**
- `ClientEntity`: Stores client information.
- `ProjectEntity`: Stores project details, linked to `ClientEntity` (Restrict on delete).
- `ExpenseEntity`: Tracks project expenses, linked to `ProjectEntity` (Cascade on delete).
- *(Future)* `LabourEntity`, `AttendanceEntity`, `MaterialEntity`, `MaterialTransactionEntity`, `PaymentEntity`, `SitePhotoEntity`, `DocumentEntity`, `NoteEntity`, `ProjectTimelineEntity`, `ProjectMilestoneEntity`, `SettingsEntity`, `BackupMetadataEntity`.

**Relationships & Performance:**
- **Foreign Keys**: Used rigorously to enforce data integrity. Deleting a project cascades down to its expenses, photos, and materials.
- **Indexes**: Applied to foreign keys (e.g., `clientId`, `projectId`) and frequently queried/sorted columns (e.g., `date`, `updatedAt`) to ensure `O(log N)` query performance.
- **Transactions**: Complex operations (like adding a material transaction that updates the material inventory) are wrapped in `@Transaction` to maintain atomic consistency.

## 2. Data Access Objects (DAOs)
- **Flow**: All reading operations (`getAll...`, `get...ById`) return `Flow<List<T>>` or `Flow<T?>`. This provides reactive updates. When the database changes, the UI updates automatically without polling.
- **Suspend Functions**: One-shot operations (insert, update, delete) are marked as `suspend` and executed on the IO dispatcher.
- **Conflict Strategy**: `OnConflictStrategy.REPLACE` is used primarily since we use UUIDs and the app is single-user offline.

## 3. Repositories (Domain vs Data)
- **Domain Interfaces**: Defined in `domain/repository` (e.g., `ClientRepository`). Exposes `Flow<List<Client>>` (Domain models, not Entities).
- **Data Implementations**: Defined in `data/repository`. Injects DAOs. Maps `ClientEntity` to `Client` and vice versa.
- **Why?**: The domain layer (UseCases, ViewModels) should not know about Room or SQLite. If we ever add a cloud sync engine, we only change the repository implementation.

## 4. DataStore Manager
- `AppPreferencesManager` uses Jetpack DataStore (Preferences).
- Stores non-relational, key-value configurations: `pin_enabled`, `dark_mode`, `currency`, `company_name`, `backup_location`.
- Exposed as `Flow<T>` for reactive UI updates (e.g., instantly updating the currency symbol across the app when changed in settings).

## 5. UseCase Base Architecture
- `SuspendUseCase<P, R>`: For one-time operations (e.g., `CreateProjectUseCase`). Automatically switches to IO dispatcher and wraps exceptions in a `Result.Failure`.
- `FlowUseCase<P, R>`: For continuous data streams (e.g., `ObserveActiveProjectsUseCase`). Catches flow exceptions and emits them safely to the ViewModel.
- **Why?**: Standardizes error handling and coroutine context switching. ViewModels become incredibly thin and testable.

## 6. Result & Error Hierarchy
- **Result Wrapper**: `Success<T>`, `Failure(AppError)`, `Loading`, `Empty`.
- **AppError Hierarchy**: 
  - `DatabaseError`: SQLite constraint violations.
  - `ValidationError`: Form validation failures (contains a Map of fields to error messages).
  - `StorageError`: Missing permissions or full disk space.
  - `SecurityError`: Invalid PIN.

## 7. Validation Framework
- Centralized validation logic (`ValidationFramework.kt`) for Names, Phones, Amounts, Quantities.
- Throws `ValidationError` map, which the UI consumes to display inline errors below TextFields.

## 8. Logging Strategy (Timber)
- **Timber** is initialized in `ConstructionApp`.
- Used extensively in the Base UseCase to log failures without crashing the app.
- Release builds will plant a custom `CrashReportingTree` to log non-fatal errors to a local encrypted text file for debugging (since Firebase Crashlytics is forbidden).

## 9. Database Migration Strategy
- **AutoMigrations**: For simple additive changes (adding a column or table).
- **Manual Migrations**: For destructive or complex data transformation (e.g., changing a column type or normalizing a table).
- **Testing**: `MigrationTestHelper` will be used in instrumentation tests to verify schema changes from v1 -> v2.

## 10. Performance Optimizations
- **No Blob Storage**: Images and PDFs are stored in the app's internal `files/` directory. Room only stores the string path. This keeps the database lightweight and fast.
- **Lazy Queries**: DAOs only fetch what is needed. Large lists use specific projection queries if the full entity is not required.
- **Coroutines**: All DB access is strictly off the Main thread.

## 11. Testing Architecture
- **In-Memory Room**: Unit testing DAOs using `Room.inMemoryDatabaseBuilder`.
- **MockK**: Mocking DAOs when testing Repositories, and mocking Repositories when testing UseCases.
- **Validation**: Pure JUnit tests for `ValidationFramework` and `FormatUtils` (fast, no Android dependencies).

## 12. Self Review
- **Strengths**: Highly decoupled. The ViewModel only talks to UseCases via `Result` objects. The database relies strictly on UUIDs which prevents auto-increment collisions and future-proofs the app for syncing.
- **Weaknesses**: Using UUIDs as primary keys is slightly slower than Integers for SQLite indexing.
- **Improvements**: For a purely offline app, Integer primary keys are faster, but the small performance hit of UUIDs is worth the flexibility if the user ever buys a second device and wants to merge backups. We will mitigate the UUID lookup speed by ensuring all foreign keys are indexed.
