package no.nav.personbruker.innloggingsstatus.openam

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class OpenAMTokenResponse (
    val token: Token,
    val attributes: List<Attribute>
)

data class Token (
    val tokenId: String
)

data class Attribute (
    val name: String,
    val values: List<String>
)