package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.MaterialEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MaterialDao {
    @Query("SELECT * FROM materials ORDER BY name ASC")
    fun getAllMaterials(): Flow<List<MaterialEntity>>

    @Query("SELECT * FROM materials WHERE id = :id")
    suspend fun getMaterialById(id: String): MaterialEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaterial(material: MaterialEntity)

    @Query("DELETE FROM materials WHERE id = :id")
    suspend fun deleteMaterial(id: String)
    
    @Query("UPDATE materials SET currentStock = currentStock + :quantity, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateStock(id: String, quantity: Double, updatedAt: Long)
}
