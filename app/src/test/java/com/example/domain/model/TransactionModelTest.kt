package com.example.domain.model

import org.junit.Assert.*
import org.junit.Test

class TransactionModelTest {

    @Test
    fun `TransactionType has all expected values`() {
        val types = TransactionType.entries
        assertEquals(3, types.size)
        assertTrue(types.contains(TransactionType.INCOME))
        assertTrue(types.contains(TransactionType.EXPENSE))
        assertTrue(types.contains(TransactionType.ADJUSTMENT))
    }

    @Test
    fun `TransactionCategory covers construction requirements`() {
        val categories = TransactionCategory.entries
        assertTrue(categories.contains(TransactionCategory.CLIENT_ADVANCE))
        assertTrue(categories.contains(TransactionCategory.CLIENT_PAYMENT))
        assertTrue(categories.contains(TransactionCategory.MATERIAL_PURCHASE))
        assertTrue(categories.contains(TransactionCategory.LABOUR_PAYMENT))
        assertTrue(categories.contains(TransactionCategory.TRANSPORT))
        assertTrue(categories.contains(TransactionCategory.MISCELLANEOUS))
        assertTrue(categories.size >= 10) // Should cover all construction expense types
    }

    @Test
    fun `PaymentMethod has all expected values`() {
        val methods = PaymentMethod.entries
        assertTrue(methods.contains(PaymentMethod.CASH))
        assertTrue(methods.contains(PaymentMethod.UPI))
        assertTrue(methods.contains(PaymentMethod.BANK_TRANSFER))
        assertTrue(methods.contains(PaymentMethod.CHEQUE))
        assertTrue(methods.size >= 5)
    }

    @Test
    fun `ProjectStatus has all required states`() {
        val statuses = ProjectStatus.entries
        assertTrue(statuses.contains(ProjectStatus.PLANNING))
        assertTrue(statuses.contains(ProjectStatus.ACTIVE))
        assertTrue(statuses.contains(ProjectStatus.ON_HOLD))
        assertTrue(statuses.contains(ProjectStatus.COMPLETED))
        assertTrue(statuses.contains(ProjectStatus.CANCELLED))
    }

    @Test
    fun `ProjectCategory covers construction types`() {
        val categories = ProjectCategory.entries
        assertTrue(categories.contains(ProjectCategory.HOUSE))
        assertTrue(categories.contains(ProjectCategory.VILLA))
        assertTrue(categories.contains(ProjectCategory.APARTMENT))
        assertTrue(categories.contains(ProjectCategory.COMMERCIAL))
        assertTrue(categories.contains(ProjectCategory.RENOVATION))
    }

    @Test
    fun `AttendanceStatus has all required values`() {
        val statuses = AttendanceStatus.entries
        assertEquals(3, statuses.size)
        assertTrue(statuses.contains(AttendanceStatus.PRESENT))
        assertTrue(statuses.contains(AttendanceStatus.ABSENT))
        assertTrue(statuses.contains(AttendanceStatus.HALF_DAY))
    }

    @Test
    fun `Client model contains required fields`() {
        val client = Client(
            id = "c1",
            name = "Test Client",
            phone = "9876543210",
            email = "test@email.com",
            address = "123 Street",
            notes = "VIP client",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        assertEquals("Test Client", client.name)
        assertEquals("9876543210", client.phone)
        assertTrue(client.isActive)
        assertFalse(client.isFavorite)
    }

    @Test
    fun `Project financial values use paise`() {
        val project = Project(
            id = "p1",
            projectNumber = "DEY-001",
            name = "Test Project",
            clientId = null,
            category = ProjectCategory.HOUSE,
            address = null,
            location = null,
            contractValuePaise = 5000000L, // 50,000 INR
            estimatedBudgetPaise = 4000000L, // 40,000 INR
            advanceReceivedPaise = 1500000L, // 15,000 INR
            expectedProfitPaise = 1000000L, // 10,000 INR
            startDate = null,
            expectedCompletionDate = null,
            actualCompletionDate = null,
            status = ProjectStatus.ACTIVE,
            priority = ProjectPriority.HIGH,
            engineerInCharge = null,
            notes = null,
            progress = 60,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        assertEquals(5000000L, project.contractValuePaise)
        assertEquals(60, project.progress)
    }

    @Test
    fun `Worker dailyWage is in paise`() {
        val worker = Worker(
            id = "w1",
            fullName = "Raju Kumar",
            mobileNumber = "9876543210",
            trade = "Mason",
            dailyWagePaise = 80000L, // 800 INR
            experience = "5 years",
            joiningDate = System.currentTimeMillis(),
            emergencyContact = null,
            address = null,
            status = "ACTIVE",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        assertEquals(80000L, worker.dailyWagePaise) // 800.00 INR
    }
}
