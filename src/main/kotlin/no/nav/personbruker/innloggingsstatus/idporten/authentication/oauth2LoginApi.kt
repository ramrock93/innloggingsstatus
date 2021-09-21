package no.nav.personbruker.innloggingsstatus.idporten.authentication

import com.auth0.jwt.interfaces.DecodedJWT
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*
import no.nav.personbruker.innloggingsstatus.config.ApplicationContext
import no.nav.personbruker.innloggingsstatus.idporten.authentication.config.Idporten

@KtorExperimentalAPI
internal fun Routing.oauth2LoginApi(applicationContext: ApplicationContext) {

    val settings = applicationContext.oauth2ServerSettings

    authenticate(Idporten.authenticatorName) {
        // This method is empty because the authenticator will redirect any calls to idporten, which will in turn
        // redirect to 'oath2/callback'. This method exists to differentiate between internal and external redirects
        get("/oauth2/login") {}

        // Users should arrive at this endpoint after a redirect from idporten, which will include a 'code' parameter
        // This parameter will be used to retrieve the user's token directly from idporten, and will then be provided
        // to the user as a token. The name of this token is determined when installing authentication.
        get("/oauth2/callback") {
            val principal = checkNotNull(call.authentication.principal<OAuthAccessTokenResponse.OAuth2>())
            when (val decodedJWT = settings.verify(principal, applicationContext)) {
                null -> call.respond(HttpStatusCode.InternalServerError, "Fant ikke ${Idporten.responseToken} i tokenrespons")
                else -> {
                    call.setTokenCookie(decodedJWT.token, applicationContext)
                    call.response.cookies.appendExpired(Idporten.postLoginRedirectCookie, path = "/") // contextPath
                    call.respondRedirect(call.request.cookies[Idporten.postLoginRedirectCookie] ?: applicationContext.environment.idportenRedirectUri)
                }
            }
        }
    }
}

@KtorExperimentalAPI
private fun ApplicationCall.setTokenCookie(token: String, applicationContext: ApplicationContext) {
    response.cookies.append(
            name = Idporten.authenticatorName,
            value = token,
            secure = true,  // secureCookie
            httpOnly = true,
            path = "/" // contextPath
    )
}

@KtorExperimentalAPI
private fun OAuthServerSettings.OAuth2ServerSettings.verify(tokenResponse: OAuthAccessTokenResponse.OAuth2?, runtimeContext: ApplicationContext): DecodedJWT? =
tokenResponse?.idToken(Idporten.responseToken)?.let {
    TokenVerifier(runtimeContext.idportenJwkProvider, clientId, runtimeContext.idportenMetadata.issuer).verify(it)
}

private fun OAuthAccessTokenResponse.OAuth2.idToken(tokenCookieName: String): String? = extraParameters[tokenCookieName]
