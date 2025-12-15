package com.yonatankarp.appointmentmanager.domain.valueobjects

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

@JvmInline
value class AppointmentDateTime(val value: ZonedDateTime) {
    companion object {
        fun of(localDateTime: LocalDateTime, zoneId: ZoneId) =
            AppointmentDateTime(localDateTime.atZone(zoneId))

        fun of(zonedDateTime: ZonedDateTime) =
            AppointmentDateTime(zonedDateTime)

        fun now(zoneId: ZoneId) =
            AppointmentDateTime(ZonedDateTime.now(zoneId))
    }
}
