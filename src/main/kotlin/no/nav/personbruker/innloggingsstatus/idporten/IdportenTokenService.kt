package no.nav.personbruker.innloggingsstatus.idporten

import com.auth0.jwt.interfaces.DecodedJWT
import io.ktor.application.ApplicationCall
import io.ktor.util.KtorExperimentalAPI
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import no.nav.personbruker.innloggingsstatus.config.ApplicationContext

@KtorExperimentalAPI
class IdportenTokenService(private val applicationContext: ApplicationContext,
                           private val idportenTokenValidator: IdportenTokenValidator) {
    fun getIdportenToken(call: ApplicationCall): IdportenTokenInfo? {
        val verifier = createVerifier(applicationContext)

        return idportenTokenValidator.getValidToken(call)?.let {
            val decodedToken = verifier.verifyAccessToken(it)
            return IdportenTokenInfo(
                decodedToken.subject,
                extractLoginLevel(decodedToken),
                convertToLocalDateTime(decodedToken.issuedAt),
                convertToLocalDateTime(decodedToken.expiresAt)
            )
        }
    }

    private fun createVerifier(applicationContext: ApplicationContext) =
        TokenVerifier(
            jwkProvider = applicationContext.jwkProvider,
            clientId = applicationContext.environment.idportenClientId,
            issuer = applicationContext.metadata.issuer
        )

    private fun extractLoginLevel(token: DecodedJWT): Int {
        return when (token.getClaim("acr").asString()) {
            "Level3" -> 3
            "Level4" -> 4
            else -> throw Exception("Innloggingsnivå ble ikke funnet.")
        }
    }

    private fun convertToLocalDateTime(date: Date): LocalDateTime {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
    }
}