package com.yonatankarp.appointmentmanager.domain.entities

import com.yonatankarp.appointmentmanager.domain.valueobjects.CommunicationChannel
import com.yonatankarp.appointmentmanager.domain.valueobjects.ContactInformation
import com.yonatankarp.appointmentmanager.domain.valueobjects.EmailAddress
import com.yonatankarp.appointmentmanager.domain.valueobjects.Language
import com.yonatankarp.appointmentmanager.domain.valueobjects.PhoneNumber
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class ClientTest {
    @Test
    fun `should create client with valid data`() {
        // Given
        val name = "John Doe"
        val language = Language.ENGLISH
        val email = EmailAddress.of("john@example.com").getOrThrow()
        val contactInfo = ContactInformation.EmailContact(email)

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
    fun `should reject blank name`() {
        // Given
        val name = "   "
        val language = Language.ENGLISH
        val email = EmailAddress.of("john@example.com").getOrThrow()
        val contactInfo = ContactInformation.EmailContact(email)

        // When
        val result = Client.create(name, language, contactInfo)

        // Then
        result.isFailure shouldBe true
    }

    @Test
    fun `should derive communication channel from WhatsApp contact`() {
        // Given
        val name = "Jane Smith"
        val language = Language.HEBREW
        val phone = PhoneNumber.of("+491234567890").getOrThrow()
        val contactInfo = ContactInformation.WhatsAppContact(phone)

        // When
        val result = Client.create(name, language, contactInfo)

        // Then
        result.isSuccess shouldBe true
        result.getOrNull()?.communicationChannel shouldBe CommunicationChannel.WhatsApp
    }

    @Test
    fun `should derive communication channel from Instagram contact`() {
        // Given
        val name = "Bob Artist"
        val language = Language.GERMAN
        val contactInfo = ContactInformation.InstagramContact("bob_artist")

        // When
        val result = Client.create(name, language, contactInfo)

        // Then
        result.isSuccess shouldBe true
        result.getOrNull()?.communicationChannel shouldBe CommunicationChannel.Instagram
    }

    @Test
    fun `should derive communication channel from Facebook contact`() {
        // Given
        val name = "Alice Wonder"
        val language = Language.ENGLISH
        val contactInfo = ContactInformation.FacebookContact("alice123")

        // When
        val result = Client.create(name, language, contactInfo)

        // Then
        result.isSuccess shouldBe true
        result.getOrNull()?.communicationChannel shouldBe CommunicationChannel.Facebook
    }
}
