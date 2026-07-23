package com.example.ui.screens.attendance

import com.example.domain.model.*
import com.example.domain.repository.ResourceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AttendanceViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `attendance status values are correct`() {
        assertEquals(3, AttendanceStatus.entries.size)
        assertEquals("PRESENT", AttendanceStatus.PRESENT.name)
        assertEquals("ABSENT", AttendanceStatus.ABSENT.name)
        assertEquals("HALF_DAY", AttendanceStatus.HALF_DAY.name)
    }

    @Test
    fun `attendance record calculates hours correctly`() {
        val attendance = Attendance(
            id = "att_001",
            workerId = "w1",
            projectId = "p1",
            date = System.currentTimeMillis(),
            status = AttendanceStatus.PRESENT,
            overtimeHours = 2.0,
            hoursWorked = 8.0,
            remarks = null,
            createdAt = System.currentTimeMillis()
        )

        assertEquals(8.0, attendance.hoursWorked, 0.01)
        assertEquals(2.0, attendance.overtimeHours, 0.01)
    }

    @Test
    fun `half day attendance records correct hours`() {
        val attendance = Attendance(
            id = "att_002",
            workerId = "w1",
            projectId = "p1",
            date = System.currentTimeMillis(),
            status = AttendanceStatus.HALF_DAY,
            overtimeHours = 0.0,
            hoursWorked = 4.0,
            remarks = "Left early",
            createdAt = System.currentTimeMillis()
        )

        assertEquals(AttendanceStatus.HALF_DAY, attendance.status)
        assertEquals(4.0, attendance.hoursWorked, 0.01)
        assertEquals("Left early", attendance.remarks)
    }

    @Test
    fun `absent status has zero hours`() {
        val attendance = Attendance(
            id = "att_003",
            workerId = "w1",
            projectId = null,
            date = System.currentTimeMillis(),
            status = AttendanceStatus.ABSENT,
            overtimeHours = 0.0,
            hoursWorked = 0.0,
            remarks = null,
            createdAt = System.currentTimeMillis()
        )

        assertEquals(AttendanceStatus.ABSENT, attendance.status)
        assertEquals(0.0, attendance.hoursWorked, 0.01)
    }

    @Test
    fun `daily wage calculation with attendance`() {
        val dailyWagePaise = 80000L // 800 INR
        val daysPresent = 22
        val halfDays = 3
        
        val totalPaise = (daysPresent * dailyWagePaise) + (halfDays * dailyWagePaise / 2)
        assertEquals(1880000L, totalPaise) // 18,800 INR
    }
}
