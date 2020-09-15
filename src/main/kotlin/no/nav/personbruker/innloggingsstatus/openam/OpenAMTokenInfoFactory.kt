package no.nav.personbruker.innloggingsstatus.openam

object OpenAMTokenInfoFactory {

    fun mapOpenAMTokenInfo(openAMResponse: OpenAMResponse): OpenAMTokenInfo {
        val ident = openAMResponse.attributeMap.getValue("uid")
        val authLevel = openAMResponse.attributeMap.getValue("SecurityLevel").toInt()

        return OpenAMTokenInfo(ident, authLevel)
    }

    private val OpenAMResponse.attributeMap: Map<String, String> get() {
        return tokenResponse!!.attributes.map {
            it.name to it.values.first()
        }.toMap()
    }
}