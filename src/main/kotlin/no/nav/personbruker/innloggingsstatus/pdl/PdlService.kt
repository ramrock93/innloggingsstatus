package no.nav.personbruker.innloggingsstatus.pdl

import no.nav.personbruker.innloggingsstatus.pdl.query.PdlNavn

class PdlService(val pdlConsumer: PdlConsumer) {

    suspend fun getSubjectName(ident: String): PdlNavn? {
        return pdlConsumer.getPersonInfo(ident)?.navn?.first()
    }
}