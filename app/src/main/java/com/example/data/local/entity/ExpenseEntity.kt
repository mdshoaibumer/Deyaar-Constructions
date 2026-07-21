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
    val amountPaise: Long, // Amount in paise for financial precision
    val category: String,
    val description: String,
    val date: Long,
    val isPaid: Boolean
)
