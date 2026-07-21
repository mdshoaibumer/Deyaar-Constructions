package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.WorkerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkerDao {
    @Query("SELECT * FROM workers ORDER BY fullName ASC")
    fun getAllWorkers(): Flow<List<WorkerEntity>>

    @Query("SELECT * FROM workers WHERE id = :id")
    suspend fun getWorkerById(id: String): WorkerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorker(worker: WorkerEntity)

    @Query("DELETE FROM workers WHERE id = :id")
    suspend fun deleteWorker(id: String)
}
