package no.nav.personbruker.innloggingsstatus.config

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.http.HttpHeaders
import io.ktor.jackson.jackson
import io.ktor.routing.*
import io.ktor.util.KtorExperimentalAPI
import io.prometheus.client.hotspot.DefaultExports
import no.nav.personbruker.innloggingsstatus.auth.authApi
import no.nav.personbruker.innloggingsstatus.auth.tokenxApi
import no.nav.personbruker.innloggingsstatus.health.healthApi
import no.nav.personbruker.ktor.features.NonStandardCORS
import no.nav.tms.token.support.idporten.IdPortenCookieAuthenticator
import no.nav.tms.token.support.idporten.SecurityLevel
import no.nav.tms.token.support.idporten.installIdPortenAuth
import no.nav.tms.token.support.tokenx.validation.TokenXAuthenticator
import no.nav.tms.token.support.tokenx.validation.installTokenXAuth

@KtorExperimentalAPI
fun Application.mainModule() {

    DefaultExports.initialize()

    install(DefaultHeaders)

    install(ContentNegotiation) {
        jackson {
            registerModule(JavaTimeModule())
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        }
    }

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

    installIdPortenAuth {
        tokenCookieName = "nav_idporten_token"
        postLogoutRedirectUri = "https://www.nav.no"
        setAsDefault = false
        alwaysRedirectToLogin = false
        securityLevel = SecurityLevel.LEVEL_4
        tokenRefreshEnabled = true
        tokenRefreshMarginPercentage = 20
    }

    installTokenXAuth {
        // config
    }


    routing {
        healthApi(applicationContext.selfTests)
        authApi(applicationContext.authTokenService)
        authenticate(IdPortenCookieAuthenticator.name) {
            tokenxApi(applicationContext.tokendingsService)
        }

        authenticate(TokenXAuthenticator.name) {  }
            get("/tokenx") {
                applicationContext.tokendingsService.getTokendingsToken(call)
            }
    }
}

fun NonStandardCORS.Configuration.registerAdditionalOrigins(origins: List<String>, schemes: List<String>) {
    origins.forEach { origin ->
        host(origin, schemes)
    }
}