package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entity.MilestoneEntity
import com.example.data.local.entity.ProjectEntity
import com.example.data.local.entity.ProjectTimelineEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects ORDER BY createdAt DESC")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT COUNT(*) FROM projects")
    fun getProjectsCountFlow(): Flow<Int>

    @Query("SELECT COUNT(*) FROM projects WHERE status IN ('ACTIVE', 'PLANNING')")
    fun getActiveProjectsCount(): Flow<Int>

    @Query("SELECT * FROM projects ORDER BY createdAt DESC LIMIT :limit")
    fun getRecentProjects(limit: Int): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE id = :id")
    suspend fun getProjectById(id: String): ProjectEntity?
    
    @Query("SELECT * FROM projects WHERE clientId = :clientId ORDER BY createdAt DESC")
    fun getProjectsForClient(clientId: String): Flow<List<ProjectEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity)

    @Update
    suspend fun updateProject(project: ProjectEntity)

    @Query("DELETE FROM projects WHERE id = :id")
    suspend fun deleteProject(id: String)

    // Milestones
    @Query("SELECT * FROM milestones WHERE projectId = :projectId ORDER BY orderIndex ASC")
    fun getMilestonesForProject(projectId: String): Flow<List<MilestoneEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMilestones(milestones: List<MilestoneEntity>)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMilestone(milestone: MilestoneEntity)

    @Update
    suspend fun updateMilestone(milestone: MilestoneEntity)

    // Timeline Events
    @Query("SELECT * FROM project_timeline_events WHERE projectId = :projectId ORDER BY timestamp DESC")
    fun getTimelineEventsForProject(projectId: String): Flow<List<ProjectTimelineEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimelineEvent(event: ProjectTimelineEventEntity)
    
    @Query("SELECT COUNT(*) FROM projects")
    suspend fun getProjectsCount(): Int

    @Query("SELECT COUNT(*) FROM projects WHERE status = 'COMPLETED'")
    fun getCompletedProjectsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM projects WHERE status = 'ON_HOLD'")
    fun getOnHoldProjectsCount(): Flow<Int>

    @Query("SELECT COALESCE(SUM(contractValuePaise), 0) FROM projects WHERE contractValuePaise IS NOT NULL")
    fun getTotalContractValue(): Flow<Long>
}
