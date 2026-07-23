package com.example.domain.usecase.dashboard

import com.example.domain.model.Project
import com.example.domain.repository.DashboardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow

/**
 * Dashboard statistics. All monetary values are in paise (1 INR = 100 paise).
 */
data class DashboardStats(
    val totalClients: Int = 0,
    val totalProjects: Int = 0,
    val activeProjects: Int = 0,
    val completedProjects: Int = 0,
    val projectsOnHold: Int = 0,
    val todaysLabourCount: Int = 0,
    val totalExpensesPaise: Long = 0L,
    val pendingPaymentsPaise: Long = 0L,
    val totalContractValuePaise: Long = 0L,
    val receivedAmountPaise: Long = 0L,
    val netProfitPaise: Long = 0L,
    val recentProjects: List<Project> = emptyList(),
    val upcomingDeadlines: List<Project> = emptyList(),
    val monthlyExpensesPaise: List<Long> = emptyList(),
    val recentExpenses: List<Pair<String, Long>> = emptyList()
)

class GetDashboardStatsUseCase(
    private val repository: DashboardRepository
) {
    operator fun invoke(): Flow<DashboardStats> {
        return combine(
            repository.getTotalClientsCount(),
            repository.getTotalProjectsCount(),
            repository.getActiveProjectsCount(),
            repository.getCompletedProjectsCount(),
            repository.getOnHoldProjectsCount()
        ) { clients, totalProjects, active, completed, onHold ->
            DashboardPartial1(clients, totalProjects, active, completed, onHold)
        }.let { partial1Flow ->
            val partial2Flow = combine(
                repository.getTodaysLabourCount(),
                repository.getTotalExpenses(),
                repository.getTotalContractValue()
            ) { labour, expenses, contractValue ->
                Triple(labour, expenses, contractValue)
            }
            combine(
                partial1Flow,
                partial2Flow,
                repository.getTotalReceived(),
                repository.getRecentProjects(5)
            ) { p1, p2, received, recentProjects ->
                val (labour, expenses, contractValue) = p2
                val pendingPayments = maxOf(0L, contractValue - received)
                val netProfit = received - expenses
                DashboardStats(
                    totalClients = p1.clients,
                    totalProjects = p1.totalProjects,
                    activeProjects = p1.active,
                    completedProjects = p1.completed,
                    projectsOnHold = p1.onHold,
                    todaysLabourCount = labour,
                    totalExpensesPaise = expenses,
                    pendingPaymentsPaise = pendingPayments,
                    totalContractValuePaise = contractValue,
                    receivedAmountPaise = received,
                    netProfitPaise = netProfit,
                    recentProjects = recentProjects
                )
            }
        }.let { baseFlow ->
            combine(
                baseFlow,
                repository.getUpcomingDeadlines(5),
                repository.getRecentExpenseDescriptions(5),
                getMonthlyExpensesFlow()
            ) { stats, deadlines, recentExpenses, monthlyExpenses ->
                stats.copy(
                    upcomingDeadlines = deadlines,
                    recentExpenses = recentExpenses,
                    monthlyExpensesPaise = monthlyExpenses
                )
            }
        }
    }

    private fun getMonthlyExpensesFlow(): Flow<List<Long>> = flow {
        emit(repository.getMonthlyExpenses(6))
    }
}

private data class DashboardPartial1(
    val clients: Int,
    val totalProjects: Int,
    val active: Int,
    val completed: Int,
    val onHold: Int
)
