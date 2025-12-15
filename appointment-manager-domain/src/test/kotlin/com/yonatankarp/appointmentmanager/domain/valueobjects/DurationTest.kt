package com.yonatankarp.appointmentmanager.domain.valueobjects

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class DurationTest {
    @Test
    fun `should create duration from valid minutes`() {
        // Given
        val minutes = 60

        // When
        val result = Duration ofMinutes minutes

        // Then
        result.isSuccess shouldBe true
        result.getOrNull()?.minutes shouldBe minutes
    }

    @Test
    fun `should reject negative duration`() {
        // Given
        val minutes = -10

        // When
        val result = Duration ofMinutes minutes

        // Then
        result.isFailure shouldBe true
    }

    @Test
    fun `should reject zero duration`() {
        // Given
        val minutes = 0

        // When
        val result = Duration ofMinutes minutes

        // Then
        result.isFailure shouldBe true
    }

    @Test
    fun `should reject duration exceeding 8 hours`() {
        // Given
        val minutes = 481

        // When
        val result = Duration ofMinutes minutes

        // Then
        result.isFailure shouldBe true
    }

    @Test
    fun `should accept duration of exactly 8 hours`() {
        // Given
        val minutes = 480

        // When
        val result = Duration ofMinutes minutes

        // Then
        result.isSuccess shouldBe true
        result.getOrNull()?.minutes shouldBe minutes
    }
}
