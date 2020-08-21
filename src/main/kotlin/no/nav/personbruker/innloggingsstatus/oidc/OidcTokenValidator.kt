package no.nav.personbruker.innloggingsstatus.oidc

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.config.ApplicationConfig
import io.ktor.util.KtorExperimentalAPI
import no.nav.security.token.support.core.configuration.IssuerProperties
import no.nav.security.token.support.core.configuration.MultiIssuerConfiguration
import no.nav.security.token.support.core.configuration.ProxyAwareResourceRetriever
import no.nav.security.token.support.core.context.TokenValidationContext
import no.nav.security.token.support.core.jwt.JwtToken
import no.nav.security.token.support.core.validation.JwtTokenValidationHandler
import java.net.URL

@KtorExperimentalAPI
class OidcTokenValidator constructor(applicationConfig: ApplicationConfig) {

    private val resourceRetriever: ProxyAwareResourceRetriever = ProxyAwareResourceRetriever()

    private val jwtTokenValidationHandler: JwtTokenValidationHandler

    private val multiIssuerConfiguration: MultiIssuerConfiguration

    init {
        val issuerPropertiesMap: Map<String, IssuerProperties> = applicationConfig.configList("no.nav.security.jwt.issuers")
            .associate { issuerConfig ->
                issuerConfig.property("issuer_name").getString() to IssuerProperties(
                    URL(issuerConfig.property("discoveryurl").getString()),
                    issuerConfig.property("accepted_audience").getString().split(","),
                    issuerConfig.propertyOrNull("cookie_name")?.getString()
                )
            }

        multiIssuerConfiguration = MultiIssuerConfiguration(issuerPropertiesMap, resourceRetriever)

        jwtTokenValidationHandler = JwtTokenValidationHandler(multiIssuerConfiguration)
    }

    fun getValidToken(call: ApplicationCall): JwtToken? {
        return jwtTokenValidationHandler.getValidatedTokens(
            JwtTokenHttpRequest(call.request.cookies, call.request.headers)
        ).firstValidToken.orElse(null)
    }
}