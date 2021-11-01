package no.nav.personbruker.innloggingsstatus.auth

import com.fasterxml.jackson.annotation.JsonInclude
import no.nav.personbruker.innloggingsstatus.idporten.IdportenTokenInfo
import no.nav.personbruker.innloggingsstatus.oidc.OidcTokenInfo
import no.nav.personbruker.innloggingsstatus.openam.OpenAMTokenInfo
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AuthInfo (
    val oidcToken: OidcTokenInfo?,
    val openAMToken: OpenAMTokenInfo?,
    val idportenToken: IdportenTokenInfo?
) {
    val nonNullTokens = listOfNotNull(openAMToken, oidcToken, idportenToken)
    val subjects = nonNullTokens.map { it.subject }.toSet()

    val authenticated: Boolean get() = (oidcToken != null || openAMToken != null || idportenToken != null) && stable

    val stable: Boolean get() = subjects.size == 1

    val subject: String? get() {
        // Return null if we find auth info for different users
        return if (stable) {
            subjects.first()
        } else {
            null
        }
    }

    val authLevel: Int? get() {
        // Same reasoning as above, but we return the highest value for authLevel if we find multiple tokens for the same user
        return if (stable) {
            nonNullTokens.map { it.authLevel }.maxOrNull()
        } else {
            null
        }
    }

    val expiryTime: LocalDateTime? get() = oidcToken?.expiryTime

    companion object {
        fun unAuthenticated(): AuthInfo = AuthInfo(null, null, null)
    }
}