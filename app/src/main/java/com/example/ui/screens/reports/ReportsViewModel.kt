package com.example.ui.screens.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.core.util.CurrencyUtils
import com.example.domain.model.*
import com.example.domain.repository.ProjectRepository
import com.example.domain.repository.ResourceRepository
import com.example.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

enum class ReportType(val displayName: String) {
    MATERIAL_USAGE("Material Usage"),
    LABOUR_COST("Labour Cost"),
    PROJECT_EXPENSES("Project Expenses"),
    PROFIT_LOSS("Profit / Loss Summary"),
    DASHBOARD_SUMMARY("Dashboard Summary")
}

data class ReportFilter(
    val projectId: String? = null,
    val projectName: String? = null,
    val startDate: Long? = null,
    val endDate: Long? = null
)

data class ReportData(
    val title: String = "",
    val generatedAt: Long = System.currentTimeMillis(),
    val filter: ReportFilter = ReportFilter(),
    val rows: List<ReportRow> = emptyList(),
    val totalAmountPaise: Long = 0L,
    val summaryLines: List<String> = emptyList()
)

data class ReportRow(
    val label: String,
    val sublabel: String? = null,
    val amountPaise: Long = 0L,
    val quantity: String? = null
)

data class ReportsUiState(
    val selectedReportType: ReportType = ReportType.MATERIAL_USAGE,
    val projects: List<Project> = emptyList(),
    val filter: ReportFilter = ReportFilter(),
    val reportData: ReportData? = null,
    val isLoading: Boolean = false,
    val isGenerating: Boolean = false,
    val pdfPath: String? = null,
    val error: String? = null
)

sealed class ReportsEvent {
    data class SelectReportType(val type: ReportType) : ReportsEvent()
    data class SelectProject(val projectId: String?, val projectName: String?) : ReportsEvent()
    data class SelectStartDate(val date: Long?) : ReportsEvent()
    data class SelectEndDate(val date: Long?) : ReportsEvent()
    object GenerateReport : ReportsEvent()
    object ClearReport : ReportsEvent()
}

class ReportsViewModel(
    private val transactionRepository: TransactionRepository,
    private val projectRepository: ProjectRepository,
    private val resourceRepository: ResourceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()

    init {
        loadProjects()
    }

    private fun loadProjects() {
        viewModelScope.launch {
            projectRepository.getAllProjects().collect { projects ->
                _uiState.update { it.copy(projects = projects) }
            }
        }
    }

    fun onEvent(event: ReportsEvent) {
        when (event) {
            is ReportsEvent.SelectReportType -> _uiState.update { it.copy(selectedReportType = event.type, reportData = null) }
            is ReportsEvent.SelectProject -> _uiState.update {
                it.copy(filter = it.filter.copy(projectId = event.projectId, projectName = event.projectName), reportData = null)
            }
            is ReportsEvent.SelectStartDate -> _uiState.update {
                it.copy(filter = it.filter.copy(startDate = event.date), reportData = null)
            }
            is ReportsEvent.SelectEndDate -> _uiState.update {
                it.copy(filter = it.filter.copy(endDate = event.date), reportData = null)
            }
            is ReportsEvent.GenerateReport -> generateReport()
            is ReportsEvent.ClearReport -> _uiState.update { it.copy(reportData = null, pdfPath = null) }
        }
    }

    private fun generateReport() {
        viewModelScope.launch {
            _uiState.update { it.copy(isGenerating = true, error = null) }
            try {
                val reportData = when (_uiState.value.selectedReportType) {
                    ReportType.MATERIAL_USAGE -> generateMaterialUsageReport()
                    ReportType.LABOUR_COST -> generateLabourCostReport()
                    ReportType.PROJECT_EXPENSES -> generateProjectExpensesReport()
                    ReportType.PROFIT_LOSS -> generateProfitLossReport()
                    ReportType.DASHBOARD_SUMMARY -> generateDashboardSummaryReport()
                }
                _uiState.update { it.copy(reportData = reportData, isGenerating = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isGenerating = false, error = e.message ?: "Report generation failed") }
            }
        }
    }

    private suspend fun generateMaterialUsageReport(): ReportData {
        val materials = resourceRepository.getAllMaterials().first()
        val filter = _uiState.value.filter

        val rows = materials.map { material ->
            ReportRow(
                label = material.name,
                sublabel = "Category: ${material.category}",
                amountPaise = (material.currentStock * material.purchasePricePaise).toLong(),
                quantity = "Stock: ${material.currentStock} ${material.unit} | Min: ${material.minimumStock} ${material.unit}"
            )
        }

        val totalValue = rows.sumOf { it.amountPaise }
        val lowStockCount = materials.count { it.currentStock <= it.minimumStock }

        return ReportData(
            title = "Material Usage Report",
            filter = filter,
            rows = rows,
            totalAmountPaise = totalValue,
            summaryLines = listOf(
                "Total Materials: ${materials.size}",
                "Total Stock Value: ${CurrencyUtils.formatPaise(totalValue)}",
                "Low Stock Items: $lowStockCount"
            )
        )
    }

    private suspend fun generateLabourCostReport(): ReportData {
        val filter = _uiState.value.filter
        val transactions = getFilteredTransactions()
        val labourTransactions = transactions.filter { it.category == TransactionCategory.LABOUR_PAYMENT }

        val rows = labourTransactions.map { t ->
            ReportRow(
                label = t.description ?: "Labour Payment",
                sublabel = formatDate(t.date),
                amountPaise = t.amountPaise
            )
        }

        val totalLabourCost = labourTransactions.sumOf { it.amountPaise }
        val workers = resourceRepository.getAllWorkers().first()
        val totalDailyWages = workers.filter { it.status == "ACTIVE" }.sumOf { it.dailyWagePaise }

        return ReportData(
            title = "Labour Cost Report",
            filter = filter,
            rows = rows,
            totalAmountPaise = totalLabourCost,
            summaryLines = listOf(
                "Total Labour Cost: ${CurrencyUtils.formatPaise(totalLabourCost)}",
                "Total Payments: ${labourTransactions.size}",
                "Active Workers: ${workers.count { it.status == "ACTIVE" }}",
                "Daily Wage Liability: ${CurrencyUtils.formatPaise(totalDailyWages)}"
            )
        )
    }

    private suspend fun generateProjectExpensesReport(): ReportData {
        val filter = _uiState.value.filter
        val transactions = getFilteredTransactions()
        val expenses = transactions.filter { it.type == TransactionType.EXPENSE }

        // Group by category
        val grouped = expenses.groupBy { it.category }
        val rows = grouped.map { (category, txns) ->
            ReportRow(
                label = category.name.replace("_", " "),
                sublabel = "${txns.size} transactions",
                amountPaise = txns.sumOf { it.amountPaise }
            )
        }.sortedByDescending { it.amountPaise }

        val totalExpenses = expenses.sumOf { it.amountPaise }

        return ReportData(
            title = "Project Expenses Report",
            filter = filter,
            rows = rows,
            totalAmountPaise = totalExpenses,
            summaryLines = listOf(
                "Total Expenses: ${CurrencyUtils.formatPaise(totalExpenses)}",
                "Categories: ${grouped.size}",
                "Transactions: ${expenses.size}"
            )
        )
    }

    private suspend fun generateProfitLossReport(): ReportData {
        val filter = _uiState.value.filter
        val transactions = getFilteredTransactions()

        val totalIncome = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amountPaise }
        val totalExpense = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amountPaise }
        val netProfit = totalIncome - totalExpense

        val materialCost = transactions.filter { it.category == TransactionCategory.MATERIAL_PURCHASE }.sumOf { it.amountPaise }
        val labourCost = transactions.filter { it.category == TransactionCategory.LABOUR_PAYMENT }.sumOf { it.amountPaise }
        val transportCost = transactions.filter { it.category == TransactionCategory.TRANSPORT }.sumOf { it.amountPaise }
        val otherExpenses = totalExpense - materialCost - labourCost - transportCost

        val rows = listOf(
            ReportRow(label = "Total Income (Received)", amountPaise = totalIncome),
            ReportRow(label = "Material Costs", amountPaise = -materialCost),
            ReportRow(label = "Labour Costs", amountPaise = -labourCost),
            ReportRow(label = "Transport", amountPaise = -transportCost),
            ReportRow(label = "Other Expenses", amountPaise = -otherExpenses),
            ReportRow(label = "NET PROFIT / LOSS", amountPaise = netProfit)
        )

        val profitMargin = if (totalIncome > 0) (netProfit.toDouble() / totalIncome.toDouble() * 100) else 0.0

        return ReportData(
            title = "Profit / Loss Summary",
            filter = filter,
            rows = rows,
            totalAmountPaise = netProfit,
            summaryLines = listOf(
                "Total Income: ${CurrencyUtils.formatPaise(totalIncome)}",
                "Total Expenses: ${CurrencyUtils.formatPaise(totalExpense)}",
                "Net Profit: ${CurrencyUtils.formatPaise(netProfit)}",
                "Profit Margin: ${String.format("%.1f", profitMargin)}%"
            )
        )
    }

    private suspend fun generateDashboardSummaryReport(): ReportData {
        val projects = projectRepository.getAllProjects().first()
        val transactions = transactionRepository.getAllTransactions().first()
        val workers = resourceRepository.getAllWorkers().first()
        val materials = resourceRepository.getAllMaterials().first()

        val totalProjects = projects.size
        val activeProjects = projects.count { it.status == ProjectStatus.ACTIVE || it.status == ProjectStatus.PLANNING }
        val completedProjects = projects.count { it.status == ProjectStatus.COMPLETED }
        val totalContractValue = projects.sumOf { it.contractValuePaise ?: 0L }
        val totalIncome = transactions.filter { it.type == TransactionType.INCOME && !it.isDeleted }.sumOf { it.amountPaise }
        val totalExpense = transactions.filter { it.type == TransactionType.EXPENSE && !it.isDeleted }.sumOf { it.amountPaise }
        val pendingPayments = maxOf(0L, totalContractValue - totalIncome)

        val rows = listOf(
            ReportRow(label = "Total Projects", quantity = "$totalProjects"),
            ReportRow(label = "Active Projects", quantity = "$activeProjects"),
            ReportRow(label = "Completed Projects", quantity = "$completedProjects"),
            ReportRow(label = "Total Workers", quantity = "${workers.size}"),
            ReportRow(label = "Total Materials", quantity = "${materials.size}"),
            ReportRow(label = "Contract Value", amountPaise = totalContractValue),
            ReportRow(label = "Total Received", amountPaise = totalIncome),
            ReportRow(label = "Total Expenses", amountPaise = totalExpense),
            ReportRow(label = "Pending Payments", amountPaise = pendingPayments),
            ReportRow(label = "Net Profit", amountPaise = totalIncome - totalExpense)
        )

        return ReportData(
            title = "Dashboard Summary Report",
            rows = rows,
            totalAmountPaise = totalIncome - totalExpense,
            summaryLines = listOf(
                "Generated at: ${formatDate(System.currentTimeMillis())}",
                "Projects: $totalProjects (Active: $activeProjects, Completed: $completedProjects)",
                "Net Position: ${CurrencyUtils.formatPaise(totalIncome - totalExpense)}"
            )
        )
    }

    private suspend fun getFilteredTransactions(): List<Transaction> {
        val filter = _uiState.value.filter
        val allTransactions = if (filter.projectId != null) {
            transactionRepository.getTransactionsForProject(filter.projectId).first()
        } else {
            transactionRepository.getAllTransactions().first()
        }

        return allTransactions.filter { t ->
            val afterStart = filter.startDate == null || t.date >= filter.startDate
            val beforeEnd = filter.endDate == null || t.date <= filter.endDate
            !t.isDeleted && afterStart && beforeEnd
        }
    }

    fun setPdfPath(path: String) {
        _uiState.update { it.copy(pdfPath = path) }
    }

    private fun formatDate(millis: Long): String {
        val sdf = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(millis))
    }
}

class ReportsViewModelFactory(
    private val transactionRepository: TransactionRepository,
    private val projectRepository: ProjectRepository,
    private val resourceRepository: ResourceRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReportsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReportsViewModel(transactionRepository, projectRepository, resourceRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
