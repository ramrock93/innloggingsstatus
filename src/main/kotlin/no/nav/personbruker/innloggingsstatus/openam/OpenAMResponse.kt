package no.nav.personbruker.innloggingsstatus.openam

class OpenAMResponse private constructor(
    val tokenResponse: OpenAMTokenResponse?,
    val errorResponse: OpenAMErrorResponse?
) {
    fun isValid() = tokenResponse != null

    fun hasErrorMessage() = errorResponse != null

    companion object {
        fun validResponse(tokenResponse: OpenAMTokenResponse): OpenAMResponse {
            return OpenAMResponse(tokenResponse, null)
        }

        fun errorResponse(errorResponse: OpenAMErrorResponse? = null): OpenAMResponse {
            return OpenAMResponse(null, errorResponse)
        }
    }
}