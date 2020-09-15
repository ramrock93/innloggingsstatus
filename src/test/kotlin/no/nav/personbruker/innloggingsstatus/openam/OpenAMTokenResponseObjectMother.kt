package no.nav.personbruker.innloggingsstatus.openam

object OpenAMTokenResponseObjectMother {

    fun createOpenAMTokenResponse(tokenId: String, subject: String, authLevel: Int): OpenAMResponse {
        val token = OpenAMToken(tokenId)

        val subjectAttribute = OpenAMAttribute("uid", listOf(subject))
        val authLevelAttribute = OpenAMAttribute("SecurityLevel", listOf(authLevel.toString()))

        val tokenResponse = OpenAMTokenResponse(token, listOf(subjectAttribute, authLevelAttribute))

        return OpenAMResponse.validResponse(tokenResponse)
    }
}