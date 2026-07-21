package com.example.domain.usecase.client

import com.example.domain.model.Client
import com.example.domain.repository.ClientRepository

class GetClientByIdUseCase(
    private val clientRepository: ClientRepository
) {
    suspend operator fun invoke(id: String): Client? {
        return clientRepository.getClientById(id)
    }
}
