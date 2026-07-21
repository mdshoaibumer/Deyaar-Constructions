# DEYAAR CONSTRUCTIONS - Application Shell Architecture

## 1. Splash Screen & Startup Flow
- **Splash Screen API**: Used to provide a seamless transition from the Android launcher to the app. 
- **Startup Flow**: 
  1. Initialize core dependencies (DataStore, Room via Coroutine block).
  2. Read `pin_enabled` from DataStore.
  3. If PIN is enabled, route to `AuthGraph`.
  4. If PIN is disabled, route directly to `MainGraph` (Dashboard).
- **Offline Indicator**: A subtle text at the bottom of the Settings/Profile screen indicating "Offline Mode Active" or simply an offline icon in the drawer/app bar to reinforce security.

## 2. Authentication & Session Management
- **PIN Lock**: A custom Compose PIN pad ensures the user is authenticated locally.
- **Biometric Prompt**: Integrated with Android's Biometric API. If available, it prompts immediately; otherwise falls back to PIN.
- **SessionManager**: Tracks `lastActiveTime`. If the app goes to the background for > 5 minutes, it forces re-authentication upon resume.

## 3. Navigation Foundation
- **Navigation Compose**: Centralized in `AppNavigation.kt`.
- **Graphs**:
  - `RootGraph`: Manages switching between Auth and Main.
  - `AuthGraph`: Setup PIN, Verify PIN.
  - `MainGraph`: Dashboard, Projects, Clients, Finances.
- **Adaptive**: Utilizes `WindowSizeClass`. On phones, a `NavigationBar` is used. On tablets/landscape, a `NavigationRail` is used.

## 4. Application Scaffold
- **Reusable `AppScaffold`**: Wraps the standard Material 3 Scaffold. Handles the global SnackbarHost, bottom navigation visibility, and floating action buttons based on the current route.

## 5. Permission Framework
- **Accompanist Permissions**: Wraps permission requests for Camera and Storage.
- **Graceful Degradation**: If storage permission is denied, it shows a rationale dialog. If permanently denied, it guides the user to Settings. It never blocks the core app flow.

## 6. Theme & Loading
- **Theme**: Automatically respects system dark mode, but allows overriding via DataStore.
- **Loading**: Prefer `SkeletonLoader` for content (projects, clients). Use standard circular indicators for blocking actions (like ZIP export).

## 7. Reusable Dialogs & Bottom Sheets
- **Dialogs**: `DeyaarDialog` generic component for Confirm, Delete, Error, and Info.
- **Bottom Sheets**: Used for contextual actions (e.g., "Sort by", "Filter", "Export options").

## 8. Global Search Framework
- A top-level search bar accessible from the Dashboard.
- Future modules will register their search providers, allowing the user to search across Clients, Projects, and Expenses from a single unified input.

## 9. File Manager
- `FileManager`: A utility class to abstract `Context.filesDir`.
- Handles creating specific directories: `/images`, `/pdfs`, `/exports`.
- Ensures all data stays strictly within the app's sandboxed storage.

## 10. Performance & Accessibility
- **Performance**: Heavy use of `remember` and `derivedStateOf`. Image paths are loaded via Coil off the main thread.
- **Accessibility**: All buttons and icons have clear `contentDescription`. Colors adhere to M3 contrast ratios.
