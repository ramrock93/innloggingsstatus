package no.nav.personbruker.innloggingsstatus.health

interface SelfTest {
    suspend fun externalServiceStatus(): ServiceStatus
    val externalServiceName: String
}

enum class ServiceStatus {
    OK, ERROR
}