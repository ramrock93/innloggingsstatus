package no.nav.personbruker.innloggingsstatus.openam

import io.ktor.application.ApplicationCall
import io.ktor.request.ApplicationRequest

class OpenAMTokenService(private val openAMTokenInfoProvider: OpenAMTokenInfoProvider) {

    private val NAV_ESSO_COOKIE = "nav-esso"

    suspend fun getOpenAMToken(call: ApplicationCall): OpenAMTokenInfo? {
        return call.request.navEssoToken?.let { essoToken ->
            openAMTokenInfoProvider.getTokenInfo(essoToken)
        }
    }

    private val ApplicationRequest.navEssoToken: String? get() {
        return cookies[NAV_ESSO_COOKIE]
            ?: headers[NAV_ESSO_COOKIE]
    }
}