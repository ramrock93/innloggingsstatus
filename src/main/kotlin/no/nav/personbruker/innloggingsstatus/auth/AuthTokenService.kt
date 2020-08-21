package no.nav.personbruker.innloggingsstatus.auth

import io.ktor.application.ApplicationCall
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import no.nav.personbruker.innloggingsstatus.oidc.OidcTokenService
import no.nav.personbruker.innloggingsstatus.openam.OpenAMTokenService
import no.nav.personbruker.innloggingsstatus.user.SubjectNameService
import kotlin.coroutines.CoroutineContext

@KtorExperimentalAPI
class AuthTokenService(private val oidcTokenService: OidcTokenService,
                       private val openAMTokenService: OpenAMTokenService,
                       private val subjectNameService: SubjectNameService) {

    suspend fun getAuthenticatedUserInfo(call: ApplicationCall): UserInfo = coroutineScope {
        val oidcToken = async { oidcTokenService.getOidcToken(call) }
        val openAMToken = async { openAMTokenService.getOpenAMToken(call) }

        val authInfo = AuthInfo(oidcToken.await(), openAMToken.await())

        getUserInfo(authInfo)
    }

    private suspend fun getUserInfo(authInfo: AuthInfo): UserInfo {
        return if (authInfo.subject != null) {
            val subjectName = subjectNameService.getSubjectName(authInfo.subject!!)
            UserInfo.authenticated(subjectName, authInfo.authLevel!!)
        } else {
            UserInfo.unAuthenticated()
        }
    }
}