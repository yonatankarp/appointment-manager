package com.yonatankarp.appointmentmanager.domain.fixtures

import com.yonatankarp.appointmentmanager.domain.valueobjects.EmailAddress

object EmailAddressFixtures {
    fun emailAddress(email: String = "test@example.com") =
        EmailAddress.of(email).getOrThrow()
}
