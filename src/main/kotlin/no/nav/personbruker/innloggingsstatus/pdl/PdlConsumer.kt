package no.nav.personbruker.innloggingsstatus.pdl

import com.fasterxml.jackson.databind.ObjectMapper
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.url
import no.nav.personbruker.innloggingsstatus.common.apiKeyHeader
import no.nav.personbruker.innloggingsstatus.common.bearerAuth
import no.nav.personbruker.innloggingsstatus.common.readObject
import no.nav.personbruker.innloggingsstatus.config.Environment
import no.nav.personbruker.innloggingsstatus.pdl.query.*
import org.slf4j.LoggerFactory
import java.lang.Exception
import java.net.URI
import java.net.URL

class PdlConsumer(private val client: HttpClient, environment: Environment) {

    val CONSUMER_ID = "innloggingsstatus"
    val GENERELL = "GEN"

    val endpoint = URI(environment.pdlApiUrl)
    val apiKey = environment.pdlApiGWKey

    val log = LoggerFactory.getLogger(PdlConsumer::class.java)
    val objectMapper = ObjectMapper()


    suspend fun getPersonInfo(ident: String, stsToken: String): PdlPersonInfo? {

        val request = createSubjectNameRequest(ident)

        return postPersonQuery(request, stsToken)?.let { responseBody ->
            parsePdlResponse(responseBody)
        }
    }

    private suspend fun postPersonQuery(request: SubjectNameRequest, stsToken: String): String? {
        return try {
            client.post {
                url(URL("$endpoint/graphql"))
                apiKeyHeader(apiKey)
                bearerAuth(stsToken)
                header("Nav-Consumer-Id", CONSUMER_ID)
                header("Nav-Consumer-Token", stsToken)
                header("Tema", GENERELL)
                body = request
            }
        } catch (e: Exception) {
            log.warn("Feil ved kontakt mot PDL", e)
            null
        }
    }

    private fun parsePdlResponse(json: String): PdlPersonInfo? {
        return try {
            val personResponse: PdlResponse = objectMapper.readObject(json)
            return personResponse.data.person
        } catch (e: Exception) {
            handleErrorResponse(json)
            null
        }
    }

    private fun handleErrorResponse(json: String) {
        try {
            val errorResponse: PdlErrorResponse = objectMapper.readObject(json)
            logErrorResponse(errorResponse)
        } catch (e: Exception) {
            log.warn("Feil ved deserialisering av svar fra pdl. Response-body lengde [${json.length}]", e)
        }
    }

    private fun logErrorResponse(response: PdlErrorResponse) {
        val firstError = response.errors.first().errorType

        when (firstError) {
            PDLErrorType.NOT_FOUND -> log.warn("Fant ikke bruker i PDL.")
            PDLErrorType.NOT_AUTHENTICATED -> log.warn("Autentiseringsfeil mot PDL. Feil i brukertoken eller systemtoken.")
            PDLErrorType.ABAC_ERROR -> log.warn("Systembruker har ikke tilgang til opplysning")
            PDLErrorType.UNKNOWN_ERROR -> log.warn("Ukjent feil mot PDL")
        }
    }
}