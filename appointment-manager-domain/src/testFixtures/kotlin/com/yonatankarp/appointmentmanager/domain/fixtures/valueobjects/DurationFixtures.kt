package com.yonatankarp.appointmentmanager.domain.fixtures.valueobjects

import com.yonatankarp.appointmentmanager.domain.valueobjects.Duration

object DurationFixtures {
    fun duration(minutes: Int = 120) =
        (Duration ofMinutes minutes).getOrThrow()
}
