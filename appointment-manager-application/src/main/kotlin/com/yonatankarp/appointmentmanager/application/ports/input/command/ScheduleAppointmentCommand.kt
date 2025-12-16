package com.yonatankarp.appointmentmanager.application.ports.input.command

import com.yonatankarp.appointmentmanager.domain.valueobjects.ClientId
import com.yonatankarp.appointmentmanager.domain.valueobjects.Duration
import com.yonatankarp.appointmentmanager.domain.valueobjects.ServiceType
import java.time.LocalDateTime

data class ScheduleAppointmentCommand(
    val clientId: ClientId,
    val localDateTime: LocalDateTime,
    val duration: Duration,
    val serviceType: ServiceType,
)
