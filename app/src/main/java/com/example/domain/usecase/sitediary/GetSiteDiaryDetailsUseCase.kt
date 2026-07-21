package com.example.domain.usecase.sitediary

import com.example.domain.model.SiteDiaryDetails
import com.example.domain.repository.SiteDiaryRepository
import kotlinx.coroutines.flow.Flow

class GetSiteDiaryDetailsUseCase(
    private val repository: SiteDiaryRepository
) {
    operator fun invoke(id: String): Flow<SiteDiaryDetails?> {
        return repository.getSiteDiaryDetails(id)
    }
}
