package no.nav.personbruker.innloggingsstatus.auth

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = ANY)
class AuthSummary private constructor(authInfo: AuthInfo) {
    private val authLevel: Int? = authInfo.authLevel
    private val authenticated: Boolean = authInfo.authenticated
    private val oidc: OidcSummary? = OidcSummary.fromAuthInfo(authInfo)
    private val openAM: OpenAMSummary? = OpenAMSummary.fromAuthInfo(authInfo)

    companion object {
        fun fromAuthInfo(authInfo: AuthInfo): AuthSummary = AuthSummary(authInfo)
    }
}

private data class OidcSummary (
    val authLevel: Int,
    val issueTime: LocalDateTime,
    val expiryTime: LocalDateTime
) {
    companion object {
        fun fromAuthInfo(authInfo: AuthInfo): OidcSummary? {
            return authInfo.takeIf { it.stable }
                ?.oidcToken
                ?.let { oidc -> OidcSummary(oidc.authLevel, oidc.issueTime, oidc.expiryTime) }
        }
    }
}

private data class OpenAMSummary (
    val authLevel: Int
) {
    companion object {
        fun fromAuthInfo(authInfo: AuthInfo): OpenAMSummary? {
            return authInfo.takeIf { it.stable }
                ?.openAMToken
                ?.let { openAM -> OpenAMSummary(openAM.authLevel) }
        }
    }
}