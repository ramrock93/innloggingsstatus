package no.nav.personbruker.innloggingsstatus.user

import no.nav.personbruker.innloggingsstatus.pdl.PdlService
import no.nav.personbruker.innloggingsstatus.pdl.query.PdlNavn

class SubjectNameService(private val pdlService: PdlService) {

    suspend fun getSubjectName(subject: String): String {
        return pdlService.getSubjectName(subject)?.let { pdlnavn ->
            concateNateFullName(pdlnavn)
        }?: ""
    }

    private fun concateNateFullName(pdlnavn: PdlNavn): String {
        return listOfNotNull(pdlnavn.fornavn, pdlnavn.mellomnavn, pdlnavn.etternavn)
            .joinToString(" ")
    }

}