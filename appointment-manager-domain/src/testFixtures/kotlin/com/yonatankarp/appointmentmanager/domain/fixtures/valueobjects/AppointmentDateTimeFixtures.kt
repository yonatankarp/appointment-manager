package com.yonatankarp.appointmentmanager.domain.fixtures.valueobjects

import com.yonatankarp.appointmentmanager.domain.valueobjects.AppointmentDateTime
import java.time.LocalDateTime
import java.time.ZoneId

object AppointmentDateTimeFixtures {
    fun berlinDateTime(dateTime: LocalDateTime = LocalDateTime.now().plusDays(7)) =
        AppointmentDateTime.of(dateTime, ZoneId.of("Europe/Berlin"))
}
