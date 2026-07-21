package com.example.domain.usecase.sitediary

import com.example.domain.model.SiteDiary
import com.example.domain.repository.SiteDiaryRepository
import java.util.UUID

class GetOrCreateSiteDiaryForDateUseCase(
    private val repository: SiteDiaryRepository
) {
    suspend operator fun invoke(projectId: String, date: Long): SiteDiary {
        val existing = repository.getSiteDiaryForProjectAndDate(projectId, date)
        if (existing != null) {
            return existing
        }
        
        val newDiary = SiteDiary(
            id = UUID.randomUUID().toString(),
            projectId = projectId,
            date = date,
            startTime = null,
            endTime = null,
            weather = null,
            temperature = null,
            overallProgress = 0,
            workSummary = null,
            engineerNotes = null,
            safetyObservations = null,
            nextDayPlan = null,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        repository.saveSiteDiary(newDiary)
        return newDiary
    }
}
