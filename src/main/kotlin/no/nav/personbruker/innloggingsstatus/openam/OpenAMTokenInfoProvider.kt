package no.nav.personbruker.innloggingsstatus.openam

import no.nav.personbruker.dittnav.common.util.cache.EvictingCache
import org.slf4j.LoggerFactory

abstract class OpenAMTokenInfoProvider(private val openAMConsumer: OpenAMConsumer) {

    abstract suspend fun getTokenInfo(essoToken: String): OpenAMTokenInfo?

    private val log = LoggerFactory.getLogger(OpenAMTokenInfoProvider::class.java)

    protected suspend fun fetchAndMapTokenAttributes(essoToken: String): OpenAMTokenInfo? {
        return fetchTokenAttributes(essoToken).takeIf { response ->
            response.isValid()
        }?.let { tokenResponse ->
            OpenAMTokenInfoFactory.mapOpenAMTokenInfo(tokenResponse)
        }
    }

    private suspend fun fetchTokenAttributes(essoToken: String): OpenAMResponse {
        try {
            return openAMConsumer.getOpenAMTokenAttributes(essoToken)
        } catch (e: Exception) {
            log.warn("Fikk feil ved kontakt mot esso/openAM under henting av attributter for esso-token.", e)
            throw e
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

