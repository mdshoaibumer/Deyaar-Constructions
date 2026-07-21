package com.example.domain.usecase.client

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidationUtilsTest {

    @Test
    fun `isValidEmail returns true for valid email`() {
        assertTrue(ValidationUtils.isValidEmail("test@example.com"))
        assertTrue(ValidationUtils.isValidEmail("user.name+tag@domain.co.in"))
    }

    @Test
    fun `isValidEmail returns false for invalid email`() {
        assertFalse(ValidationUtils.isValidEmail("test@example"))
        assertFalse(ValidationUtils.isValidEmail("test.com"))
        assertFalse(ValidationUtils.isValidEmail("@example.com"))
    }

    @Test
    fun `isValidEmail returns true for blank email (optional)`() {
        assertTrue(ValidationUtils.isValidEmail(""))
    }

    @Test
    fun `isValidPhone returns true for valid 10 digit phone`() {
        assertTrue(ValidationUtils.isValidPhone("9876543210"))
    }

    @Test
    fun `isValidPhone returns false for invalid phone`() {
        assertFalse(ValidationUtils.isValidPhone("12345"))
        assertFalse(ValidationUtils.isValidPhone("98765432101"))
        assertFalse(ValidationUtils.isValidPhone("abcdefghij"))
        assertFalse(ValidationUtils.isValidPhone(""))
    }

    @Test
    fun `isValidGst returns true for valid GST`() {
        assertTrue(ValidationUtils.isValidGst("22AAAAA0000A1Z5"))
    }

    @Test
    fun `isValidGst returns false for invalid GST`() {
        assertFalse(ValidationUtils.isValidGst("22AAAAA0000A1Z"))
        assertFalse(ValidationUtils.isValidGst("22AAAA0000A1Z5"))
    }

    @Test
    fun `isValidGst returns true for blank GST (optional)`() {
        assertTrue(ValidationUtils.isValidGst(""))
    }

    @Test
    fun `isValidPan returns true for valid PAN`() {
        assertTrue(ValidationUtils.isValidPan("ABCDE1234F"))
    }

    @Test
    fun `isValidPan returns false for invalid PAN`() {
        assertFalse(ValidationUtils.isValidPan("ABCD1234F"))
        assertFalse(ValidationUtils.isValidPan("ABCDE123F"))
    }

    @Test
    fun `isValidPan returns true for blank PAN (optional)`() {
        assertTrue(ValidationUtils.isValidPan(""))
    }
}
