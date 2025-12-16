package com.yonatankarp.appointmentmanager.domain.fixtures

import com.yonatankarp.appointmentmanager.domain.valueobjects.ContactInformation

object ContactInformationFixtures {
    fun contactInformation() = emailContact()

    fun emailContact(emailAddress: String = "test@example.com") =
        ContactInformation.EmailContact(EmailAddressFixtures.emailAddress(emailAddress))

    fun whatsAppContact(phoneNumber: String = "+972501234567") =
        ContactInformation.WhatsAppContact(PhoneNumberFixtures.phoneNumber(phoneNumber))

    fun instagramContact(username: String = "test_user") =
        ContactInformation.InstagramContact(username)

    fun facebookContact(userId: String = "123456789") =
        ContactInformation.FacebookContact(userId)
}
