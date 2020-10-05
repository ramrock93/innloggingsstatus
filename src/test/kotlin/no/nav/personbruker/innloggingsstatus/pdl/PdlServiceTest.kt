package no.nav.personbruker.innloggingsstatus.pdl

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.innloggingsstatus.sts.STSException
import no.nav.personbruker.innloggingsstatus.sts.StsService
import org.amshove.kluent.`should equal`
import org.junit.jupiter.api.Test

internal class PdlServiceTest {

    val stsService: StsService = mockk()
    val pdlConsumer: PdlConsumer = mockk()

    val ident = "123456"
    val fornavn = "Fornavn"
    val mellomnavn = "Mellomnavn"
    val etternavn = "Etternavn"

    val stsToken = "stsToken"

    val pdlService = PdlService(pdlConsumer, stsService)

    @Test
    fun `should return null if we do not have a valid sts token`() {
        coEvery { stsService.getStsToken() } throws STSException()

        val response = runBlocking { pdlService.getSubjectName(ident) }

        response `should equal` null
    }

    @Test
    fun `should invalidate sts cache if we received an error response from pdl due to a bad token, and then return null`() {
        coEvery { stsService.getStsToken() } returns stsToken
        coEvery { pdlConsumer.getPersonInfo(ident, stsToken) } throws PdlAuthenticationException()
        coEvery { stsService.invalidateToken() } returns Unit

        val response = runBlocking { pdlService.getSubjectName(ident) }

        response `should equal` null
        coVerify(exactly = 1) { stsService.invalidateToken() }
    }

    @Test
    fun `should just return null if we did not receive a valid response for any other reason`() {
        coEvery { stsService.getStsToken() } returns stsToken
        coEvery { pdlConsumer.getPersonInfo(ident, stsToken) } throws PdlException()

        val response = runBlocking { pdlService.getSubjectName(ident) }

        response `should equal` null
        coVerify(exactly = 0) { stsService.invalidateToken() }
    }

    @Test
    fun `should map valid response correctly`() {
        val pdlResponse = PdlPersonInfoObjectMother.createPdlPersonInfo(fornavn, mellomnavn, etternavn)

        coEvery { stsService.getStsToken() } returns stsToken
        coEvery { pdlConsumer.getPersonInfo(ident, stsToken) } returns pdlResponse

        val response = runBlocking { pdlService.getSubjectName(ident) }

        response?.fornavn `should equal` fornavn
        response?.mellomnavn `should equal` mellomnavn
        response?.etternavn `should equal` etternavn
    }
}