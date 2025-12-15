package com.yonatankarp.appointmentmanager.domain.valueobjects

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class EmailAddressTest {
    @Test
    fun `should create email address from valid format`() {
        // Given
        val email = "user@example.com"

        // When
        val result = EmailAddress.of(email)

        // Then
        result.isSuccess shouldBe true
        result.getOrNull()?.value shouldBe "user@example.com"
    }

    @Test
    fun `should reject email without at symbol`() {
        // Given
        val email = "userexample.com"

        // When
        val result = EmailAddress.of(email)

        // Then
        result.isFailure shouldBe true
    }

    @Test
    fun `should reject email without local part`() {
        // Given
        val email = "@example.com"

        // When
        val result = EmailAddress.of(email)

        // Then
        result.isFailure shouldBe true
    }

    @Test
    fun `should reject email without domain`() {
        // Given
        val email = "user@"

        // When
        val result = EmailAddress.of(email)

        // Then
        result.isFailure shouldBe true
    }

    @Test
    fun `should accept valid email with subdomain`() {
        // Given
        val email = "user@mail.example.com"

        // When
        val result = EmailAddress.of(email)

        // Then
        result.isSuccess shouldBe true
    }
}
