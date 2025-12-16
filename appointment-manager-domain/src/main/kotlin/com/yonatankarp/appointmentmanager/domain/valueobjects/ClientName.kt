package com.yonatankarp.appointmentmanager.domain.valueobjects

@JvmInline
value class ClientName(val value: String) {
    companion object {
        fun of(value: String) = runCatching {
            require(value.isNotBlank()) { "Client name cannot be blank" }
            ClientName(value)
        }
    }
}
