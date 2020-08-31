package no.nav.personbruker.innloggingsstatus.oidc

import no.nav.personbruker.innloggingsstatus.common.toUtcDateTime
import no.nav.security.token.support.core.jwt.JwtToken
import java.time.LocalDateTime
import java.util.*

object OidcTokenInfoFactory {

    fun mapOidcTokenInfo(token: JwtToken): OidcTokenInfo {

        val ident: String = getIdent(token)
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
        return token.jwtTokenClaims.get("exp")
            .let { it as Date }
            .toUtcDateTime()
    }

    private fun getTokenIssueLocalDateTime(token: JwtToken): LocalDateTime {
        return token.jwtTokenClaims.get("iat")
            .let { it as Date }
            .toUtcDateTime()
    }

    private fun getIdent(token: JwtToken): String {
        val claims = token.jwtTokenClaims

        return when {
            claims.allClaims.containsKey("pid") -> claims.getStringClaim("pid")
            claims.allClaims.containsKey("sub") -> claims.getStringClaim("sub")
            else -> throw RuntimeException("Fant ikke et token-claim med ident i 'pid' eller 'sub'.")
        }
    }

}
