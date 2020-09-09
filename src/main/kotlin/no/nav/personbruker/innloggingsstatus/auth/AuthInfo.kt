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
    val authenticated: Boolean get() = oidcToken != null || openAMToken != null

    val stable: Boolean get() = openAMToken == null || oidcToken == null || oidcToken.subject == openAMToken.subject

    val subject: String? get() {
        // If we find auth info for different users, let oidc tokens take precedence
        return if (stable) {
            oidcToken?.subject
                ?: openAMToken?.subject
        } else {
            oidcToken?.subject
        }
    }

    val authLevel: Int? get() {
        // Same reasoning as above, but we return the highest value for authLevel if we find multiple tokens for the same user
        return if (stable) {
            max(oidcToken?.authLevel, openAMToken?.authLevel)
        } else {
            oidcToken?.authLevel
        }
    }

    val expiryTime: LocalDateTime? get() = oidcToken?.expiryTime
}

private fun max(a: Int?, b: Int?): Int? {
    return when {
        a == null -> b
        b == null -> a
        else -> kotlin.math.max(a, b)
    }
}