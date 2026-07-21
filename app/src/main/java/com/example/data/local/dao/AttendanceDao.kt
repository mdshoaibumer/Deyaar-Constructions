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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: AttendanceEntity)
}
