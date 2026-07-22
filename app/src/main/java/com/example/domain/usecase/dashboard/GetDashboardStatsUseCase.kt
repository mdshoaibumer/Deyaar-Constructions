package com.example.domain.usecase.dashboard

import com.example.domain.model.Project
import com.example.domain.repository.DashboardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * Dashboard statistics. All monetary values are in paise (1 INR = 100 paise).
 */
data class DashboardStats(
    val totalClients: Int,
    val totalProjects: Int,
    val activeProjects: Int,
    val completedProjects: Int,
    val projectsOnHold: Int,
    val todaysLabourCount: Int,
    val totalExpensesPaise: Long,
    val pendingPaymentsPaise: Long,
    val totalContractValuePaise: Long,
    val receivedAmountPaise: Long,
    val netProfitPaise: Long,
    val recentProjects: List<Project>
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
            // Second partial: group labour + expenses + contractValue
            val partial2Flow = combine(
                repository.getTodaysLabourCount(),
                repository.getTotalExpenses(),
                repository.getTotalContractValue()
            ) { labour, expenses, contractValue ->
                Triple(labour, expenses, contractValue)
            }
            // Final combine: partial1 + partial2 + received + recentProjects
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
        }
    }
}

private data class DashboardPartial1(
    val clients: Int,
    val totalProjects: Int,
    val active: Int,
    val completed: Int,
    val onHold: Int
)
