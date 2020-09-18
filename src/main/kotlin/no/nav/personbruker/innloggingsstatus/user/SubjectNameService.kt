package no.nav.personbruker.innloggingsstatus.user

import no.nav.personbruker.dittnav.common.util.cache.EvictingCache
import no.nav.personbruker.innloggingsstatus.pdl.PdlService

class SubjectNameService(private val pdlService: PdlService, private val cache: EvictingCache<String, String>) {

    suspend fun getSubjectName(subject: String): String {
        return cache.getEntry(subject, this::fetchNameFromPdlAndConcatenate)
            ?: subject
    }

    private suspend fun fetchNameFromPdlAndConcatenate(subject: String): String? {
        return pdlService.getSubjectName(subject)
            ?.let { pdlNavn -> listOf(pdlNavn.fornavn, pdlNavn.mellomnavn, pdlNavn.etternavn) }
            ?.filter { navn -> !navn.isNullOrBlank() }
            ?.joinToString(" ")
    }

}