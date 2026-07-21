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
            repository.getTotalExpenses(),
            repository.getRecentProjects(5)
        ) { clients, totalProjects, activeProjects, expenses, recentProjects ->
            DashboardStats(
                totalClients = clients,
                totalProjects = totalProjects,
                activeProjects = activeProjects,
                completedProjects = 0, // Mocked until feature implemented
                projectsOnHold = 0,    // Mocked until feature implemented
                todaysLabourCount = 0, // Mocked until feature implemented
                totalExpensesPaise = expenses,
                pendingPaymentsPaise = 0L, // Mocked until feature implemented
                totalContractValuePaise = 0L, // Mocked until feature implemented
                receivedAmountPaise = 0L,     // Mocked until feature implemented
                netProfitPaise = 0L - expenses, // Mocked
                recentProjects = recentProjects
            )
        }
    }
}
