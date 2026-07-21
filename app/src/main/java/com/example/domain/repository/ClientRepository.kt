package com.example.domain.repository

import com.example.domain.model.Client
import kotlinx.coroutines.flow.Flow

interface ClientRepository {
    fun getAllClients(): Flow<List<Client>>
    suspend fun getClientById(id: String): Client?
    suspend fun saveClient(client: Client)
    suspend fun deleteClient(id: String)
}
