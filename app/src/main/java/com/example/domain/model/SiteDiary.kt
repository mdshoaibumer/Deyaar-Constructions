package com.example.domain.model

data class SiteDiary(
    val id: String,
    val projectId: String,
    val date: Long,
    val startTime: String?,
    val endTime: String?,
    val weather: String?,
    val temperature: String?,
    val overallProgress: Int,
    val workSummary: String?,
    val engineerNotes: String?,
    val safetyObservations: String?,
    val nextDayPlan: String?,
    val createdAt: Long,
    val updatedAt: Long
)

data class LabourSummary(
    val id: String,
    val siteDiaryId: String,
    val masons: Int,
    val helpers: Int,
    val carpenters: Int,
    val steelWorkers: Int,
    val painters: Int,
    val electricians: Int,
    val plumbers: Int,
    val machineOperators: Int,
    val other: Int,
    val totalWagesPaise: Long, // Amount in paise for financial precision
    val attendanceSummary: String?
) {
    val totalWorkers: Int
        get() = masons + helpers + carpenters + steelWorkers + painters + electricians + plumbers + machineOperators + other
}

data class MaterialSummary(
    val id: String,
    val siteDiaryId: String,
    val materialName: String,
    val type: MaterialType, // RECEIVED, USED
    val supplier: String?,
    val quantity: Double,
    val unit: String,
    val remarks: String?,
    val lowStockWarning: Boolean
)

enum class MaterialType {
    RECEIVED, USED
}

data class ExpenseSummary(
    val id: String,
    val siteDiaryId: String,
    val category: ExpenseCategory,
    val amountPaise: Long, // Amount in paise for financial precision
    val remarks: String?
)

enum class ExpenseCategory(val displayName: String) {
    TRANSPORT("Transport"),
    FOOD("Food"),
    EQUIPMENT_RENTAL("Equipment Rental"),
    FUEL("Fuel"),
    MISCELLANEOUS("Miscellaneous"),
    OTHER("Other")
}

data class WorkItem(
    val id: String,
    val siteDiaryId: String,
    val description: String,
    val percentageComplete: Int,
    val remarks: String?
)

data class SiteIssue(
    val id: String,
    val siteDiaryId: String,
    val type: IssueType,
    val description: String,
    val resolved: Boolean
)

enum class IssueType(val displayName: String) {
    MATERIAL_DELAY("Material Delay"),
    WEATHER_DELAY("Weather Delay"),
    WORKER_SHORTAGE("Worker Shortage"),
    MACHINE_FAILURE("Machine Failure"),
    PAYMENT_DELAY("Payment Delay"),
    CLIENT_REQUEST("Client Request"),
    OTHER("Other")
}

data class SiteDiaryDetails(
    val diary: SiteDiary,
    val labourSummary: LabourSummary?,
    val materials: List<MaterialSummary>,
    val expenses: List<ExpenseSummary>,
    val workItems: List<WorkItem>,
    val issues: List<SiteIssue>,
    val photos: List<String> = emptyList() // Architecture only
)
