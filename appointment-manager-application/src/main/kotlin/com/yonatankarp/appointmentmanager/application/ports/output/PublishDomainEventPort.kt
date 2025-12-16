package com.yonatankarp.appointmentmanager.application.ports.output

import com.yonatankarp.appointmentmanager.domain.events.DomainEvent

interface PublishDomainEventPort {
    operator fun invoke(event: DomainEvent)
}
