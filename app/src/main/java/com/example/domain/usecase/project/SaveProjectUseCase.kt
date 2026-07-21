package com.example.domain.usecase.project

import com.example.domain.model.Project
import com.example.domain.repository.ProjectRepository

class SaveProjectUseCase(
    private val repository: ProjectRepository
) {
    suspend operator fun invoke(project: Project) {
        repository.saveProject(project)
    }
}
