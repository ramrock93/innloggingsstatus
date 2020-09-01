package no.nav.personbruker.innloggingsstatus.pdl.health

data class PdlLivenessResponse (
    val status: LivenessStatus
)

enum class LivenessStatus {
    DOWN, OUT_OF_SERVICE, UP, UNKNOWN
}