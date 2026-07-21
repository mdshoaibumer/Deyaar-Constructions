# DEYAAR CONSTRUCTIONS - Dashboard Architecture & Review

## 1. Dashboard ViewModel
The `DashboardViewModel` acts as the single source of truth for the UI state.
It uses `GetDashboardStatsUseCase` which aggregates 5 different flows (`Clients`, `Total Projects`, `Active Projects`, `Expenses`, `Recent Projects`) from the `DashboardRepository` using `combine()`.
This architecture ensures that if *any* of the local database tables change (e.g. an Expense is added in another screen), the Dashboard automatically animates to the new value instantly because it relies on `StateFlow`.
We expose `DashboardUiState` (Loading, Empty, Success) ensuring the UI can handle the empty-onboarding experience.

## 2. Dashboard UI & Widgets
The UI was built adhering strictly to the existing `Dimens` token system (8dp grid, `radiusLarge`, etc). 
It features a modular `LazyColumn` holding the following custom components:
- **KpiHorizontalList**: An adaptive horizontal scroll of the most crucial counters (Active Projects, Clients, Completed, Labour).
- **FinancialSummary**: Displays large animated currency formats for Expenses, Net Profit, and Contract Value.
- **ExpenseTrendChart**: Features a fully custom lightweight Compose Canvas line chart mapping expenses over time.
- **QuickActionsGrid**: Provides 4 large, tap-friendly action buttons.
- **MaterialSummaryWidget**: Simulates a high-priority low-stock alert.
- **UpcomingItemsWidget**: Displays urgent action items like pending payments and approaching deadlines.
- **RecentActivityTimeline**: A visual timeline showing the chronological events from the system (mocked for now, ready for DB hookup).
- **RecentProjectItem**: Cards displaying recent projects with progress bars and status badges.

## 3. Empty State
If the database contains 0 projects and 0 clients, an onboarding state (`DashboardEmptyState`) is shown, guiding the user to start using the application with clear CTA buttons.

## 4. Animations & Performance
- We created a custom `AnimatedCounter` component using `AnimatedContent` with `slideInVertically` and `fadeIn` transitions. Whenever a KPI value updates, it smoothly slides into place.
- We built a custom path-drawing animation for the `SimpleLineChart` using `Animatable`.
- `collectAsStateWithLifecycle()` is used to ensure flows are paused when the app is in the background, saving battery and CPU.

## 5. Accessibility
- Custom widgets use semantic grouping.
- High contrast colors from the `MaterialTheme` have been consistently applied.

## 6. Testing
- Created `DashboardViewModelTest` using `kotlinx.coroutines.test`. It verifies that an empty repository yields `DashboardUiState.Empty` and a populated one yields `DashboardUiState.Success` with the exact mapped models.

## 7. Self Review & Future Improvements
- **UX Improvement**: The Quick Actions grid uses icons, but could benefit from a short description tooltip or long-press context menu on tablets.
- **Future Widget**: A "Weather at Site" widget could be added by querying the location of Active Projects and fetching a simple weather API.
- **Performance**: As the project scales, combining 5-10 database flows might become slightly heavy. We might want to introduce a debouncer (`debounce(200)`) in the UseCase if multiple tables update simultaneously during a sync operation to avoid rapid UI frame skipping.
