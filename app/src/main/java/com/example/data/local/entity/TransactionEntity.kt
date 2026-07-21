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
        Index(value = ["category"]),
        Index(value = ["isDeleted"])
    ]
)
data class TransactionEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val date: Long,
    val time: Long,
    val type: String,
    val category: String,
    val amountPaise: Long, // Amount in paise (1 INR = 100 paise) for financial precision
    val paymentMethod: String,
    val referenceNumber: String?,
    val description: String?,
    val createdBy: String,
    val isDeleted: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
    val attachmentPath: String?
)
