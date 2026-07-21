package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "suppliers",
    indices = [
        Index(value = ["name"])
    ]
)
data class SupplierEntity(
    @PrimaryKey val id: String,
    val name: String,
    val phone: String,
    val gst: String?,
    val address: String?,
    val materialCategories: String, // comma separated
    val outstandingBalancePaise: Long, // Amount in paise for financial precision
    val notes: String?,
    val createdAt: Long,
    val updatedAt: Long
)
