package no.nav.personbruker.innloggingsstatus.pdl

import no.nav.personbruker.innloggingsstatus.pdl.query.PdlNavn
import no.nav.personbruker.innloggingsstatus.pdl.query.PdlPersonInfo

object PdlPersonInfoObjectMother {
    fun createPdlPersonInfo(fornavn: String, mellomnavn: String, etternavn: String): PdlPersonInfo {
        val pdlNavn = PdlNavn(fornavn, mellomnavn, etternavn)
        return PdlPersonInfo(listOf(pdlNavn))
    }
}