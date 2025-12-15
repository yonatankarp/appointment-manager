package com.yonatankarp.appointmentmanager.domain.valueobjects

import java.util.UUID

@JvmInline
value class ClientId(val value: UUID) {
    companion object {
        fun new() = ClientId(UUID.randomUUID())
        fun of(value: UUID) = ClientId(value)
    }
}
