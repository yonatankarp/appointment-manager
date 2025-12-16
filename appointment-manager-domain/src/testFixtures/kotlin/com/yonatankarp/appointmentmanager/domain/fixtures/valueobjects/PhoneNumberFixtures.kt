package com.yonatankarp.appointmentmanager.domain.fixtures.valueobjects

import com.yonatankarp.appointmentmanager.domain.valueobjects.PhoneNumber

object PhoneNumberFixtures {
    fun phoneNumber(number: String = "+972501234567") =
        PhoneNumber.of(number).getOrThrow()

    fun israeliPhoneNumber() =
        PhoneNumber.of("+972501234567").getOrThrow()

    fun germanPhoneNumber() =
        PhoneNumber.of("+491234567890").getOrThrow()

    fun usPhoneNumber() =
        PhoneNumber.of("+11234567890").getOrThrow()
}
