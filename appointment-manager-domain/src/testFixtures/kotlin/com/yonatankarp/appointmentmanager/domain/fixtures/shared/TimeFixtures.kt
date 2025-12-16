package com.yonatankarp.appointmentmanager.domain.fixtures.shared

import java.time.LocalDateTime

object TimeFixtures {
    fun yesterday(): LocalDateTime = LocalDateTime.now().minusDays(1)
    fun tomorrow(): LocalDateTime = LocalDateTime.now().plusDays(1)
    fun inTwoDays(): LocalDateTime = LocalDateTime.now().plusDays(2)
    fun inAWeek(): LocalDateTime = LocalDateTime.now().plusWeeks(1)
}
