package no.nav.personbruker.innloggingsstatus.oidc

import io.ktor.application.ApplicationCall
import io.ktor.util.KtorExperimentalAPI
import no.nav.personbruker.innloggingsstatus.config.Environment


@KtorExperimentalAPI
class OidcTokenService(private val oidcTokenValidator: OidcTokenValidator,
                       private val environment: Environment) {

    fun getOidcToken(call: ApplicationCall): OidcTokenInfo? {
        return oidcTokenValidator.getValidToken(call)?.let { jwtToken ->
            OidcTokenInfoFactory.mapOidcTokenInfo(jwtToken, environment.identityClaim)
        }
    }
}