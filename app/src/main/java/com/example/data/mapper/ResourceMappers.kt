package com.example.data.mapper

import com.example.data.local.entity.*
import com.example.domain.model.*

// Material
fun MaterialEntity.toDomain() = Material(
    id = id, name = name, category = category, unit = unit,
    currentStock = currentStock, minimumStock = minimumStock, openingStock = openingStock,
    purchasePricePaise = purchasePricePaise, averageCostPaise = averageCostPaise, remarks = remarks,
    status = status, createdAt = createdAt, updatedAt = updatedAt
)

fun Material.toEntity() = MaterialEntity(
    id = id, name = name, category = category, unit = unit,
    currentStock = currentStock, minimumStock = minimumStock, openingStock = openingStock,
    purchasePricePaise = purchasePricePaise, averageCostPaise = averageCostPaise, remarks = remarks,
    status = status, createdAt = createdAt, updatedAt = updatedAt
)

// Worker
fun WorkerEntity.toDomain() = Worker(
    id = id, fullName = fullName, mobileNumber = mobileNumber, trade = trade,
    dailyWagePaise = dailyWagePaise, experience = experience, joiningDate = joiningDate,
    emergencyContact = emergencyContact, address = address, status = status,
    createdAt = createdAt, updatedAt = updatedAt
)

fun Worker.toEntity() = WorkerEntity(
    id = id, fullName = fullName, mobileNumber = mobileNumber, trade = trade,
    dailyWagePaise = dailyWagePaise, experience = experience, joiningDate = joiningDate,
    emergencyContact = emergencyContact, address = address, status = status,
    createdAt = createdAt, updatedAt = updatedAt
)

// Supplier
fun SupplierEntity.toDomain() = Supplier(
    id = id, name = name, phone = phone, gst = gst, address = address,
    materialCategories = materialCategories.split(",").filter { it.isNotBlank() },
    outstandingBalancePaise = outstandingBalancePaise, notes = notes,
    createdAt = createdAt, updatedAt = updatedAt
)

fun Supplier.toEntity() = SupplierEntity(
    id = id, name = name, phone = phone, gst = gst, address = address,
    materialCategories = materialCategories.joinToString(","),
    outstandingBalancePaise = outstandingBalancePaise, notes = notes,
    createdAt = createdAt, updatedAt = updatedAt
)

// ResourceAllocation
fun ResourceAllocationEntity.toDomain() = ResourceAllocation(
    id = id, projectId = projectId, date = date,
    resourceType = ResourceType.valueOf(resourceType), resourceId = resourceId,
    quantity = quantity, hours = hours, costPaise = costPaise, remarks = remarks,
    siteDiaryId = siteDiaryId, transactionId = transactionId, createdAt = createdAt
)

fun ResourceAllocation.toEntity() = ResourceAllocationEntity(
    id = id, projectId = projectId, date = date,
    resourceType = resourceType.name, resourceId = resourceId,
    quantity = quantity, hours = hours, costPaise = costPaise, remarks = remarks,
    siteDiaryId = siteDiaryId, transactionId = transactionId, createdAt = createdAt
)

// Attendance
fun AttendanceEntity.toDomain() = Attendance(
    id = id, workerId = workerId, projectId = projectId, date = date,
    status = AttendanceStatus.valueOf(status), overtimeHours = overtimeHours,
    hoursWorked = hoursWorked, remarks = remarks, createdAt = createdAt
)

fun Attendance.toEntity() = AttendanceEntity(
    id = id, workerId = workerId, projectId = projectId, date = date,
    status = status.name, overtimeHours = overtimeHours,
    hoursWorked = hoursWorked, remarks = remarks, createdAt = createdAt
)
