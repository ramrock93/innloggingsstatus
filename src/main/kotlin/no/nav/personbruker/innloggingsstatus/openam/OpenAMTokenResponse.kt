package no.nav.personbruker.innloggingsstatus.openam


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