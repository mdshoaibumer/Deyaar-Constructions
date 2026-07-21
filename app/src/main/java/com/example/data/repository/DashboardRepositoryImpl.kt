package com.example.data.repository

import com.example.data.local.dao.ClientDao
import com.example.data.local.dao.TransactionDao
import com.example.data.local.dao.ProjectDao
import com.example.data.mapper.toDomain
import com.example.domain.model.Project
import com.example.domain.repository.DashboardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DashboardRepositoryImpl(
    private val clientDao: ClientDao,
    private val projectDao: ProjectDao,
    private val transactionDao: TransactionDao
) : DashboardRepository {
    override fun getTotalClientsCount(): Flow<Int> {
        return clientDao.getClientsCount()
    }

    override fun getTotalProjectsCount(): Flow<Int> {
        return projectDao.getProjectsCountFlow()
    }

    override fun getActiveProjectsCount(): Flow<Int> {
        return projectDao.getActiveProjectsCount()
    }

    override fun getTotalExpenses(): Flow<Long> {
        return transactionDao.getGlobalTotalExpenses().map { it ?: 0L }
    }

    override fun getRecentProjects(limit: Int): Flow<List<Project>> {
        return projectDao.getRecentProjects(limit).map { projects ->
            projects.map { it.toDomain() }
        }
    }
}
