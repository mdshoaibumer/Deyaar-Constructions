package com.example.ui.components.layout

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.HomeRepairService
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.HomeRepairService
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.ui.components.DeyaarBottomNavigation
import com.example.ui.components.NavigationItem
import com.example.ui.navigation.Screen

private val navItems = listOf(
    NavigationItem(
        route = Screen.Dashboard.route,
        label = "Dashboard",
        selectedIcon = Icons.Filled.Dashboard,
        unselectedIcon = Icons.Outlined.Dashboard
    ),
    NavigationItem(
        route = Screen.Projects.route,
        label = "Projects",
        selectedIcon = Icons.Filled.HomeRepairService,
        unselectedIcon = Icons.Outlined.HomeRepairService
    ),
    NavigationItem(
        route = Screen.Clients.route,
        label = "Clients",
        selectedIcon = Icons.Filled.People,
        unselectedIcon = Icons.Outlined.People
    ),
    NavigationItem(
        route = Screen.Settings.route,
        label = "Settings",
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings
    )
)

@Composable
fun AppScaffold(
    navController: NavController,
    windowSizeClass: WindowWidthSizeClass,
    content: @Composable (PaddingValues) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    val bottomNavRoutes = navItems.map { it.route }
    val showBottomBar = currentRoute in bottomNavRoutes && windowSizeClass == WindowWidthSizeClass.Compact

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (showBottomBar) {
                DeyaarBottomNavigation(
                    items = navItems,
                    currentRoute = currentRoute,
                    onNavigate = { route -> navigateToTopLevel(navController, route) }
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        content(innerPadding)
    }
}

private fun navigateToTopLevel(navController: NavController, route: String) {
    navController.navigate(route) {
        popUpTo(Screen.Dashboard.route) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
