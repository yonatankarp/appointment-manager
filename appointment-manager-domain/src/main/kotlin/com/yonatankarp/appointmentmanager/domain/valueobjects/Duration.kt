package com.yonatankarp.appointmentmanager.domain.valueobjects

@JvmInline
value class Duration(val minutes: Int) {
    companion object {
        infix fun ofMinutes(minutes: Int) = runCatching {
            require(minutes > 0) { "Duration must be positive" }
            require(minutes <= 480) { "Duration cannot exceed 8 hours (480 minutes)" }
            Duration(minutes)
        }
    }
}
