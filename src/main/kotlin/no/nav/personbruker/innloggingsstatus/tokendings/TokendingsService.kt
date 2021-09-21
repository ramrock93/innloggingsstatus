package no.nav.personbruker.innloggingsstatus.tokendings

import io.ktor.application.*
import io.ktor.util.*
import no.nav.personbruker.innloggingsstatus.idporten.authentication.IdPortenService
import no.nav.tms.token.support.idporten.user.IdportenUser
import no.nav.tms.token.support.idporten.user.IdportenUserFactory

@KtorExperimentalAPI
class TokendingsService(val tokendingsTokenValidator: TokendingsTokenValidator, val idportenService: IdPortenService) {

    private val NAV_TOKENDINGS = "nav-tokendings-token"

    fun getIdportenToken(call: ApplicationCall): IdportenUser? {
        return if (call.request.cookies[NAV_TOKENDINGS] != null) {
             tokendingsTokenValidator.getValidToken(call)?.let {
                idportenService.redirectLogin(call)

                IdportenUserFactory.createIdportenUser(call)
            }
        } else
            null
    }

}
