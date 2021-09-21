package no.nav.personbruker.innloggingsstatus.tokendings

import io.ktor.application.*
import io.ktor.config.*
import io.ktor.util.*
import no.nav.personbruker.innloggingsstatus.oidc.JwtTokenHttpRequest
import no.nav.security.token.support.core.configuration.IssuerProperties
import no.nav.security.token.support.core.configuration.MultiIssuerConfiguration
import no.nav.security.token.support.core.configuration.ProxyAwareResourceRetriever
import no.nav.security.token.support.core.jwt.JwtToken
import no.nav.security.token.support.core.validation.JwtTokenValidationHandler
import java.net.URL

@KtorExperimentalAPI
class TokendingsTokenValidator constructor(applicationConfig: ApplicationConfig) {

    private val resourceRetriever: ProxyAwareResourceRetriever = ProxyAwareResourceRetriever()

    private val jwtTokenValidationHandler: JwtTokenValidationHandler

    private val multiIssuerConfiguration: MultiIssuerConfiguration

    private val NAV_TOKENDINGS = "nav-tokendings-token"

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
        ).getJwtTokenAsOptional(NAV_TOKENDINGS).orElse(null)
    }
}