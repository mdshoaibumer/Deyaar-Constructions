package com.example.domain.usecase.sitediary

import com.example.domain.model.SiteDiary
import com.example.domain.repository.SiteDiaryRepository
import kotlinx.coroutines.flow.Flow

class GetSiteDiariesForProjectUseCase(
    private val repository: SiteDiaryRepository
) {
    operator fun invoke(projectId: String): Flow<List<SiteDiary>> {
        return repository.getSiteDiariesForProject(projectId)
    }
}
