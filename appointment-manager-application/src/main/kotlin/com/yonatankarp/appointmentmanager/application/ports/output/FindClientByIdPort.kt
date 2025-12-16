package com.yonatankarp.appointmentmanager.application.ports.output

import com.yonatankarp.appointmentmanager.domain.entities.Client
import com.yonatankarp.appointmentmanager.domain.valueobjects.ClientId

interface FindClientByIdPort {
    operator fun invoke(clientId: ClientId): Client?
}
