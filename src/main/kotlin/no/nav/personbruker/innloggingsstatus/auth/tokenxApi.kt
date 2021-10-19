package no.nav.personbruker.innloggingsstatus.auth

import io.ktor.application.*
import io.ktor.routing.*
import io.ktor.util.*
import no.nav.personbruker.innloggingsstatus.tokendings.TokendingsService

@KtorExperimentalAPI
fun Route.tokenxApi(tokendingsService: TokendingsService) {

    get("/tokenx") {
        tokendingsService.getTokendingsToken(call)
    }

}