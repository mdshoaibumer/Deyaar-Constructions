package com.example.core.validation

import com.example.core.error.AppError
import java.util.regex.Pattern

object ValidationFramework {

    private val PHONE_PATTERN = Pattern.compile("^[+]?[0-9]{10,14}\$")

    fun validateName(name: String): String? {
        if (name.isBlank()) return "Name cannot be empty"
        if (name.length < 2) return "Name must be at least 2 characters long"
        return null
    }

    fun validatePhone(phone: String): String? {
        if (phone.isBlank()) return "Phone number cannot be empty"
        if (!PHONE_PATTERN.matcher(phone).matches()) return "Invalid phone number format"
        return null
    }

    fun validateAmount(amount: Double): String? {
        if (amount < 0) return "Amount cannot be negative"
        return null
    }

    fun validateQuantity(quantity: Int): String? {
        if (quantity < 0) return "Quantity cannot be negative"
        return null
    }

    /**
     * Throws [AppError.ValidationError] if any errors are present in the map.
     */
    fun checkErrors(errors: Map<String, String?>) {
        val nonNullErrors = errors.filterValues { it != null }.mapValues { it.value!! }
        if (nonNullErrors.isNotEmpty()) {
            throw AppError.ValidationError("Validation failed", nonNullErrors)
        }
    }
}
