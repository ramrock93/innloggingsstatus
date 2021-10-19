package no.nav.personbruker.innloggingsstatus.tokendings

import io.ktor.application.*
import io.ktor.util.*
import no.nav.security.token.support.core.jwt.JwtToken

@KtorExperimentalAPI
class TokendingsService(val tokendingsTokenValidator: TokendingsTokenValidator) {

    private val NAV_TOKENDINGS = "nav-tokendings-token"

    fun getTokendingsToken(call: ApplicationCall): JwtToken? {
        return if (call.request.cookies[NAV_TOKENDINGS] != null) {
            tokendingsTokenValidator.getValidToken(call)
        } else {
            null
        }
    }

}
