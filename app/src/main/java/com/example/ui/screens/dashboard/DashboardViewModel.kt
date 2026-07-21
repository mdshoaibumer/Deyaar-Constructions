package com.example.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.domain.usecase.dashboard.DashboardStats
import com.example.domain.usecase.dashboard.GetDashboardStatsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

sealed interface DashboardUiState {
    data object Loading : DashboardUiState
    data object Empty : DashboardUiState
    data class Success(val stats: DashboardStats) : DashboardUiState
}

class DashboardViewModel(
    private val getDashboardStatsUseCase: GetDashboardStatsUseCase
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> = getDashboardStatsUseCase()
        .map { stats ->
            if (stats.totalProjects == 0 && stats.totalClients == 0) {
                DashboardUiState.Empty
            } else {
                DashboardUiState.Success(stats)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DashboardUiState.Loading
        )

    class Factory(
        private val getDashboardStatsUseCase: GetDashboardStatsUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DashboardViewModel(getDashboardStatsUseCase) as T
        }
    }
}
