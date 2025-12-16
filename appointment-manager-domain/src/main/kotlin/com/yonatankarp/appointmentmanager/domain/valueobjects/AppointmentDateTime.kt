package com.yonatankarp.appointmentmanager.domain.valueobjects

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

@JvmInline
value class AppointmentDateTime(val value: ZonedDateTime) {
    operator fun plus(duration: Duration) =
        AppointmentDateTime(value.plusMinutes(duration.minutes.toLong()))

    operator fun compareTo(other: ZonedDateTime) = value.compareTo(other)

    fun toZonedDateTime() = value

    companion object {
        fun of(localDateTime: LocalDateTime, zoneId: ZoneId) =
            AppointmentDateTime(localDateTime.atZone(zoneId))

        fun of(zonedDateTime: ZonedDateTime) =
            AppointmentDateTime(zonedDateTime)

        fun now(zoneId: ZoneId) =
            AppointmentDateTime(ZonedDateTime.now(zoneId))
    }
}
