package no.nav.personbruker.innloggingsstatus.common

import java.time.LocalDateTime
import java.time.ZoneOffset

val LocalDateTime.epochSecondUtc get() = toEpochSecond(ZoneOffset.UTC)

fun getSecondsSinceUtcEpoch() = LocalDateTime.now().epochSecondUtc