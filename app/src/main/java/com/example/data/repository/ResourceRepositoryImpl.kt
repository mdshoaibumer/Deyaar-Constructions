package com.example.data.repository

import com.example.data.local.dao.*
import com.example.data.local.entity.*
import com.example.domain.model.*
import com.example.domain.repository.ResourceRepository
import kotlinx.coroutines.flow.Flow
import androidx.room.withTransaction
import com.example.data.local.AppDatabase
import kotlinx.coroutines.flow.map

class ResourceRepositoryImpl(
    private val database: AppDatabase,
    private val materialDao: MaterialDao,
    private val workerDao: WorkerDao,
    private val supplierDao: SupplierDao,
    private val resourceAllocationDao: ResourceAllocationDao,
    private val attendanceDao: AttendanceDao
) : ResourceRepository {

    override fun getAllMaterials(): Flow<List<Material>> =
        materialDao.getAllMaterials().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getMaterialById(id: String): Material? =
        materialDao.getMaterialById(id)?.toDomain()

    override suspend fun saveMaterial(material: Material) {
        materialDao.insertMaterial(material.toEntity())
    }

    override suspend fun deleteMaterial(id: String) {
        materialDao.deleteMaterial(id)
    }

    override suspend fun updateStock(id: String, quantityChange: Double) {
        materialDao.updateStock(id, quantityChange, System.currentTimeMillis())
    }

    override fun getAllWorkers(): Flow<List<Worker>> =
        workerDao.getAllWorkers().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getWorkerById(id: String): Worker? =
        workerDao.getWorkerById(id)?.toDomain()

    override suspend fun saveWorker(worker: Worker) {
        workerDao.insertWorker(worker.toEntity())
    }

    override suspend fun deleteWorker(id: String) {
        workerDao.deleteWorker(id)
    }

    override fun getAllSuppliers(): Flow<List<Supplier>> =
        supplierDao.getAllSuppliers().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getSupplierById(id: String): Supplier? =
        supplierDao.getSupplierById(id)?.toDomain()

    override suspend fun saveSupplier(supplier: Supplier) {
        supplierDao.insertSupplier(supplier.toEntity())
    }

    override suspend fun deleteSupplier(id: String) {
        supplierDao.deleteSupplier(id)
    }

    override fun getAllocationsForProject(projectId: String): Flow<List<ResourceAllocation>> =
        resourceAllocationDao.getAllocationsForProject(projectId).map { entities -> entities.map { it.toDomain() } }

    override fun getAllocationsForResource(resourceId: String): Flow<List<ResourceAllocation>> =
        resourceAllocationDao.getAllocationsForResource(resourceId).map { entities -> entities.map { it.toDomain() } }

    override suspend fun saveAllocation(allocation: ResourceAllocation) {
        database.withTransaction {
            resourceAllocationDao.insertAllocation(allocation.toEntity())
            // Adjust stock if it's a material
            if (allocation.resourceType == ResourceType.MATERIAL) {
                // Negative quantity for allocation (outgoing)
                materialDao.updateStock(allocation.resourceId, -allocation.quantity, System.currentTimeMillis())
            }
        }
    }

    override suspend fun deleteAllocation(id: String) {
        resourceAllocationDao.deleteAllocation(id)
    }

    override fun getAttendanceForWorker(workerId: String): Flow<List<Attendance>> =
        attendanceDao.getAttendanceForWorker(workerId).map { entities -> entities.map { it.toDomain() } }

    override fun getAttendanceForDate(date: Long): Flow<List<Attendance>> =
        attendanceDao.getAttendanceForDate(date).map { entities -> entities.map { it.toDomain() } }

    override suspend fun saveAttendance(attendance: Attendance) {
        attendanceDao.insertAttendance(attendance.toEntity())
    }

    override suspend fun saveAllAttendance(attendanceList: List<Attendance>) {
        attendanceDao.insertAllAttendance(attendanceList.map { it.toEntity() })
    }

    override suspend fun deleteAttendance(id: String) {
        attendanceDao.deleteAttendance(id)
    }

    // Mappers
    private fun MaterialEntity.toDomain() = Material(
        id = id, name = name, category = category, unit = unit,
        currentStock = currentStock, minimumStock = minimumStock, openingStock = openingStock,
        purchasePricePaise = purchasePricePaise, averageCostPaise = averageCostPaise, remarks = remarks,
        status = status, createdAt = createdAt, updatedAt = updatedAt
    )

    private fun Material.toEntity() = MaterialEntity(
        id = id, name = name, category = category, unit = unit,
        currentStock = currentStock, minimumStock = minimumStock, openingStock = openingStock,
        purchasePricePaise = purchasePricePaise, averageCostPaise = averageCostPaise, remarks = remarks,
        status = status, createdAt = createdAt, updatedAt = updatedAt
    )

    private fun WorkerEntity.toDomain() = Worker(
        id = id, fullName = fullName, mobileNumber = mobileNumber, trade = trade,
        dailyWagePaise = dailyWagePaise, experience = experience, joiningDate = joiningDate,
        emergencyContact = emergencyContact, address = address, status = status,
        createdAt = createdAt, updatedAt = updatedAt
    )

    private fun Worker.toEntity() = WorkerEntity(
        id = id, fullName = fullName, mobileNumber = mobileNumber, trade = trade,
        dailyWagePaise = dailyWagePaise, experience = experience, joiningDate = joiningDate,
        emergencyContact = emergencyContact, address = address, status = status,
        createdAt = createdAt, updatedAt = updatedAt
    )

    private fun SupplierEntity.toDomain() = Supplier(
        id = id, name = name, phone = phone, gst = gst, address = address,
        materialCategories = materialCategories.split(",").filter { it.isNotBlank() },
        outstandingBalancePaise = outstandingBalancePaise, notes = notes,
        createdAt = createdAt, updatedAt = updatedAt
    )

    private fun Supplier.toEntity() = SupplierEntity(
        id = id, name = name, phone = phone, gst = gst, address = address,
        materialCategories = materialCategories.joinToString(","),
        outstandingBalancePaise = outstandingBalancePaise, notes = notes,
        createdAt = createdAt, updatedAt = updatedAt
    )

    private fun ResourceAllocationEntity.toDomain() = ResourceAllocation(
        id = id, projectId = projectId, date = date,
        resourceType = ResourceType.valueOf(resourceType), resourceId = resourceId,
        quantity = quantity, hours = hours, costPaise = costPaise, remarks = remarks,
        siteDiaryId = siteDiaryId, transactionId = transactionId, createdAt = createdAt
    )

    private fun ResourceAllocation.toEntity() = ResourceAllocationEntity(
        id = id, projectId = projectId, date = date,
        resourceType = resourceType.name, resourceId = resourceId,
        quantity = quantity, hours = hours, costPaise = costPaise, remarks = remarks,
        siteDiaryId = siteDiaryId, transactionId = transactionId, createdAt = createdAt
    )

    private fun AttendanceEntity.toDomain() = Attendance(
        id = id, workerId = workerId, projectId = projectId, date = date,
        status = AttendanceStatus.valueOf(status), overtimeHours = overtimeHours,
        hoursWorked = hoursWorked, remarks = remarks, createdAt = createdAt
    )

    private fun Attendance.toEntity() = AttendanceEntity(
        id = id, workerId = workerId, projectId = projectId, date = date,
        status = status.name, overtimeHours = overtimeHours,
        hoursWorked = hoursWorked, remarks = remarks, createdAt = createdAt
    )
}
