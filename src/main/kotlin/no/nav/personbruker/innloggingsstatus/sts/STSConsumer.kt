package no.nav.personbruker.innloggingsstatus.sts

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import no.nav.personbruker.innloggingsstatus.common.apiKeyHeader
import no.nav.personbruker.innloggingsstatus.common.basicAuth
import no.nav.personbruker.innloggingsstatus.config.Environment
import java.net.URI
import java.net.URL


class STSConsumer(private val client: HttpClient, environment: Environment) {

    private val endpoint = URI(environment.securityTokenServiceUrl)
    private val apiKey = environment.stsApiGWKey
    private val username = environment.serviceUsername
    private val password = environment.servicePassword

    suspend fun getStsToken(): String {
        return fetchStsToken().accessToken
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