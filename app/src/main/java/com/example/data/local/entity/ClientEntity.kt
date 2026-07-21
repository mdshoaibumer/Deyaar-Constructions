package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.domain.model.ClientCategory

@Entity(
    tableName = "clients",
    indices = [
        Index(value = ["name"]),
        Index(value = ["isActive"]),
        Index(value = ["category"])
    ]
)
data class ClientEntity(
    @PrimaryKey val id: String,
    val name: String,
    val companyName: String? = null,
    val phone: String,
    val altPhone: String? = null,
    val whatsapp: String? = null,
    val email: String?,
    val gstNumber: String? = null,
    val panNumber: String? = null,
    val address: String?,
    val city: String? = null,
    val state: String? = null,
    val pincode: String? = null,
    val mapsLocation: String? = null,
    val category: ClientCategory = ClientCategory.RESIDENTIAL,
    val notes: String?,
    val photoPath: String? = null,
    val isActive: Boolean = true,
    val isFavorite: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long
)
