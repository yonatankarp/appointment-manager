package com.yonatankarp.appointmentmanager.domain.valueobjects

import com.yonatankarp.appointmentmanager.domain.events.DomainEvent

data class Effect<out T, out E : DomainEvent>(
    val value: T,
    val event: E,
)

fun <T, E : DomainEvent> effect(value: T, event: E) = Effect(value, event)
