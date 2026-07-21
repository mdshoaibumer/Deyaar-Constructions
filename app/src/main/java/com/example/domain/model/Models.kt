package com.example.domain.model

data class Client(
    val id: String,
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

data class Expense(
    val id: String,
    val projectId: String,
    val category: String,
    val description: String,
    val amountPaise: Long, // Amount in paise for financial precision
    val date: Long,
    val receiptPhotoPath: String?,
    val createdAt: Long
)
