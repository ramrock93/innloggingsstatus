package no.nav.personbruker.innloggingsstatus.oidc

import io.ktor.util.date.toGMTDate
import no.nav.personbruker.innloggingsstatus.common.toUtcDateTime
import no.nav.security.token.support.core.jwt.JwtToken
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

object OidcTokenInfoFactory {

    private val IDENT_CLAIM_REGEX = "^[0-9]{1,11}$".toRegex()

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
        return token.jwtTokenClaims.getStringClaim("sub").takeIf { sub ->
            IDENT_CLAIM_REGEX.matches(sub)
        }?: token.jwtTokenClaims.getStringClaim("pid").takeIf { pid ->
            IDENT_CLAIM_REGEX.matches(pid)
        }?: throw RuntimeException("Fant ikke et token-claim som så ut som en ident i 'sub' eller 'pid'.")
    }

}
