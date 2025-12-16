package com.yonatankarp.appointmentmanager.application.ports.output

import com.yonatankarp.appointmentmanager.domain.aggregates.Appointment

interface SaveAppointmentPort {
    operator fun invoke(appointment: Appointment): Appointment
}
