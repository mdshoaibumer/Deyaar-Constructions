package com.example.domain.usecase.project

import com.example.domain.model.Milestone
import com.example.domain.repository.ProjectRepository
import kotlinx.coroutines.flow.Flow

class GetProjectMilestonesUseCase(
    private val repository: ProjectRepository
) {
    operator fun invoke(projectId: String): Flow<List<Milestone>> {
        return repository.getMilestonesForProject(projectId)
    }
}
