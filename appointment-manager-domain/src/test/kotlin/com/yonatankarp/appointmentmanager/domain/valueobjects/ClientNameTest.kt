package com.yonatankarp.appointmentmanager.domain.valueobjects

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class ClientNameTest {
    @Test
    fun `should create client name with valid value`() {
        // Given
        val name = "John Doe"

        // When
        val result = ClientName.of(name)

        // Then
        result.isSuccess shouldBe true
        result.getOrNull()?.value shouldBe name
    }

    @Test
    fun `should reject blank name`() {
        // Given
        val name = "   "

        // When
        val result = ClientName.of(name)

        // Then
        result.isFailure shouldBe true
    }

    @Test
    fun `should reject empty name`() {
        // Given
        val name = ""

        // When
        val result = ClientName.of(name)

        // Then
        result.isFailure shouldBe true
    }
}
