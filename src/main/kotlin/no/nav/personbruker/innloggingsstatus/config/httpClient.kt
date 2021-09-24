package no.nav.personbruker.innloggingsstatus.config

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import no.nav.personbruker.innloggingsstatus.idporten.authentication.OauthServerConfigurationMetadata
import org.slf4j.LoggerFactory

internal suspend fun HttpClient.getOAuthServerConfigurationMetadata(url: String)
        : OauthServerConfigurationMetadata = withContext(Dispatchers.IO) {
    val log = LoggerFactory.getLogger(HttpClient::class.java)
    log.info("Fetching OAuth metadata from url: $url")
    request {
        method = HttpMethod.Get
        url(url)
        accept(ContentType.Application.Json)
    }
}