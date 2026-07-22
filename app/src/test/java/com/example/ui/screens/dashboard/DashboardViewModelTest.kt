package com.example.ui.screens.dashboard

import com.example.domain.model.Project
import com.example.domain.repository.DashboardRepository
import com.example.domain.usecase.dashboard.GetDashboardStatsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when repository is empty, state is Empty`() = runTest(testDispatcher) {
        val mockRepo = object : DashboardRepository {
            override fun getTotalClientsCount() = flowOf(0)
            override fun getTotalProjectsCount() = flowOf(0)
            override fun getActiveProjectsCount() = flowOf(0)
            override fun getTotalExpenses() = flowOf(0L)
            override fun getRecentProjects(limit: Int) = flowOf(emptyList<Project>())
        }

        val useCase = GetDashboardStatsUseCase(mockRepo)
        val viewModel = DashboardViewModel(useCase)

        // Collect state to trigger WhileSubscribed
        val collectJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }

        testScheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value is DashboardUiState.Empty)
        collectJob.cancel()
    }

    @Test
    fun `when repository has data, state is Success`() = runTest(testDispatcher) {
        val mockRepo = object : DashboardRepository {
            override fun getTotalClientsCount() = flowOf(5)
            override fun getTotalProjectsCount() = flowOf(3)
            override fun getActiveProjectsCount() = flowOf(2)
            override fun getTotalExpenses() = flowOf(150000L)
            override fun getRecentProjects(limit: Int) = flowOf(emptyList<Project>())
        }

        val useCase = GetDashboardStatsUseCase(mockRepo)
        val viewModel = DashboardViewModel(useCase)

        // Collect state to trigger WhileSubscribed
        val collectJob = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }

        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is DashboardUiState.Success)
        
        val successState = state as DashboardUiState.Success
        assertEquals(5, successState.stats.totalClients)
        assertEquals(3, successState.stats.totalProjects)
        assertEquals(150000L, successState.stats.totalExpensesPaise)
        
        collectJob.cancel()
    }
}
