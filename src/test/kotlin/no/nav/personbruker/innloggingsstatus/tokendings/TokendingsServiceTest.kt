package no.nav.personbruker.innloggingsstatus.tokendings

import com.auth0.jwt.interfaces.DecodedJWT
import io.ktor.application.*
import io.ktor.util.*
import io.mockk.*
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.innloggingsstatus.idporten.authentication.IdPortenService
import no.nav.security.token.support.core.jwt.JwtToken
import no.nav.tms.token.support.idporten.user.IdportenUser
import no.nav.tms.token.support.idporten.user.IdportenUserFactory
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.time.Instant

@KtorExperimentalAPI
internal class TokendingsServiceTest {

    private val call: ApplicationCall = mockk()

    private val tokendingsTokenValidator: TokendingsTokenValidator = mockk()
    private val idportenService: IdPortenService = mockk()
    private val jwtToken: JwtToken = mockk()

    private val tokendingsService: TokendingsService = TokendingsService(tokendingsTokenValidator, idportenService)

    private val ident = "12345"
    private val authLevel = 4
    private val expirationTime: Instant = mockk()
    private val decodedJWT: DecodedJWT = mockk()

    private val tokendingsToken = "token"

    @AfterEach
    fun cleanUp() {
        clearMocks(call)
    }

    @Test
    fun `should not attempt to fetch token info if nav-tokendings cookie is missing`() {
        every { call.request.cookies[any()] } returns null

        val response = runBlocking { tokendingsService.getIdportenToken(call) }

        response `should be equal to` null

        coVerify(exactly = 0) { tokendingsTokenValidator.getValidToken(call) }
    }

    @Test
    fun `should query for token info when nav-tokendings cookie is present`() {
        every { call.request.cookies[any()] } returns tokendingsToken
        mockkObject(IdportenUserFactory)

        coEvery { tokendingsTokenValidator.getValidToken(call) } returns jwtToken
        coEvery { idportenService.redirectLogin(call) } just runs
        coEvery { IdportenUserFactory.createIdportenUser(call) } returns
                IdportenUser(ident, authLevel, expirationTime, decodedJWT)

        val response = runBlocking { tokendingsService.getIdportenToken(call) }

        response?.loginLevel `should be equal to` 4
        response?.ident `should be equal to` ident
    }

}