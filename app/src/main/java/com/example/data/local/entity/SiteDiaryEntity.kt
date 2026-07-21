package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity(
    
    
    tableName = "site_diaries",
    indices = [Index("projectId")],
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SiteDiaryEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val date: Long,
    val startTime: String?,
    val endTime: String?,
    val weather: String?,
    val temperature: String?,
    val overallProgress: Int,
    val workSummary: String?,
    val engineerNotes: String?,
    val safetyObservations: String?,
    val nextDayPlan: String?,
    val createdAt: Long,
    val updatedAt: Long
)

@Entity(
    
    
    tableName = "labour_summaries",
    indices = [Index("siteDiaryId")],
    foreignKeys = [
        ForeignKey(
            entity = SiteDiaryEntity::class,
            parentColumns = ["id"],
            childColumns = ["siteDiaryId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class LabourSummaryEntity(
    @PrimaryKey val id: String,
    val siteDiaryId: String,
    val masons: Int,
    val helpers: Int,
    val carpenters: Int,
    val steelWorkers: Int,
    val painters: Int,
    val electricians: Int,
    val plumbers: Int,
    val machineOperators: Int,
    val other: Int,
    val totalWagesPaise: Long, // Amount in paise for financial precision
    val attendanceSummary: String?
)

@Entity(
    
    
    tableName = "material_summaries",
    indices = [Index("siteDiaryId")],
    foreignKeys = [
        ForeignKey(
            entity = SiteDiaryEntity::class,
            parentColumns = ["id"],
            childColumns = ["siteDiaryId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MaterialSummaryEntity(
    @PrimaryKey val id: String,
    val siteDiaryId: String,
    val materialName: String,
    val type: String,
    val supplier: String?,
    val quantity: Double,
    val unit: String,
    val remarks: String?,
    val lowStockWarning: Boolean
)

@Entity(
    
    
    tableName = "expense_summaries",
    indices = [Index("siteDiaryId")],
    foreignKeys = [
        ForeignKey(
            entity = SiteDiaryEntity::class,
            parentColumns = ["id"],
            childColumns = ["siteDiaryId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ExpenseSummaryEntity(
    @PrimaryKey val id: String,
    val siteDiaryId: String,
    val category: String,
    val amountPaise: Long, // Amount in paise for financial precision
    val remarks: String?
)

@Entity(
    
    
    tableName = "work_items",
    indices = [Index("siteDiaryId")],
    foreignKeys = [
        ForeignKey(
            entity = SiteDiaryEntity::class,
            parentColumns = ["id"],
            childColumns = ["siteDiaryId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class WorkItemEntity(
    @PrimaryKey val id: String,
    val siteDiaryId: String,
    val description: String,
    val percentageComplete: Int,
    val remarks: String?
)

@Entity(
    
    
    tableName = "site_issues",
    indices = [Index("siteDiaryId")],
    foreignKeys = [
        ForeignKey(
            entity = SiteDiaryEntity::class,
            parentColumns = ["id"],
            childColumns = ["siteDiaryId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class SiteIssueEntity(
    @PrimaryKey val id: String,
    val siteDiaryId: String,
    val type: String,
    val description: String,
    val resolved: Boolean
)
