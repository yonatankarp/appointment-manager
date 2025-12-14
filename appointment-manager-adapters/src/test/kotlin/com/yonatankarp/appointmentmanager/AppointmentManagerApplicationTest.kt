package com.yonatankarp.appointmentmanager

import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Test

class AppointmentManagerApplicationTest {

    @Test
    fun `app should have a greeting`() {
        // Given
        val classUnderTest = AppointmentManagerApplication()

        // When
        val greeting = classUnderTest.greeting

        // Then
        greeting shouldNotBe null
    }
}
