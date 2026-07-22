# Deyaar Constructions

A premium Android construction project management application built with Jetpack Compose and Material Design 3.

## Overview

Deyaar Constructions is an enterprise-grade mobile application designed for construction companies to manage projects, clients, finances, resources, and daily site operations — all from a single, polished interface.

## Features

- **Project Management** — Create, track, and manage construction projects with milestones, timelines, and progress tracking
- **Client Management** — Maintain a comprehensive client database with contact details, favorites, and project associations
- **Financial Ledger** — Track income and expenses per project with detailed cost analysis (materials, labour, other)
- **Daily Site Diary** — Record daily progress, weather, work items, and resource usage on-site
- **Resource Management** — Manage materials inventory, worker workforce, and supplier contacts
- **Daily Attendance** — Mark and track worker attendance with present/absent/half-day status
- **Reports & PDF Export** — Generate financial and project reports with PDF export and sharing
- **Documentation** — Capture and organize site photos and project documents
- **Security** — PIN lock, biometric authentication, and encrypted database backups

## Tech Stack

| Layer | Technology |
|-------|-----------|
| UI Framework | Jetpack Compose |
| Design System | Material Design 3 |
| Architecture | MVVM + Clean Architecture |
| Database | Room (SQLite) |
| Navigation | Navigation Compose |
| Async | Kotlin Coroutines + Flow |
| Image Loading | Coil |
| Camera | CameraX |
| Security | Biometric API + EncryptedSharedPreferences |
| Background Work | WorkManager |

## Requirements

- Android 7.0+ (API 24)
- Kotlin 2.2.10
- Gradle 9.1.1

## Building

```bash
# Debug build (compilation only)
./gradlew compileDebugKotlin

# Full debug APK (requires debug.keystore)
./gradlew assembleDebug

# Run tests
./gradlew testDebugUnitTest
```

## Project Structure

```
app/src/main/java/com/example/
├── core/               # Security, notifications, utilities
│   ├── security/       # PIN, biometric, backup, session management
│   ├── util/           # Date, currency, file, PDF utilities
│   └── validation/     # Input validation framework
├── data/               # Data layer
│   ├── local/          # Room database, DAOs, entities
│   └── repository/     # Repository implementations
├── domain/             # Business logic
│   ├── model/          # Domain models
│   ├── repository/     # Repository interfaces
│   └── usecase/        # Use cases
├── di/                 # Dependency injection (manual)
└── ui/                 # Presentation layer
    ├── components/     # Reusable composables (buttons, cards, charts, dialogs, shimmer)
    ├── navigation/     # Navigation graph and screen routes
    ├── screens/        # Feature screens
    │   ├── attendance/ # Daily attendance tracking
    │   ├── auth/       # PIN entry/setup
    │   ├── client/     # Client CRUD
    │   ├── dashboard/  # Business overview
    │   ├── documentation/ # Photos & documents
    │   ├── finance/    # Financial ledger & transactions
    │   ├── project/    # Project CRUD & details
    │   ├── reports/    # Report generation
    │   ├── resource/   # Materials, workers, suppliers
    │   ├── settings/   # App configuration
    │   ├── sitediary/  # Daily site diary
    │   └── splash/     # Splash screen
    └── theme/          # Material 3 theme (colors, typography, shapes, dimensions)
```

## Design System

The app uses a custom Material 3 theme with:

- **Primary**: Blueprint Blue (`#0056D2`) — trust, professionalism
- **Secondary**: Steel Gray (`#525F7F`) — stability, construction
- **Tertiary**: Safety Orange (`#E65100`) — alerts, construction visibility
- **Extended Colors**: Semantic success (green), warning (amber), info (blue) tokens accessible via `DeyaarTheme.colors`
- **Typography**: System sans-serif with proper weight hierarchy
- **Motion**: Spring-based navigation transitions, animated counters, shimmer loading states
- **Shapes**: 5-tier corner radius system (4dp → 28dp)

## License

Proprietary — Deyaar Constructions. All rights reserved.
