package com.yonatankarp.appointmentmanager.application.ports.input

import com.yonatankarp.appointmentmanager.application.ports.input.command.ScheduleAppointmentCommand
import com.yonatankarp.appointmentmanager.domain.aggregates.Appointment

interface ScheduleAppointmentPort {
    infix fun execute(command: ScheduleAppointmentCommand): Result<Appointment>
}
