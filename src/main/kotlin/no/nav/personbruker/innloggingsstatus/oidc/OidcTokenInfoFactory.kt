package no.nav.personbruker.innloggingsstatus.oidc

import no.nav.security.token.support.core.jwt.JwtToken
import java.time.LocalDateTime
import java.time.ZoneId

object OidcTokenInfoFactory {

    fun mapOidcTokenInfo(token: JwtToken): OidcTokenInfo {

        val ident: String = token.jwtTokenClaims.getStringClaim("sub")
        val authLevel = extractAuthLevel(token)
        val expiryTime = getTokenExpiryLocalDateTime(token)

        return OidcTokenInfo(ident, authLevel, expiryTime)
    }

    private fun extractAuthLevel(token: JwtToken): Int {

        return when (token.jwtTokenClaims.getStringClaim("acr")) {
            "Level3" -> 3
            "Level4" -> 4
            else -> throw Exception("Innloggingsnivå ble ikke funnet. Dette skal ikke kunne skje.")
        }
    }

    private fun getTokenExpiryLocalDateTime(token: JwtToken): LocalDateTime {
        return token.jwtTokenClaims
                .expirationTime
                .toInstant()
                .atZone(ZoneId.of("Europe/Oslo"))
                .toLocalDateTime()
    }
}
