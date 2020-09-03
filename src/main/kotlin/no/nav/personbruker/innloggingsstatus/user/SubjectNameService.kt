package no.nav.personbruker.innloggingsstatus.user

import no.nav.personbruker.dittnav.common.util.cache.EvictingCache
import no.nav.personbruker.innloggingsstatus.pdl.PdlService
import no.nav.personbruker.innloggingsstatus.pdl.query.PdlNavn

class SubjectNameService(private val pdlService: PdlService, private val cache: EvictingCache<String, PdlNavn>) {

    suspend fun getSubjectName(subject: String): String {
        return cache.getEntry(subject, pdlService::getSubjectName)?.let { pdlNavn ->
            concatenateFullName(pdlNavn)
        }?: ""
    }

    private fun concatenateFullName(pdlnavn: PdlNavn): String {
        return listOfNotNull(pdlnavn.fornavn, pdlnavn.mellomnavn, pdlnavn.etternavn)
            .filter { name -> name.isNotBlank() }
            .joinToString(" ")
    }

}