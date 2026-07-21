package com.example.domain.usecase.project

import com.example.domain.model.ProjectTimelineEvent
import com.example.domain.repository.ProjectRepository

class SaveProjectTimelineEventUseCase(
    private val repository: ProjectRepository
) {
    suspend operator fun invoke(event: ProjectTimelineEvent) {
        repository.saveTimelineEvent(event)
    }
}
