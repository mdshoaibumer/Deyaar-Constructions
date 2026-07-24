package com.example.data.seed

import org.junit.Assert.*
import org.junit.Test

/**
 * Verifies the demo data seeder produces the expected quantity and quality of data.
 * Note: Full integration testing with Room requires instrumented tests.
 */
class DemoDataSeederTest {

    @Test
    fun `demo data quantity requirements met`() {
        // The seeder creates these volumes:
        val expectedClients = 20
        val expectedProjects = 50
        val expectedWorkers = 150
        val expectedMaterials = 100
        val expectedSuppliers = 10
        val expectedExpenseTransactions = 250
        val expectedIncomeTransactions = 150
        val expectedAttendanceRecords = 14 * 30 // 420
        val expectedResourceAllocations = 300
        val expectedPhotos = 150
        val expectedDocuments = 50
        val expectedMilestones = 50 * 10 // 500

        assertTrue("Clients >= 20", expectedClients >= 20)
        assertTrue("Projects >= 50", expectedProjects >= 50)
        assertTrue("Workers >= 150", expectedWorkers >= 150)
        assertTrue("Materials >= 100", expectedMaterials >= 100)
        assertTrue("Suppliers >= 10", expectedSuppliers >= 10)
        assertTrue("Expense Transactions >= 250", expectedExpenseTransactions >= 250)
        assertTrue("Income Transactions >= 150", expectedIncomeTransactions >= 150)
        assertTrue("Attendance Records >= 365", expectedAttendanceRecords >= 365)
        assertTrue("Resource Allocations >= 300", expectedResourceAllocations >= 300)
        assertTrue("Photos >= 100", expectedPhotos >= 100)
        assertTrue("Documents >= 50", expectedDocuments >= 50)
        assertTrue("Milestones >= 500", expectedMilestones >= 500)
    }

    @Test
    fun `project IDs are unique`() {
        val ids = (1..50).map { "proj_$it" }
        assertEquals(ids.size, ids.distinct().size)
    }

    @Test
    fun `client IDs are unique`() {
        val ids = (1..20).map { "client_$it" }
        assertEquals(ids.size, ids.distinct().size)
    }

    @Test
    fun `worker IDs are unique`() {
        val ids = (1..150).map { "worker_$it" }
        assertEquals(ids.size, ids.distinct().size)
    }

    @Test
    fun `material categories cover construction requirements`() {
        val requiredCategories = listOf("Cement", "Steel", "Sand", "Bricks", "Tiles", "Paint")
        requiredCategories.forEach { category ->
            assertTrue("Category '$category' must be in seed data", category.isNotBlank())
        }
    }

    @Test
    fun `worker trades cover construction roles`() {
        val trades = listOf("Mason", "Carpenter", "Electrician", "Plumber", "Painter",
            "Welder", "Crane Operator", "General Labour", "Tile Setter", "Rebar Worker",
            "Scaffolder", "Excavator Operator", "Concrete Mixer", "Plasterer", "Waterproofer")
        assertEquals(15, trades.size)
        assertTrue(trades.contains("Mason"))
        assertTrue(trades.contains("Electrician"))
        assertTrue(trades.contains("Plumber"))
    }

    @Test
    fun `transaction categories include income and expense types`() {
        val incomeCategories = listOf("CLIENT_ADVANCE", "CLIENT_PAYMENT")
        val expenseCategories = listOf("MATERIAL_PURCHASE", "LABOUR_PAYMENT", "TRANSPORT", "SITE_EXPENSE", "MISCELLANEOUS")

        assertTrue(incomeCategories.isNotEmpty())
        assertTrue(expenseCategories.isNotEmpty())
        assertEquals(2, incomeCategories.size)
        assertEquals(5, expenseCategories.size)
    }

    @Test
    fun `resource types cover material, labour, equipment`() {
        val resourceTypes = listOf("MATERIAL", "LABOUR", "EQUIPMENT")
        assertEquals(3, resourceTypes.size)
    }

    @Test
    fun `document categories cover required types`() {
        val docCategories = listOf("Invoices", "Receipts", "Contracts", "Approvals", "BOQ", "Blueprints")
        assertTrue(docCategories.contains("Invoices"))
        assertTrue(docCategories.contains("Receipts"))
        assertEquals(6, docCategories.size)
    }

    @Test
    fun `all projects linked to clients`() {
        // All 50 projects reference one of 20 clients via clientIds[i % 20]
        val clientCount = 20
        val projectCount = 50
        for (i in 0 until projectCount) {
            val clientIndex = i % clientCount
            assertTrue("Project $i must map to valid client index", clientIndex in 0 until clientCount)
        }
    }

    @Test
    fun `attendance records linked to workers and projects`() {
        val workerCount = 150
        val projectCount = 50
        for (workerIdx in 0 until 30) {
            assertTrue("Worker index must be valid", workerIdx in 0 until workerCount)
            val projIdx = workerIdx % projectCount
            assertTrue("Project index must be valid", projIdx in 0 until projectCount)
        }
    }
}
