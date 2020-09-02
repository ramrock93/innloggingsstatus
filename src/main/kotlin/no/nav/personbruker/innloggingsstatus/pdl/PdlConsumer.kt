package no.nav.personbruker.innloggingsstatus.pdl

import io.ktor.client.HttpClient
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import no.nav.personbruker.innloggingsstatus.common.apiKeyHeader
import no.nav.personbruker.innloggingsstatus.common.bearerAuth
import no.nav.personbruker.innloggingsstatus.common.readObject
import no.nav.personbruker.innloggingsstatus.config.Environment
import no.nav.personbruker.innloggingsstatus.config.JsonDeserialize.objectMapper
import no.nav.personbruker.innloggingsstatus.health.SelfTest
import no.nav.personbruker.innloggingsstatus.health.ServiceStatus
import no.nav.personbruker.innloggingsstatus.pdl.query.*
import org.slf4j.LoggerFactory
import java.net.URI
import java.net.URL
import kotlin.Exception

class PdlConsumer(private val client: HttpClient, environment: Environment): SelfTest {

    val CONSUMER_ID = "innloggingsstatus"
    val GENERELL = "GEN"

    val endpoint = URI(environment.pdlApiUrl)
    val apiKey = environment.pdlApiGWKey

    val log = LoggerFactory.getLogger(PdlConsumer::class.java)

    override val externalServiceName: String get() = "PDL-api"

    suspend fun getPersonInfo(ident: String, stsToken: String): PdlPersonInfo {

        val request = createSubjectNameRequest(ident)

        return postPersonQuery(request, stsToken).let { responseBody ->
            parsePdlResponse(responseBody)
        }
    }

    private suspend fun postPersonQuery(request: SubjectNameRequest, stsToken: String): String {
        return try {
            client.post {
                url(URL("$endpoint/graphql"))
                contentType(ContentType.Application.Json)
                apiKeyHeader(apiKey)
                bearerAuth(stsToken)
                header("Nav-Consumer-Id", CONSUMER_ID)
                header("Nav-Consumer-Token", stsToken)
                header("Tema", GENERELL)
                body = request
            }
        } catch (e: Exception) {
            log.warn("Feil ved kontakt mot PDL", e)
            throw e
        }
    }

    private fun parsePdlResponse(json: String): PdlPersonInfo {
        return try {
            val personResponse: PdlResponse = objectMapper.readObject(json)
            personResponse.data.person
        } catch (e: Exception) {
            handleErrorResponse(json)
        }
    }

    private fun handleErrorResponse(json: String): Nothing {
        try {
            val errorResponse: PdlErrorResponse = objectMapper.readObject(json)
            logErrorResponse(errorResponse)
            throwAppropriateException(errorResponse)
        } catch (e: Exception) {
            log.warn("Feil ved deserialisering av svar fra pdl. Response-body lengde [${json.length}]", e)
            throw e
        }
    }

    private fun throwAppropriateException(response: PdlErrorResponse): Nothing {
        val firstError = response.errors.first().errorType

        if (firstError == PDLErrorType.NOT_AUTHENTICATED) {
            throw PdlAuthenticationException("Fikk autentiseringsfeil mot PDL [$firstError]")
        } else {
            throw Exception("Fikk feil fra pdl med type [$firstError]")
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

    override suspend fun externalServiceStatus(): ServiceStatus {
        return try {
            val response = getLivenessResponse()
            when (response.status) {
                HttpStatusCode.OK -> ServiceStatus.OK
                else -> ServiceStatus.ERROR
            }
        } catch (e: Exception) {
            ServiceStatus.ERROR
        }
    }

    private suspend fun getLivenessResponse(): HttpResponse {
        return client.options {
            url(URL("$endpoint/graphql"))
            apiKeyHeader(apiKey)
        }
    }
}
