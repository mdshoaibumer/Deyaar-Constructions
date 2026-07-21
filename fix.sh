#!/bin/bash
cat << 'INNER_EOF' > app/src/main/java/com/example/data/local/entity/TransactionEntity.kt
package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
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
        Index(value = ["date"]),
        Index(value = ["type"]),
        Index(value = ["category"])
    ]
)
data class TransactionEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val date: Long,
    val time: Long,
    val type: String,
    val category: String,
    val amount: Double,
    val paymentMethod: String,
    val referenceNumber: String?,
    val description: String?,
    val createdBy: String,
    val isDeleted: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
    val attachmentPath: String?
)
INNER_EOF

cat << 'INNER_EOF' > app/src/main/java/com/example/data/local/entity/AttendanceEntity.kt
package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "attendance",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = WorkerEntity::class,
            parentColumns = ["id"],
            childColumns = ["workerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["projectId"]),
        Index(value = ["workerId"]),
        Index(value = ["date"])
    ]
)
data class AttendanceEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val workerId: String,
    val date: Long,
    val isPresent: Boolean,
    val checkInTime: Long?,
    val checkOutTime: Long?,
    val hoursWorked: Double,
    val overtimeHours: Double,
    val remarks: String?
)
INNER_EOF

cat << 'INNER_EOF' > app/src/main/java/com/example/data/local/entity/ExpenseEntity.kt
package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "expenses",
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
        Index(value = ["date"]),
        Index(value = ["category"])
    ]
)
data class ExpenseEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val amount: Double,
    val category: String,
    val description: String,
    val date: Long,
    val isPaid: Boolean
)
INNER_EOF

cat << 'INNER_EOF' > app/src/main/java/com/example/data/local/entity/MilestoneEntity.kt
package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "milestones",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["projectId"])
    ]
)
data class MilestoneEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val name: String,
    val isCompleted: Boolean,
    val completionDate: Long?
)
INNER_EOF

cat << 'INNER_EOF' > app/src/main/java/com/example/data/local/entity/ProjectTimelineEventEntity.kt
package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "project_timeline_events",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["projectId"])
    ]
)
data class ProjectTimelineEventEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val title: String,
    val timestamp: Long
)
INNER_EOF

cat << 'INNER_EOF' > app/src/main/java/com/example/data/local/entity/ResourceAllocationEntity.kt
package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "resource_allocations",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["projectId"])
    ]
)
data class ResourceAllocationEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val resourceType: String,
    val resourceId: String,
    val quantity: Double,
    val startDate: Long,
    val endDate: Long?,
    val costPerUnit: Double,
    val totalCost: Double,
    val status: String,
    val notes: String?
)
INNER_EOF
