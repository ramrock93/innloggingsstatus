package no.nav.personbruker.innloggingsstatus.config

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.jackson.jackson
import io.ktor.routing.routing
import io.ktor.util.KtorExperimentalAPI
import io.prometheus.client.hotspot.DefaultExports
import no.nav.personbruker.innloggingsstatus.auth.authApi
import no.nav.personbruker.innloggingsstatus.health.healthApi

@KtorExperimentalAPI
fun Application.mainModule() {

    DefaultExports.initialize()

    install(DefaultHeaders)

    val applicationContext = ApplicationContext(this.environment.config)

    install(ContentNegotiation) {
        jackson {
            registerModule(JavaTimeModule())
        }
    }

    routing {
        healthApi(applicationContext.selfTests)
        authApi(applicationContext.authTokenService)
    }
}