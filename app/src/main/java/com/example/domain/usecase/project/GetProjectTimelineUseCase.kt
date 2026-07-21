package com.example.domain.usecase.project

import com.example.domain.model.ProjectTimelineEvent
import com.example.domain.repository.ProjectRepository
import kotlinx.coroutines.flow.Flow

class GetProjectTimelineUseCase(
    private val repository: ProjectRepository
) {
    operator fun invoke(projectId: String): Flow<List<ProjectTimelineEvent>> {
        return repository.getTimelineEventsForProject(projectId)
    }
}
