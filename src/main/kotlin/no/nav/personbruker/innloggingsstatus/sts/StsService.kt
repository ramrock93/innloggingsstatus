package no.nav.personbruker.innloggingsstatus.sts

interface StsService {
    suspend fun getStsToken(): String
    fun invalidateToken()
}