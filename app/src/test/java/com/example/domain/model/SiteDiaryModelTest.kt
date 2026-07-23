package com.example.domain.model

import org.junit.Assert.*
import org.junit.Test

class SiteDiaryModelTest {

    @Test
    fun `LabourSummary totalWorkers is correctly calculated`() {
        val summary = LabourSummary(
            id = "ls1",
            siteDiaryId = "sd1",
            masons = 5,
            helpers = 10,
            carpenters = 3,
            steelWorkers = 4,
            painters = 2,
            electricians = 3,
            plumbers = 2,
            machineOperators = 1,
            other = 2,
            totalWagesPaise = 250000L,
            attendanceSummary = null
        )

        assertEquals(32, summary.totalWorkers)
    }

    @Test
    fun `LabourSummary totalWorkers is zero when all zero`() {
        val summary = LabourSummary(
            id = "ls2",
            siteDiaryId = "sd1",
            masons = 0, helpers = 0, carpenters = 0, steelWorkers = 0,
            painters = 0, electricians = 0, plumbers = 0, machineOperators = 0, other = 0,
            totalWagesPaise = 0L,
            attendanceSummary = null
        )

        assertEquals(0, summary.totalWorkers)
    }

    @Test
    fun `MaterialType enum values are correct`() {
        assertEquals(2, MaterialType.entries.size)
        assertTrue(MaterialType.entries.contains(MaterialType.RECEIVED))
        assertTrue(MaterialType.entries.contains(MaterialType.USED))
    }

    @Test
    fun `ExpenseCategory has display names`() {
        assertEquals("Transport", ExpenseCategory.TRANSPORT.displayName)
        assertEquals("Food", ExpenseCategory.FOOD.displayName)
        assertEquals("Equipment Rental", ExpenseCategory.EQUIPMENT_RENTAL.displayName)
    }

    @Test
    fun `IssueType covers construction scenarios`() {
        val types = IssueType.entries
        assertTrue(types.size >= 6)
        assertTrue(types.contains(IssueType.MATERIAL_DELAY))
        assertTrue(types.contains(IssueType.WEATHER_DELAY))
        assertTrue(types.contains(IssueType.WORKER_SHORTAGE))
    }

    @Test
    fun `SiteDiaryDetails aggregates all components`() {
        val diary = SiteDiary(
            id = "sd1", projectId = "p1", date = System.currentTimeMillis(),
            startTime = "08:00", endTime = "17:00", weather = "Sunny",
            temperature = "32C", overallProgress = 65, workSummary = "Good progress",
            engineerNotes = null, safetyObservations = null, nextDayPlan = null,
            createdAt = System.currentTimeMillis(), updatedAt = System.currentTimeMillis()
        )

        val details = SiteDiaryDetails(
            diary = diary,
            labourSummary = null,
            materials = emptyList(),
            expenses = emptyList(),
            workItems = emptyList(),
            issues = emptyList(),
            photos = emptyList()
        )

        assertEquals("sd1", details.diary.id)
        assertEquals(65, details.diary.overallProgress)
        assertNull(details.labourSummary)
        assertTrue(details.materials.isEmpty())
    }
}
