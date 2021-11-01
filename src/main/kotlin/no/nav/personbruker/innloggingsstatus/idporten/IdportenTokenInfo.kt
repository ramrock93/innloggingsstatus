package no.nav.personbruker.innloggingsstatus.idporten

import no.nav.personbruker.innloggingsstatus.auth.TokenInfo
import java.time.LocalDateTime

data class IdportenTokenInfo(
    override val subject: String,
    override val authLevel: Int,
    override val issueTime: LocalDateTime,
    override val expiryTime: LocalDateTime
): TokenInfo