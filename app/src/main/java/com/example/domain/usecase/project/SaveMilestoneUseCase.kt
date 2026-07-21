package com.example.domain.usecase.project

import com.example.domain.model.Milestone
import com.example.domain.repository.ProjectRepository

class SaveMilestoneUseCase(
    private val repository: ProjectRepository
) {
    suspend operator fun invoke(milestone: Milestone) {
        repository.saveMilestone(milestone)
    }
}
