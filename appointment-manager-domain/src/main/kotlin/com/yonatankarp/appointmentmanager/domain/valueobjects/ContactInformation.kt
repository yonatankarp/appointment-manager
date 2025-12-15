package com.yonatankarp.appointmentmanager.domain.valueobjects

sealed class ContactInformation(val channel: CommunicationChannel) {
    data class InstagramContact(val username: String) : ContactInformation(CommunicationChannel.Instagram)
    data class WhatsAppContact(val phoneNumber: PhoneNumber) : ContactInformation(CommunicationChannel.WhatsApp)
    data class FacebookContact(val userId: String) : ContactInformation(CommunicationChannel.Facebook)
    data class EmailContact(val emailAddress: EmailAddress) : ContactInformation(CommunicationChannel.Email)
}
