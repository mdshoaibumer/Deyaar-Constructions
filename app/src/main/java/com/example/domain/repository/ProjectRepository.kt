package com.example.domain.repository

import com.example.domain.model.Milestone
import com.example.domain.model.Project
import com.example.domain.model.ProjectTimelineEvent
import kotlinx.coroutines.flow.Flow

interface ProjectRepository {
    fun getAllProjects(): Flow<List<Project>>
    fun getProjectsForClient(clientId: String): Flow<List<Project>>
    suspend fun getProjectById(id: String): Project?
    suspend fun saveProject(project: Project)
    suspend fun deleteProject(id: String)
    
    fun getMilestonesForProject(projectId: String): Flow<List<Milestone>>
    suspend fun saveMilestone(milestone: Milestone)
    suspend fun saveMilestones(milestones: List<Milestone>)
    
    fun getTimelineEventsForProject(projectId: String): Flow<List<ProjectTimelineEvent>>
    suspend fun saveTimelineEvent(event: ProjectTimelineEvent)
}
