import re

with open('app/src/main/java/com/example/ui/navigation/AppNavigation.kt', 'r') as f:
    content = f.read()

navhost_start = """    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = Modifier.fillMaxSize(),
        enterTransition = { androidx.compose.animation.fadeIn(animationSpec = androidx.compose.animation.core.tween(300)) + androidx.compose.animation.slideInHorizontally(initialOffsetX = { it / 8 }) },
        exitTransition = { androidx.compose.animation.fadeOut(animationSpec = androidx.compose.animation.core.tween(300)) + androidx.compose.animation.slideOutHorizontally(targetOffsetX = { -it / 8 }) },
        popEnterTransition = { androidx.compose.animation.fadeIn(animationSpec = androidx.compose.animation.core.tween(300)) + androidx.compose.animation.slideInHorizontally(initialOffsetX = { -it / 8 }) },
        popExitTransition = { androidx.compose.animation.fadeOut(animationSpec = androidx.compose.animation.core.tween(300)) + androidx.compose.animation.slideOutHorizontally(targetOffsetX = { it / 8 }) }
    ) {"""

content = content.replace("""    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = Modifier.fillMaxSize()
    ) {""", navhost_start)

with open('app/src/main/java/com/example/ui/navigation/AppNavigation.kt', 'w') as f:
    f.write(content)

