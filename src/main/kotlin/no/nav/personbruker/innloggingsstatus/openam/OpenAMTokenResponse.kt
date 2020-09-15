package no.nav.personbruker.innloggingsstatus.openam

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class OpenAMTokenResponse (
    val token: OpenAMToken,
    val attributes: List<OpenAMAttribute>
)

data class OpenAMToken (
    val tokenId: String
)

data class OpenAMAttribute (
    val name: String,
    val values: List<String>
)