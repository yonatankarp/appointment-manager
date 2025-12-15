package com.yonatankarp.appointmentmanager.domain.services

import com.yonatankarp.appointmentmanager.domain.aggregates.Appointment
import com.yonatankarp.appointmentmanager.domain.valueobjects.AppointmentDateTime
import com.yonatankarp.appointmentmanager.domain.valueobjects.ClientId
import com.yonatankarp.appointmentmanager.domain.valueobjects.Duration
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

class CancellationPolicyValidatorTest {
    private val berlinZone = ZoneId.of("Europe/Berlin")
    private val validator = CancellationPolicyValidator()

    @Test
    fun `should allow cancellation with more than 24 hours notice`() {
        // Given
        val clientId = ClientId.new()
        val appointmentTime = LocalDateTime.of(2025, 12, 22, 14, 0)
        val dateTime = AppointmentDateTime.of(appointmentTime, berlinZone)
        val duration = Duration ofMinutes 120
        val appointment = Appointment.schedule(clientId, dateTime, duration.getOrThrow(), "Session").getOrNull()
        appointment.shouldNotBeNull()
        val now = ZonedDateTime.of(LocalDateTime.of(2025, 12, 20, 13, 0), berlinZone)

        // When
        val result = validator.canCancel(appointment.value, now)

        // Then
        result.isSuccess shouldBe true
        result.getOrNull() shouldBe true
    }

    @Test
    fun `should reject cancellation within 24 hours`() {
        // Given
        val clientId = ClientId.new()
        val appointmentTime = LocalDateTime.of(2025, 12, 21, 14, 0)
        val dateTime = AppointmentDateTime.of(appointmentTime, berlinZone)
        val duration = Duration ofMinutes 120
        val appointment = Appointment.schedule(clientId, dateTime, duration.getOrThrow(), "Session").getOrNull()
        appointment.shouldNotBeNull()
        val now = ZonedDateTime.of(LocalDateTime.of(2025, 12, 20, 15, 0), berlinZone)

        // When
        val result = validator.canCancel(appointment.value, now)

        // Then
        result.isSuccess shouldBe true
        result.getOrNull() shouldBe false
    }

    @Test
    fun `should reject cancellation for non-scheduled appointment`() {
        // Given
        val clientId = ClientId.new()
        val appointmentTime = LocalDateTime.of(2025, 12, 22, 14, 0)
        val dateTime = AppointmentDateTime.of(appointmentTime, berlinZone)
        val duration = Duration ofMinutes 120
        val appointment = Appointment.schedule(clientId, dateTime, duration.getOrThrow(), "Session").getOrNull()
        appointment.shouldNotBeNull()
        val now = ZonedDateTime.of(LocalDateTime.of(2025, 12, 20, 13, 0), berlinZone)
        val cancelledEffect = appointment.value.cancel("Test", now).getOrNull()
        cancelledEffect.shouldNotBeNull()

        // When
        val result = validator.canCancel(cancelledEffect.value, now)

        // Then
        result.isSuccess shouldBe true
        result.getOrNull() shouldBe false
    }
}
