package com.yonatankarp.appointmentmanager.domain.services

import com.yonatankarp.appointmentmanager.domain.aggregates.Appointment
import com.yonatankarp.appointmentmanager.domain.valueobjects.AppointmentDateTime
import com.yonatankarp.appointmentmanager.domain.valueobjects.ClientId
import com.yonatankarp.appointmentmanager.domain.valueobjects.Duration
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.ZoneId

class AppointmentConflictDetectorTest {
    private val berlinZone = ZoneId.of("Europe/Berlin")
    private val detector = AppointmentConflictDetector()

    @Test
    fun `should detect no conflicts when list is empty`() {
        // Given
        val clientId = ClientId.new()
        val dateTime = AppointmentDateTime.of(LocalDateTime.of(2025, 12, 20, 14, 0), berlinZone)
        val duration = Duration ofMinutes 120
        val newAppointment = Appointment.schedule(clientId, dateTime, duration.getOrThrow(), "Session").getOrNull()
        newAppointment.shouldNotBeNull()
        val existingAppointments = emptyList<Appointment>()

        // When
        val conflicts = detector.detectConflicts(newAppointment.value, existingAppointments)

        // Then
        conflicts.shouldBeEmpty()
    }

    @Test
    fun `should detect conflict with overlapping appointment`() {
        // Given
        val clientId = ClientId.new()
        val dateTime1 = AppointmentDateTime.of(LocalDateTime.of(2025, 12, 20, 14, 0), berlinZone)
        val dateTime2 = AppointmentDateTime.of(LocalDateTime.of(2025, 12, 20, 15, 0), berlinZone)
        val duration = Duration ofMinutes 120
        val newAppointment = Appointment.schedule(clientId, dateTime1, duration.getOrThrow(), "New").getOrNull()
        newAppointment.shouldNotBeNull()
        val existing = Appointment.schedule(clientId, dateTime2, duration.getOrThrow(), "Existing").getOrNull()
        existing.shouldNotBeNull()

        // When
        val conflicts = detector.detectConflicts(newAppointment.value, listOf(existing.value))

        // Then
        conflicts shouldHaveSize 1
        conflicts.first() shouldBe existing.value
    }

    @Test
    fun `should not detect conflict with adjacent appointment`() {
        // Given
        val clientId = ClientId.new()
        val dateTime1 = AppointmentDateTime.of(LocalDateTime.of(2025, 12, 20, 14, 0), berlinZone)
        val dateTime2 = AppointmentDateTime.of(LocalDateTime.of(2025, 12, 20, 16, 0), berlinZone)
        val duration = Duration ofMinutes 120
        val newAppointment = Appointment.schedule(clientId, dateTime1, duration.getOrThrow(), "New").getOrNull()
        newAppointment.shouldNotBeNull()
        val existing = Appointment.schedule(clientId, dateTime2, duration.getOrThrow(), "Existing").getOrNull()
        existing.shouldNotBeNull()

        // When
        val conflicts = detector.detectConflicts(newAppointment.value, listOf(existing.value))

        // Then
        conflicts.shouldBeEmpty()
    }
}
