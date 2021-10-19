package no.nav.personbruker.innloggingsstatus.idporten.authentication

import com.auth0.jwk.JwkProvider
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.util.*
import no.nav.personbruker.innloggingsstatus.config.Environment
import no.nav.personbruker.innloggingsstatus.idporten.authentication.config.Idporten
import no.nav.tms.token.support.idporten.IdPortenCookieAuthenticator
import org.slf4j.LoggerFactory

@KtorExperimentalAPI
class IdPortenService(
    val jwkProvider: JwkProvider,
    val metadata: OauthServerConfigurationMetadata,
    val environment: Environment
) {
    private val log = LoggerFactory.getLogger(IdPortenService::class.java)

    private val tokenCookieName = "innloggingstatus_idporten"
    private val contextPath = ""
    private val issuer = metadata.issuer
    private val clientId = environment.idportenClientId

    private val provider = IdTokenAuthenticationProvider.build(tokenCookieName)

    fun redirectLogin(call: ApplicationCall) {
        val verifier = TokenVerifier(jwkProvider, clientId, issuer)
        log.info("Intercepting call: ${call.request}")
        provider.pipeline.intercept(AuthenticationPipeline.RequestAuthentication) { context ->
            val idToken = call.request.cookies[tokenCookieName]
            if (idToken != null) {
                try {
                    val decodedJWT = verifier.verify(idToken)
                    context.principal(IdTokenPrincipal(decodedJWT))
                } catch (e: Throwable) {
                    val message = e.message ?: e.javaClass.simpleName
                    log.debug("Token verification failed: {}", message)
                    call.response.cookies.appendExpired(tokenCookieName)
                    context.challengeAndRedirect(contextPath)
                }
            } else {
                log.debug("Couldn't find cookie $tokenCookieName.")
                context.challengeAndRedirect(contextPath)
            }
        }
    }

    private fun AuthenticationContext.challengeAndRedirect(contextPath: String) {
        call.response.cookies.append(Idporten.postLoginRedirectCookie, call.request.pathWithParameters(), path = "/$contextPath")

        challenge("JWTAuthKey", AuthenticationFailedCause.InvalidCredentials) {
            call.respondRedirect(getLoginUrl(contextPath))
            it.complete()
        }
    }

    private fun AuthenticationContext.challengeAndRespondUnauthorized() {
        challenge("JWTAuthKey", AuthenticationFailedCause.InvalidCredentials) {
            call.respond(HttpStatusCode.Unauthorized)
            it.complete()
        }
    }

    private fun ApplicationRequest.pathWithParameters(): String {
        return if (queryParameters.isEmpty()) {
            path()
        } else {
            val params = ParametersBuilder().apply {
                queryParameters.forEach { name, values ->
                    appendAll(name, values)
                }
            }.build().formUrlEncode()

            "${path()}?$params"
        }
    }

    private fun getLoginUrl(contextPath: String): String {
        return if (contextPath.isBlank()) {
            "/oauth2/login"
        } else {
            "/$contextPath/oauth2/login"
        }
    }
}



class IdTokenAuthenticationProvider constructor(config: Configuration) : AuthenticationProvider(config) {

    class Configuration(name: String?) : AuthenticationProvider.Configuration(name)

    companion object {
        fun build(name: String?) = IdTokenAuthenticationProvider(Configuration(name))
    }
}


