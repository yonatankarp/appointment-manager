package com.yonatankarp.appointmentmanager.application.ports.output

import com.yonatankarp.appointmentmanager.domain.aggregates.Appointment
import java.time.ZonedDateTime

interface FindAppointmentsInTimeRangePort {
    operator fun invoke(start: ZonedDateTime, end: ZonedDateTime): List<Appointment>
}
