package com.example.domain.usecase.project

import com.example.domain.model.Milestone
import com.example.domain.repository.ProjectRepository

class SaveMilestonesUseCase(
    private val repository: ProjectRepository
) {
    suspend operator fun invoke(milestones: List<Milestone>) {
        repository.saveMilestones(milestones)
    }
}
