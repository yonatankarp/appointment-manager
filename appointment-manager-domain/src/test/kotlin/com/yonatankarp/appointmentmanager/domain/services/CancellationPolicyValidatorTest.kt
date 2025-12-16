package com.yonatankarp.appointmentmanager.domain.services

import com.yonatankarp.appointmentmanager.domain.fixtures.AppointmentDateTimeFixtures.berlinDateTime
import com.yonatankarp.appointmentmanager.domain.fixtures.AppointmentFixtures.cancelledAppointment
import com.yonatankarp.appointmentmanager.domain.fixtures.AppointmentFixtures.scheduledAppointment
import com.yonatankarp.appointmentmanager.domain.fixtures.ClientIdFixtures.clientId
import com.yonatankarp.appointmentmanager.domain.fixtures.DurationFixtures.duration
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
        val appointmentTime = LocalDateTime.of(2025, 12, 22, 14, 0)
        val appointment = scheduledAppointment(
            clientId = clientId(),
            dateTime = berlinDateTime(appointmentTime),
            duration = duration(),
        )
        val now = ZonedDateTime.of(LocalDateTime.of(2025, 12, 20, 13, 0), berlinZone)

        // When
        val result = validator.canCancel(appointment, now)

        // Then
        result.isSuccess shouldBe true
        result.getOrNull() shouldBe true
    }

    @Test
    fun `should reject cancellation within 24 hours`() {
        // Given
        val appointmentTime = LocalDateTime.of(2025, 12, 21, 14, 0)
        val appointment = scheduledAppointment(
            clientId = clientId(),
            dateTime = berlinDateTime(appointmentTime),
            duration = duration(),
        )
        val now = ZonedDateTime.of(LocalDateTime.of(2025, 12, 20, 15, 0), berlinZone)

        // When
        val result = validator.canCancel(appointment, now)

        // Then
        result.isSuccess shouldBe true
        result.getOrNull() shouldBe false
    }

    @Test
    fun `should reject cancellation for non-scheduled appointment`() {
        // Given
        val appointmentTime = LocalDateTime.of(2025, 12, 22, 14, 0)
        val now = ZonedDateTime.of(LocalDateTime.of(2025, 12, 20, 13, 0), berlinZone)
        val cancelled = cancelledAppointment(
            clientId = clientId(),
            dateTime = berlinDateTime(appointmentTime),
            duration = duration(),
            cancelAt = now,
        )

        // When
        val result = validator.canCancel(cancelled, now)

        // Then
        result.isSuccess shouldBe true
        result.getOrNull() shouldBe false
    }
}
