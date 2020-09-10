package no.nav.personbruker.innloggingsstatus.openam

import io.ktor.application.ApplicationCall
import io.ktor.request.ApplicationRequest
import org.slf4j.LoggerFactory

class OpenAMTokenService(val openAMConsumer: OpenAMConsumer) {

    private val NAV_ESSO_COOKIE = "nav-esso"

    private val log = LoggerFactory.getLogger(OpenAMTokenService::class.java)

    suspend fun getOpenAMToken(call: ApplicationCall): OpenAMTokenInfo? {
        return call.request.navEssoToken?.let { essoToken ->
            fetchTokenAttributes(essoToken)
        }?.takeIf { response ->
            response.isValid()
        }?.let { tokenResponse ->
            OpenAMTokenInfoFactory.mapOpenAMTokenInfo(tokenResponse)
        }
    }

    private suspend fun fetchTokenAttributes(essoToken: String): OpenAMResponse {
        try {
            return openAMConsumer.getOpenAMTokenAttributes(essoToken)
        } catch (e: Exception) {
            log.warn("Fikk feil ved kontakt mot esso/openAM under henting av attributter for esso-token.", e)
            throw e
        }
    }

    private val ApplicationRequest.navEssoToken: String? get() {
        return cookies[NAV_ESSO_COOKIE]
    }
}