package no.nav.personbruker.innloggingsstatus.openam

data class OpenAMErrorResponse (
    val exception: OpenAMExceptionResponse
)

data class OpenAMExceptionResponse(
    val name: String,
    val message: String
)