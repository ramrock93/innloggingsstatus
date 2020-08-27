package no.nav.personbruker.innloggingsstatus.openam

import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import no.nav.personbruker.innloggingsstatus.common.readObject
import no.nav.personbruker.innloggingsstatus.config.Environment
import no.nav.personbruker.innloggingsstatus.config.JsonDeserialize.objectMapper
import org.slf4j.LoggerFactory
import java.lang.Exception
import java.net.URI
import java.net.URL

class OpenAMConsumer(
    private val client: HttpClient,
    environment: Environment
) {

    private val openAMServiceUrl = URI(environment.openAMServiceUrl)

    private val log = LoggerFactory.getLogger(OpenAMConsumer::class.java)

    suspend fun getOpenAMTokenAttributes(token: String): OpenAMResponse? {
        return fetchOpenAmTokenAttributesJsonString(token)?.let {json ->
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
            OpenAMResponse.errorResponse()
        }
    }

    private suspend fun fetchOpenAmTokenAttributesJsonString(token: String): String? {
        return try {
             val response: String = client.get {
                url(URL("$openAMServiceUrl/identity/json/attributes"))
                parameter("subjectId", token)
                parameter("attributenames", "uid")
                parameter("attributenames", "SecurityLevel")
            }

            response
        } catch (e: Exception) {
            log.warn("Feil ved henting av atributter for nav-esso token", e)
            null
        }
    }

}