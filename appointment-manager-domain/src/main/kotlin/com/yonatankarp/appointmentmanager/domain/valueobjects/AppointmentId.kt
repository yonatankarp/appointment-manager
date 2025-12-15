package com.yonatankarp.appointmentmanager.domain.valueobjects

import java.util.UUID

@JvmInline
value class AppointmentId(val value: UUID) {
    companion object {
        fun new() = AppointmentId(UUID.randomUUID())
        fun of(value: UUID) = AppointmentId(value)
    }
}
