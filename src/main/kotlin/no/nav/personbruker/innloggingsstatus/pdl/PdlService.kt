package no.nav.personbruker.innloggingsstatus.pdl

import no.nav.personbruker.innloggingsstatus.pdl.query.PdlNavn
import no.nav.personbruker.innloggingsstatus.sts.StsService
import org.slf4j.LoggerFactory

class PdlService(private val pdlConsumer: PdlConsumer, private val stsService: StsService) {

    val log = LoggerFactory.getLogger(PdlService::class.java)

    suspend fun getSubjectName(ident: String): PdlNavn? {
        return try {
            stsService.getStsToken().let { stsToken ->
                pdlConsumer.getPersonInfo(ident, stsToken)
                    .navn.first()
            }
        } catch (e: PdlAuthenticationException) {
            stsService.invalidateToken()
            log.info("Invalidating sts token")
            null
        } catch (e: Exception) {
            log.warn("Pdl-related exception.", e)
            null
        }
    }
}