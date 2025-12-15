package com.yonatankarp.appointmentmanager.domain.valueobjects

@JvmInline
value class EmailAddress(val value: String) {
    companion object {
        private val EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()

        fun of(value: String) = runCatching {
            require(value.matches(EMAIL_REGEX)) { "Invalid email address format" }
            EmailAddress(value)
        }
    }
}
