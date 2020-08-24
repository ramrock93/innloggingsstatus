package no.nav.personbruker.innloggingsstatus.sts

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import no.nav.personbruker.innloggingsstatus.common.apiKeyHeader
import no.nav.personbruker.innloggingsstatus.common.basicAuth
import no.nav.personbruker.innloggingsstatus.config.Environment
import org.slf4j.LoggerFactory
import java.net.URI
import java.net.URL


class STSConsumer(private val client: HttpClient, environment: Environment) {

    private val endpoint = URI(environment.securityTokenServiceUrl)
    private val apiKey = environment.stsApiGWKey
    private val username = environment.serviceUsername
    private val password = environment.servicePassword

    private val log = LoggerFactory.getLogger(STSConsumer::class.java)

    suspend fun getStsToken(): String? {
        return try {
            fetchStsToken().accessToken
        } catch (e: Exception) {
            log.warn("Klarte ikke hente sts-token", e)
            null
        }
    }

    private suspend fun fetchStsToken(): StsTokenResponse {
        return client.get {
            url(URL("$endpoint/rest/v1/sts/token"))
            parameter("grant_type", "client_credentials")
            parameter("scope", "openid")
            apiKeyHeader(apiKey)
            basicAuth(username, password)
        }
    }
}