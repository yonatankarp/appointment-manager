package com.yonatankarp.appointmentmanager.domain.events

import com.yonatankarp.appointmentmanager.domain.valueobjects.AppointmentDateTime
import com.yonatankarp.appointmentmanager.domain.valueobjects.AppointmentId
import com.yonatankarp.appointmentmanager.domain.valueobjects.ClientId
import java.time.Instant
import java.util.UUID

interface DomainEvent {
    val eventId: UUID
    val occurredAt: Instant
}

data class AppointmentScheduled(
    override val eventId: UUID = UUID.randomUUID(),
    override val occurredAt: Instant = Instant.now(),
    val appointmentId: AppointmentId,
    val clientId: ClientId,
    val dateTime: AppointmentDateTime,
) : DomainEvent

data class AppointmentCompleted(
    override val eventId: UUID = UUID.randomUUID(),
    override val occurredAt: Instant = Instant.now(),
    val appointmentId: AppointmentId,
    val clientId: ClientId,
) : DomainEvent

data class AppointmentCancelled(
    override val eventId: UUID = UUID.randomUUID(),
    override val occurredAt: Instant = Instant.now(),
    val appointmentId: AppointmentId,
    val clientId: ClientId,
    val reason: String?,
) : DomainEvent
