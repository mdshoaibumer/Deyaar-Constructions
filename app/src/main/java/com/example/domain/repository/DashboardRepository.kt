package com.example.domain.repository

import com.example.domain.model.Project
import kotlinx.coroutines.flow.Flow

interface DashboardRepository {
    fun getTotalClientsCount(): Flow<Int>
    fun getTotalProjectsCount(): Flow<Int>
    fun getActiveProjectsCount(): Flow<Int>
    fun getCompletedProjectsCount(): Flow<Int>
    fun getOnHoldProjectsCount(): Flow<Int>
    fun getTodaysLabourCount(): Flow<Int>
    fun getTotalExpenses(): Flow<Long>
    fun getTotalContractValue(): Flow<Long>
    fun getTotalReceived(): Flow<Long>
    fun getRecentProjects(limit: Int): Flow<List<Project>>
    fun getUpcomingDeadlines(limit: Int): Flow<List<Project>>
    suspend fun getMonthlyExpenses(monthsBack: Int): List<Long>
    fun getRecentExpenseDescriptions(limit: Int): Flow<List<Pair<String, Long>>>
}
