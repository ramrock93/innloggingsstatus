package no.nav.personbruker.innloggingsstatus.oidc

import io.ktor.application.ApplicationCall
import io.ktor.util.KtorExperimentalAPI


@KtorExperimentalAPI
class OidcTokenService(private val oidcTokenValidator: OidcTokenValidator) {

    fun getOidcToken(call: ApplicationCall): OidcTokenInfo? {
        return oidcTokenValidator.getValidToken(call)?.let { jwtToken ->
            OidcTokenInfoFactory.mapOidcTokenInfo(jwtToken)
        }
    }
}