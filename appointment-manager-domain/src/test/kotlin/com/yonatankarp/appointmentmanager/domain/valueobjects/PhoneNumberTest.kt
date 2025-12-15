package com.yonatankarp.appointmentmanager.domain.valueobjects

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class PhoneNumberTest {
    @Test
    fun `should create phone number from valid E164 format`() {
        // Given
        val phoneNumber = "+491234567890"

        // When
        val result = PhoneNumber.of(phoneNumber)

        // Then
        result.isSuccess shouldBe true
        result.getOrNull()?.value shouldBe "+491234567890"
    }

    @Test
    fun `should reject phone number without plus sign`() {
        // Given
        val phoneNumber = "491234567890"

        // When
        val result = PhoneNumber.of(phoneNumber)

        // Then
        result.isFailure shouldBe true
    }

    @Test
    fun `should reject phone number with non-digits after plus`() {
        // Given
        val phoneNumber = "+49abc567890"

        // When
        val result = PhoneNumber.of(phoneNumber)

        // Then
        result.isFailure shouldBe true
    }

    @Test
    fun `should reject phone number shorter than 7 digits`() {
        // Given
        val phoneNumber = "+12345"

        // When
        val result = PhoneNumber.of(phoneNumber)

        // Then
        result.isFailure shouldBe true
    }

    @Test
    fun `should reject phone number longer than 15 digits`() {
        // Given
        val phoneNumber = "+1234567890123456"

        // When
        val result = PhoneNumber.of(phoneNumber)

        // Then
        result.isFailure shouldBe true
    }

    @Test
    fun `should accept phone number with 7 digits`() {
        // Given
        val phoneNumber = "+1234567"

        // When
        val result = PhoneNumber.of(phoneNumber)

        // Then
        result.isSuccess shouldBe true
    }

    @Test
    fun `should accept phone number with 15 digits`() {
        // Given
        val phoneNumber = "+123456789012345"

        // When
        val result = PhoneNumber.of(phoneNumber)

        // Then
        result.isSuccess shouldBe true
    }
}
