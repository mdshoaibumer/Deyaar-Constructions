package com.example.domain.repository

import com.example.domain.model.Project
import kotlinx.coroutines.flow.Flow

interface DashboardRepository {
    fun getTotalClientsCount(): Flow<Int>
    fun getTotalProjectsCount(): Flow<Int>
    fun getActiveProjectsCount(): Flow<Int>
    fun getTotalExpenses(): Flow<Long>
    fun getRecentProjects(limit: Int): Flow<List<Project>>
}
