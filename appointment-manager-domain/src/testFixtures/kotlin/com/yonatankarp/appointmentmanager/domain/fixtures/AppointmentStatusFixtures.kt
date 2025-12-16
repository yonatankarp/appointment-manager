package com.yonatankarp.appointmentmanager.domain.fixtures

import com.yonatankarp.appointmentmanager.domain.valueobjects.AppointmentStatus

object AppointmentStatusFixtures {
    fun status() = AppointmentStatus.SCHEDULED

    fun scheduled() = AppointmentStatus.SCHEDULED

    fun completed() = AppointmentStatus.COMPLETED

    fun cancelled() = AppointmentStatus.CANCELLED
}
