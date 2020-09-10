package no.nav.personbruker.innloggingsstatus.openam

import io.ktor.application.ApplicationCall
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.`should equal`
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test


internal class OpenAMTokenServiceTest {

    private val call: ApplicationCall = mockk()
    private val openAMConsumer: OpenAMConsumer = mockk()

    private val subject = "123456"
    private val authLevel = 4
    private val essoToken = "token"

    private val openAMTokenService = OpenAMTokenService(openAMConsumer)

    @AfterEach
    fun cleanUp() {
        clearMocks(openAMConsumer, call)
    }

    @Test
    fun `should return no token info if nav-esso cookie is missing`() {
        every { call.request.cookies[any()] } returns null

        val response = runBlocking { openAMTokenService.getOpenAMToken(call) }

        response `should equal` null
    }

    @Test
    fun `should query for token info if nav-esso cookie is found and map response correctly`() {
        val tokenResponse = OpenAMTokenResponseObjectMother.createOpenAMTokenResponse(essoToken, subject, authLevel)

        every { call.request.cookies["nav-esso"] } returns essoToken
        coEvery { openAMConsumer.getOpenAMTokenAttributes(essoToken) } returns tokenResponse

        val response = runBlocking { openAMTokenService.getOpenAMToken(call) }

        response?.authLevel `should equal` authLevel
        response?.subject `should equal` subject
    }

    @Test
    fun `should query for token info if nav-esso cookie is found and return null if we received an error response`() {
        every { call.request.cookies["nav-esso"] } returns essoToken
        coEvery { openAMConsumer.getOpenAMTokenAttributes(essoToken) } returns OpenAMResponse.errorResponse()

        val response = runBlocking { openAMTokenService.getOpenAMToken(call) }

        response `should equal` null
    }

}