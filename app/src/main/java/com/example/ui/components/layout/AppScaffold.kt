package com.example.ui.components.layout

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.HomeRepairService
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.People
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
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.ui.navigation.Screen

@Composable
fun AppScaffold(
    navController: NavController,
    windowSizeClass: WindowWidthSizeClass,
    content: @Composable (PaddingValues) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    val bottomNavRoutes = listOf(
        Screen.Dashboard.route,
        Screen.Projects.route,
        Screen.Clients.route,
        Screen.Finance.route
    )

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
        // Handle side rail for tablets here if windowSizeClass is Medium/Expanded
        content(innerPadding)
    }
}

@Composable
fun DeyaarBottomNav(navController: NavController, currentRoute: String?) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = com.example.ui.theme.Dimens.elevationLevel3
    ) {
        NavigationBarItem(
            selected = currentRoute == Screen.Dashboard.route,
            onClick = { navigateToTopLevel(navController, Screen.Dashboard.route) },
            icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
            label = { Text("Dashboard") }
        )
        NavigationBarItem(
            selected = currentRoute == Screen.Projects.route,
            onClick = { navigateToTopLevel(navController, Screen.Projects.route) },
            icon = { Icon(Icons.Default.HomeRepairService, contentDescription = "Projects") },
            label = { Text("Projects") }
        )
        NavigationBarItem(
            selected = currentRoute == Screen.Clients.route,
            onClick = { navigateToTopLevel(navController, Screen.Clients.route) },
            icon = { Icon(Icons.Default.People, contentDescription = "Clients") },
            label = { Text("Clients") }
        )
        NavigationBarItem(
            selected = currentRoute == Screen.Finance.route,
            onClick = { navigateToTopLevel(navController, Screen.Finance.route) },
            icon = { Icon(Icons.Default.MonetizationOn, contentDescription = "Finance") },
            label = { Text("Finance") }
        )
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
