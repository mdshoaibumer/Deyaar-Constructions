package com.example.domain.model

data class Project(
    val id: String,
    val projectNumber: String,
    val name: String,
    val clientId: String?,
    val category: ProjectCategory,
    val address: String?,
    val location: String?,
    val contractValuePaise: Long?, // Amount in paise for financial precision
    val estimatedBudgetPaise: Long?, // Amount in paise
    val advanceReceivedPaise: Long?, // Amount in paise
    val expectedProfitPaise: Long?, // Amount in paise
    val startDate: Long?,
    val expectedCompletionDate: Long?,
    val actualCompletionDate: Long?,
    val status: ProjectStatus,
    val priority: ProjectPriority,
    val engineerInCharge: String?,
    val notes: String?,
    val progress: Int = 0,
    val createdAt: Long,
    val updatedAt: Long
)

enum class ProjectCategory(val displayName: String) {
    HOUSE("House"),
    VILLA("Villa"),
    APARTMENT("Apartment"),
    COMMERCIAL("Commercial Building"),
    OFFICE("Office"),
    INDUSTRIAL("Industrial"),
    RENOVATION("Renovation"),
    INTERIOR("Interior"),
    OTHER("Other")
}

enum class ProjectStatus(val displayName: String) {
    PLANNING("Planning"),
    ACTIVE("Active"),
    ON_HOLD("On Hold"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled")
}

enum class ProjectPriority(val displayName: String) {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High"),
    URGENT("Urgent")
}

data class Milestone(
    val id: String,
    val projectId: String,
    val name: String,
    val isCompleted: Boolean,
    val completionDate: Long?,
    val notes: String?,
    val orderIndex: Int
)

data class ProjectTimelineEvent(
    val id: String,
    val projectId: String,
    val title: String,
    val description: String,
    val timestamp: Long,
    val type: TimelineEventType
)

enum class TimelineEventType {
    CREATED, EDITED, STATUS_CHANGED, MILESTONE_ADDED, CUSTOM
}
