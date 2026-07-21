package com.example.data.local.entity
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
@Entity(
    tableName = "milestones",
    indices = [androidx.room.Index(value = ["projectId"])],
    foreignKeys = [ForeignKey(entity = ProjectEntity::class, parentColumns = ["id"], childColumns = ["projectId"], onDelete = ForeignKey.CASCADE)]
)
data class MilestoneEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val name: String,
    val isCompleted: Boolean,
    val completionDate: Long?,
    val notes: String?,
    val orderIndex: Int
)
