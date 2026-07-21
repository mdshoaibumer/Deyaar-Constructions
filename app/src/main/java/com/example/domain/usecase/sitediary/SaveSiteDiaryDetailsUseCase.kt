package com.example.domain.usecase.sitediary

import com.example.domain.model.SiteDiaryDetails
import com.example.domain.repository.SiteDiaryRepository

class SaveSiteDiaryDetailsUseCase(
    private val repository: SiteDiaryRepository
) {
    suspend operator fun invoke(details: SiteDiaryDetails) {
        repository.saveSiteDiaryDetails(details)
    }
}
