package no.nav.personbruker.innloggingsstatus.openam

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import no.nav.personbruker.innloggingsstatus.common.readObject
import no.nav.personbruker.innloggingsstatus.config.Environment
import no.nav.personbruker.innloggingsstatus.config.JsonDeserialize.objectMapper
import no.nav.personbruker.innloggingsstatus.health.SelfTest
import no.nav.personbruker.innloggingsstatus.health.ServiceStatus
import no.nav.personbruker.innloggingsstatus.openam.health.DUMMY_SUBJECT_TOKEN
import org.slf4j.LoggerFactory
import java.lang.Exception
import java.net.URI
import java.net.URL

class OpenAMConsumer(
    private val client: HttpClient,
    environment: Environment
) : SelfTest {

    private val endpoint = URI(environment.openAMServiceUrl)

    private val log = LoggerFactory.getLogger(OpenAMConsumer::class.java)

    override val externalServiceName: String get() = "OpenAm / Nav-esso"

    suspend fun getOpenAMTokenAttributes(token: String): OpenAMResponse {
        return fetchOpenAmTokenAttributesJsonString(token).let {json ->
            parseJsonResponse(json)
        }
    }

    private fun parseJsonResponse(json: String): OpenAMResponse {
        return try {
            val tokenResponse: OpenAMTokenResponse = objectMapper.readObject(json)
            OpenAMResponse.validResponse(tokenResponse)
        } catch (e: Exception) {
            parseJsonErrorResponse(json)
        }
    }

    private fun parseJsonErrorResponse(json: String): OpenAMResponse {
        return try {
            val errorResponse: OpenAMErrorResponse = objectMapper.readObject(json)
            OpenAMResponse.errorResponse(errorResponse)
        } catch (e: Exception) {
            log.warn("Klarte ikke utlede token eller feilinfo fra openAM-response.", e)
            OpenAMResponse.errorResponse()
        }
    }

    private suspend fun fetchOpenAmTokenAttributesJsonString(token: String): String {
        return try {
             client.get {
                url(URL("$endpoint/identity/json/attributes"))
                parameter("subjectid", token)
                parameter("attributenames", "uid")
                parameter("attributenames", "SecurityLevel")
            }
        } catch (e: Exception) {
            throw OpenAMException("Feil ved henting av attributter for nav-esso token")
        }
    }


    // The esso service does not appear to offer a dedicated liveness path, and performing a call on behalf of a
    // dummy token and checking whether we got the expected 401 http-response seems to be the convention
    override suspend fun externalServiceStatus(): ServiceStatus {
        return try {
            when (probeForLiveness().status) {
                HttpStatusCode.Unauthorized -> ServiceStatus.OK
                else -> ServiceStatus.ERROR
            }
        } catch (e: Exception) {
            ServiceStatus.ERROR
        }
    }

    private suspend fun probeForLiveness(): HttpResponse {
        return client.get {
            url(URL("$endpoint/identity/json/attributes"))
            parameter("subjectId", DUMMY_SUBJECT_TOKEN)
        }
    }

}