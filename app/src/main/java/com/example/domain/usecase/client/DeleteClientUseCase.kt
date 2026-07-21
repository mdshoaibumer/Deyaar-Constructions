package com.example.domain.usecase.client

import com.example.domain.repository.ClientRepository

class DeleteClientUseCase(
    private val clientRepository: ClientRepository
) {
    suspend operator fun invoke(id: String) {
        clientRepository.deleteClient(id)
    }
}
