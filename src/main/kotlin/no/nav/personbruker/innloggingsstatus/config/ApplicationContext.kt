package no.nav.personbruker.innloggingsstatus.config

import io.ktor.config.ApplicationConfig
import io.ktor.util.KtorExperimentalAPI
import no.nav.personbruker.innloggingsstatus.auth.AuthTokenService
import no.nav.personbruker.innloggingsstatus.oidc.OidcTokenService
import no.nav.personbruker.innloggingsstatus.oidc.OidcTokenValidator
import no.nav.personbruker.innloggingsstatus.openam.OpenAMConsumer
import no.nav.personbruker.innloggingsstatus.openam.OpenAMTokenService

@KtorExperimentalAPI
class ApplicationContext(config: ApplicationConfig) {

    val environment = Environment()

    val oidcTokenValidator = OidcTokenValidator(config)
    val oidcValidationService = OidcTokenService(oidcTokenValidator)

    val openAMConsumer = OpenAMConsumer()
    val openAMValidationService = OpenAMTokenService(openAMConsumer)

    val authTokenService = AuthTokenService(oidcValidationService)

//    val healthService = HealthService(this)
}