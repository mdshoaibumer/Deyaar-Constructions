package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "workers",
    indices = [
        Index(value = ["fullName"]),
        Index(value = ["trade"]),
        Index(value = ["status"])
    ]
)
data class WorkerEntity(
    @PrimaryKey val id: String,
    val fullName: String,
    val mobileNumber: String,
    val trade: String,
    val dailyWagePaise: Long, // Amount in paise for financial precision
    val experience: String?,
    val joiningDate: Long,
    val emergencyContact: String?,
    val address: String?,
    val status: String,
    val createdAt: Long,
    val updatedAt: Long
)
