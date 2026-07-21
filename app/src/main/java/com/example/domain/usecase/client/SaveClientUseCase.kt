package com.example.domain.usecase.client

import com.example.domain.model.Client
import com.example.domain.repository.ClientRepository

class SaveClientUseCase(
    private val clientRepository: ClientRepository
) {
    suspend operator fun invoke(client: Client) {
        clientRepository.saveClient(client)
    }
}
