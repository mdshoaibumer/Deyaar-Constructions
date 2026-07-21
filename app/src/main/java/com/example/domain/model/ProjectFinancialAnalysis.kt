package com.example.domain.model

/**
 * Financial analysis for a project. All monetary values are in paise (1 INR = 100 paise)
 * for mathematical precision.
 */
data class ProjectFinancialAnalysis(
    val projectId: String,
    val totalIncomePaise: Long,
    val totalExpensePaise: Long,
    val materialCostPaise: Long,
    val labourCostPaise: Long,
    val otherExpensesPaise: Long,
    val contractValuePaise: Long?,
    val advanceReceivedPaise: Long,
    val balancePayableByClientPaise: Long
) {
    val currentBalancePaise: Long
        get() = totalIncomePaise - totalExpensePaise

    val estimatedProfitPaise: Long?
        get() = contractValuePaise?.let { it - totalExpensePaise }

    /**
     * Profit margin as a percentage (e.g., 25.5 means 25.5%).
     * Computed using Double only for the final display percentage, not for money.
     */
    val profitMarginPercent: Double?
        get() = if (totalIncomePaise > 0) {
            (currentBalancePaise.toDouble() / totalIncomePaise.toDouble()) * 100.0
        } else null
}
