package com.yonatankarp.appointmentmanager.domain.aggregates

import com.yonatankarp.appointmentmanager.domain.events.AppointmentCancelled
import com.yonatankarp.appointmentmanager.domain.events.AppointmentScheduled
import com.yonatankarp.appointmentmanager.domain.valueobjects.AppointmentDateTime
import com.yonatankarp.appointmentmanager.domain.valueobjects.AppointmentId
import com.yonatankarp.appointmentmanager.domain.valueobjects.AppointmentStatus
import com.yonatankarp.appointmentmanager.domain.valueobjects.ClientId
import com.yonatankarp.appointmentmanager.domain.valueobjects.Duration
import com.yonatankarp.appointmentmanager.domain.valueobjects.Effect
import com.yonatankarp.appointmentmanager.domain.valueobjects.effect
import java.time.Instant
import java.time.ZonedDateTime

data class Appointment(
    val id: AppointmentId,
    val clientId: ClientId,
    val dateTime: AppointmentDateTime,
    val duration: Duration,
    val status: AppointmentStatus,
    val serviceType: String,
    val createdAt: Instant,
    val updatedAt: Instant,
    val cancelledAt: Instant?,
) {
    fun overlaps(other: Appointment): Boolean {
        if (this.status != AppointmentStatus.SCHEDULED || other.status != AppointmentStatus.SCHEDULED) {
            return false
        }

        val thisStart = this.dateTime.value
        val thisEnd = thisStart.plusMinutes(this.duration.minutes.toLong())
        val otherStart = other.dateTime.value
        val otherEnd = otherStart.plusMinutes(other.duration.minutes.toLong())

        return thisStart.isBefore(otherEnd) && thisEnd.isAfter(otherStart)
    }

    fun cancel(reason: String?, now: ZonedDateTime): Result<Effect<Appointment, AppointmentCancelled>> = runCatching {
        require(status == AppointmentStatus.SCHEDULED) { "Can only cancel scheduled appointments" }
        require(dateTime.value.isAfter(now.plusHours(24))) { "Cannot cancel appointment within 24 hours of scheduled time" }

        val cancelledAppointment = this.copy(
            status = AppointmentStatus.CANCELLED,
            cancelledAt = Instant.now(),
            updatedAt = Instant.now(),
        )

        val event = AppointmentCancelled(
            appointmentId = id,
            clientId = clientId,
            reason = reason,
        )

        effect(cancelledAppointment, event)
    }

    companion object {
        fun schedule(
            clientId: ClientId,
            dateTime: AppointmentDateTime,
            duration: Duration,
            serviceType: String,
        ): Result<Effect<Appointment, AppointmentScheduled>> = runCatching {
            require(serviceType.isNotBlank()) { "Service type cannot be blank" }

            val now = Instant.now()
            val appointment = Appointment(
                id = AppointmentId.new(),
                clientId = clientId,
                dateTime = dateTime,
                duration = duration,
                status = AppointmentStatus.SCHEDULED,
                serviceType = serviceType,
                createdAt = now,
                updatedAt = now,
                cancelledAt = null,
            )

            val event = AppointmentScheduled(
                appointmentId = appointment.id,
                clientId = clientId,
                dateTime = dateTime,
            )

            effect(appointment, event)
        }
    }
}
