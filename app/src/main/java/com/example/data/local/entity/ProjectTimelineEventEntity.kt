package com.example.data.local.entity
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.domain.model.TimelineEventType
@Entity(
    tableName = "project_timeline_events",
    indices = [androidx.room.Index(value = ["projectId"])],
    foreignKeys = [ForeignKey(entity = ProjectEntity::class, parentColumns = ["id"], childColumns = ["projectId"], onDelete = ForeignKey.CASCADE)]
)
data class ProjectTimelineEventEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val title: String,
    val description: String,
    val timestamp: Long,
    val type: TimelineEventType
)
