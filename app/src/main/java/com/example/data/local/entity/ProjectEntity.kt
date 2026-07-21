package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.domain.model.ProjectCategory
import com.example.domain.model.ProjectPriority
import com.example.domain.model.ProjectStatus

@Entity(
    tableName = "projects",
    foreignKeys = [
        ForeignKey(
            entity = ClientEntity::class,
            parentColumns = ["id"],
            childColumns = ["clientId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["clientId"]),
        Index(value = ["status"]),
        Index(value = ["createdAt"])
    ]
)
data class ProjectEntity(
    @PrimaryKey
    val id: String,
    val projectNumber: String,
    val name: String,
    val clientId: String?,
    val category: ProjectCategory,
    val address: String?,
    val location: String?,
    val contractValuePaise: Long?, // Amount in paise for financial precision
    val estimatedBudgetPaise: Long?, // Amount in paise
    val advanceReceivedPaise: Long?, // Amount in paise
    val expectedProfitPaise: Long?, // Amount in paise
    val startDate: Long?,
    val expectedCompletionDate: Long?,
    val actualCompletionDate: Long?,
    val status: ProjectStatus,
    val priority: ProjectPriority,
    val engineerInCharge: String?,
    val notes: String?,
    val progress: Int,
    val createdAt: Long,
    val updatedAt: Long
)
