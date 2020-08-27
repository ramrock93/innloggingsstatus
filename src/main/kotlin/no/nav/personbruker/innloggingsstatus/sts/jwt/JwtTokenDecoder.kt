package no.nav.personbruker.innloggingsstatus.sts.jwt

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.JsonMappingException
import no.nav.personbruker.innloggingsstatus.common.fromBase64
import no.nav.personbruker.innloggingsstatus.common.readObject
import no.nav.personbruker.innloggingsstatus.config.JsonDeserialize.objectMapper
import java.lang.Exception
import java.time.Instant

object JwtTokenDecoder {

    fun decodeExpiryTime(jwtToken: String): Instant {
        return try {
            val payloadString = jwtToken.split("\\.")
                .let { segments -> segments[1].fromBase64()}

            val payload: JwtPayload = objectMapper.readObject(payloadString)
            Instant.ofEpochSecond(payload.expiryTime)
        } catch (e: JsonMappingException) {
            throw Exception("Error parsing expiry date from jwt token.", e)
        } catch (e: JsonParseException) {
            throw Exception("Invalid json found in jwt token.", e)
        }
    }
}

private class JwtPayload constructor(@JsonProperty(value = "exp") expiryTime: String) {
    val expiryTime = expiryTime.toLong()
}