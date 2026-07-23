package com.example.core.util

import org.junit.Assert.*
import org.junit.Test
import java.util.Calendar

class DateUtilsTest {

    @Test
    fun `formatToDate produces expected format`() {
        // Jan 15, 2024 (UTC - actual output depends on timezone but format should be MMM dd, yyyy)
        val timestamp = 1705276800000L // Approx Jan 15, 2024
        val result = DateUtils.formatToDate(timestamp)
        assertTrue(result.contains("2024"))
        assertTrue(result.contains(","))
    }

    @Test
    fun `getRelativeTimeString returns Just now for recent timestamps`() {
        val now = System.currentTimeMillis()
        val result = DateUtils.getRelativeTimeString(now - 10_000) // 10 seconds ago
        assertEquals("Just now", result)
    }

    @Test
    fun `getRelativeTimeString returns minutes for recent past`() {
        val now = System.currentTimeMillis()
        val fiveMinAgo = now - 5 * 60 * 1000
        val result = DateUtils.getRelativeTimeString(fiveMinAgo)
        assertTrue(result.contains("m ago"))
    }

    @Test
    fun `getRelativeTimeString returns hours for same day`() {
        val now = System.currentTimeMillis()
        val threeHoursAgo = now - 3 * 60 * 60 * 1000
        val result = DateUtils.getRelativeTimeString(threeHoursAgo)
        assertTrue(result.contains("h ago"))
    }

    @Test
    fun `getRelativeTimeString returns Yesterday for 1 day ago`() {
        val now = System.currentTimeMillis()
        val yesterday = now - 25 * 60 * 60 * 1000 // 25 hours ago
        val result = DateUtils.getRelativeTimeString(yesterday)
        assertEquals("Yesterday", result)
    }

    @Test
    fun `getRelativeTimeString returns days for recent past`() {
        val now = System.currentTimeMillis()
        val threeDaysAgo = now - 3L * 24 * 60 * 60 * 1000
        val result = DateUtils.getRelativeTimeString(threeDaysAgo)
        assertTrue(result.contains("d ago"))
    }

    @Test
    fun `getRelativeTimeString returns formatted date for old timestamps`() {
        val now = System.currentTimeMillis()
        val twoWeeksAgo = now - 14L * 24 * 60 * 60 * 1000
        val result = DateUtils.getRelativeTimeString(twoWeeksAgo)
        // Should be a full date like "Jul 08, 2026"
        assertTrue(result.contains(","))
    }

    @Test
    fun `getGreeting returns time-appropriate greeting`() {
        val greeting = DateUtils.getGreeting()
        assertTrue(
            greeting == "Good morning" || greeting == "Good afternoon" ||
            greeting == "Good evening" || greeting.contains("Good")
        )
    }

    @Test
    fun `formatToDate does not crash on zero timestamp`() {
        val result = DateUtils.formatToDate(0L)
        assertNotNull(result)
        assertTrue(result.isNotBlank())
    }

    @Test
    fun `formatToTime does not crash on current timestamp`() {
        val result = DateUtils.formatToTime(System.currentTimeMillis())
        assertNotNull(result)
        assertTrue(result.isNotBlank())
    }
}
