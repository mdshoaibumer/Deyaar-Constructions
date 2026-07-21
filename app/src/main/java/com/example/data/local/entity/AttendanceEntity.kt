package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "attendance",
    foreignKeys = [
        ForeignKey(
            entity = WorkerEntity::class,
            parentColumns = ["id"],
            childColumns = ["workerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["workerId"]),
        Index(value = ["projectId"]),
        Index(value = ["date"])
    ]
)
data class AttendanceEntity(
    @PrimaryKey val id: String,
    val workerId: String,
    val projectId: String?,
    val date: Long,
    val status: String,
    val overtimeHours: Double,
    val hoursWorked: Double,
    val remarks: String?,
    val createdAt: Long
)
