# DEYAAR CONSTRUCTIONS - Premium Design System

## 1. Design Philosophy
The Deyaar Constructions design system is built on the principles of **Professionalism, Clarity, and Efficiency**.
- **Premium & Elegant**: Utilizing subtle gradients, deep slate colors, and refined typography.
- **Clean & Spacious**: Leveraging generous negative space (8dp grid) to prevent cognitive overload.
- **Fast & Responsive**: Animations are purposeful and quick (under 300ms) to ensure the app feels like a high-performance tool, not a toy.
- **Legible Outdoors**: High contrast ratios for site engineers working in direct sunlight.

## 2. Color System
Inspired by construction materials (steel, concrete, blueprint blue) and enterprise trust.
- **Primary (Blueprint Blue - #0056D2)**: Represents trust, stability, and engineering precision. Used for primary actions, active states, and key highlights.
- **On Primary (#FFFFFF)**: Crisp white for text on primary buttons.
- **Primary Container (#D9E2FF)**: Soft blue for selected states and subtle highlights.
- **Secondary (Steel Gray - #525F7F)**: Represents structural integrity. Used for secondary actions and less prominent UI elements.
- **Tertiary (Safety Orange - #FF6D00)**: Used sparingly for prominent calls to action or alerts requiring immediate attention without being an error.
- **Background (Light: #F8F9FA, Dark: #121212)**: Soft off-white to reduce eye strain compared to pure white. Deep gray for dark mode to save OLED battery and reduce glare.
- **Surface (#FFFFFF / #1E1E1E)**: Pure white for cards in light mode to provide depth against the off-white background.
- **Error (Danger Red - #D32F2F)**: Universal error color.
- **Success (Emerald Green - #2E7D32)**: Used for completed projects, successful payments, and positive trends.
- **Warning (Amber - #F57F17)**: Used for pending payments, low stock, or approaching deadlines.

## 3. Typography
Using **Inter** (or standard Roboto with customized tracking) for pristine legibility across all screen densities.
- **Display Large (57sp, -0.25sp tracking)**: Grand totals, primary dashboard metrics.
- **Headline Medium (28sp, 0sp tracking)**: Screen titles (e.g., "Projects", "Clients").
- **Title Medium (16sp, 0.15sp tracking, Medium weight)**: Card titles, primary list items.
- **Body Large (16sp, 0.5sp tracking, Regular)**: Standard text, notes, descriptions.
- **Label Large (14sp, 0.1sp tracking, Medium)**: Buttons, tabs, and small contextual labels.

## 4. Spacing System (8dp Grid)
- **4dp (Micro)**: Space between an icon and its adjacent text.
- **8dp (Small)**: Space between related items within a card.
- **16dp (Medium)**: Standard screen padding, margin between separate components.
- **24dp (Large)**: Space between distinct sections (e.g., "Recent Activity" vs "Upcoming Payments").
- **32dp (Extra Large)**: Bottom padding above the Navigation Bar or FAB.

## 5. Corner Radius (Shapes)
- **Small (4dp)**: Checkboxes, small badges, tooltips.
- **Medium (12dp)**: Buttons, text fields, standard cards.
- **Large (16dp)**: Prominent dashboard KPI cards, bottom sheet top corners.
- **Extra Large (28dp)**: Floating Action Buttons (FAB).

## 6. Iconography
**Material Symbols Rounded (Outlined style for unselected/inactive, Filled for selected/active).**
- *Why*: Rounded icons feel more modern and approachable, reducing the harshness of enterprise software while maintaining clarity. Filled icons for active states provide a clear, unmistakable visual cue.

## 7. Elevation System
- **Level 0 (0dp)**: Screen backgrounds, flat lists.
- **Level 1 (1dp + subtle shadow)**: Standard project/client cards.
- **Level 2 (3dp)**: Sticky headers, search bars.
- **Level 3 (6dp)**: FAB, Bottom App Bar.
- **Level 4 (8dp)**: Bottom Sheets, Dialogs.
*Note: In Dark Mode, elevation is represented by a surface color overlay (lightening) rather than just shadows, adhering to Material 3 specs.*

## 8. Animation System
- **Transitions**: Fade-through (navigating between bottom bar tabs) and Shared Z-Axis (drilling down into a project).
- **Durations**:
  - Micro-interactions (Button press, checkbox): 100ms.
  - Simple transitions (Fade): 200ms.
  - Complex transitions (Shared element, bottom sheet): 300ms.
- **Easing**: Emphasized Decelerate (Fast out, slow in) for a snappy, responsive feel.

## 9. Component Library
- **Primary Button**: Solid Blueprint Blue, 12dp radius, used for main screen actions (e.g., "Save Project").
- **Outlined Button**: For secondary actions (e.g., "Cancel", "View All").
- **KPI Stat Card**: Large number at the top left, subtle background icon at the bottom right.
- **Empty State**: Centered illustration (or large faint icon), Title, Body text, and a Primary Button to create the first item.
- **Loading Skeleton**: Shimmering gray shapes matching the exact layout of the expected content.

## 10. Dashboard Design
- **Top**: Greeting ("Good Morning, Deyaar") and a summary of active projects.
- **KPI Row**: Horizontal scrolling or 2x2 grid of Stat Cards (Total Revenue, Active Projects, Pending Payments).
- **Recent Activity**: Vertical list of the most recent 5 updates (e.g., "Cement delivered to Site A").
- **Quick Actions**: FAB or prominent icon buttons for "New Project", "Add Expense".

## 11. Navigation Foundation
- **Phone**: Bottom Navigation Bar with 4 main destinations: Dashboard, Projects, Finance, More (Settings/Reports).
- **Tablet**: Navigation Rail on the left side to utilize wider screens efficiently.
- **Drill-down**: Top App Bar with a back arrow when viewing project details.

## 12. Empty States
- **No Projects**: Icon: `ic_construction`. "No Active Projects. Start building your portfolio." -> [Create Project]
- **No Expenses**: Icon: `ic_receipt_long`. "No expenses recorded." -> [Add Expense]

## 13. Loading & Errors
- **Loading**: Prefer Skeleton loading for dashboards. Use a circular progress indicator for small, localized actions (e.g., saving a form).
- **Errors**: Inline error text below TextFields for validation. Snackbars for transient errors ("Failed to save backup"). Full-screen error state with a "Retry" button for critical DB failures.

## 14. Forms
- **Fields**: OutlinedTextField with 12dp radius.
- **Keyboard**: Proper `KeyboardType` (Number for costs, Text for names). `ImeAction.Next` to smoothly move through forms.
- **Validation**: Real-time validation, disabling the 'Save' button until the form is valid.

## 15. Accessibility & Responsiveness
- **Touch Targets**: Minimum 48x48dp for all clickable elements.
- **Contrast**: Ensuring text passes WCAG AA standards against backgrounds.
- **Responsiveness**: `WindowSizeClass` used to switch from Bottom Nav to Nav Rail, and from single-column lists to grids on tablets.

## 16. Package Structure
```
ui/
 ├── theme/
 │    ├── Color.kt
 │    ├── Theme.kt
 │    ├── Type.kt
 │    ├── Shape.kt
 │    └── Dimens.kt
 ├── components/
 │    ├── buttons/
 │    ├── cards/
 │    ├── inputs/
 │    ├── feedback/ (Snackbar, dialogs)
 │    └── layout/ (Scaffolds, app bars)
 └── navigation/
```

## 17. Self Review
**Strengths**: Highly cohesive, prioritizes user efficiency, offline-friendly (no waiting for network animations).
**Weaknesses**: Customizing Material 3 heavily can sometimes lead to minor inconsistencies if developers don't use the wrapper components (e.g., using a raw `Button` instead of `DeyaarPrimaryButton`).
**Mitigation**: Strict code reviews and lint rules to enforce the usage of the `components/` package. The design system is highly maintainable because it relies strictly on Compose `CompositionLocal` and established Material 3 token patterns, avoiding hardcoded values.
