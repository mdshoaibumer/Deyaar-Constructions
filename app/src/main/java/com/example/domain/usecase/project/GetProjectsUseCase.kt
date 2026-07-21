package com.example.domain.usecase.project

import com.example.domain.model.Project
import com.example.domain.repository.ProjectRepository
import kotlinx.coroutines.flow.Flow

class GetProjectsUseCase(
    private val repository: ProjectRepository
) {
    operator fun invoke(): Flow<List<Project>> {
        return repository.getAllProjects()
    }
}
