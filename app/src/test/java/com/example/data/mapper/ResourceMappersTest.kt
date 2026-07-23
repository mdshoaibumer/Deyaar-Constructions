package com.example.data.mapper

import com.example.data.local.entity.*
import com.example.domain.model.*
import org.junit.Assert.*
import org.junit.Test

class ResourceMappersTest {

    @Test
    fun materialEntity_toDomain_mapsAllFields() {
        val entity = MaterialEntity(
            id = "mat_1", name = "OPC Cement", category = "Cement",
            unit = "Bags", currentStock = 100.0, minimumStock = 20.0,
            openingStock = 200.0, purchasePricePaise = 38000L,
            averageCostPaise = 36100L, remarks = "Grade 53",
            status = "IN_STOCK", createdAt = 1000L, updatedAt = 2000L
        )

        val domain = entity.toDomain()

        assertEquals("mat_1", domain.id)
        assertEquals("OPC Cement", domain.name)
        assertEquals("Cement", domain.category)
        assertEquals(100.0, domain.currentStock, 0.001)
        assertEquals(38000L, domain.purchasePricePaise)
        assertEquals("Grade 53", domain.remarks)
    }

    @Test
    fun material_toEntity_mapsAllFields() {
        val domain = Material(
            id = "mat_2", name = "TMT Steel", category = "Steel",
            unit = "Tonnes", currentStock = 5.0, minimumStock = 2.0,
            openingStock = 10.0, purchasePricePaise = 5500000L,
            averageCostPaise = 5200000L, remarks = null,
            status = "LOW_STOCK", createdAt = 3000L, updatedAt = 4000L
        )

        val entity = domain.toEntity()

        assertEquals("mat_2", entity.id)
        assertEquals("TMT Steel", entity.name)
        assertEquals(5500000L, entity.purchasePricePaise)
        assertNull(entity.remarks)
    }

    @Test
    fun material_roundTrip_preservesData() {
        val original = Material(
            id = "mat_rt", name = "Sand", category = "Sand",
            unit = "Cu.M", currentStock = 50.5, minimumStock = 10.0,
            openingStock = 100.0, purchasePricePaise = 180000L,
            averageCostPaise = 171000L, remarks = "River sand",
            status = "IN_STOCK", createdAt = 5000L, updatedAt = 6000L
        )

        val result = original.toEntity().toDomain()

        assertEquals(original, result)
    }

    @Test
    fun workerEntity_toDomain_mapsAllFields() {
        val entity = WorkerEntity(
            id = "w_1", fullName = "Raju Yadav", mobileNumber = "9876543210",
            trade = "Mason", dailyWagePaise = 75000L, experience = "5 years",
            joiningDate = 1000L, emergencyContact = "9812345678",
            address = "Village Ramnagar", status = "ACTIVE",
            createdAt = 1000L, updatedAt = 2000L
        )

        val domain = entity.toDomain()

        assertEquals("Raju Yadav", domain.fullName)
        assertEquals("Mason", domain.trade)
        assertEquals(75000L, domain.dailyWagePaise)
        assertEquals("5 years", domain.experience)
    }

    @Test
    fun worker_roundTrip_preservesData() {
        val original = Worker(
            id = "w_rt", fullName = "Suresh Patel", mobileNumber = "9123456789",
            trade = "Electrician", dailyWagePaise = 85000L, experience = "8 years",
            joiningDate = 2000L, emergencyContact = null, address = null,
            status = "ACTIVE", createdAt = 3000L, updatedAt = 4000L
        )

        val result = original.toEntity().toDomain()

        assertEquals(original, result)
    }

    @Test
    fun supplierEntity_toDomain_splitsMaterialCategories() {
        val entity = SupplierEntity(
            id = "sup_1", name = "Ultratech", phone = "9811223344",
            gst = "07AABCT1234R1ZH", address = "Industrial Area",
            materialCategories = "Cement,Concrete,Sand",
            outstandingBalancePaise = 250000L, notes = null,
            createdAt = 1000L, updatedAt = 2000L
        )

        val domain = entity.toDomain()

        assertEquals(3, domain.materialCategories.size)
        assertEquals("Cement", domain.materialCategories[0])
        assertEquals("Concrete", domain.materialCategories[1])
        assertEquals("Sand", domain.materialCategories[2])
    }

    @Test
    fun supplier_toEntity_joinsMaterialCategories() {
        val domain = Supplier(
            id = "sup_2", name = "Tata Steel", phone = "9822334455",
            gst = null, address = "Sector 6",
            materialCategories = listOf("Steel", "Iron"),
            outstandingBalancePaise = 0L, notes = "Preferred",
            createdAt = 1000L, updatedAt = 2000L
        )

        val entity = domain.toEntity()

        assertEquals("Steel,Iron", entity.materialCategories)
    }

    @Test
    fun attendanceEntity_toDomain_mapsStatus() {
        val entity = AttendanceEntity(
            id = "att_1", workerId = "w_1", projectId = "proj_1",
            date = 1000L, status = "PRESENT", overtimeHours = 2.0,
            hoursWorked = 10.0, remarks = "Good work", createdAt = 1000L
        )

        val domain = entity.toDomain()

        assertEquals(AttendanceStatus.PRESENT, domain.status)
        assertEquals(2.0, domain.overtimeHours, 0.001)
        assertEquals(10.0, domain.hoursWorked, 0.001)
    }

    @Test
    fun attendance_toEntity_convertsStatusToString() {
        val domain = Attendance(
            id = "att_2", workerId = "w_2", projectId = null,
            date = 2000L, status = AttendanceStatus.HALF_DAY,
            overtimeHours = 0.0, hoursWorked = 4.0,
            remarks = null, createdAt = 2000L
        )

        val entity = domain.toEntity()

        assertEquals("HALF_DAY", entity.status)
        assertNull(entity.projectId)
    }

    @Test
    fun resourceAllocation_roundTrip_preservesData() {
        val original = ResourceAllocation(
            id = "alloc_1", projectId = "proj_1", date = 1000L,
            resourceType = ResourceType.MATERIAL, resourceId = "mat_1",
            quantity = 50.0, hours = null, costPaise = 1900000L,
            remarks = "For foundation", siteDiaryId = "diary_1",
            transactionId = null, createdAt = 1000L
        )

        val result = original.toEntity().toDomain()

        assertEquals(original, result)
    }

    @Test
    fun resourceAllocation_labourType_mapsCorrectly() {
        val entity = ResourceAllocationEntity(
            id = "alloc_2", projectId = "proj_1", date = 1000L,
            resourceType = "LABOUR", resourceId = "w_1",
            quantity = 1.0, hours = 8.0, costPaise = 75000L,
            remarks = null, siteDiaryId = null, transactionId = "txn_1",
            createdAt = 1000L
        )

        val domain = entity.toDomain()

        assertEquals(ResourceType.LABOUR, domain.resourceType)
        assertEquals(8.0, domain.hours!!, 0.001)
        assertEquals("txn_1", domain.transactionId)
    }
}
