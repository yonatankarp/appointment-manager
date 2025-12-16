package com.yonatankarp.appointmentmanager.application.usecases

import com.yonatankarp.appointmentmanager.application.ports.input.ScheduleAppointmentPort
import com.yonatankarp.appointmentmanager.application.ports.input.command.ScheduleAppointmentCommand
import com.yonatankarp.appointmentmanager.application.ports.output.FindAppointmentsInTimeRangePort
import com.yonatankarp.appointmentmanager.application.ports.output.FindClientByIdPort
import com.yonatankarp.appointmentmanager.application.ports.output.PublishDomainEventPort
import com.yonatankarp.appointmentmanager.application.ports.output.SaveAppointmentPort
import com.yonatankarp.appointmentmanager.domain.aggregates.Appointment
import com.yonatankarp.appointmentmanager.domain.services.AppointmentConflictDetector
import com.yonatankarp.appointmentmanager.domain.valueobjects.AppointmentDateTime
import java.time.ZoneId
import java.time.ZonedDateTime.now

class ScheduleAppointmentUseCase(
    private val saveAppointment: SaveAppointmentPort,
    private val findClient: FindClientByIdPort,
    private val findAppointmentsInRange: FindAppointmentsInTimeRangePort,
    private val publishEvent: PublishDomainEventPort,
    private val conflictDetector: AppointmentConflictDetector,
    private val timezone: ZoneId,
) : ScheduleAppointmentPort {
    override infix fun execute(command: ScheduleAppointmentCommand): Result<Appointment> = runCatching {
        requireNotNull(findClient(command.clientId)) { "Client not found: ${command.clientId}" }

        val appointmentDateTime = AppointmentDateTime.of(command.localDateTime, timezone)
        require(appointmentDateTime > now(timezone)) { "Cannot schedule appointment in the past" }

        val endDateTime = appointmentDateTime + command.duration
        val existingAppointments = findAppointmentsInRange(
            appointmentDateTime.toZonedDateTime(),
            endDateTime.toZonedDateTime()
        )

        val effect = Appointment.schedule(
            clientId = command.clientId,
            dateTime = appointmentDateTime,
            duration = command.duration,
            serviceType = command.serviceType,
        ).getOrThrow()

        val conflicts = conflictDetector.detectConflicts(effect.value, existingAppointments)
        require(conflicts.isEmpty()) { "Appointment time conflicts with existing appointment" }

        saveAppointment(effect.value).also { publishEvent(effect.event) }
    }
}
