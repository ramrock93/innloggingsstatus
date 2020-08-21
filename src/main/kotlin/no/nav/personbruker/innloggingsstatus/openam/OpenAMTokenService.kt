package no.nav.personbruker.innloggingsstatus.openam

import io.ktor.application.ApplicationCall
import io.ktor.request.ApplicationRequest

class OpenAMTokenService(val openAMConsumer: OpenAMConsumer) {

    private val NAV_ESSO_COOKIE = "nav-esso"

    suspend fun getOpenAMToken(call: ApplicationCall): OpenAMTokenInfo? {
        return call.request.navEssoToken?.let { essoToken ->
            openAMConsumer.getOpenAMTokenAttributes(essoToken)
        }?.let { tokenResponse ->
            OpenAMTokenInfoFactory.mapOpenAMTokenInfo(tokenResponse)
        }
    }

    private val ApplicationRequest.navEssoToken: String? get() {
        return call.request.cookies[NAV_ESSO_COOKIE]
    }
}