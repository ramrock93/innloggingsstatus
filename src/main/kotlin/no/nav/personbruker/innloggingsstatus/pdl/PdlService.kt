package no.nav.personbruker.innloggingsstatus.pdl

import no.nav.personbruker.innloggingsstatus.pdl.query.PdlNavn
import no.nav.personbruker.innloggingsstatus.sts.STSConsumer
import no.nav.personbruker.innloggingsstatus.sts.StsService

class PdlService(private val pdlConsumer: PdlConsumer, private val stsService: StsService) {

    suspend fun getSubjectName(ident: String): PdlNavn? {
        return try {
            stsService.getStsToken().let { stsToken ->
                pdlConsumer.getPersonInfo(ident, stsToken)
                    .navn.first()
            }
        } catch (e: PdlAuthenticationException) {
            stsService.invalidateToken()
            null
        } catch (e: Exception) {
            null
        }
    }
}