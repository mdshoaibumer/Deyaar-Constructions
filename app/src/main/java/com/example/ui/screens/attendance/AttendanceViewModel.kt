package com.example.ui.screens.attendance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Attendance
import com.example.domain.model.AttendanceStatus
import com.example.domain.model.Worker
import com.example.domain.repository.ResourceRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.UUID

data class WorkerAttendanceRow(
    val worker: Worker,
    val attendance: Attendance?
)

data class AttendanceDailyUiState(
    val selectedDate: Long = getTodayStart(),
    val workers: List<Worker> = emptyList(),
    val attendanceRows: List<WorkerAttendanceRow> = emptyList(),
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val error: String? = null,
    val saveSuccess: Boolean = false,
    // Statistics
    val presentCount: Int = 0,
    val absentCount: Int = 0,
    val halfDayCount: Int = 0
)

sealed class AttendanceEvent {
    data class DateChanged(val date: Long) : AttendanceEvent()
    data class StatusChanged(val workerId: String, val status: AttendanceStatus) : AttendanceEvent()
    data class OvertimeChanged(val workerId: String, val hours: Double) : AttendanceEvent()
    data class RemarksChanged(val workerId: String, val remarks: String) : AttendanceEvent()
    object SaveAll : AttendanceEvent()
}

class AttendanceViewModel(
    private val repository: ResourceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AttendanceDailyUiState())
    val uiState: StateFlow<AttendanceDailyUiState> = _uiState.asStateFlow()

    // In-memory edits keyed by workerId
    private val editedAttendance = mutableMapOf<String, Attendance>()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // Combine workers list with attendance for selected date
            combine(
                repository.getAllWorkers(),
                repository.getAttendanceForDate(_uiState.value.selectedDate)
            ) { workers, attendanceList ->
                val activeWorkers = workers.filter { it.status == "ACTIVE" }
                val attendanceMap = attendanceList.associateBy { it.workerId }
                
                val rows = activeWorkers.map { worker ->
                    WorkerAttendanceRow(
                        worker = worker,
                        attendance = editedAttendance[worker.id] ?: attendanceMap[worker.id]
                    )
                }
                
                // Initialize edits from DB if not already edited
                activeWorkers.forEach { worker ->
                    if (!editedAttendance.containsKey(worker.id)) {
                        val existing = attendanceMap[worker.id]
                        if (existing != null) {
                            editedAttendance[worker.id] = existing
                        }
                    }
                }

                val present = rows.count { it.attendance?.status == AttendanceStatus.PRESENT }
                val absent = rows.count { it.attendance?.status == AttendanceStatus.ABSENT }
                val halfDay = rows.count { it.attendance?.status == AttendanceStatus.HALF_DAY }

                _uiState.update { state ->
                    state.copy(
                        workers = activeWorkers,
                        attendanceRows = rows,
                        isLoading = false,
                        presentCount = present,
                        absentCount = absent,
                        halfDayCount = halfDay
                    )
                }
            }.collect()
        }
    }

    fun onEvent(event: AttendanceEvent) {
        when (event) {
            is AttendanceEvent.DateChanged -> {
                editedAttendance.clear()
                _uiState.update { it.copy(selectedDate = event.date, isLoading = true, saveSuccess = false) }
                loadData()
            }
            is AttendanceEvent.StatusChanged -> {
                updateAttendanceField(event.workerId) { it.copy(status = event.status) }
            }
            is AttendanceEvent.OvertimeChanged -> {
                updateAttendanceField(event.workerId) { it.copy(overtimeHours = event.hours) }
            }
            is AttendanceEvent.RemarksChanged -> {
                updateAttendanceField(event.workerId) { it.copy(remarks = event.remarks.takeIf { r -> r.isNotBlank() }) }
            }
            is AttendanceEvent.SaveAll -> saveAll()
        }
    }

    private fun updateAttendanceField(workerId: String, transform: (Attendance) -> Attendance) {
        val existing = editedAttendance[workerId] ?: Attendance(
            id = UUID.randomUUID().toString(),
            workerId = workerId,
            projectId = null,
            date = _uiState.value.selectedDate,
            status = AttendanceStatus.PRESENT,
            overtimeHours = 0.0,
            hoursWorked = 8.0,
            remarks = null,
            createdAt = System.currentTimeMillis()
        )
        editedAttendance[workerId] = transform(existing)
        
        // Update the UI rows
        val currentRows = _uiState.value.attendanceRows
        val updatedRows = currentRows.map { row ->
            if (row.worker.id == workerId) {
                row.copy(attendance = editedAttendance[workerId])
            } else row
        }
        val present = updatedRows.count { it.attendance?.status == AttendanceStatus.PRESENT }
        val absent = updatedRows.count { it.attendance?.status == AttendanceStatus.ABSENT }
        val halfDay = updatedRows.count { it.attendance?.status == AttendanceStatus.HALF_DAY }
        
        _uiState.update { it.copy(
            attendanceRows = updatedRows,
            presentCount = present,
            absentCount = absent,
            halfDayCount = halfDay
        ) }
    }

    private fun saveAll() {
        val toSave = editedAttendance.values.toList()
        if (toSave.isEmpty()) {
            _uiState.update { it.copy(error = "No attendance marked") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }
            try {
                repository.saveAllAttendance(toSave)
                _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false, error = e.message ?: "Save failed") }
            }
        }
    }
}

class AttendanceViewModelFactory(
    private val repository: ResourceRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AttendanceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AttendanceViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

private fun getTodayStart(): Long {
    val cal = Calendar.getInstance()
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.timeInMillis
}
