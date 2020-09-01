package no.nav.personbruker.innloggingsstatus.sts

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import no.nav.personbruker.innloggingsstatus.common.apiKeyHeader
import no.nav.personbruker.innloggingsstatus.common.basicAuth
import no.nav.personbruker.innloggingsstatus.config.Environment
import no.nav.personbruker.innloggingsstatus.health.SelfTest
import no.nav.personbruker.innloggingsstatus.health.ServiceStatus
import no.nav.personbruker.innloggingsstatus.pdl.health.LivenessStatus
import org.slf4j.LoggerFactory
import java.net.URI
import java.net.URL


class STSConsumer(private val client: HttpClient, environment: Environment): SelfTest {

    private val endpoint = URI(environment.securityTokenServiceUrl)
    private val apiKey = environment.stsApiGWKey
    private val username = environment.serviceUsername
    private val password = environment.servicePassword

    private val log = LoggerFactory.getLogger(STSConsumer::class.java)

    override val externalServiceName: String get() = "Security-Token-Service"

    suspend fun getStsToken(): String {
        return try {
            fetchStsToken().accessToken
        } catch (e: Exception) {
            log.warn("Klarte ikke hente sts-token", e)
            throw e
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

    override suspend fun externalServiceStatus(): ServiceStatus {
        return try {
            when (getLivenessResponse()) {
                true -> ServiceStatus.OK
                false -> ServiceStatus.ERROR
            }
        } catch (e: Exception) {
            ServiceStatus.ERROR
        }
    }

    private suspend fun getLivenessResponse(): Boolean {
        return client.get {
            url(URL("$endpoint/isAlive"))
            apiKeyHeader(apiKey)
        }
    }
}