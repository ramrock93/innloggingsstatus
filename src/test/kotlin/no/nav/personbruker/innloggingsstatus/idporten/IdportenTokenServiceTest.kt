package no.nav.personbruker.innloggingsstatus.idporten

import io.ktor.application.ApplicationCall
import io.ktor.util.KtorExperimentalAPI
import io.mockk.mockk
import no.nav.personbruker.innloggingsstatus.config.ApplicationContext
import org.junit.jupiter.api.Test

@KtorExperimentalAPI
class IdportenTokenServiceTest {

    val applicationContext: ApplicationContext = mockk()
    val idportenTokenValidator: IdportenTokenValidator = mockk()

    val idportenTokenService = IdportenTokenService(applicationContext, idportenTokenValidator)

    val call: ApplicationCall = mockk()

    @Test
    fun `should return null when token is not found`() {

        /*every { idportenTokenValidator.getValidToken(call) } returns null

        val idportenTokenInfo = idportenTokenService.getIdportenToken(call)

        assertNull(idportenTokenInfo)*/
    }

    @Test
    fun `should map correctly when token is not found`() {

        /*
        every { idportenTokenValidator.getValidToken(call) } returns "eyJraWQiOiJjWmswME1rbTVIQzRnN3Z0NmNwUDVGSFpMS0pzdzhmQkFJdUZiUzRSVEQ0IiwiYWxnIjoiUlMyNTYifQ.eyJzdWIiOiJJQldhaThEVEdmU3o4V0FFajdtQk1vYmRyZl9lRmo1TFdpYnQ2NUdtQWI0PSIsImlzcyI6Imh0dHBzOlwvXC9vaWRjLXZlcjIuZGlmaS5ub1wvaWRwb3J0ZW4tb2lkYy1wcm92aWRlclwvIiwiY2xpZW50X2FtciI6InByaXZhdGVfa2V5X2p3dCIsInBpZCI6IjA2MDI1ODAwMTc0IiwidG9rZW5fdHlwZSI6IkJlYXJlciIsImNsaWVudF9pZCI6IjRmMjYwODE2LWIxMjEtNDE2OS1iMmQ4LWYzOWE1NDNmMDQyOCIsImF1ZCI6Imh0dHBzOlwvXC90b2tlbngiLCJhY3IiOiJMZXZlbDQiLCJzY29wZSI6Im9wZW5pZCIsImV4cCI6MTYzNTg2MDI0NywiaWF0IjoxNjM1ODU2NjQ3LCJjbGllbnRfb3Jnbm8iOiI4ODk2NDA3ODIiLCJqdGkiOiJvQmxtZVJXZTc4d0llS1U3SjNjRWttLWl1cHZRcVlMUW10U0d1WkRHRUp3IiwiY29uc3VtZXIiOnsiYXV0aG9yaXR5IjoiaXNvNjUyMy1hY3RvcmlkLXVwaXMiLCJJRCI6IjAxOTI6ODg5NjQwNzgyIn19.zG5Fupk_wfb9gwf57GvHbeGzFnr6GR3drsxgloikMKV2_WQYoQAL5C0taeY1tjlm4eB2LwUrg3B8vAJBWGE37eRevz-AP4z0pbzN8eJYm5T6wsXzYUX5FDUPXrWNkDZp4hSjs6kofSwzw0dpyJ_98RK-sXlznhSSpCmucmqDQ5_cLp0SJuWUwEbTNXb7OZKJKnBRTwnLAztA3AZNHzu-cKxgGaMudN_esUXLp7if_IAR6sqz4mU5i7IDOSU0KvSFl9uacNs8nvi6UISKpdbJT9jReFpeda6T1g8Cv_mwY8f5tgOnMa2IPYp9g-mh55kmX3xPtMhHYhVrKI1ZapdZtw"
        val idportenTokenInfo = idportenTokenService.getIdportenToken(call)

        assertNotNull(idportenTokenInfo)
         */
    }
}