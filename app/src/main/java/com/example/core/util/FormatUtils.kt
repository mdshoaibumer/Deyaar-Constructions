package com.example.core.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Currency
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Date formatting and relative time utilities.
 * Uses SimpleDateFormat for minSdk 24 compatibility.
 */
object DateUtils {

    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    private val dateTimeFormat = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault())

    fun formatToDate(timestamp: Long): String {
        return dateFormat.format(Date(timestamp))
    }

    fun formatToTime(timestamp: Long): String {
        return timeFormat.format(Date(timestamp))
    }

    fun formatToDateTime(timestamp: Long): String {
        return dateTimeFormat.format(Date(timestamp))
    }

    /**
     * Returns a human-friendly relative time string.
     * "Just now" < 1 min, "Xm ago" < 1h, "Xh ago" < 24h,
     * "Yesterday", "Xd ago" < 7d, then full date.
     */
    fun getRelativeTimeString(timestampMillis: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestampMillis

        if (diff < 0) return formatToDate(timestampMillis)

        val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
        val hours = TimeUnit.MILLISECONDS.toHours(diff)
        val days = TimeUnit.MILLISECONDS.toDays(diff)

        return when {
            minutes < 1 -> "Just now"
            minutes < 60 -> "${minutes}m ago"
            hours < 24 -> "${hours}h ago"
            days == 1L -> "Yesterday"
            days < 7 -> "${days}d ago"
            else -> formatToDate(timestampMillis)
        }
    }

    /**
     * Returns a time-of-day greeting.
     */
    fun getGreeting(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when {
            hour < 12 -> "Good morning"
            hour < 17 -> "Good afternoon"
            else -> "Good evening"
        }
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
