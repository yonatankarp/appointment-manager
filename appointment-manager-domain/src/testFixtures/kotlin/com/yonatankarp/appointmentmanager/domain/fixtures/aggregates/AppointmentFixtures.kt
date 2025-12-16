package com.yonatankarp.appointmentmanager.domain.fixtures.aggregates

import com.yonatankarp.appointmentmanager.domain.aggregates.Appointment
import com.yonatankarp.appointmentmanager.domain.fixtures.valueobjects.AppointmentDateTimeFixtures.berlinDateTime
import com.yonatankarp.appointmentmanager.domain.fixtures.valueobjects.ClientIdFixtures.clientId
import com.yonatankarp.appointmentmanager.domain.fixtures.valueobjects.DurationFixtures.duration
import com.yonatankarp.appointmentmanager.domain.fixtures.valueobjects.ServiceTypeFixtures.serviceType
import com.yonatankarp.appointmentmanager.domain.valueobjects.AppointmentDateTime
import com.yonatankarp.appointmentmanager.domain.valueobjects.ClientId
import com.yonatankarp.appointmentmanager.domain.valueobjects.Duration
import com.yonatankarp.appointmentmanager.domain.valueobjects.ServiceType
import java.time.ZonedDateTime

object AppointmentFixtures {
    fun scheduledAppointment(
        clientId: ClientId = clientId(),
        dateTime: AppointmentDateTime = berlinDateTime(),
        duration: Duration = duration(),
        serviceType: ServiceType = serviceType(),
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
        serviceType: ServiceType = serviceType(),
        reason: String? = "Client requested cancellation",
        cancelAt: ZonedDateTime = dateTime.value.minusDays(2),
    ) = scheduledAppointment(
        clientId = clientId,
        dateTime = dateTime,
        duration = duration,
        serviceType = serviceType,
    ).cancel(reason, cancelAt).getOrThrow().value
}
