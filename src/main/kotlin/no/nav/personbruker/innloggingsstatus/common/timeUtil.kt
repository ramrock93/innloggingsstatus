package no.nav.personbruker.innloggingsstatus.common

import java.time.*
import java.util.*

val LocalDateTime.epochSecondUtc get() = toEpochSecond(ZoneOffset.UTC)

fun Date.toUtcDateTime() = LocalDateTime.ofInstant(this.toInstant(), ZoneId.of("UTC"))

fun getSecondsSinceUtcEpoch() = Instant.now().epochSecond