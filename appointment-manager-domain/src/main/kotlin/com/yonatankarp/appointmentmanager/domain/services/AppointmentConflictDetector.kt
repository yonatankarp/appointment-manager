package com.yonatankarp.appointmentmanager.domain.services

import com.yonatankarp.appointmentmanager.domain.aggregates.Appointment

class AppointmentConflictDetector {
    fun detectConflicts(newAppointment: Appointment, existingAppointments: List<Appointment>): List<Appointment> =
        existingAppointments.filter { existing ->
            newAppointment.overlaps(existing)
        }
}
