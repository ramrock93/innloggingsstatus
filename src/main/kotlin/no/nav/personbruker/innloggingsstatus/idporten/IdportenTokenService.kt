package no.nav.personbruker.innloggingsstatus.idporten

import com.auth0.jwt.JWT
import io.ktor.application.*
import java.time.LocalDateTime

class IdportenTokenService {
    fun getIdportenToken(call: ApplicationCall): IdportenTokenInfo? {
        val idToken = call.request.cookies["user_id_token"]
        val decodedToken = JWT.decode(idToken)
        // todo: fix datoer
        return IdportenTokenInfo(decodedToken.subject, decodedToken.getClaim("acr").asInt(), LocalDateTime.now(), LocalDateTime.now())
    }
}