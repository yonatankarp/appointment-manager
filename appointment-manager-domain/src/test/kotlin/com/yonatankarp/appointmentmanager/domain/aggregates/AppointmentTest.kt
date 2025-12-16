package com.yonatankarp.appointmentmanager.domain.aggregates

import com.yonatankarp.appointmentmanager.domain.events.AppointmentCancelled
import com.yonatankarp.appointmentmanager.domain.events.AppointmentScheduled
import com.yonatankarp.appointmentmanager.domain.fixtures.aggregates.AppointmentFixtures.scheduledAppointment
import com.yonatankarp.appointmentmanager.domain.fixtures.valueobjects.AppointmentDateTimeFixtures.berlinDateTime
import com.yonatankarp.appointmentmanager.domain.fixtures.valueobjects.ClientIdFixtures.clientId
import com.yonatankarp.appointmentmanager.domain.fixtures.valueobjects.DurationFixtures.duration
import com.yonatankarp.appointmentmanager.domain.fixtures.valueobjects.ServiceTypeFixtures.tattoo
import com.yonatankarp.appointmentmanager.domain.valueobjects.AppointmentStatus
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

class AppointmentTest {
    @Test
    fun `should schedule appointment with valid data`() {
        // Given
        val clientId = clientId()
        val dateTime = berlinDateTime()
        val duration = duration()
        val serviceType = tattoo()

        // When
        val result = Appointment.schedule(clientId, dateTime, duration, serviceType)

        // Then
        result.isSuccess shouldBe true
        val effect = result.getOrNull()
        effect.shouldNotBeNull()
        effect.value.clientId shouldBe clientId
        effect.value.dateTime shouldBe dateTime
        effect.value.duration shouldBe duration
        effect.value.serviceType shouldBe serviceType
        effect.value.status shouldBe AppointmentStatus.SCHEDULED
        effect.event.shouldBeInstanceOf<AppointmentScheduled>()
        effect.event.appointmentId shouldBe effect.value.id
        effect.event.clientId shouldBe clientId
    }

    @Test
    fun `should detect completely overlapping appointments`() {
        // Given
        val dateTime = berlinDateTime(LocalDateTime.of(2025, 12, 20, 14, 0))
        val appointment1 = scheduledAppointment(dateTime = dateTime, duration = duration(120))
        val appointment2 = scheduledAppointment(dateTime = dateTime, duration = duration(120))

        // When
        val overlaps = appointment1.overlaps(appointment2)

        // Then
        overlaps shouldBe true
    }

    @Test
    fun `should detect partially overlapping appointments`() {
        // Given
        val dateTime1 = berlinDateTime(LocalDateTime.of(2025, 12, 20, 10, 0))
        val dateTime2 = berlinDateTime(LocalDateTime.of(2025, 12, 20, 10, 30))
        val appointment1 = scheduledAppointment(dateTime = dateTime1, duration = duration(60))
        val appointment2 = scheduledAppointment(dateTime = dateTime2, duration = duration(60))

        // When
        val overlaps = appointment1.overlaps(appointment2)

        // Then
        overlaps shouldBe true
    }

    @Test
    fun `should allow adjacent appointments`() {
        // Given
        val dateTime1 = berlinDateTime(LocalDateTime.of(2025, 12, 20, 10, 0))
        val dateTime2 = berlinDateTime(LocalDateTime.of(2025, 12, 20, 11, 0))
        val appointment1 = scheduledAppointment(dateTime = dateTime1, duration = duration(60))
        val appointment2 = scheduledAppointment(dateTime = dateTime2, duration = duration(60))

        // When
        val overlaps = appointment1.overlaps(appointment2)

        // Then
        overlaps shouldBe false
    }

    @Test
    fun `should reject cancellation with insufficient notice`() {
        // Given
        val berlinZone = ZoneId.of("Europe/Berlin")
        val appointmentTime = LocalDateTime.of(2025, 12, 21, 14, 0)
        val appointment = scheduledAppointment(dateTime = berlinDateTime(appointmentTime))
        val now = ZonedDateTime.of(LocalDateTime.of(2025, 12, 21, 13, 0), berlinZone)

        // When
        val result = appointment.cancel("Client requested", now)

        // Then
        result.isFailure shouldBe true
    }

    @Test
    fun `should allow cancellation at exactly 24 hours`() {
        // Given
        val berlinZone = ZoneId.of("Europe/Berlin")
        val appointmentTime = LocalDateTime.of(2025, 12, 21, 14, 0)
        val appointment = scheduledAppointment(dateTime = berlinDateTime(appointmentTime))
        val now = ZonedDateTime.of(LocalDateTime.of(2025, 12, 20, 14, 0), berlinZone)

        // When
        val result = appointment.cancel("Client requested", now)

        // Then
        result.isSuccess shouldBe true
    }

    @Test
    fun `should only allow cancellation of scheduled appointments`() {
        // Given
        val berlinZone = ZoneId.of("Europe/Berlin")
        val appointmentTime = LocalDateTime.of(2025, 12, 22, 14, 0)
        val appointment = scheduledAppointment(dateTime = berlinDateTime(appointmentTime))
        val now = ZonedDateTime.of(LocalDateTime.of(2025, 12, 20, 14, 0), berlinZone)
        val cancelled = appointment.cancel("First cancellation", now).getOrThrow().value

        // When
        val result = cancelled.cancel("Second attempt", now)

        // Then
        result.isFailure shouldBe true
    }

    @Test
    fun `should generate AppointmentCancelled event with all fields`() {
        // Given
        val berlinZone = ZoneId.of("Europe/Berlin")
        val appointmentTime = LocalDateTime.of(2025, 12, 22, 14, 0)
        val appointment = scheduledAppointment(dateTime = berlinDateTime(appointmentTime))
        val now = ZonedDateTime.of(LocalDateTime.of(2025, 12, 20, 14, 0), berlinZone)
        val reason = "Client requested cancellation"

        // When
        val result = appointment.cancel(reason, now)

        // Then
        result.isSuccess shouldBe true
        val effect = result.getOrNull()
        effect.shouldNotBeNull()
        effect.event.shouldBeInstanceOf<AppointmentCancelled>()
        effect.event.appointmentId shouldBe appointment.id
        effect.event.clientId shouldBe appointment.clientId
        effect.event.reason shouldBe reason
    }

    @Test
    fun `should update timestamps correctly on cancellation`() {
        // Given
        val berlinZone = ZoneId.of("Europe/Berlin")
        val appointmentTime = LocalDateTime.of(2025, 12, 22, 14, 0)
        val appointment = scheduledAppointment(dateTime = berlinDateTime(appointmentTime))
        val now = ZonedDateTime.of(LocalDateTime.of(2025, 12, 20, 14, 0), berlinZone)
        val originalUpdatedAt = appointment.updatedAt
        val originalCancelledAt = appointment.cancelledAt

        // When
        val result = appointment.cancel("Client requested", now)

        // Then
        result.isSuccess shouldBe true
        val effect = result.getOrNull()
        effect.shouldNotBeNull()
        effect.value.cancelledAt.shouldNotBeNull()
        originalCancelledAt.shouldBeNull()
        effect.value.updatedAt shouldBeGreaterThan originalUpdatedAt
    }
}
