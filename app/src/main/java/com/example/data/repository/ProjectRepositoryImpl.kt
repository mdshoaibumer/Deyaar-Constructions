package com.example.data.repository

import com.example.data.local.dao.ProjectDao
import com.example.data.mapper.toDomain
import com.example.data.mapper.toEntity
import com.example.domain.model.Milestone
import com.example.domain.model.Project
import com.example.domain.model.ProjectTimelineEvent
import com.example.domain.repository.ProjectRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProjectRepositoryImpl(
    private val dao: ProjectDao
) : ProjectRepository {
    override fun getAllProjects(): Flow<List<Project>> {
        return dao.getAllProjects().map { list -> list.map { it.toDomain() } }
    }
    
    override fun getProjectsForClient(clientId: String): Flow<List<Project>> {
        return dao.getProjectsForClient(clientId).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getProjectById(id: String): Project? {
        return dao.getProjectById(id)?.toDomain()
    }

    override suspend fun saveProject(project: Project) {
        val existing = dao.getProjectById(project.id)
        if (existing == null) {
            dao.insertProject(project.toEntity())
        } else {
            dao.updateProject(project.toEntity())
        }
    }

    override suspend fun deleteProject(id: String) {
        dao.deleteProject(id)
    }

    override fun getMilestonesForProject(projectId: String): Flow<List<Milestone>> {
        return dao.getMilestonesForProject(projectId).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun saveMilestone(milestone: Milestone) {
        dao.insertMilestone(milestone.toEntity())
    }

    override suspend fun saveMilestones(milestones: List<Milestone>) {
        dao.insertMilestones(milestones.map { it.toEntity() })
    }

    override fun getTimelineEventsForProject(projectId: String): Flow<List<ProjectTimelineEvent>> {
        return dao.getTimelineEventsForProject(projectId).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun saveTimelineEvent(event: ProjectTimelineEvent) {
        dao.insertTimelineEvent(event.toEntity())
    }
}
