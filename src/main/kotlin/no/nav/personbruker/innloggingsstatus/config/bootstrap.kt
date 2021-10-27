package no.nav.personbruker.innloggingsstatus.config

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.http.HttpHeaders
import io.ktor.jackson.jackson
import io.ktor.routing.routing
import io.ktor.util.KtorExperimentalAPI
import io.prometheus.client.hotspot.DefaultExports
import no.nav.personbruker.innloggingsstatus.auth.authApi
import no.nav.personbruker.innloggingsstatus.health.healthApi
import no.nav.personbruker.ktor.features.NonStandardCORS
import no.nav.tms.token.support.idporten.SecurityLevel
import no.nav.tms.token.support.idporten.installIdPortenAuth

@KtorExperimentalAPI
fun Application.mainModule() {

    DefaultExports.initialize()

    install(DefaultHeaders)

    val applicationContext = ApplicationContext(this.environment.config)

    val environment = applicationContext.environment

    install(NonStandardCORS) {
        host(
            host = environment.corsAllowedHost,
            schemes = environment.corsAllowedSchemes,
            subDomains = environment.corsAllowedSubdomains
        )
        registerAdditionalOrigins(environment.corsAdditionalAllowedOrigins, environment.corsAllowedSchemes)
        allowCredentials = true
        header(HttpHeaders.ContentType)
    }

    install(ContentNegotiation) {
        jackson {
            registerModule(JavaTimeModule())
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        }
    }

    //installIdPortenAuth {
    //    tokenCookieName = "user_id_token"
    //    postLogoutRedirectUri = "https://www.nav.no"
    //    setAsDefault = true
    //    securityLevel = SecurityLevel.LEVEL_4
    //    tokenRefreshEnabled = true
    //}

    routing {
        healthApi(applicationContext.selfTests)
        authApi(applicationContext.authTokenService)
    }
}

fun NonStandardCORS.Configuration.registerAdditionalOrigins(origins: List<String>, schemes: List<String>) {
    origins.forEach { origin ->
        host(origin, schemes)
    }
}