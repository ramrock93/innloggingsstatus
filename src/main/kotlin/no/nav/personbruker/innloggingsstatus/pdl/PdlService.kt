package no.nav.personbruker.innloggingsstatus.pdl

import no.nav.personbruker.innloggingsstatus.pdl.query.PdlNavn
import no.nav.personbruker.innloggingsstatus.sts.STSConsumer

class PdlService(private val pdlConsumer: PdlConsumer, private val stsConsumer: STSConsumer) {

    suspend fun getSubjectName(ident: String): PdlNavn? {
        return stsConsumer.getStsToken()?.let { stsToken ->
            return pdlConsumer.getPersonInfo(ident, stsToken)?.navn?.first()
        }
    }
}