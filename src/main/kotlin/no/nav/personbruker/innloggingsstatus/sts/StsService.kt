package no.nav.personbruker.innloggingsstatus.sts

interface StsService {
    suspend fun getStsToken(): String
    suspend fun invalidateToken()
}