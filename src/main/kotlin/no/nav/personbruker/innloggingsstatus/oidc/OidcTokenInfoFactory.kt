package no.nav.personbruker.innloggingsstatus.oidc

import no.nav.security.token.support.core.jwt.JwtToken
import java.time.LocalDateTime
import java.time.ZoneId

object OidcTokenInfoFactory {

    fun mapOidcTokenInfo(token: JwtToken): OidcTokenInfo {

        val ident: String = token.jwtTokenClaims.getStringClaim("sub")
        val authLevel = extractAuthLevel(token)
        val issueTime = getTokenIssueLocalDateTime(token)
        val expiryTime = getTokenExpiryLocalDateTime(token)

        return OidcTokenInfo(ident, authLevel, issueTime, expiryTime)
    }

    private fun extractAuthLevel(token: JwtToken): Int {

        return when (token.jwtTokenClaims.getStringClaim("acr")) {
            "Level3" -> 3
            "Level4" -> 4
            else -> throw Exception("Innloggingsnivå ble ikke funnet. Dette skal ikke kunne skje.")
        }
    }

    private fun getTokenExpiryLocalDateTime(token: JwtToken): LocalDateTime {
        return token.jwtTokenClaims.getStringClaim("exp")
            .let { LocalDateTime.parse(it) }
    }

    private fun getTokenIssueLocalDateTime(token: JwtToken): LocalDateTime {
        return token.jwtTokenClaims.getStringClaim("iat")
            .let { LocalDateTime.parse(it) }
    }

}
