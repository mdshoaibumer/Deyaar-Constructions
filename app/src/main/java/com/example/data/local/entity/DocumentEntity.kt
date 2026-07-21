package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "documents",
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
        Index(value = ["category"]),
        Index(value = ["createdAt"])
    ]
)
data class DocumentEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val title: String,
    val category: String, // PDF, Images, Word, Excel, Drawings, Contracts, Approvals, Invoices, Receipts, BOQ, Blueprints, Other
    val uri: String,
    val description: String?,
    val tags: List<String>, // Converted via Room TypeConverter
    val createdAt: Long,
    val updatedAt: Long
)
