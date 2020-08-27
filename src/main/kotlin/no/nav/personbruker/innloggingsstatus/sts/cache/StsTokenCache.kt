package no.nav.personbruker.innloggingsstatus.sts.cache

import no.nav.personbruker.innloggingsstatus.config.Environment
import no.nav.personbruker.innloggingsstatus.sts.STSConsumer
import no.nav.personbruker.innloggingsstatus.sts.StsService
import org.slf4j.LoggerFactory
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

class StsTokenCache (private val stsConsumer: STSConsumer, environment: Environment): StsService {

    private val lock = ReentrantReadWriteLock()
    private val log = LoggerFactory.getLogger(StsTokenCache::class.java)

    private val tokenSlot = TokenSlot(environment.stsCacheExpiryMarginMinutes.toLong())

    override suspend fun getStsToken(): String {
        return getValidTokenOrNull()
            ?: getRenewedToken()
    }

    override fun invalidateToken() {
        lock.write {
            tokenSlot.clear()
        }
    }

    private fun getValidTokenOrNull(): String? {
        return lock.read {
            if (tokenSlot.holdsValidToken()) {
                tokenSlot.get()
            } else {
                null
            }
        }
    }

    private suspend fun getRenewedToken(): String {
        return lock.write {
            if (tokenSlot.holdsValidToken()) {
                tokenSlot.get()
            } else {
                log.info("Renewing cached sts token")
                val token = stsConsumer.getStsToken()
                tokenSlot.set(token)
                token
            }
        }
    }
}

private class TokenSlot(val expiryMargin: Long) {
    private var token: StsTokenWithClaims? = null

    fun clear() {
        token = null
    }

    fun set(newToken: String) {
        token = StsTokenWithClaims(newToken)
    }

    fun get(): String {
        return token?.tokenString ?: throw RuntimeException("Called 'get' on empty token slot")
    }

    fun holdsValidToken(): Boolean {
        return token != null && !tokenIsExpiringOrExpired()
    }

    private fun tokenIsExpiringOrExpired(): Boolean {
        return token?.expiryClaim
            ?.isBefore(currentTimePlusMargin())
            ?: false
    }

    private fun currentTimePlusMargin(): Instant {
        return Instant.now().plus(expiryMargin, ChronoUnit.MINUTES)
    }
}