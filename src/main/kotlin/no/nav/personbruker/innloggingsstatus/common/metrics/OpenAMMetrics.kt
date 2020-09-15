package no.nav.personbruker.innloggingsstatus.common.metrics

import no.nav.personbruker.innloggingsstatus.auth.AuthInfo

data class OpenAMMetrics private constructor (
    val authenticated: Boolean,
    val authLevel: Int
) {
    companion object {
        fun fromAuthInfo(authInfo: AuthInfo): OpenAMMetrics {
            return authInfo.openAMToken?.let { openAMToken ->
                OpenAMMetrics(
                    true,
                    openAMToken.authLevel
                )
            }?: OpenAMMetrics(false, -1)
        }
    }
}