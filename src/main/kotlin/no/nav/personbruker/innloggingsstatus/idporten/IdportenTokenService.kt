package no.nav.personbruker.innloggingsstatus.idporten

import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import io.ktor.application.ApplicationCall
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

class IdportenTokenService(private val idportenTokenValidator: IdportenTokenValidator) {
    fun getIdportenToken(call: ApplicationCall): IdportenTokenInfo? {
        return idportenTokenValidator.getValidToken(call)?.let {
            val decodedToken = JWT.decode(it)
            return IdportenTokenInfo(
                decodedToken.subject,
                extractLoginLevel(decodedToken),
                convertToLocalDateTime(decodedToken.issuedAt),
                convertToLocalDateTime(decodedToken.expiresAt)
            )
        }
    }

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