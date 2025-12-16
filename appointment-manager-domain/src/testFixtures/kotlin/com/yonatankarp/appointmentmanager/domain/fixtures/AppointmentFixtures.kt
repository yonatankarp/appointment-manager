package com.yonatankarp.appointmentmanager.domain.fixtures

import com.yonatankarp.appointmentmanager.domain.aggregates.Appointment
import com.yonatankarp.appointmentmanager.domain.fixtures.AppointmentDateTimeFixtures.berlinDateTime
import com.yonatankarp.appointmentmanager.domain.fixtures.ClientIdFixtures.clientId
import com.yonatankarp.appointmentmanager.domain.fixtures.DurationFixtures.duration
import com.yonatankarp.appointmentmanager.domain.valueobjects.AppointmentDateTime
import com.yonatankarp.appointmentmanager.domain.valueobjects.ClientId
import com.yonatankarp.appointmentmanager.domain.valueobjects.Duration
import java.time.ZonedDateTime

object AppointmentFixtures {
    fun scheduledAppointment(
        clientId: ClientId = clientId(),
        dateTime: AppointmentDateTime = berlinDateTime(),
        duration: Duration = duration(),
        serviceType: String = "Tattoo Session",
    ) = Appointment.schedule(
        clientId = clientId,
        dateTime = dateTime,
        duration = duration,
        serviceType = serviceType,
    ).getOrThrow().value

    fun cancelledAppointment(
        clientId: ClientId = clientId(),
        dateTime: AppointmentDateTime = berlinDateTime(),
        duration: Duration = duration(),
        serviceType: String = "Tattoo Session",
        reason: String? = "Client requested cancellation",
        cancelAt: ZonedDateTime = dateTime.value.minusDays(2),
    ) = scheduledAppointment(
        clientId = clientId,
        dateTime = dateTime,
        duration = duration,
        serviceType = serviceType,
    ).cancel(reason, cancelAt).getOrThrow().value
}
