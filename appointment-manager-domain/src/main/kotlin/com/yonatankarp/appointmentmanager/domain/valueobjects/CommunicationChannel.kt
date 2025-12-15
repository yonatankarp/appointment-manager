package com.yonatankarp.appointmentmanager.domain.valueobjects

sealed class CommunicationChannel {
    data object Instagram : CommunicationChannel()
    data object WhatsApp : CommunicationChannel()
    data object Facebook : CommunicationChannel()
    data object Email : CommunicationChannel()
}
