package no.nav.personbruker.innloggingsstatus.oidc

import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.PlainJWT
import no.nav.security.token.support.core.jwt.JwtToken
import java.time.LocalDateTime
import java.time.ZoneOffset

object JwtTokenObjectMother {
    fun createJwtToken(subject: String, level: Int, issueTime: LocalDateTime, expiry: LocalDateTime) =
        plainSubjectToken(subject, level, issueTime, expiry)
            .serialize()
            .let { JwtToken(it) }

    private fun tokenClaims(sub: String, acr: String, iat: Long, exp: Long) = JWTClaimsSet.Builder()
        .claim("exp", exp)
        .claim("nbf", 1577876400)
        .claim("ver", "1.0")
        .claim("iss", "http://dummy.com")
        .claim("sub", sub)
        .claim("aud", "audience")
        .claim("acr", acr)
        .claim("nonce", "nonce")
        .claim("iat", iat)
        .claim("auth_time", 1577876400)
        .claim("jti", "dummy-user")
        .build()

    private fun plainSubjectToken(subject: String, level: Int,  issueTime: LocalDateTime, expiry: LocalDateTime) =
        PlainJWT(
            tokenClaims(
                sub = subject,
                acr = "Level$level",
                iat = issueTime.toEpochSecond(ZoneOffset.UTC),
                exp = expiry.toEpochSecond(ZoneOffset.UTC)
            )
        )
}