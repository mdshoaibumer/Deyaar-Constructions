package com.example.domain.usecase.client

object ValidationUtils {
    fun isValidEmail(email: String): Boolean {
        if (email.isBlank()) return true // Optional
        val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})".toRegex()
        return email.matches(emailRegex)
    }

    fun isValidPhone(phone: String): Boolean {
        if (phone.isBlank()) return false
        val phoneRegex = "^[0-9]{10}$".toRegex()
        return phone.matches(phoneRegex)
    }

    fun isValidGst(gst: String): Boolean {
        if (gst.isBlank()) return true // Optional
        val gstRegex = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$".toRegex()
        return gst.matches(gstRegex)
    }

    fun isValidPan(pan: String): Boolean {
        if (pan.isBlank()) return true // Optional
        val panRegex = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$".toRegex()
        return pan.matches(panRegex)
    }
}
