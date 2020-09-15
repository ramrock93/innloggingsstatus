package no.nav.personbruker.innloggingsstatus.pdl.query

data class PdlResponse(
        val data: PdlData
)

data class PdlData (
        val person: PdlPersonInfo
)