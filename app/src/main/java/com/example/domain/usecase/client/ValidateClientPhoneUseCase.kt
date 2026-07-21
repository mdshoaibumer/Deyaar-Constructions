package com.example.domain.usecase.client

import com.example.domain.repository.ClientRepository
import kotlinx.coroutines.flow.first

class ValidateClientPhoneUseCase(
    private val clientRepository: ClientRepository
) {
    suspend operator fun invoke(phone: String, currentClientId: String?): Boolean {
        val clients = clientRepository.getAllClients().first()
        val duplicate = clients.find { it.phone == phone && it.id != currentClientId }
        return duplicate != null
    }
}
