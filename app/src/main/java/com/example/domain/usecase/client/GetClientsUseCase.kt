package com.example.domain.usecase.client

import com.example.domain.model.Client
import com.example.domain.repository.ClientRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetClientsUseCase(
    private val clientRepository: ClientRepository
) {
    operator fun invoke(
        searchQuery: String = "",
        sortBy: ClientSortType = ClientSortType.NEWEST,
        filterByStatus: ClientStatusFilter = ClientStatusFilter.ALL,
        filterByCategory: ClientCategoryFilter = ClientCategoryFilter.ALL,
        favoritesOnly: Boolean = false
    ): Flow<List<Client>> {
        return clientRepository.getAllClients().map { clients ->
            var filtered = clients

            // Search
            if (searchQuery.isNotBlank()) {
                val query = searchQuery.lowercase()
                filtered = filtered.filter {
                    it.name.lowercase().contains(query) ||
                    (it.companyName?.lowercase()?.contains(query) == true) ||
                    it.phone.contains(query) ||
                    (it.city?.lowercase()?.contains(query) == true)
                }
            }

            // Filter by Status
            filtered = when (filterByStatus) {
                ClientStatusFilter.ALL -> filtered
                ClientStatusFilter.ACTIVE -> filtered.filter { it.isActive }
                ClientStatusFilter.INACTIVE -> filtered.filter { !it.isActive }
            }

            // Filter by Category
            filtered = when (filterByCategory) {
                ClientCategoryFilter.ALL -> filtered
                ClientCategoryFilter.RESIDENTIAL -> filtered.filter { it.category.name == "RESIDENTIAL" }
                ClientCategoryFilter.COMMERCIAL -> filtered.filter { it.category.name == "COMMERCIAL" }
                ClientCategoryFilter.INDUSTRIAL -> filtered.filter { it.category.name == "INDUSTRIAL" }
                ClientCategoryFilter.GOVERNMENT -> filtered.filter { it.category.name == "GOVERNMENT" }
            }

            // Filter Favorites
            if (favoritesOnly) {
                filtered = filtered.filter { it.isFavorite }
            }

            // Sort
            filtered = when (sortBy) {
                ClientSortType.NEWEST -> filtered.sortedByDescending { it.createdAt }
                ClientSortType.OLDEST -> filtered.sortedBy { it.createdAt }
                ClientSortType.ALPHABETICAL -> filtered.sortedBy { it.name.lowercase() }
                ClientSortType.RECENTLY_UPDATED -> filtered.sortedByDescending { it.updatedAt }
            }

            filtered
        }
    }
}

enum class ClientSortType {
    NEWEST,
    OLDEST,
    ALPHABETICAL,
    RECENTLY_UPDATED
}

enum class ClientStatusFilter {
    ALL,
    ACTIVE,
    INACTIVE
}

enum class ClientCategoryFilter {
    ALL,
    RESIDENTIAL,
    COMMERCIAL,
    INDUSTRIAL,
    GOVERNMENT
}
