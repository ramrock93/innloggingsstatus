package no.nav.personbruker.innloggingsstatus.idporten.authentication.config

internal object Idporten {
    const val scope = "openid"
    const val responseToken = "id_token"
    const val authenticatorName = "innloggingstatus_idporten"
    const val postLoginRedirectCookie = "redirect_uri"
}

