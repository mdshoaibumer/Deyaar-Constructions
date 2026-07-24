package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.ResourceAllocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ResourceAllocationDao {
    @Query("SELECT * FROM resource_allocations WHERE projectId = :projectId ORDER BY date DESC")
    fun getAllocationsForProject(projectId: String): Flow<List<ResourceAllocationEntity>>

    @Query("SELECT * FROM resource_allocations WHERE resourceId = :resourceId ORDER BY date DESC")
    fun getAllocationsForResource(resourceId: String): Flow<List<ResourceAllocationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllocation(allocation: ResourceAllocationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllocations(allocations: List<ResourceAllocationEntity>)
    
    @Query("DELETE FROM resource_allocations WHERE id = :id")
    suspend fun deleteAllocation(id: String)
}
