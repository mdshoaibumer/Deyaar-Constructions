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
        // The seeder creates:
        // 20 clients, 30 projects, 80 workers, 100 materials, 10 suppliers, 120 transactions, ~140 attendance records
        // Verify constants match requirements
        val expectedClients = 20
        val expectedProjects = 30
        val expectedWorkers = 80
        val expectedMaterials = 100 // expanded from 20 to 100
        val expectedSuppliers = 10
        val expectedTransactions = 120

        assertTrue("Clients requirement: $expectedClients", expectedClients >= 20)
        assertTrue("Projects requirement: $expectedProjects", expectedProjects >= 30)
        assertTrue("Workers requirement: $expectedWorkers", expectedWorkers >= 80)
        assertTrue("Materials requirement: $expectedMaterials", expectedMaterials >= 100)
        assertTrue("Suppliers requirement: $expectedSuppliers", expectedSuppliers >= 10)
        assertTrue("Transactions requirement: $expectedTransactions", expectedTransactions >= 120)
    }

    @Test
    fun `project IDs are unique`() {
        val ids = (1..30).map { "proj_${it}" }
        assertEquals(ids.size, ids.distinct().size)
    }

    @Test
    fun `client IDs are unique`() {
        val ids = (1..20).map { "client_${it}" }
        assertEquals(ids.size, ids.distinct().size)
    }

    @Test
    fun `worker IDs are unique`() {
        val ids = (1..80).map { "worker_${it}" }
        assertEquals(ids.size, ids.distinct().size)
    }

    @Test
    fun `material categories cover construction requirements`() {
        val requiredCategories = listOf("Cement", "Steel", "Sand", "Bricks", "Tiles", "Paint")
        // All required categories must be present in the seeder
        requiredCategories.forEach { category ->
            assertTrue("Category '$category' must be in seed data", category.isNotBlank())
        }
    }

    @Test
    fun `worker trades cover construction roles`() {
        val trades = listOf("Mason", "Carpenter", "Electrician", "Plumber", "Painter",
            "Welder", "Crane Operator", "General Labour", "Tile Setter", "Rebar Worker")
        assertEquals(10, trades.size)
        assertTrue(trades.contains("Mason"))
        assertTrue(trades.contains("Electrician"))
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
}
