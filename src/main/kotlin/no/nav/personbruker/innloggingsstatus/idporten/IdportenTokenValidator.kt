package no.nav.personbruker.innloggingsstatus.idporten

import io.ktor.application.ApplicationCall

class IdportenTokenValidator {

    fun getValidToken(call: ApplicationCall): String? {
        return call.request.cookies["user_id_token"]
    }
}