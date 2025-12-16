package com.yonatankarp.appointmentmanager.domain.services

import com.yonatankarp.appointmentmanager.domain.aggregates.Appointment
import com.yonatankarp.appointmentmanager.domain.fixtures.AppointmentDateTimeFixtures.berlinDateTime
import com.yonatankarp.appointmentmanager.domain.fixtures.AppointmentFixtures.scheduledAppointment
import com.yonatankarp.appointmentmanager.domain.fixtures.ClientIdFixtures.clientId
import com.yonatankarp.appointmentmanager.domain.fixtures.DurationFixtures.duration
import com.yonatankarp.appointmentmanager.domain.valueobjects.AppointmentDateTime
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.ZoneId

class AppointmentConflictDetectorTest {
    private val detector = AppointmentConflictDetector()

    @Test
    fun `should detect no conflicts when list is empty`() {
        // Given
        val newAppointment = scheduledAppointment()
        val existingAppointments = emptyList<Appointment>()

        // When
        val conflicts = detector.detectConflicts(newAppointment, existingAppointments)

        // Then
        conflicts.shouldBeEmpty()
    }

    @Test
    fun `should detect conflict with overlapping appointment`() {
        // Given
        val dateTime1 = berlinDateTime(LocalDateTime.of(2025, 12, 20, 14, 0))
        val dateTime2 = berlinDateTime(LocalDateTime.of(2025, 12, 20, 15, 0))
        val newAppointment = scheduledAppointment(
            clientId = clientId(),
            dateTime = dateTime1,
            duration = duration(),
        )
        val existing = scheduledAppointment(
            clientId = clientId(),
            dateTime = dateTime2,
            duration = duration(),
        )

        // When
        val conflicts = detector.detectConflicts(newAppointment, listOf(existing))

        // Then
        conflicts shouldHaveSize 1
        conflicts.first() shouldBe existing
    }

    @Test
    fun `should not detect conflict with adjacent appointment`() {
        // Given
        val dateTime1 = berlinDateTime(LocalDateTime.of(2025, 12, 20, 14, 0))
        val dateTime2 = berlinDateTime(LocalDateTime.of(2025, 12, 20, 16, 0))
        val newAppointment = scheduledAppointment(
            clientId = clientId(),
            dateTime = dateTime1,
            duration = duration(),
        )
        val existing = scheduledAppointment(
            clientId = clientId(),
            dateTime = dateTime2,
            duration = duration(),
        )

        // When
        val conflicts = detector.detectConflicts(newAppointment, listOf(existing))

        // Then
        conflicts.shouldBeEmpty()
    }
}
