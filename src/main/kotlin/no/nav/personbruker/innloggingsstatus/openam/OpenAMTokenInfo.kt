package no.nav.personbruker.innloggingsstatus.openam

import no.nav.personbruker.innloggingsstatus.auth.TokenInfo
import java.time.LocalDateTime

data class OpenAMTokenInfo (
    override val subject: String,
    override val authLevel: Int
): TokenInfo {
    override val expiryTime: LocalDateTime? get() = null
}