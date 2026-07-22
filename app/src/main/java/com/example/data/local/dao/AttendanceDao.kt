package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.AttendanceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {
    @Query("SELECT * FROM attendance WHERE workerId = :workerId ORDER BY date DESC")
    fun getAttendanceForWorker(workerId: String): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance WHERE date = :date")
    fun getAttendanceForDate(date: Long): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance WHERE projectId = :projectId AND date = :date")
    fun getAttendanceForProjectAndDate(projectId: String, date: Long): Flow<List<AttendanceEntity>>

    @Query("SELECT COUNT(DISTINCT workerId) FROM attendance WHERE date = :date AND status = 'PRESENT'")
    fun getPresentCountForDate(date: Long): Flow<Int>

    @Query("SELECT * FROM attendance WHERE workerId = :workerId AND date >= :startDate AND date <= :endDate ORDER BY date DESC")
    fun getAttendanceForWorkerInRange(workerId: String, startDate: Long, endDate: Long): Flow<List<AttendanceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: AttendanceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAttendance(attendance: List<AttendanceEntity>)

    @Query("DELETE FROM attendance WHERE id = :id")
    suspend fun deleteAttendance(id: String)
}
