package com.example.core.validation

import org.junit.Assert.*
import org.junit.Test

class ValidationFrameworkTest {

    @Test
    fun `validateName returns null for valid name`() {
        assertNull(ValidationFramework.validateName("Rajesh Kumar"))
        assertNull(ValidationFramework.validateName("Al"))
    }

    @Test
    fun `validateName returns error for blank name`() {
        assertNotNull(ValidationFramework.validateName(""))
        assertNotNull(ValidationFramework.validateName("   "))
    }

    @Test
    fun `validateName returns error for single character`() {
        assertNotNull(ValidationFramework.validateName("A"))
    }

    @Test
    fun `validatePhone returns null for valid Indian phone`() {
        assertNull(ValidationFramework.validatePhone("9876543210"))
        assertNull(ValidationFramework.validatePhone("+919876543210"))
    }

    @Test
    fun `validatePhone returns error for invalid phone`() {
        assertNotNull(ValidationFramework.validatePhone(""))
        assertNotNull(ValidationFramework.validatePhone("12345"))
        assertNotNull(ValidationFramework.validatePhone("abcdefghij"))
    }

    @Test
    fun `validateAmount returns null for valid amount`() {
        assertNull(ValidationFramework.validateAmount(100.0))
        assertNull(ValidationFramework.validateAmount(0.0))
    }

    @Test
    fun `validateAmount returns error for negative amount`() {
        assertNotNull(ValidationFramework.validateAmount(-1.0))
        assertNotNull(ValidationFramework.validateAmount(-0.01))
    }

    @Test
    fun `validateQuantity returns null for valid quantity`() {
        assertNull(ValidationFramework.validateQuantity(100))
        assertNull(ValidationFramework.validateQuantity(0))
    }

    @Test
    fun `validateQuantity returns error for negative quantity`() {
        assertNotNull(ValidationFramework.validateQuantity(-1))
    }

    @Test
    fun `checkErrors does not throw when all values are null`() {
        // Should not throw
        ValidationFramework.checkErrors(mapOf("name" to null, "phone" to null))
    }

    @Test(expected = com.example.core.error.AppError.ValidationError::class)
    fun `checkErrors throws when errors present`() {
        ValidationFramework.checkErrors(mapOf("name" to "Name is required"))
    }
}
