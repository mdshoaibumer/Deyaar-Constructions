package com.example.ui.components.layout

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.HomeRepairService
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.HomeRepairService
import androidx.compose.material.icons.outlined.MonetizationOn
import androidx.compose.material.icons.outlined.People
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.ui.navigation.Screen

private data class NavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val contentDescription: String,
)

private val navItems = listOf(
    NavItem(
        route = Screen.Dashboard.route,
        label = "Dashboard",
        selectedIcon = Icons.Filled.Dashboard,
        unselectedIcon = Icons.Outlined.Dashboard,
        contentDescription = "Dashboard tab"
    ),
    NavItem(
        route = Screen.Projects.route,
        label = "Projects",
        selectedIcon = Icons.Filled.HomeRepairService,
        unselectedIcon = Icons.Outlined.HomeRepairService,
        contentDescription = "Projects tab"
    ),
    NavItem(
        route = Screen.Clients.route,
        label = "Clients",
        selectedIcon = Icons.Filled.People,
        unselectedIcon = Icons.Outlined.People,
        contentDescription = "Clients tab"
    ),
    NavItem(
        route = Screen.Finance.route,
        label = "Finance",
        selectedIcon = Icons.Filled.MonetizationOn,
        unselectedIcon = Icons.Outlined.MonetizationOn,
        contentDescription = "Finance tab"
    ),
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
                DeyaarBottomNav(navController = navController, currentRoute = currentRoute)
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        content(innerPadding)
    }
}

@Composable
fun DeyaarBottomNav(navController: NavController, currentRoute: String?) {
    NavigationBar {
        navItems.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = { navigateToTopLevel(navController, item.route) },
                icon = {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.contentDescription
                    )
                },
                label = { Text(item.label) }
            )
        }
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
