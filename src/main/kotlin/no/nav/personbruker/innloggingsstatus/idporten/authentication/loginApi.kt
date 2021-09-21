package no.nav.personbruker.innloggingsstatus.idporten.authentication

import com.auth0.jwt.interfaces.DecodedJWT
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*
import no.nav.personbruker.innloggingsstatus.config.ApplicationContext
import no.nav.personbruker.innloggingsstatus.idporten.authentication.config.Idporten

@KtorExperimentalAPI
internal fun Routing.loginApi(applicationContext: ApplicationContext) {

    val contextPath = ""
    val tokenCookieName = "innloggingstatus_idporten"
    val verifier = createVerifier(applicationContext)

    get("/login") {
        val redirectUri = call.redirectUri

        if (redirectUri != null) {
            call.response.cookies.append(Idporten.postLoginRedirectCookie, redirectUri, path = "/")
        }

        call.respondRedirect(getLoginUrl(contextPath))
    }

    get("/login/status") {
        val idToken = call.validIdTokenOrNull(tokenCookieName, verifier)

        if (idToken == null) {
            call.respond(LoginStatus.unAuthenticated())
        } else {
            call.respond(LoginStatus.authenticated(extractLoginLevel(idToken)))
        }
    }
}

private fun ApplicationCall.validIdTokenOrNull(tokenCookieName: String, verifier: TokenVerifier): DecodedJWT? {

    val idToken = request.cookies[tokenCookieName]

    return if (idToken != null) {
        try {
            verifier.verify(idToken)
        } catch (e: Throwable) {
            null
        }
    } else {
        null
    }
}

@KtorExperimentalAPI
private fun createVerifier(applicationContext: ApplicationContext) = TokenVerifier(
        jwkProvider = applicationContext.idportenJwkProvider,
        clientId = applicationContext.environment.idportenClientId,
        issuer = applicationContext.idportenMetadata.issuer
)

private fun getLoginUrl(contextPath: String): String {
    return if (contextPath.isBlank()) {
        "/oauth2/login"
    } else {
        "/$contextPath/oauth2/login"
    }
}

private fun extractLoginLevel(token: DecodedJWT): Int {

    return when (token.getClaim("acr").asString()) {
        "Level3" -> 3
        "Level4" -> 4
        else -> throw Exception("Innloggingsnivå ble ikke funnet. Dette skal ikke kunne skje.")
    }
}

private val ApplicationCall.redirectUri: String? get() = request.queryParameters["redirect_uri"]
