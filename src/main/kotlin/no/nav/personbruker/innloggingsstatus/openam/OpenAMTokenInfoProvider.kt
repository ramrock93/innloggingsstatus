package no.nav.personbruker.innloggingsstatus.openam

import no.nav.personbruker.dittnav.common.util.cache.EvictingCache

abstract class OpenAMTokenInfoProvider(private val openAMConsumer: OpenAMConsumer) {

    abstract suspend fun getTokenInfo(essoToken: String): OpenAMTokenInfo?

    protected suspend fun fetchAndMapTokenAttributes(essoToken: String): OpenAMTokenInfo? {
        return fetchTokenAttributes(essoToken).takeIf { response ->
            response.isValid()
        }?.let { tokenResponse ->
            OpenAMTokenInfoFactory.mapOpenAMTokenInfo(tokenResponse)
        }
    }

    private suspend fun fetchTokenAttributes(essoToken: String): OpenAMResponse {
        return try {
            openAMConsumer.getOpenAMTokenAttributes(essoToken)
        } catch (e: Exception) {
            OpenAMResponse.errorResponse()
        }
    }
}

class CachingOpenAmTokenInfoProvider(
    openAMConsumer: OpenAMConsumer,
    private val cache: EvictingCache<String, OpenAMTokenInfo>
) : OpenAMTokenInfoProvider(openAMConsumer) {

    override suspend fun getTokenInfo(essoToken: String): OpenAMTokenInfo? {
        return cache.getEntry(essoToken, this::fetchAndMapTokenAttributes)
    }
}

class NonCachingOpenAmTokenInfoProvider(openAMConsumer: OpenAMConsumer): OpenAMTokenInfoProvider(openAMConsumer) {

    override suspend fun getTokenInfo(essoToken: String): OpenAMTokenInfo? {
        return fetchAndMapTokenAttributes(essoToken)
    }
}

