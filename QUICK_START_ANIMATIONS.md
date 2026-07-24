# 🎬 Quick Start Guide - Premium Animations & UI/UX

## How to Use the New Premium Components

### 1. Premium Floating Action Button

```kotlin
// Instead of regular FloatingActionButton
FloatingActionButton(onClick = {}) {
    Icon(Icons.Default.Add, contentDescription = "Add")
}

// Use PremiumFloatingActionButton
PremiumFloatingActionButton(
    onClick = { navigateToAdd() },
    icon = Icons.Default.Add,
    contentDescription = "Add new item"
)
```

**Features**:
- Smooth scale animation (0.92f) on press
- Spring physics for natural feel
- Automatically handles interaction tracking

---

### 2. Premium Text Field

```kotlin
// Instead of OutlinedTextField
OutlinedTextField(
    value = search,
    onValueChange = { search = it }
)

// Use PremiumTextField
PremiumTextField(
    value = search,
    onValueChange = { search = it },
    label = "Search...",
    placeholder = "Type to search",
    leadingIcon = Icons.Default.Search
)
```

**Features**:
- Smooth focus animations
- Color transitions (300ms)
- Automatic clear button
- Material Design 3 styling

---

### 3. Premium Button

```kotlin
// Use PremiumButton for filled buttons
PremiumButton(
    onClick = { /* action */ },
    label = "Save Changes",
    icon = Icons.Default.Save
)

// Use PremiumOutlinedButton for outlined
PremiumOutlinedButton(
    onClick = { /* action */ },
    label = "Cancel",
    icon = Icons.Default.Close
)
```

**Features**:
- Press animation (0.96x scale)
- Optional icon support
- Spring physics feedback

---

### 4. Transition Animations

```kotlin
// In AnimatedVisibility
AnimatedVisibility(
    visible = showContent,
    enter = fadeScaleEnter(),
    exit = fadeScaleExit()
) {
    MyContent()
}

// For bottom sheet
AnimatedVisibility(
    visible = showSheet,
    enter = slideInFromBottom(),
    exit = slideOutToBottom()
) {
    BottomSheet()
}

// For navigation
AnimatedVisibility(
    visible = currentScreen == Screen.Details,
    enter = slideInFromStart(),
    exit = slideOutToStart()
) {
    DetailsScreen()
}
```

**Available Transitions**:
- `fadeScaleEnter()` / `fadeScaleExit()` - Center appear/disappear
- `slideInFromBottom()` / `slideOutToBottom()` - Bottom sheet style
- `slideInFromStart()` / `slideOutToStart()` - Navigation drawer style

---

### 5. Spacing Utilities

```kotlin
// Use semantic spacing
Column(
    verticalArrangement = Arrangement.spacedBy(Dimens.spaceLarge)
) {
    Text("Title")
    SpaceMedium()
    Text("Subtitle")
    SpaceSmall()
    PremiumDivider()
    SpaceLarge()
    Button(onClick = {}) {
        Text("Action")
    }
}
```

**Available Spacers**:
- `SpaceXSmall()` - 4dp
- `SpaceSmall()` - 8dp
- `SpaceMedium()` - 16dp
- `SpaceLarge()` - 24dp
- `SpaceXLarge()` - 32dp
- `PremiumDivider()` - Styled separator

---

### 6. Dark Mode Support

```kotlin
// Get theme-aware colors
val bgColor = getPremiumSurfaceColor(elevation = 1)
val textColor = getPremiumTextColor()
val secondaryText = getPremiumSecondaryTextColor()
val dividerColor = getPremiumDividerColor()

// Use in your UI
Surface(
    color = bgColor,
    shape = RoundedCornerShape(8.dp)
) {
    Text(
        "Title",
        color = textColor
    )
    PremiumDivider()
    Text(
        "Subtitle",
        color = secondaryText
    )
}
```

**Features**:
- Automatic theme detection
- Smooth transitions (300ms)
- Elevation-aware styling
- WCAG AA compliance

---

### 7. Accessibility Support

```kotlin
// Validate color accessibility
val isAccessible = validateColorAccessibility(
    foreground = textColor,
    background = bgColor,
    isLargeText = false
)

// Get accessible text color
val safTextColor = getAccessibleTextColor(
    backgroundColor = surface,
    preferredColor = primary
)

// Add semantics
Button(
    onClick = {},
    modifier = Modifier.semantics(mergeDescendants = true) {
        contentDescription = "Save changes to profile"
        role = Role.Button
    }
) {
    Text("Save")
}
```

**Features**:
- WCAG AA contrast validation
- Automatic color adjustments
- Semantic descriptions
- Screen reader support

---

## 🎨 Animation Timing Reference

```kotlin
// Quick feedback (button press)
animateFloatAsState(
    targetValue = 0.96f,
    animationSpec = spring(stiffness = 600f, damping = 10f)
)

// Smooth color transition (300ms)
animateColorAsState(
    targetValue = newColor,
    animationSpec = tween(300, easing = FastOutSlowInEasing)
)

// Scale on interaction (filter chip)
animateFloatAsState(
    targetValue = if (isSelected) 1.05f else 1f,
    animationSpec = spring(stiffness = 400f, damping = 12f)
)

// Screen transition (350ms)
slideInHorizontally(
    initialOffsetX = { -it },
    animationSpec = tween(350, easing = FastOutSlowInEasing)
)
```

---

## 🚀 Implementation Checklist

When adding animations to a new screen:

- [ ] Import animation classes from `androidx.compose.animation.*`
- [ ] Import `MutableInteractionSource` for interaction tracking
- [ ] Use `spring()` for tactile feedback (press, scale)
- [ ] Use `tween()` with `FastOutSlowInEasing` for color transitions
- [ ] Set animation duration to 300ms for color, 250-300ms for UI transitions
- [ ] Use `scale()` modifier for press feedback (0.92f - 0.98f)
- [ ] Add proper semantics for accessibility
- [ ] Test dark mode with `DarkModeSupport` utilities
- [ ] Validate color accessibility with `AccessibilityUtils`
- [ ] Test on low-end device (Pixel 3a) for 60 FPS

---

## 📊 Performance Tips

1. **Use Spring Physics for Interactions**
   - Feels more natural
   - Auto-cancels previous animation
   - CPU efficient

2. **Keep Color Transitions to 300ms**
   - Fast enough for snappy UI
   - Smooth without feeling slow

3. **Batch Related Animations**
   - Color + scale together
   - Entrance + shadow together

4. **Use `animateItem()` for Lists**
   - Built-in for LazyColumn/LazyRow
   - Automatic staggering support

5. **Avoid Animating Many Properties**
   - Focus on 1-2 properties per element
   - Compose handles the rest

---

## 🎯 Common Patterns

### Button Press Feedback
```kotlin
var isPressed by remember { mutableStateOf(false) }
val scale by animateFloatAsState(
    targetValue = if (isPressed) 0.96f else 1f,
    animationSpec = spring(stiffness = 600f, damping = 10f)
)

Button(
    onClick = {},
    modifier = Modifier.scale(scale)
)
```

### Filter Selection
```kotlin
val scale by animateFloatAsState(
    targetValue = if (isSelected) 1.05f else 1f,
    animationSpec = spring(stiffness = 400f, damping = 12f)
)
val bgColor by animateColorAsState(
    targetValue = if (isSelected) primaryContainer else surface,
    animationSpec = tween(300)
)

Surface(
    color = bgColor,
    modifier = Modifier.scale(scale)
) { }
```

### List Item Animation
```kotlin
LazyColumn {
    items(items, key = { it.id }) { item ->
        ItemCard(
            item = item,
            modifier = Modifier.animateItem()
        )
    }
}
```

---

## ✅ Testing Checklist

- [ ] Animations run at 60 FPS on Pixel 6
- [ ] Animations run at 60 FPS on Pixel 3a (low-end)
- [ ] Dark mode transitions are smooth
- [ ] Press feedback feels natural
- [ ] No animation jank during list scrolling
- [ ] Color contrast is WCAG AA compliant
- [ ] Screen reader announces all elements
- [ ] Touch targets are 48dp minimum
- [ ] Keyboard navigation works
- [ ] Reduced motion settings respected (future enhancement)

---

## 📚 References

- **Material Design 3**: https://m3.material.io/
- **Compose Animation**: https://developer.android.com/jetpack/compose/animation
- **Accessibility Guidelines**: https://www.w3.org/WAI/WCAG21/quickref/

---

**Need Help?**
Refer to the main animation files for examples:
- `TransitionAnimations.kt` - Standard transitions
- `DashboardScreen.kt` - Implementation example
- `PremiumButton.kt` - Component example
