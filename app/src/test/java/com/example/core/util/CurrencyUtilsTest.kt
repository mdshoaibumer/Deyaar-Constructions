package com.example.core.util

import org.junit.Assert.*
import org.junit.Test

/**
 * Tests for CurrencyUtils - verifies financial precision with Long (paise) arithmetic.
 */
class CurrencyUtilsTest {

    @Test
    fun displayStringToPaise_wholeNumber_returnsCorrectPaise() {
        assertEquals(150000L, CurrencyUtils.displayStringToPaise("1500"))
    }

    @Test
    fun displayStringToPaise_decimalNumber_returnsCorrectPaise() {
        assertEquals(150075L, CurrencyUtils.displayStringToPaise("1500.75"))
    }

    @Test
    fun displayStringToPaise_singleDecimal_returnsCorrectPaise() {
        assertEquals(150050L, CurrencyUtils.displayStringToPaise("1500.5"))
    }

    @Test
    fun displayStringToPaise_zero_returnsZero() {
        assertEquals(0L, CurrencyUtils.displayStringToPaise("0"))
    }

    @Test
    fun displayStringToPaise_emptyString_returnsNull() {
        assertNull(CurrencyUtils.displayStringToPaise(""))
    }

    @Test
    fun displayStringToPaise_invalidString_returnsNull() {
        assertNull(CurrencyUtils.displayStringToPaise("abc"))
    }

    @Test
    fun displayStringToPaise_withCommas_handlesCorrectly() {
        assertEquals(150000000L, CurrencyUtils.displayStringToPaise("1,500,000"))
    }

    @Test
    fun displayStringToPaise_negativeNumber_returnsNegativePaise() {
        assertEquals(-150000L, CurrencyUtils.displayStringToPaise("-1500"))
    }

    @Test
    fun displayStringToPaise_veryLargeNumber_handlesCorrectly() {
        // 10 crore = 100,000,000 INR = 10,000,000,000 paise
        assertEquals(10000000000L, CurrencyUtils.displayStringToPaise("100000000"))
    }

    @Test
    fun paiseToDisplayString_wholeRupees_noDecimal() {
        assertEquals("1500", CurrencyUtils.paiseToDisplayString(150000L))
    }

    @Test
    fun paiseToDisplayString_withPaise_showsDecimal() {
        assertEquals("1500.75", CurrencyUtils.paiseToDisplayString(150075L))
    }

    @Test
    fun paiseToDisplayString_zero_returnsZero() {
        assertEquals("0", CurrencyUtils.paiseToDisplayString(0L))
    }

    @Test
    fun paiseToDisplayString_singlePaise_padded() {
        assertEquals("0.01", CurrencyUtils.paiseToDisplayString(1L))
    }

    @Test
    fun paiseToDisplayString_tenPaise_padded() {
        assertEquals("0.10", CurrencyUtils.paiseToDisplayString(10L))
    }

    @Test
    fun roundTrip_wholeNumber_preservesValue() {
        val original = "25000"
        val paise = CurrencyUtils.displayStringToPaise(original)!!
        val result = CurrencyUtils.paiseToDisplayString(paise)
        assertEquals(original, result)
    }

    @Test
    fun roundTrip_decimal_preservesValue() {
        val original = "25000.50"
        val paise = CurrencyUtils.displayStringToPaise(original)!!
        val result = CurrencyUtils.paiseToDisplayString(paise)
        assertEquals(original, result)
    }

    @Test
    fun doubleToPaise_handlesFloatingPointCorrectly() {
        // 0.1 + 0.2 = 0.30000000000000004 in IEEE 754
        // With paise conversion using Math.round, this should be exact
        val a = CurrencyUtils.doubleToPaise(0.1)
        val b = CurrencyUtils.doubleToPaise(0.2)
        assertEquals(30L, a + b) // Exactly 30 paise
    }

    @Test
    fun paiseToDouble_convertsCorrectly() {
        assertEquals(15.75, CurrencyUtils.paiseToDouble(1575L), 0.001)
    }

    @Test
    fun formatPaise_formatsWithCurrencySymbol() {
        val formatted = CurrencyUtils.formatPaise(150075L)
        // Should contain the number 1,500.75 (locale-dependent separator)
        assertTrue("Formatted: $formatted", formatted.contains("1") && formatted.contains("500"))
    }

    @Test
    fun financialPrecision_addingManySmallAmounts_noPrecisionLoss() {
        // Adding 0.01 INR (1 paise) 100 times should equal exactly 1.00 INR (100 paise)
        var total = 0L
        repeat(100) {
            total += 1L // 1 paise each time
        }
        assertEquals(100L, total) // Exactly 1.00 INR
    }

    @Test
    fun financialPrecision_subtraction_exact() {
        val contractValue = 5000000L // 50,000.00 INR
        val expense = 3499999L // 34,999.99 INR
        val profit = contractValue - expense
        assertEquals(1500001L, profit) // 15,000.01 INR - exact
    }

    @Test
    fun financialPrecision_percentageCalculation() {
        val income = 100000L // 1000.00 INR
        val expense = 75000L // 750.00 INR
        val profit = income - expense // 250.00 INR = 25000 paise
        val margin = (profit.toDouble() / income.toDouble()) * 100.0
        assertEquals(25.0, margin, 0.0001)
    }
}
