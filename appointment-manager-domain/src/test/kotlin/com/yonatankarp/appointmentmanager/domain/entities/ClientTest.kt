package com.yonatankarp.appointmentmanager.domain.entities

import com.yonatankarp.appointmentmanager.domain.fixtures.ClientNameFixtures.aliceWonder
import com.yonatankarp.appointmentmanager.domain.fixtures.ClientNameFixtures.bobArtist
import com.yonatankarp.appointmentmanager.domain.fixtures.ClientNameFixtures.janeSmith
import com.yonatankarp.appointmentmanager.domain.fixtures.ClientNameFixtures.johnDoe
import com.yonatankarp.appointmentmanager.domain.fixtures.ContactInformationFixtures.emailContact
import com.yonatankarp.appointmentmanager.domain.fixtures.ContactInformationFixtures.facebookContact
import com.yonatankarp.appointmentmanager.domain.fixtures.ContactInformationFixtures.instagramContact
import com.yonatankarp.appointmentmanager.domain.fixtures.ContactInformationFixtures.whatsAppContact
import com.yonatankarp.appointmentmanager.domain.fixtures.LanguageFixtures.english
import com.yonatankarp.appointmentmanager.domain.fixtures.LanguageFixtures.german
import com.yonatankarp.appointmentmanager.domain.fixtures.LanguageFixtures.hebrew
import com.yonatankarp.appointmentmanager.domain.valueobjects.CommunicationChannel
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class ClientTest {
    @Test
    fun `should create client with valid data`() {
        // Given
        val name = johnDoe()
        val language = english()
        val contactInfo = emailContact()

        // When
        val result = Client.create(name, language, contactInfo)

        // Then
        result.isSuccess shouldBe true
        val client = result.getOrNull()
        client?.name shouldBe name
        client?.preferredLanguage shouldBe language
        client?.contactInformation shouldBe contactInfo
        client?.communicationChannel shouldBe CommunicationChannel.Email
    }

    @Test
    fun `should derive communication channel from WhatsApp contact`() {
        // Given
        val name = janeSmith()
        val language = hebrew()
        val contactInfo = whatsAppContact()

        // When
        val result = Client.create(name, language, contactInfo)

        // Then
        result.isSuccess shouldBe true
        result.getOrNull()?.communicationChannel shouldBe CommunicationChannel.WhatsApp
    }

    @Test
    fun `should derive communication channel from Instagram contact`() {
        // Given
        val name = bobArtist()
        val language = german()
        val contactInfo = instagramContact()

        // When
        val result = Client.create(name, language, contactInfo)

        // Then
        result.isSuccess shouldBe true
        result.getOrNull()?.communicationChannel shouldBe CommunicationChannel.Instagram
    }

    @Test
    fun `should derive communication channel from Facebook contact`() {
        // Given
        val name = aliceWonder()
        val language = english()
        val contactInfo = facebookContact()

        // When
        val result = Client.create(name, language, contactInfo)

        // Then
        result.isSuccess shouldBe true
        result.getOrNull()?.communicationChannel shouldBe CommunicationChannel.Facebook
    }
}
