package no.nav.personbruker.innloggingsstatus.auth

import io.ktor.application.ApplicationCall
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import no.nav.personbruker.innloggingsstatus.common.metrics.MetricsCollector
import no.nav.personbruker.innloggingsstatus.oidc.OidcTokenService
import no.nav.personbruker.innloggingsstatus.openam.OpenAMTokenService
import no.nav.personbruker.innloggingsstatus.user.SubjectNameService
import org.slf4j.LoggerFactory
import kotlin.coroutines.CoroutineContext

@KtorExperimentalAPI
class AuthTokenService(private val oidcTokenService: OidcTokenService,
                       private val openAMTokenService: OpenAMTokenService,
                       private val subjectNameService: SubjectNameService,
                       private val metricsCollector: MetricsCollector) {

    val log = LoggerFactory.getLogger(AuthTokenService::class.java)

    suspend fun getAuthenticatedUserInfo(call: ApplicationCall): UserInfo {
        return try {
            fetchAndParseAuthenticatedUserInfo(call)
        } catch (e: Exception) {
            log.warn("Feil ved henting av brukers innloggingsinfo", e)
            UserInfo.unAuthenticated()
        }
    }

    suspend fun getDetailedAuthInfo(call: ApplicationCall): AuthInfo {
        return fetchAndParseAuthInfo(call)
    }

    private suspend fun fetchAndParseAuthenticatedUserInfo(call: ApplicationCall): UserInfo = coroutineScope {
        val oidcToken = async { oidcTokenService.getOidcToken(call) }
        val openAMToken = async { openAMTokenService.getOpenAMToken(call) }

        val authInfo = AuthInfo(oidcToken.await(), openAMToken.await())

        val userInfo = getUserInfo(authInfo)

        metricsCollector.recordAuthMetrics(authInfo, userInfo)


        userInfo
    }

    private suspend fun fetchAndParseAuthInfo(call: ApplicationCall): AuthInfo = coroutineScope {
        val oidcToken = async { oidcTokenService.getOidcToken(call) }
        val openAMToken = async { openAMTokenService.getOpenAMToken(call) }

        AuthInfo(oidcToken.await(), openAMToken.await())
    }

    private suspend fun getUserInfo(authInfo: AuthInfo): UserInfo {
        return if (authInfo.subject != null && authInfo.stable) {
            val subjectName = subjectNameService.getSubjectName(authInfo.subject!!)
            UserInfo.authenticated(subjectName, authInfo.authLevel!!)
        } else {
            UserInfo.unAuthenticated()
        }
    }
}