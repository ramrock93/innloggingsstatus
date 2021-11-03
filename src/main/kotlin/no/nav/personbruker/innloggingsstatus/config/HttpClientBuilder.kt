package no.nav.personbruker.innloggingsstatus.config

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.apache.Apache
import io.ktor.client.engine.apache.ApacheEngineConfig
import io.ktor.client.features.HttpTimeout
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import java.net.ProxySelector
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

fun buildJsonSerializer(): JacksonSerializer {
    return JacksonSerializer {
        registerModule(JavaTimeModule())
    }
}

private fun HttpClientConfig<ApacheEngineConfig>.enableSystemDefaultProxy() {
    engine {
        customizeClient { setRoutePlanner(SystemDefaultRoutePlanner(ProxySelector.getDefault())) }
    }
}