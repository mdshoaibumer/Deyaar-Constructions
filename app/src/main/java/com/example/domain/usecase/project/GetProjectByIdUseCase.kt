package com.example.domain.usecase.project

import com.example.domain.model.Project
import com.example.domain.repository.ProjectRepository

class GetProjectByIdUseCase(
    private val repository: ProjectRepository
) {
    suspend operator fun invoke(id: String): Project? {
        return repository.getProjectById(id)
    }
}
