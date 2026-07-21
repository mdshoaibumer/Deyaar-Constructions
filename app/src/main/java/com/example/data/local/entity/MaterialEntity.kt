package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "materials",
    indices = [
        Index(value = ["name"]),
        Index(value = ["category"]),
        Index(value = ["status"])
    ]
)
data class MaterialEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val unit: String,
    val currentStock: Double,
    val minimumStock: Double,
    val openingStock: Double,
    val purchasePricePaise: Long, // Amount in paise for financial precision
    val averageCostPaise: Long, // Amount in paise for financial precision
    val remarks: String?,
    val status: String,
    val createdAt: Long,
    val updatedAt: Long
)
