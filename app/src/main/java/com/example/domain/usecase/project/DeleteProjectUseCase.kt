package com.example.domain.usecase.project

import com.example.domain.repository.ProjectRepository

class DeleteProjectUseCase(
    private val repository: ProjectRepository
) {
    suspend operator fun invoke(id: String) {
        repository.deleteProject(id)
    }
}
