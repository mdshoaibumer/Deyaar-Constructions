package com.example.data.repository

import com.example.data.local.dao.ClientDao
import com.example.data.mapper.toDomain
import com.example.data.mapper.toEntity
import com.example.domain.model.Client
import com.example.domain.repository.ClientRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ClientRepositoryImpl(
    private val clientDao: ClientDao
) : ClientRepository {
    override fun getAllClients(): Flow<List<Client>> {
        return clientDao.getAllClients().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getClientById(id: String): Client? {
        return clientDao.getClientById(id)?.toDomain()
    }

    override suspend fun saveClient(client: Client) {
        val existing = clientDao.getClientById(client.id)
        if (existing == null) {
            clientDao.insertClient(client.toEntity())
        } else {
            clientDao.updateClient(client.toEntity())
        }
    }

    override suspend fun deleteClient(id: String) {
        clientDao.deleteClient(id)
    }
}
