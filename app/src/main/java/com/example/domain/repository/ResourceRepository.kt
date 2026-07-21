package com.example.domain.repository

import com.example.domain.model.*
import kotlinx.coroutines.flow.Flow

interface ResourceRepository {
    // Materials
    fun getAllMaterials(): Flow<List<Material>>
    suspend fun getMaterialById(id: String): Material?
    suspend fun saveMaterial(material: Material)
    suspend fun deleteMaterial(id: String)
    suspend fun updateStock(id: String, quantityChange: Double)

    // Workers
    fun getAllWorkers(): Flow<List<Worker>>
    suspend fun getWorkerById(id: String): Worker?
    suspend fun saveWorker(worker: Worker)
    suspend fun deleteWorker(id: String)

    // Suppliers
    fun getAllSuppliers(): Flow<List<Supplier>>
    suspend fun getSupplierById(id: String): Supplier?
    suspend fun saveSupplier(supplier: Supplier)
    suspend fun deleteSupplier(id: String)

    // Resource Allocations
    fun getAllocationsForProject(projectId: String): Flow<List<ResourceAllocation>>
    fun getAllocationsForResource(resourceId: String): Flow<List<ResourceAllocation>>
    suspend fun saveAllocation(allocation: ResourceAllocation)
    suspend fun deleteAllocation(id: String)

    // Attendance
    fun getAttendanceForWorker(workerId: String): Flow<List<Attendance>>
    fun getAttendanceForDate(date: Long): Flow<List<Attendance>>
    suspend fun saveAttendance(attendance: Attendance)
}
