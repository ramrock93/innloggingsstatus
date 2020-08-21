package no.nav.personbruker.innloggingsstatus.auth

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.util.KtorExperimentalAPI

@KtorExperimentalAPI
fun Route.authApi(authService: AuthTokenService) {

    get("/auth") {
        try {
            authService.getAuthenticatedUserInfo(call).let { userInfo ->
                call.respond(HttpStatusCode.OK, userInfo)
            }
        } catch(exception: Exception) {
            call.respond(HttpStatusCode.InternalServerError)
        }
    }
}