package com.example.domain.model

import org.junit.Assert.*
import org.junit.Test

/**
 * Tests for ProjectFinancialAnalysis computed properties.
 */
class ProjectFinancialAnalysisTest {

    @Test
    fun currentBalance_incomeMinusExpense() {
        val analysis = createAnalysis(
            totalIncomePaise = 500000L,
            totalExpensePaise = 350000L
        )
        assertEquals(150000L, analysis.currentBalancePaise)
    }

    @Test
    fun currentBalance_canBeNegative() {
        val analysis = createAnalysis(
            totalIncomePaise = 100000L,
            totalExpensePaise = 200000L
        )
        assertEquals(-100000L, analysis.currentBalancePaise)
    }

    @Test
    fun estimatedProfit_withContractValue() {
        val analysis = createAnalysis(
            totalExpensePaise = 350000L,
            contractValuePaise = 500000L
        )
        assertEquals(150000L, analysis.estimatedProfitPaise)
    }

    @Test
    fun estimatedProfit_nullWhenNoContractValue() {
        val analysis = createAnalysis(contractValuePaise = null)
        assertNull(analysis.estimatedProfitPaise)
    }

    @Test
    fun profitMarginPercent_calculatesCorrectly() {
        val analysis = createAnalysis(
            totalIncomePaise = 1000000L,
            totalExpensePaise = 750000L
        )
        // Profit = 250000, margin = 250000/1000000 * 100 = 25%
        assertNotNull(analysis.profitMarginPercent)
        assertEquals(25.0, analysis.profitMarginPercent!!, 0.0001)
    }

    @Test
    fun profitMarginPercent_nullWhenZeroIncome() {
        val analysis = createAnalysis(totalIncomePaise = 0L)
        assertNull(analysis.profitMarginPercent)
    }

    @Test
    fun profitMarginPercent_canBeNegative() {
        val analysis = createAnalysis(
            totalIncomePaise = 100000L,
            totalExpensePaise = 150000L
        )
        // Profit = -50000, margin = -50000/100000 * 100 = -50%
        assertNotNull(analysis.profitMarginPercent)
        assertEquals(-50.0, analysis.profitMarginPercent!!, 0.0001)
    }

    private fun createAnalysis(
        totalIncomePaise: Long = 0L,
        totalExpensePaise: Long = 0L,
        materialCostPaise: Long = 0L,
        labourCostPaise: Long = 0L,
        otherExpensesPaise: Long = 0L,
        contractValuePaise: Long? = null,
        advanceReceivedPaise: Long = 0L,
        balancePayableByClientPaise: Long = 0L
    ) = ProjectFinancialAnalysis(
        projectId = "test-project",
        totalIncomePaise = totalIncomePaise,
        totalExpensePaise = totalExpensePaise,
        materialCostPaise = materialCostPaise,
        labourCostPaise = labourCostPaise,
        otherExpensesPaise = otherExpensesPaise,
        contractValuePaise = contractValuePaise,
        advanceReceivedPaise = advanceReceivedPaise,
        balancePayableByClientPaise = balancePayableByClientPaise
    )
}
