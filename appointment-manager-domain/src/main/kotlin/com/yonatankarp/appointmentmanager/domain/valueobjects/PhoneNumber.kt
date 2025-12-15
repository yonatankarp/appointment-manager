package com.yonatankarp.appointmentmanager.domain.valueobjects

@JvmInline
value class PhoneNumber(val value: String) {
    companion object {
        private const val MIN_LENGTH = 7
        private const val MAX_LENGTH = 15

        fun of(value: String) = runCatching {
            require(value.startsWith("+")) { "Phone number must start with +" }
            val digits = value.substring(1)
            require(digits.all { it.isDigit() }) { "Phone number must contain only digits after +" }
            require(digits.length in MIN_LENGTH..MAX_LENGTH) { "Phone number must have between $MIN_LENGTH and $MAX_LENGTH digits" }
            PhoneNumber(value)
        }
    }
}
