package no.nav.personbruker.innloggingsstatus.config

import io.ktor.client.HttpClient
import io.ktor.config.ApplicationConfig
import io.ktor.util.KtorExperimentalAPI
import no.nav.personbruker.dittnav.common.metrics.MetricsReporter
import no.nav.personbruker.dittnav.common.metrics.StubMetricsReporter
import no.nav.personbruker.dittnav.common.metrics.influx.InfluxMetricsReporter
import no.nav.personbruker.dittnav.common.metrics.influx.SensuConfig
import no.nav.personbruker.dittnav.common.util.cache.EvictingCache
import no.nav.personbruker.innloggingsstatus.auth.AuthTokenService
import no.nav.personbruker.innloggingsstatus.common.metrics.MetricsCollector
import no.nav.personbruker.innloggingsstatus.oidc.OidcTokenService
import no.nav.personbruker.innloggingsstatus.oidc.OidcTokenValidator
import no.nav.personbruker.innloggingsstatus.openam.OpenAMConsumer
import no.nav.personbruker.innloggingsstatus.openam.OpenAMTokenService
import no.nav.personbruker.innloggingsstatus.pdl.PdlConsumer
import no.nav.personbruker.innloggingsstatus.pdl.PdlService
import no.nav.personbruker.innloggingsstatus.pdl.query.PdlNavn
import no.nav.personbruker.innloggingsstatus.sts.CachingStsService
import no.nav.personbruker.innloggingsstatus.sts.NonCachingStsService
import no.nav.personbruker.innloggingsstatus.sts.STSConsumer
import no.nav.personbruker.innloggingsstatus.sts.StsService
import no.nav.personbruker.innloggingsstatus.sts.cache.StsTokenCache
import no.nav.personbruker.innloggingsstatus.user.SubjectNameService

@KtorExperimentalAPI
class ApplicationContext(config: ApplicationConfig) {

    val environment = Environment()

    val httpClient = HttpClientBuilder.build()

    val oidcTokenValidator = OidcTokenValidator(config)
    val oidcValidationService = OidcTokenService(oidcTokenValidator)

    val openAMConsumer = OpenAMConsumer(httpClient, environment)
    val openAMValidationService = OpenAMTokenService(openAMConsumer)

    val stsConsumer = STSConsumer(httpClient, environment)
    val pdlConsumer = PdlConsumer(httpClient, environment)
    val stsService = resolveStsService(stsConsumer, environment)
    val pdlService = PdlService(pdlConsumer, stsService)

    val subjectNameCache = EvictingCache<String, String>()
    val subjectNameService = SubjectNameService(pdlService, subjectNameCache)

    val metricsReporter = resolveMetricsReporter(environment)
    val metricsCollector = MetricsCollector(metricsReporter)

    val authTokenService = AuthTokenService(oidcValidationService, openAMValidationService, subjectNameService, metricsCollector)

    val selfTests = listOf(openAMConsumer, stsConsumer, pdlConsumer)
}

private fun resolveMetricsReporter(environment: Environment): MetricsReporter {
    return if (environment.sensuHost == "" || environment.sensuHost == "stub") {
        StubMetricsReporter()
    } else {
        val sensuConfig = SensuConfig(
            applicationName = environment.applicationName,
            hostName = environment.sensuHost,
            hostPort = environment.sensuPort.toInt(),
            clusterName = environment.clusterName,
            namespace = environment.namespace,
            eventsTopLevelName = "personbruker-innloggingsstatus"
        )

        InfluxMetricsReporter(sensuConfig)
    }
}

private fun resolveStsService(stsConsumer: STSConsumer, environment: Environment): StsService {

    return if (environment.stsCacheEnabled.toBoolean()) {
        val stsTokenCache = StsTokenCache(stsConsumer, environment)
        CachingStsService(stsTokenCache)
    } else {
        NonCachingStsService(stsConsumer)
    }
}
