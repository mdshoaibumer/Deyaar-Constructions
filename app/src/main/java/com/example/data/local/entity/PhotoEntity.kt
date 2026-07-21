package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "photos",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["projectId"]),
        Index(value = ["linkedSiteDiaryId"]),
        Index(value = ["linkedMilestoneId"]),
        Index(value = ["category"]),
        Index(value = ["date"])
    ]
)
data class PhotoEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val uri: String, // Local storage path
    val description: String?,
    val tags: List<String>, // Converted via Room TypeConverter
    val category: String, // e.g. Site Progress, Before, After, Milestone, Daily
    val date: Long,
    val capturedBy: String?,
    val location: String?,
    val linkedSiteDiaryId: String?,
    val linkedMilestoneId: String?
)
