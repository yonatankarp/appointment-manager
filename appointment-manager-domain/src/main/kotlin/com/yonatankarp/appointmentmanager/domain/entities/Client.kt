package com.yonatankarp.appointmentmanager.domain.entities

import com.yonatankarp.appointmentmanager.domain.valueobjects.ClientId
import com.yonatankarp.appointmentmanager.domain.valueobjects.CommunicationChannel
import com.yonatankarp.appointmentmanager.domain.valueobjects.ContactInformation
import com.yonatankarp.appointmentmanager.domain.valueobjects.Language
import java.time.Instant

data class Client(
    val id: ClientId,
    val name: String,
    val preferredLanguage: Language,
    val contactInformation: ContactInformation,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    val communicationChannel: CommunicationChannel
        get() = contactInformation.channel

    companion object {
        fun create(
            name: String,
            preferredLanguage: Language,
            contactInformation: ContactInformation,
        ) = runCatching {
            require(name.isNotBlank()) { "Client name cannot be blank" }
            val now = Instant.now()
            Client(
                id = ClientId.new(),
                name = name,
                preferredLanguage = preferredLanguage,
                contactInformation = contactInformation,
                createdAt = now,
                updatedAt = now,
            )
        }
    }
}
