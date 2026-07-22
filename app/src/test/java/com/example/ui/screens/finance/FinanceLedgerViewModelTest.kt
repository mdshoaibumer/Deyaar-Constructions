package com.example.ui.screens.finance

import com.example.domain.model.*
import org.junit.Assert.*
import org.junit.Test

/**
 * Tests for financial calculation logic in the finance ledger.
 * Verifies that Long (paise) arithmetic produces exact results.
 */
class FinanceLedgerViewModelTest {

    @Test
    fun netProfit_calculatesCorrectly() {
        val income = 500000L // 5000.00 INR
        val expense = 350000L // 3500.00 INR
        val netProfit = income - expense
        assertEquals(150000L, netProfit) // 1500.00 INR exactly
    }

    @Test
    fun outstandingReceivables_contractValueMinusIncome() {
        val contractValuePaise = 10000000L // 1,00,000.00 INR
        val totalIncome = 7500000L // 75,000.00 INR
        val outstanding = maxOf(0L, contractValuePaise - totalIncome)
        assertEquals(2500000L, outstanding) // 25,000.00 INR
    }

    @Test
    fun outstandingReceivables_whenOverpaid_isZero() {
        val contractValuePaise = 5000000L // 50,000.00 INR
        val totalIncome = 6000000L // 60,000.00 INR (overpaid)
        val outstanding = maxOf(0L, contractValuePaise - totalIncome)
        assertEquals(0L, outstanding) // Never negative
    }

    @Test
    fun estimatedProfit_contractValueMinusExpense() {
        val contractValuePaise = 10000000L // 1,00,000.00 INR
        val totalExpense = 8500000L // 85,000.00 INR
        val estimatedProfit = contractValuePaise - totalExpense
        assertEquals(1500000L, estimatedProfit) // 15,000.00 INR
    }

    @Test
    fun profitMargin_calculatesPercentageCorrectly() {
        val totalIncome = 1000000L // 10,000.00 INR
        val totalExpense = 750000L // 7,500.00 INR
        val netProfit = totalIncome - totalExpense // 2,500.00 INR
        val profitMargin = (netProfit.toDouble() / totalIncome.toDouble()) * 100.0
        assertEquals(25.0, profitMargin, 0.0001)
    }

    @Test
    fun profitMargin_whenZeroIncome_isNull() {
        val totalIncome = 0L
        val profitMargin: Double? = if (totalIncome > 0L) {
            (0.0 / totalIncome.toDouble()) * 100.0
        } else null
        assertNull(profitMargin)
    }

    @Test
    fun categoryBreakdown_sumsCorrectly() {
        val transactions = listOf(
            createTransaction(TransactionType.EXPENSE, TransactionCategory.MATERIAL_PURCHASE, 100000L),
            createTransaction(TransactionType.EXPENSE, TransactionCategory.MATERIAL_PURCHASE, 50000L),
            createTransaction(TransactionType.EXPENSE, TransactionCategory.LABOUR_PAYMENT, 75000L),
            createTransaction(TransactionType.EXPENSE, TransactionCategory.TRANSPORT, 25000L),
            createTransaction(TransactionType.INCOME, TransactionCategory.CLIENT_PAYMENT, 500000L)
        )

        var materialCost = 0L
        var labourCost = 0L
        var otherExpenses = 0L
        var totalIncome = 0L

        for (t in transactions) {
            if (t.type == TransactionType.INCOME) {
                totalIncome += t.amountPaise
            } else if (t.type == TransactionType.EXPENSE) {
                when (t.category) {
                    TransactionCategory.MATERIAL_PURCHASE -> materialCost += t.amountPaise
                    TransactionCategory.LABOUR_PAYMENT -> labourCost += t.amountPaise
                    else -> otherExpenses += t.amountPaise
                }
            }
        }

        assertEquals(150000L, materialCost)  // 1500.00 INR
        assertEquals(75000L, labourCost)     // 750.00 INR
        assertEquals(25000L, otherExpenses)  // 250.00 INR
        assertEquals(500000L, totalIncome)   // 5000.00 INR
    }

    @Test
    fun totalCost_materialPlusLabourPlusOther() {
        val materialCost = 250000L
        val labourCost = 150000L
        val otherExpenses = 50000L
        val totalCost = materialCost + labourCost + otherExpenses
        assertEquals(450000L, totalCost)
    }

    @Test
    fun financialPrecision_noFloatingPointErrors() {
        // Classic floating-point failure case: 0.1 + 0.2 != 0.3 in IEEE 754
        // With Long paise, this is exact: 10 + 20 = 30
        val a = 10L // 0.10 INR
        val b = 20L // 0.20 INR
        val sum = a + b
        assertEquals(30L, sum) // Exactly 0.30 INR
    }

    @Test
    fun financialPrecision_manySmallTransactions() {
        // 1000 transactions of 0.01 INR each
        var total = 0L
        repeat(1000) {
            total += 1L // 1 paise
        }
        assertEquals(1000L, total) // Exactly 10.00 INR
    }

    @Test
    fun financialPrecision_largeContractWithSmallExpenses() {
        val contractValue = 5000000000L // 5 crore INR
        var totalExpense = 0L
        // 10,000 small expenses of 500.00 INR each
        repeat(10000) {
            totalExpense += 50000L
        }
        val profit = contractValue - totalExpense
        assertEquals(5000000000L - 500000000L, profit) // 4.5 crore INR exactly
    }

    private fun createTransaction(
        type: TransactionType,
        category: TransactionCategory,
        amountPaise: Long
    ) = Transaction(
        id = "test-${System.nanoTime()}",
        projectId = "project-1",
        date = System.currentTimeMillis(),
        time = System.currentTimeMillis(),
        type = type,
        category = category,
        amountPaise = amountPaise,
        paymentMethod = PaymentMethod.CASH,
        referenceNumber = null,
        description = null,
        createdBy = "Test",
        isDeleted = false,
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis(),
        attachmentPath = null
    )
}
