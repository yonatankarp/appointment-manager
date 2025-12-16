package com.yonatankarp.appointmentmanager.domain.aggregates

import com.yonatankarp.appointmentmanager.domain.events.AppointmentScheduled
import com.yonatankarp.appointmentmanager.domain.fixtures.AppointmentDateTimeFixtures.berlinDateTime
import com.yonatankarp.appointmentmanager.domain.fixtures.ClientIdFixtures.clientId
import com.yonatankarp.appointmentmanager.domain.fixtures.DurationFixtures.duration
import com.yonatankarp.appointmentmanager.domain.valueobjects.AppointmentStatus
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.junit.jupiter.api.Test

class AppointmentTest {
    @Test
    fun `should schedule appointment with valid data`() {
        // Given
        val clientId = clientId()
        val dateTime = berlinDateTime()
        val duration = duration()
        val serviceType = "Tattoo Session"

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
}
