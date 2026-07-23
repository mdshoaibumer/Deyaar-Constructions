package com.example.ui.screens.dashboard

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.domain.usecase.dashboard.DashboardStats
import com.example.ui.theme.MyApplicationTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DashboardScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun dashboardContent_displaysGreeting() {
        composeTestRule.setContent {
            MyApplicationTheme {
                DashboardContent(
                    stats = DashboardStats(totalProjects = 5, activeProjects = 3, completedProjects = 2, pendingPaymentsPaise = 500000L),
                    onNavigateToProjects = {},
                    onNavigateToClients = {},
                    onNavigateToResources = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Alex", substring = true).assertIsDisplayed()
    }

    @Test
    fun dashboardContent_showsTotalProjects() {
        composeTestRule.setContent {
            MyApplicationTheme {
                DashboardContent(
                    stats = DashboardStats(totalProjects = 12, activeProjects = 8, completedProjects = 4),
                    onNavigateToProjects = {},
                    onNavigateToClients = {},
                    onNavigateToResources = {}
                )
            }
        }

        composeTestRule.onNodeWithText("12").assertIsDisplayed()
        composeTestRule.onNodeWithText("Total Projects").assertIsDisplayed()
    }

    @Test
    fun dashboardContent_showsActiveProjects() {
        composeTestRule.setContent {
            MyApplicationTheme {
                DashboardContent(
                    stats = DashboardStats(totalProjects = 10, activeProjects = 7),
                    onNavigateToProjects = {},
                    onNavigateToClients = {},
                    onNavigateToResources = {}
                )
            }
        }

        composeTestRule.onNodeWithText("7").assertIsDisplayed()
        composeTestRule.onNodeWithText("Active").assertIsDisplayed()
    }

    @Test
    fun dashboardContent_showsCompletedProjects() {
        composeTestRule.setContent {
            MyApplicationTheme {
                DashboardContent(
                    stats = DashboardStats(totalProjects = 10, completedProjects = 3),
                    onNavigateToProjects = {},
                    onNavigateToClients = {},
                    onNavigateToResources = {}
                )
            }
        }

        composeTestRule.onNodeWithText("3").assertIsDisplayed()
        composeTestRule.onNodeWithText("Completed").assertIsDisplayed()
    }

    @Test
    fun dashboardContent_showsQuickActions() {
        composeTestRule.setContent {
            MyApplicationTheme {
                DashboardContent(
                    stats = DashboardStats(),
                    onNavigateToProjects = {},
                    onNavigateToClients = {},
                    onNavigateToResources = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Quick Actions").assertIsDisplayed()
        composeTestRule.onNodeWithText("Project").assertIsDisplayed()
        composeTestRule.onNodeWithText("Client").assertIsDisplayed()
    }

    @Test
    fun dashboardContent_showsMonthlyExpensesChart() {
        composeTestRule.setContent {
            MyApplicationTheme {
                DashboardContent(
                    stats = DashboardStats(monthlyExpensesPaise = listOf(100000L, 200000L, 150000L, 180000L, 220000L, 190000L)),
                    onNavigateToProjects = {},
                    onNavigateToClients = {},
                    onNavigateToResources = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Monthly Expenses", substring = true).assertIsDisplayed()
    }

    @Test
    fun dashboardContent_emptyChart_showsEmptyMessage() {
        composeTestRule.setContent {
            MyApplicationTheme {
                DashboardContent(
                    stats = DashboardStats(monthlyExpensesPaise = emptyList()),
                    onNavigateToProjects = {},
                    onNavigateToClients = {},
                    onNavigateToResources = {}
                )
            }
        }

        composeTestRule.onNodeWithText("No expense data available yet").assertIsDisplayed()
    }

    @Test
    fun dashboardContent_searchIconIsDisplayed() {
        composeTestRule.setContent {
            MyApplicationTheme {
                DashboardContent(
                    stats = DashboardStats(),
                    onNavigateToProjects = {},
                    onNavigateToClients = {},
                    onNavigateToResources = {}
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Search projects, clients, and workers").assertIsDisplayed()
    }

    @Test
    fun dashboardContent_settingsIconIsDisplayed() {
        composeTestRule.setContent {
            MyApplicationTheme {
                DashboardContent(
                    stats = DashboardStats(),
                    onNavigateToProjects = {},
                    onNavigateToClients = {},
                    onNavigateToResources = {}
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Open settings").assertIsDisplayed()
    }

    @Test
    fun dashboardContent_accessibilitySemantics_bentoCards() {
        composeTestRule.setContent {
            MyApplicationTheme {
                DashboardContent(
                    stats = DashboardStats(totalProjects = 5, activeProjects = 3),
                    onNavigateToProjects = {},
                    onNavigateToClients = {},
                    onNavigateToResources = {}
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Total Projects: 5").assertExists()
        composeTestRule.onNodeWithContentDescription("Active: 3").assertExists()
    }
}
