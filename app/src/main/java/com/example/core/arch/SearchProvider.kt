package com.example.core.arch

import kotlinx.coroutines.flow.Flow

/**
 * Global Search Framework Architecture.
 * Future feature modules (Clients, Projects, Expenses) will implement this interface.
 * A central SearchRegistry will collect all providers and query them when the user searches from the Dashboard.
 */
interface SearchProvider<T : SearchResult> {
    
    /**
     * Unique identifier for this provider (e.g., "clients", "projects")
     */
    val providerId: String

    /**
     * Executes a search query across the module's local database.
     * @param query The user's search input.
     * @return A Flow of search results that updates as the database changes.
     */
    fun search(query: String): Flow<List<T>>
}

interface SearchResult {
    val id: String
    val title: String
    val subtitle: String?
    val type: String
}
