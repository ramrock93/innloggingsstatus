package no.nav.personbruker.innloggingsstatus.config

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.apache.Apache
import io.ktor.client.engine.apache.ApacheEngineConfig
import io.ktor.client.features.HttpTimeout
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import java.net.ProxySelector
import kotlinx.serialization.json.Json
import org.apache.http.impl.conn.SystemDefaultRoutePlanner

object HttpClientBuilder {

    fun build(): HttpClient {
        return HttpClient(Apache) {
            install(JsonFeature) {
                serializer = buildJsonSerializer()
            }
            install(HttpTimeout)

            enableSystemDefaultProxy()
        }
    }
}

private fun buildJsonSerializer() = KotlinxSerializer (
    Json {
        ignoreUnknownKeys = true
    }
)

private fun HttpClientConfig<ApacheEngineConfig>.enableSystemDefaultProxy() {
    engine {
        customizeClient { setRoutePlanner(SystemDefaultRoutePlanner(ProxySelector.getDefault())) }
    }
}