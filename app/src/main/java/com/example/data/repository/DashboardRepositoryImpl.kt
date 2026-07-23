package com.example.data.repository

import com.example.data.local.dao.AttendanceDao
import com.example.data.local.dao.ClientDao
import com.example.data.local.dao.TransactionDao
import com.example.data.local.dao.ProjectDao
import com.example.data.mapper.toDomain
import com.example.domain.model.Project
import com.example.domain.repository.DashboardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar

class DashboardRepositoryImpl(
    private val clientDao: ClientDao,
    private val projectDao: ProjectDao,
    private val transactionDao: TransactionDao,
    private val attendanceDao: AttendanceDao
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

    override fun getCompletedProjectsCount(): Flow<Int> {
        return projectDao.getCompletedProjectsCount()
    }

    override fun getOnHoldProjectsCount(): Flow<Int> {
        return projectDao.getOnHoldProjectsCount()
    }

    override fun getTodaysLabourCount(): Flow<Int> {
        val todayStart = getTodayStartMillis()
        return attendanceDao.getPresentCountForDate(todayStart)
    }

    override fun getTotalExpenses(): Flow<Long> {
        return transactionDao.getGlobalTotalExpenses().map { it ?: 0L }
    }

    override fun getTotalContractValue(): Flow<Long> {
        return projectDao.getTotalContractValue()
    }

    override fun getTotalReceived(): Flow<Long> {
        return transactionDao.getGlobalTotalReceived().map { it ?: 0L }
    }

    override fun getRecentProjects(limit: Int): Flow<List<Project>> {
        return projectDao.getRecentProjects(limit).map { projects ->
            projects.map { it.toDomain() }
        }
    }

    override fun getUpcomingDeadlines(limit: Int): Flow<List<Project>> {
        return projectDao.getUpcomingDeadlines(System.currentTimeMillis(), limit).map { projects ->
            projects.map { it.toDomain() }
        }
    }

    override suspend fun getMonthlyExpenses(monthsBack: Int): List<Long> {
        val result = mutableListOf<Long>()
        val cal = Calendar.getInstance()
        // Start from current month going back
        for (i in monthsBack - 1 downTo 0) {
            val periodCal = Calendar.getInstance()
            periodCal.add(Calendar.MONTH, -i)
            periodCal.set(Calendar.DAY_OF_MONTH, 1)
            periodCal.set(Calendar.HOUR_OF_DAY, 0)
            periodCal.set(Calendar.MINUTE, 0)
            periodCal.set(Calendar.SECOND, 0)
            periodCal.set(Calendar.MILLISECOND, 0)
            val startDate = periodCal.timeInMillis

            periodCal.add(Calendar.MONTH, 1)
            val endDate = periodCal.timeInMillis

            val expenses = transactionDao.getExpensesForPeriod(startDate, endDate)
            result.add(expenses)
        }
        return result
    }

    override fun getRecentExpenseDescriptions(limit: Int): Flow<List<Pair<String, Long>>> {
        return transactionDao.getRecentExpenses(limit).map { entities ->
            entities.map { (it.description ?: it.category) to it.amountPaise }
        }
    }

    private fun getTodayStartMillis(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}
