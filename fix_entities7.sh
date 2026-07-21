#!/bin/bash
cat << 'INNER_EOF' > app/src/main/java/com/example/data/local/entity/ProjectTimelineEventEntity.kt
package com.example.data.local.entity
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.domain.model.TimelineEventType
@Entity(
    tableName = "project_timeline_events",
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
INNER_EOF

cat << 'INNER_EOF' > app/src/main/java/com/example/data/local/entity/ResourceAllocationEntity.kt
package com.example.data.local.entity
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
@Entity(
    tableName = "resource_allocations",
    foreignKeys = [ForeignKey(entity = ProjectEntity::class, parentColumns = ["id"], childColumns = ["projectId"], onDelete = ForeignKey.CASCADE)]
)
data class ResourceAllocationEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val date: Long,
    val resourceType: String,
    val resourceId: String,
    val quantity: Double,
    val hours: Double?,
    val cost: Double?,
    val remarks: String?,
    val siteDiaryId: String?,
    val transactionId: String?,
    val createdAt: Long
)
INNER_EOF

cat << 'INNER_EOF' > app/src/main/java/com/example/data/local/entity/AttendanceEntity.kt
package com.example.data.local.entity
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
@Entity(
    tableName = "attendance",
    foreignKeys = [
        ForeignKey(entity = WorkerEntity::class, parentColumns = ["id"], childColumns = ["workerId"], onDelete = ForeignKey.CASCADE)
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
INNER_EOF
