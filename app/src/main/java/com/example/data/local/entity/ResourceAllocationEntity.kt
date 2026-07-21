package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "resource_allocations",
    indices = [
        Index(value = ["projectId"]),
        Index(value = ["siteDiaryId"]),
        Index(value = ["transactionId"]),
        Index(value = ["resourceId"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ResourceAllocationEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val date: Long,
    val resourceType: String,
    val resourceId: String,
    val quantity: Double,
    val hours: Double?,
    val costPaise: Long, // Amount in paise for financial precision
    val remarks: String?,
    val siteDiaryId: String?,
    val transactionId: String?,
    val createdAt: Long
)
