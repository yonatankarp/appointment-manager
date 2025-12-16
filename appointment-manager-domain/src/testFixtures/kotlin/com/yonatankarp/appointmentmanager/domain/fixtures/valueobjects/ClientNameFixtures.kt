package com.yonatankarp.appointmentmanager.domain.fixtures.valueobjects

import com.yonatankarp.appointmentmanager.domain.valueobjects.ClientName

object ClientNameFixtures {
    fun clientName(name: String) =
        ClientName.of(name).getOrThrow()

    fun johnDoe() = ClientName.of("John Doe").getOrThrow()

    fun janeSmith() = ClientName.of("Jane Smith").getOrThrow()

    fun bobArtist() = ClientName.of("Bob Artist").getOrThrow()

    fun aliceWonder() = ClientName.of("Alice Wonder").getOrThrow()
}
