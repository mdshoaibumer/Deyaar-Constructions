package com.example.domain.model

data class Material(
    val id: String,
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

data class Worker(
    val id: String,
    val fullName: String,
    val mobileNumber: String,
    val trade: String,
    val dailyWagePaise: Long, // Amount in paise for financial precision
    val experience: String?,
    val joiningDate: Long,
    val emergencyContact: String?,
    val address: String?,
    val status: String,
    val createdAt: Long,
    val updatedAt: Long
)

data class Supplier(
    val id: String,
    val name: String,
    val phone: String,
    val gst: String?,
    val address: String?,
    val materialCategories: List<String>,
    val outstandingBalancePaise: Long, // Amount in paise for financial precision
    val notes: String?,
    val createdAt: Long,
    val updatedAt: Long
)

data class ResourceAllocation(
    val id: String,
    val projectId: String,
    val date: Long,
    val resourceType: ResourceType,
    val resourceId: String,
    val quantity: Double,
    val hours: Double?,
    val costPaise: Long, // Amount in paise for financial precision
    val remarks: String?,
    val siteDiaryId: String?,
    val transactionId: String?,
    val createdAt: Long
)

enum class ResourceType {
    MATERIAL, LABOUR, EQUIPMENT
}

data class Attendance(
    val id: String,
    val workerId: String,
    val projectId: String?,
    val date: Long,
    val status: AttendanceStatus,
    val overtimeHours: Double,
    val hoursWorked: Double,
    val remarks: String?,
    val createdAt: Long
)

enum class AttendanceStatus {
    PRESENT, ABSENT, HALF_DAY
}
