package com.example.domain.usecase.search

import com.example.core.arch.SearchResult
import com.example.domain.model.*
import com.example.domain.repository.ClientRepository
import com.example.domain.repository.ProjectRepository
import com.example.domain.repository.ResourceRepository
import kotlinx.coroutines.flow.*

/**
 * A unified search result item displayed in the global search UI.
 */
data class GlobalSearchResult(
    override val id: String,
    override val title: String,
    override val subtitle: String?,
    override val type: String,
    val route: String // Navigation route to open this item
) : SearchResult

/**
 * Searches across all modules: Clients, Projects, Workers, Materials, Suppliers.
 */
class GlobalSearchUseCase(
    private val clientRepository: ClientRepository,
    private val projectRepository: ProjectRepository,
    private val resourceRepository: ResourceRepository
) {
    operator fun invoke(query: String): Flow<List<GlobalSearchResult>> {
        if (query.isBlank() || query.length < 2) return flowOf(emptyList())

        val normalizedQuery = query.trim().lowercase()

        val clientsFlow = clientRepository.getAllClients().map { clients ->
            clients.filter { matchesQuery(it.name, normalizedQuery) || matchesQuery(it.phone, normalizedQuery) || matchesQuery(it.companyName, normalizedQuery) }
                .map { client ->
                    GlobalSearchResult(
                        id = client.id,
                        title = client.name,
                        subtitle = client.phone + (client.companyName?.let { " • $it" } ?: ""),
                        type = "Client",
                        route = "client_details/${client.id}"
                    )
                }
        }

        val projectsFlow = projectRepository.getAllProjects().map { projects ->
            projects.filter { matchesQuery(it.name, normalizedQuery) || matchesQuery(it.projectNumber, normalizedQuery) || matchesQuery(it.location, normalizedQuery) || matchesQuery(it.address, normalizedQuery) }
                .map { project ->
                    GlobalSearchResult(
                        id = project.id,
                        title = project.name,
                        subtitle = "${project.status.displayName} • ${project.projectNumber}",
                        type = "Project",
                        route = "project_details/${project.id}"
                    )
                }
        }

        val workersFlow = resourceRepository.getAllWorkers().map { workers ->
            workers.filter { matchesQuery(it.fullName, normalizedQuery) || matchesQuery(it.mobileNumber, normalizedQuery) || matchesQuery(it.trade, normalizedQuery) }
                .map { worker ->
                    GlobalSearchResult(
                        id = worker.id,
                        title = worker.fullName,
                        subtitle = "${worker.trade} • ${worker.mobileNumber}",
                        type = "Worker",
                        route = "worker_edit/${worker.id}"
                    )
                }
        }

        val materialsFlow = resourceRepository.getAllMaterials().map { materials ->
            materials.filter { matchesQuery(it.name, normalizedQuery) || matchesQuery(it.category, normalizedQuery) }
                .map { material ->
                    GlobalSearchResult(
                        id = material.id,
                        title = material.name,
                        subtitle = "${material.category} • Stock: ${material.currentStock} ${material.unit}",
                        type = "Material",
                        route = "material_edit/${material.id}"
                    )
                }
        }

        val suppliersFlow = resourceRepository.getAllSuppliers().map { suppliers ->
            suppliers.filter { matchesQuery(it.name, normalizedQuery) || matchesQuery(it.phone, normalizedQuery) }
                .map { supplier ->
                    GlobalSearchResult(
                        id = supplier.id,
                        title = supplier.name,
                        subtitle = "Supplier • ${supplier.phone}",
                        type = "Supplier",
                        route = "supplier_edit/${supplier.id}"
                    )
                }
        }

        return combine(clientsFlow, projectsFlow, workersFlow, materialsFlow, suppliersFlow) { clients, projects, workers, materials, suppliers ->
            (clients + projects + workers + materials + suppliers)
                .take(50) // Cap results for performance
        }
    }

    private fun matchesQuery(value: String?, query: String): Boolean {
        return value?.lowercase()?.contains(query) == true
    }
}
