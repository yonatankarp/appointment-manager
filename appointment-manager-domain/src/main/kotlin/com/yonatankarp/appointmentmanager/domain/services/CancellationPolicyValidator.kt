package com.yonatankarp.appointmentmanager.domain.services

import com.yonatankarp.appointmentmanager.domain.aggregates.Appointment
import com.yonatankarp.appointmentmanager.domain.valueobjects.AppointmentStatus
import java.time.ZonedDateTime

class CancellationPolicyValidator {
    companion object {
        private const val CANCELLATION_NOTICE_HOURS = 24L
    }

    fun canCancel(appointment: Appointment, now: ZonedDateTime): Result<Boolean> = runCatching {
        if (appointment.status != AppointmentStatus.SCHEDULED) {
            return@runCatching false
        }

        appointment.dateTime.value.isAfter(now.plusHours(CANCELLATION_NOTICE_HOURS))
    }
}
