package com.example.core.util

import java.text.NumberFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Currency
import java.util.Locale

object DateUtils {
    private val defaultZone = ZoneId.systemDefault()
    private val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy").withZone(defaultZone)
    private val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a").withZone(defaultZone)
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy - hh:mm a").withZone(defaultZone)

    fun formatToDate(timestamp: Long): String {
        return dateFormatter.format(Instant.ofEpochMilli(timestamp))
    }

    fun formatToTime(timestamp: Long): String {
        return timeFormatter.format(Instant.ofEpochMilli(timestamp))
    }

    fun formatToDateTime(timestamp: Long): String {
        return dateTimeFormatter.format(Instant.ofEpochMilli(timestamp))
    }
}

/**
 * Currency utilities for formatting and converting monetary amounts.
 * All internal monetary values are stored as Long in paise (1 INR = 100 paise)
 * to avoid floating-point precision errors.
 */
object CurrencyUtils {

    private const val PAISE_PER_UNIT = 100L

    /**
     * Formats an amount in paise to a localized currency string.
     * Example: 150075L -> "₹1,500.75" (for INR locale)
     */
    fun formatPaise(amountPaise: Long, currencyCode: String = "INR"): String {
        val majorUnits = amountPaise.toDouble() / PAISE_PER_UNIT.toDouble()
        val format = NumberFormat.getCurrencyInstance(Locale.Builder().setLanguage("en").setRegion("IN").build())
        format.currency = Currency.getInstance(currencyCode)
        return format.format(majorUnits)
    }

    /**
     * Legacy overload that accepts Double for backward compatibility with UI display.
     * Deprecated: prefer formatPaise(Long) for new code.
     */
    fun formatCurrency(amountPaise: Long, currencyCode: String = "INR"): String {
        return formatPaise(amountPaise, currencyCode)
    }

    /**
     * Converts a user-entered string (e.g. "1500.75") to paise (Long).
     * Returns null if the string is not a valid number.
     */
    fun displayStringToPaise(displayString: String): Long? {
        val cleaned = displayString.trim().replace(",", "")
        val doubleVal = cleaned.toDoubleOrNull() ?: return null
        return Math.round(doubleVal * PAISE_PER_UNIT)
    }

    /**
     * Converts paise to a plain decimal string suitable for display in an input field.
     * Example: 150075L -> "1500.75"
     */
    fun paiseToDisplayString(amountPaise: Long): String {
        val major = amountPaise / PAISE_PER_UNIT
        val minor = amountPaise % PAISE_PER_UNIT
        return if (minor == 0L) {
            major.toString()
        } else {
            "$major.${minor.toString().padStart(2, '0')}"
        }
    }

    /**
     * Converts a Double amount (in major currency units) to paise.
     * Use for migration/import scenarios only.
     */
    fun doubleToPaise(amount: Double): Long {
        return Math.round(amount * PAISE_PER_UNIT)
    }

    /**
     * Converts paise to Double (in major currency units).
     * Use only for display calculations (e.g., percentages).
     */
    fun paiseToDouble(amountPaise: Long): Double {
        return amountPaise.toDouble() / PAISE_PER_UNIT.toDouble()
    }
}
