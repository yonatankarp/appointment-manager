package com.yonatankarp.appointmentmanager.application.usecases

import com.yonatankarp.appointmentmanager.application.ports.input.command.ScheduleAppointmentCommand
import com.yonatankarp.appointmentmanager.application.ports.output.FindAppointmentsInTimeRangePort
import com.yonatankarp.appointmentmanager.application.ports.output.FindClientByIdPort
import com.yonatankarp.appointmentmanager.application.ports.output.PublishDomainEventPort
import com.yonatankarp.appointmentmanager.application.ports.output.SaveAppointmentPort
import com.yonatankarp.appointmentmanager.application.usecases.ScheduleAppointmentUseCaseTest.Fixtures.appointmentDateTime
import com.yonatankarp.appointmentmanager.application.usecases.ScheduleAppointmentUseCaseTest.Fixtures.scheduleAppointmentCommand
import com.yonatankarp.appointmentmanager.domain.fixtures.aggregates.AppointmentFixtures.scheduledAppointment
import com.yonatankarp.appointmentmanager.domain.fixtures.entities.ClientFixtures.emailClient
import com.yonatankarp.appointmentmanager.domain.fixtures.shared.TimeFixtures.inTwoDays
import com.yonatankarp.appointmentmanager.domain.fixtures.shared.TimeFixtures.yesterday
import com.yonatankarp.appointmentmanager.domain.fixtures.valueobjects.ClientIdFixtures.clientId
import com.yonatankarp.appointmentmanager.domain.fixtures.valueobjects.DurationFixtures.duration
import com.yonatankarp.appointmentmanager.domain.fixtures.valueobjects.ServiceTypeFixtures.tattoo
import com.yonatankarp.appointmentmanager.domain.services.AppointmentConflictDetector
import com.yonatankarp.appointmentmanager.domain.valueobjects.AppointmentDateTime
import com.yonatankarp.appointmentmanager.domain.valueobjects.ClientId
import com.yonatankarp.appointmentmanager.domain.valueobjects.Duration
import com.yonatankarp.appointmentmanager.domain.valueobjects.ServiceType
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

class ScheduleAppointmentUseCaseTest {
    private val saveAppointment: SaveAppointmentPort = mockk()
    private val findClient: FindClientByIdPort = mockk()
    private val findAppointmentsInRange: FindAppointmentsInTimeRangePort = mockk()
    private val publishEvent: PublishDomainEventPort = mockk()
    private val conflictDetector = AppointmentConflictDetector()
    private val timezone = ZoneId.of("Europe/Berlin")

    @BeforeEach
    fun setup() {
        clearAllMocks()
    }

    @Test
    fun `should fail when client not found`() {
        // Given
        val useCase = createUseCase()
        val command = scheduleAppointmentCommand()
        every { findClient(command.clientId) } returns null

        // When
        val result = useCase execute command

        // Then
        result.isFailure shouldBe true
        result.exceptionOrNull()?.message shouldBe "Client not found: ${command.clientId}"
    }

    @Test
    fun `should fail when appointment is in the past`() {
        // Given
        val useCase = createUseCase()
        val command = scheduleAppointmentCommand(localDateTime = yesterday())
        every { findClient(command.clientId) } returns emailClient()

        // When
        val result = useCase execute command

        // Then
        result.isFailure shouldBe true
        result.exceptionOrNull()?.message shouldBe "Cannot schedule appointment in the past"
    }

    @Test
    fun `should fail when appointment overlaps with existing`() {
        // Given
        val useCase = createUseCase()
        val command = scheduleAppointmentCommand()
        val dateTime = appointmentDateTime(localDateTime = command.localDateTime, zoneId = timezone)
        val existingAppointment = scheduledAppointment(
            clientId = clientId(),
            dateTime = dateTime,
            duration = command.duration,
        )
        every { findClient(command.clientId) } returns emailClient()
        every { 
            findAppointmentsInRange(
                any(ZonedDateTime::class), 
                any(ZonedDateTime::class)
            ) 
        } returns listOf(existingAppointment)

        // When
        val result = useCase execute command

        // Then
        result.isFailure shouldBe true
        result.exceptionOrNull()?.message shouldBe "Appointment time conflicts with existing appointment"
    }

    @Test
    fun `should succeed when appointment time is adjacent`() {
        // Given
        val useCase = createUseCase()
        val command = scheduleAppointmentCommand()
        val dateTime = appointmentDateTime(localDateTime = command.localDateTime, zoneId = timezone)
        val adjacentAppointment = scheduledAppointment(
            clientId = clientId(),
            dateTime = dateTime + command.duration,
            duration = duration(),
        )
        every { findClient(command.clientId) } returns emailClient()
        every {
            findAppointmentsInRange(
                any(ZonedDateTime::class),
                any(ZonedDateTime::class)
            )
        } returns listOf(adjacentAppointment)
        every { saveAppointment(any()) } answers { firstArg() }
        every { publishEvent(any()) } returns Unit

        // When
        val result = useCase execute command

        // Then
        result.isSuccess shouldBe true
    }

    private fun createUseCase() = ScheduleAppointmentUseCase(
        saveAppointment = saveAppointment,
        findClient = findClient,
        findAppointmentsInRange = findAppointmentsInRange,
        publishEvent = publishEvent,
        conflictDetector = conflictDetector,
        timezone = timezone,
    )

    private object Fixtures {
        fun scheduleAppointmentCommand(
            clientId: ClientId = clientId(),
            localDateTime: LocalDateTime = inTwoDays(),
            duration: Duration = duration(),
            serviceType: ServiceType = tattoo(),
        ) = ScheduleAppointmentCommand(
            clientId = clientId,
            localDateTime = localDateTime,
            duration = duration,
            serviceType = serviceType,
        )

        fun appointmentDateTime(
            localDateTime: LocalDateTime = inTwoDays(),
            zoneId: ZoneId = ZoneId.of("Europe/Berlin"),
        ) = AppointmentDateTime.of(localDateTime, zoneId)
    }
}
