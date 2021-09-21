package no.nav.personbruker.innloggingsstatus.idporten.authentication.logout

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*
import io.ktor.util.date.*
import no.nav.personbruker.innloggingsstatus.config.ApplicationContext
import no.nav.personbruker.innloggingsstatus.idporten.authentication.IdTokenPrincipal
import java.net.URI

@KtorExperimentalAPI
internal fun Routing.logoutApi(context: ApplicationContext) {

    val contextPath = ""
    val postLogoutRedirectUri = "https://nav.no"

    authenticate(LogoutAuthenticator.name) {
        // Calling this endpoint with a bearer token will send a redirect to idporten to trigger single-logout
        get("/logout") {
            val principal = call.principal<IdTokenPrincipal>()

            call.invalidateCookie(context.idportenTokenCookieName, contextPath)

            if (principal == null) {
                call.respondRedirect(postLogoutRedirectUri)
            } else {
                call.redirectToSingleLogout(principal.decodedJWT.token, context.idportenMetadata.logoutEndpoint, postLogoutRedirectUri)
            }
        }
    }

    // Calls to this endpoint should be initiated by ID-porten through the user, after the user has signed out elsewhere
    get("/oauth2/logout") {
        call.invalidateCookieForExternalLogout(context.idportenTokenCookieName, contextPath, secure = true)
        call.respond(HttpStatusCode.OK)
    }
}

private fun ApplicationCall.invalidateCookie(cookieName: String, contextPath: String) {
    response.cookies.appendExpired(cookieName, path = "/$contextPath")
}

private suspend fun ApplicationCall.redirectToSingleLogout(idToken: String, signoutUrl: String, postLogoutUrl: String) {
    val urlBuilder = URLBuilder()
    urlBuilder.takeFrom(URI(signoutUrl))
    urlBuilder.parameters.apply {
        append("id_token_hint", idToken)
        append("post_logout_redirect_uri", postLogoutUrl)
    }

    val redirectUrl = urlBuilder.buildString()

    respondRedirect(redirectUrl)
}

private fun ApplicationCall.invalidateCookieForExternalLogout(cookieName: String, contextPath: String, secure: Boolean) {

    if (secure) {
        response.cookies.appendExpiredCrossSite(cookieName, contextPath)
    } else {
        response.cookies.appendExpired(cookieName, path = "/$contextPath")
    }
}

private fun ResponseCookies.appendExpiredCrossSite(cookieName: String, contextPath: String) {
    append(
        name = cookieName,
        value = "",
        path = "/$contextPath",
        expires = GMTDate.START,
        secure = true,
        extensions = mapOf("SameSite" to "None")
    )
}
