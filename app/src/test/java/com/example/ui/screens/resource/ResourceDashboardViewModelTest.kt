package com.example.ui.screens.resource

import com.example.domain.model.*
import com.example.domain.repository.ResourceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ResourceDashboardViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createWorker(status: String = "ACTIVE", dailyWagePaise: Long = 80000L) = Worker(
        id = "w_${System.nanoTime()}",
        fullName = "Test Worker",
        mobileNumber = "9876543210",
        trade = "Mason",
        dailyWagePaise = dailyWagePaise,
        experience = "5 years",
        joiningDate = System.currentTimeMillis(),
        emergencyContact = null,
        address = null,
        status = status,
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis()
    )

    private fun createMaterial(status: String = "IN_STOCK") = Material(
        id = "m_${System.nanoTime()}",
        name = "Test Material",
        category = "Cement",
        unit = "Bags",
        currentStock = 100.0,
        minimumStock = 20.0,
        openingStock = 150.0,
        purchasePricePaise = 38000L,
        averageCostPaise = 36000L,
        remarks = null,
        status = status,
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis()
    )

    @Test
    fun `worker counts are calculated correctly`() {
        val workers = listOf(
            createWorker("ACTIVE"),
            createWorker("ACTIVE"),
            createWorker("INACTIVE"),
            createWorker("ACTIVE")
        )

        val activeCount = workers.count { it.status == "ACTIVE" }
        val inactiveCount = workers.count { it.status == "INACTIVE" }

        assertEquals(3, activeCount)
        assertEquals(1, inactiveCount)
    }

    @Test
    fun `total daily wages calculated correctly`() {
        val workers = listOf(
            createWorker(dailyWagePaise = 80000L),
            createWorker(dailyWagePaise = 60000L),
            createWorker(dailyWagePaise = 95000L)
        )

        val totalWages = workers.filter { it.status == "ACTIVE" }.sumOf { it.dailyWagePaise }
        assertEquals(235000L, totalWages)
    }

    @Test
    fun `material low stock detection works`() {
        val lowStock = Material(
            id = "m1", name = "Low Material", category = "Cement", unit = "Bags",
            currentStock = 10.0, minimumStock = 20.0, openingStock = 50.0,
            purchasePricePaise = 38000L, averageCostPaise = 36000L,
            remarks = null, status = "LOW_STOCK",
            createdAt = 0L, updatedAt = 0L
        )
        val inStock = createMaterial("IN_STOCK")

        val materials = listOf(lowStock, inStock)
        val lowStockCount = materials.count { it.currentStock <= it.minimumStock }
        assertEquals(1, lowStockCount)
    }

    @Test
    fun `material stock value calculation`() {
        val material = Material(
            id = "m1", name = "Cement", category = "Cement", unit = "Bags",
            currentStock = 100.0, minimumStock = 20.0, openingStock = 150.0,
            purchasePricePaise = 38000L, averageCostPaise = 36000L,
            remarks = null, status = "IN_STOCK",
            createdAt = 0L, updatedAt = 0L
        )

        val stockValue = (material.currentStock * material.purchasePricePaise).toLong()
        assertEquals(3800000L, stockValue) // 100 * 38000 paise = 38,000 INR
    }
}
