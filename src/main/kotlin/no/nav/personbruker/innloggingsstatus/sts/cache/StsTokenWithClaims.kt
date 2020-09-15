package no.nav.personbruker.innloggingsstatus.sts.cache

import no.nav.personbruker.innloggingsstatus.sts.jwt.JwtTokenDecoder
import java.time.Instant

data class StsTokenWithClaims (val tokenString: String) {
    val expiryClaim: Instant = JwtTokenDecoder.decodeExpiryTime(tokenString)
}