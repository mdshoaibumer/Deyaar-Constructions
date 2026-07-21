package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.PhotoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao {
    @Query("SELECT * FROM photos WHERE projectId = :projectId ORDER BY date DESC")
    fun getPhotosForProject(projectId: String): Flow<List<PhotoEntity>>

    @Query("SELECT * FROM photos WHERE id = :id")
    suspend fun getPhotoById(id: String): PhotoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: PhotoEntity)

    @Query("DELETE FROM photos WHERE id = :id")
    suspend fun deletePhoto(id: String)
}
