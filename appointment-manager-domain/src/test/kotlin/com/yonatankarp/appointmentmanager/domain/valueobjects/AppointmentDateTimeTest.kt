package com.yonatankarp.appointmentmanager.domain.valueobjects

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.ZoneId

class AppointmentDateTimeTest {
    private val berlinZone = ZoneId.of("Europe/Berlin")

    @Test
    fun `should create appointment date time in Berlin timezone`() {
        // Given
        val localDateTime = LocalDateTime.of(2025, 12, 15, 14, 30)

        // When
        val appointmentDateTime = AppointmentDateTime.of(localDateTime, berlinZone)

        // Then
        appointmentDateTime.value.zone shouldBe berlinZone
        appointmentDateTime.value.toLocalDateTime() shouldBe localDateTime
    }
}
