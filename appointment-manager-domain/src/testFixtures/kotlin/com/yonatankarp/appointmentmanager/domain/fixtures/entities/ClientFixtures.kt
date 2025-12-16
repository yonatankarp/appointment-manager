package com.yonatankarp.appointmentmanager.domain.fixtures.entities

import com.yonatankarp.appointmentmanager.domain.entities.Client
import com.yonatankarp.appointmentmanager.domain.fixtures.valueobjects.ClientNameFixtures
import com.yonatankarp.appointmentmanager.domain.fixtures.valueobjects.ContactInformationFixtures
import com.yonatankarp.appointmentmanager.domain.fixtures.valueobjects.LanguageFixtures
import com.yonatankarp.appointmentmanager.domain.valueobjects.ClientName
import com.yonatankarp.appointmentmanager.domain.valueobjects.ContactInformation
import com.yonatankarp.appointmentmanager.domain.valueobjects.Language

object ClientFixtures {
    fun client(
        name: ClientName = ClientNameFixtures.johnDoe(),
        preferredLanguage: Language = LanguageFixtures.english(),
        contactInformation: ContactInformation = ContactInformationFixtures.emailContact(),
    ) = Client.create(
        name = name,
        preferredLanguage = preferredLanguage,
        contactInformation = contactInformation,
    ).getOrThrow()

    fun emailClient(
        name: ClientName = ClientNameFixtures.johnDoe(),
        preferredLanguage: Language = LanguageFixtures.english(),
        email: String = "test@example.com",
    ) = Client.create(
        name = name,
        preferredLanguage = preferredLanguage,
        contactInformation = ContactInformationFixtures.emailContact(email),
    ).getOrThrow()

    fun whatsAppClient(
        name: ClientName = ClientNameFixtures.johnDoe(),
        preferredLanguage: Language = LanguageFixtures.english(),
        phoneNumber: String = "+972501234567",
    ) = Client.create(
        name = name,
        preferredLanguage = preferredLanguage,
        contactInformation = ContactInformationFixtures.whatsAppContact(phoneNumber),
    ).getOrThrow()

    fun instagramClient(
        name: ClientName = ClientNameFixtures.johnDoe(),
        preferredLanguage: Language = LanguageFixtures.english(),
        username: String = "test_user",
    ) = Client.create(
        name = name,
        preferredLanguage = preferredLanguage,
        contactInformation = ContactInformationFixtures.instagramContact(username),
    ).getOrThrow()

    fun facebookClient(
        name: ClientName = ClientNameFixtures.johnDoe(),
        preferredLanguage: Language = LanguageFixtures.english(),
        userId: String = "123456789",
    ) = Client.create(
        name = name,
        preferredLanguage = preferredLanguage,
        contactInformation = ContactInformationFixtures.facebookContact(userId),
    ).getOrThrow()
}
