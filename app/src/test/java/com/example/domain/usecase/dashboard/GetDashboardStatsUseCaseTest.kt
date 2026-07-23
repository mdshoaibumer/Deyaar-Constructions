package com.example.domain.usecase.dashboard

import com.example.domain.model.Project
import com.example.domain.model.ProjectCategory
import com.example.domain.model.ProjectPriority
import com.example.domain.model.ProjectStatus
import com.example.domain.repository.DashboardRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetDashboardStatsUseCaseTest {

    private fun createMockRepo(
        clients: Int = 5,
        projects: Int = 10,
        active: Int = 6,
        completed: Int = 3,
        onHold: Int = 1,
        labour: Int = 12,
        expenses: Long = 500000L,
        contractValue: Long = 2000000L,
        received: Long = 1200000L
    ): DashboardRepository = object : DashboardRepository {
        override fun getTotalClientsCount() = flowOf(clients)
        override fun getTotalProjectsCount() = flowOf(projects)
        override fun getActiveProjectsCount() = flowOf(active)
        override fun getCompletedProjectsCount() = flowOf(completed)
        override fun getOnHoldProjectsCount() = flowOf(onHold)
        override fun getTodaysLabourCount() = flowOf(labour)
        override fun getTotalExpenses() = flowOf(expenses)
        override fun getTotalContractValue() = flowOf(contractValue)
        override fun getTotalReceived() = flowOf(received)
        override fun getRecentProjects(limit: Int) = flowOf(emptyList<Project>())
        override fun getUpcomingDeadlines(limit: Int) = flowOf(emptyList<Project>())
        override suspend fun getMonthlyExpenses(monthsBack: Int) = List(monthsBack) { 100000L }
        override fun getRecentExpenseDescriptions(limit: Int) = flowOf(emptyList<Pair<String, Long>>())
    }

    @Test
    fun `stats calculated correctly from repository`() = runTest {
        val useCase = GetDashboardStatsUseCase(createMockRepo())
        val stats = useCase().first()

        assertEquals(5, stats.totalClients)
        assertEquals(10, stats.totalProjects)
        assertEquals(6, stats.activeProjects)
        assertEquals(3, stats.completedProjects)
        assertEquals(1, stats.projectsOnHold)
        assertEquals(12, stats.todaysLabourCount)
        assertEquals(500000L, stats.totalExpensesPaise)
        assertEquals(2000000L, stats.totalContractValuePaise)
        assertEquals(1200000L, stats.receivedAmountPaise)
    }

    @Test
    fun `pending payments calculated as contract minus received`() = runTest {
        val useCase = GetDashboardStatsUseCase(createMockRepo(contractValue = 5000000L, received = 3000000L))
        val stats = useCase().first()

        assertEquals(2000000L, stats.pendingPaymentsPaise)
    }

    @Test
    fun `pending payments never goes negative`() = runTest {
        val useCase = GetDashboardStatsUseCase(createMockRepo(contractValue = 1000000L, received = 2000000L))
        val stats = useCase().first()

        assertEquals(0L, stats.pendingPaymentsPaise)
    }

    @Test
    fun `net profit is received minus expenses`() = runTest {
        val useCase = GetDashboardStatsUseCase(createMockRepo(expenses = 300000L, received = 500000L))
        val stats = useCase().first()

        assertEquals(200000L, stats.netProfitPaise)
    }

    @Test
    fun `net profit can be negative`() = runTest {
        val useCase = GetDashboardStatsUseCase(createMockRepo(expenses = 800000L, received = 500000L))
        val stats = useCase().first()

        assertEquals(-300000L, stats.netProfitPaise)
    }

    @Test
    fun `monthly expenses are populated`() = runTest {
        val useCase = GetDashboardStatsUseCase(createMockRepo())
        val stats = useCase().first()

        assertEquals(6, stats.monthlyExpensesPaise.size)
    }

    @Test
    fun `empty state returns zeroes`() = runTest {
        val useCase = GetDashboardStatsUseCase(createMockRepo(
            clients = 0, projects = 0, active = 0, completed = 0, onHold = 0,
            labour = 0, expenses = 0L, contractValue = 0L, received = 0L
        ))
        val stats = useCase().first()

        assertEquals(0, stats.totalClients)
        assertEquals(0, stats.totalProjects)
        assertEquals(0L, stats.pendingPaymentsPaise)
        assertEquals(0L, stats.netProfitPaise)
    }
}
