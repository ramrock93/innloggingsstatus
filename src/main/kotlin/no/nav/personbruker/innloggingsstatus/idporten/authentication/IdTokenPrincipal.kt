package no.nav.personbruker.innloggingsstatus.idporten.authentication

import com.auth0.jwt.interfaces.DecodedJWT
import io.ktor.auth.*

internal data class IdTokenPrincipal(val decodedJWT: DecodedJWT) : Principal
