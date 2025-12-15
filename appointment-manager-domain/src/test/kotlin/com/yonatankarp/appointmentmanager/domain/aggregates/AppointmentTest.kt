package com.yonatankarp.appointmentmanager.domain.aggregates


import com.yonatankarp.appointmentmanager.domain.events.AppointmentScheduled
import com.yonatankarp.appointmentmanager.domain.valueobjects.AppointmentDateTime
import com.yonatankarp.appointmentmanager.domain.valueobjects.AppointmentStatus
import com.yonatankarp.appointmentmanager.domain.valueobjects.ClientId
import com.yonatankarp.appointmentmanager.domain.valueobjects.Duration
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.ZoneId

class AppointmentTest {
    private val berlinZone = ZoneId.of("Europe/Berlin")

    @Test
    fun `should schedule appointment with valid data`() {
        // Given
        val clientId = ClientId.new()
        val dateTime = AppointmentDateTime.of(
            LocalDateTime.of(2025, 12, 20, 14, 0),
            berlinZone
        )
        val duration = Duration ofMinutes 120
        val serviceType = "Tattoo Session"

        // When
        val result = Appointment.schedule(clientId, dateTime, duration.getOrThrow(), serviceType)

        // Then
        result.isSuccess shouldBe true
        val effect = result.getOrNull()
        effect.shouldNotBeNull()
        effect.value.clientId shouldBe clientId
        effect.value.dateTime shouldBe dateTime
        effect.value.duration shouldBe duration.getOrThrow()
        effect.value.serviceType shouldBe serviceType
        effect.value.status shouldBe AppointmentStatus.SCHEDULED
        effect.event.shouldBeInstanceOf<AppointmentScheduled>()
        effect.event.appointmentId shouldBe effect.value.id
        effect.event.clientId shouldBe clientId
    }
}
