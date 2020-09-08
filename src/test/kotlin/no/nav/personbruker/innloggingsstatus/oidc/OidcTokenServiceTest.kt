package no.nav.personbruker.innloggingsstatus.oidc

import io.ktor.application.ApplicationCall
import io.ktor.util.KtorExperimentalAPI
import io.mockk.every
import io.mockk.mockk
import org.amshove.kluent.`should equal`
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.ZoneOffset

@KtorExperimentalAPI
internal class OidcTokenServiceTest {

    val oidcTokenValidator: OidcTokenValidator = mockk()

    val oidcTokenService = OidcTokenService(oidcTokenValidator)

    val call: ApplicationCall = mockk()

    @Test
    fun `should extract correct information from a jwt token`() {
        val subject = "1234"
        val level = 3
        val issueTime = LocalDateTime.now()
        val expiryTime = issueTime.plusHours(1)

        val jwtToken = JwtTokenObjectMother.createJwtToken(subject, level, issueTime, expiryTime)

        every { oidcTokenValidator.getValidToken(call) } returns jwtToken

        val oidcTokenInfo = oidcTokenService.getOidcToken(call)

        oidcTokenInfo?.subject `should equal` subject
        oidcTokenInfo?.authLevel `should equal` level
        oidcTokenInfo?.issueTime?.toEpochSecond(ZoneOffset.UTC) `should equal` issueTime.toEpochSecond(ZoneOffset.UTC)
        oidcTokenInfo?.expiryTime?.toEpochSecond(ZoneOffset.UTC) `should equal` expiryTime.toEpochSecond(ZoneOffset.UTC)
    }
}