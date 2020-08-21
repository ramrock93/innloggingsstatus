package no.nav.personbruker.innloggingsstatus.auth

import java.time.LocalDateTime

interface TokenInfo {
    val subject: String
    val authLevel: Int
    val expiryTime: LocalDateTime?
}