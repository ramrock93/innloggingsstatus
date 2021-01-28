package no.nav.personbruker.innloggingsstatus.cache

import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.slot
import no.nav.personbruker.dittnav.common.cache.EvictingCache

object MockedEvictingCacheFactory {
    inline fun <reified K: Any, reified V: Any> createShallowCache(): EvictingCache<K, V> {
        val key = slot<K>()
        val fetcher = slot<suspend (K)->V?>()

        val mockedCache: EvictingCache<K, V> = mockk()

        coEvery { mockedCache.getEntry(capture(key), capture(fetcher)) } coAnswers {
            fetcher.captured(key.captured)
        }

        return mockedCache
    }
}
