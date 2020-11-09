package no.nav.personbruker.innloggingsstatus.auth

import com.fasterxml.jackson.annotation.JsonInclude
import no.nav.personbruker.innloggingsstatus.oidc.OidcTokenInfo
import no.nav.personbruker.innloggingsstatus.openam.OpenAMTokenInfo
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AuthInfo (
    val oidcToken: OidcTokenInfo?,
    val openAMToken: OpenAMTokenInfo?
) {
    val authenticated: Boolean get() = (oidcToken != null || openAMToken != null) && stable

    val stable: Boolean get() = openAMToken == null || oidcToken == null || oidcToken.subject == openAMToken.subject

    val subject: String? get() {
        // Return null if we find auth info for different users
        return if (stable) {
            oidcToken?.subject
                ?: openAMToken?.subject
        } else {
            null
        }
    }

    val authLevel: Int? get() {
        // Same reasoning as above, but we return the highest value for authLevel if we find multiple tokens for the same user
        return if (stable) {
            max(oidcToken?.authLevel, openAMToken?.authLevel)
        } else {
            null
        }
    }

    val expiryTime: LocalDateTime? get() = oidcToken?.expiryTime

    companion object {
        fun unAuthenticated(): AuthInfo = AuthInfo(null, null)
    }
}

private fun max(a: Int?, b: Int?): Int? {
    return when {
        a == null -> b
        b == null -> a
        else -> kotlin.math.max(a, b)
    }
}