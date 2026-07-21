package com.example.ui.screens.client

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Client
import com.example.domain.usecase.client.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ClientListUiState(
    val clients: List<Client> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val sortType: ClientSortType = ClientSortType.NEWEST,
    val statusFilter: ClientStatusFilter = ClientStatusFilter.ALL,
    val categoryFilter: ClientCategoryFilter = ClientCategoryFilter.ALL,
    val favoritesOnly: Boolean = false
)

class ClientViewModel(
    private val getClientsUseCase: GetClientsUseCase,
    private val deleteClientUseCase: DeleteClientUseCase,
    private val saveClientUseCase: SaveClientUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClientListUiState(isLoading = true))
    val uiState: StateFlow<ClientListUiState> = _uiState.asStateFlow()

    init {
        loadClients()
    }

    private fun loadClients() {
        viewModelScope.launch {
            val state = _uiState.value
            getClientsUseCase(
                searchQuery = state.searchQuery,
                sortBy = state.sortType,
                filterByStatus = state.statusFilter,
                filterByCategory = state.categoryFilter,
                favoritesOnly = state.favoritesOnly
            ).catch { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }.collect { clients ->
                _uiState.update { it.copy(clients = clients, isLoading = false, error = null) }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        loadClients()
    }

    fun onSortTypeChanged(sortType: ClientSortType) {
        _uiState.update { it.copy(sortType = sortType) }
        loadClients()
    }

    fun onStatusFilterChanged(statusFilter: ClientStatusFilter) {
        _uiState.update { it.copy(statusFilter = statusFilter) }
        loadClients()
    }

    fun onCategoryFilterChanged(categoryFilter: ClientCategoryFilter) {
        _uiState.update { it.copy(categoryFilter = categoryFilter) }
        loadClients()
    }

    fun toggleFavoriteFilter() {
        _uiState.update { it.copy(favoritesOnly = !it.favoritesOnly) }
        loadClients()
    }
    
    fun toggleFavoriteStatus(client: Client) {
        viewModelScope.launch {
            saveClientUseCase(client.copy(isFavorite = !client.isFavorite))
        }
    }

    fun deleteClient(id: String) {
        viewModelScope.launch {
            deleteClientUseCase(id)
        }
    }
}

class ClientViewModelFactory(
    private val getClientsUseCase: GetClientsUseCase,
    private val deleteClientUseCase: DeleteClientUseCase,
    private val saveClientUseCase: SaveClientUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ClientViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ClientViewModel(getClientsUseCase, deleteClientUseCase, saveClientUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
