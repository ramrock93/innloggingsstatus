package no.nav.personbruker.innloggingsstatus.config

import com.auth0.jwk.JwkProvider
import com.auth0.jwk.JwkProviderBuilder
import io.ktor.auth.*
import io.ktor.client.*
import io.ktor.config.ApplicationConfig
import io.ktor.http.*
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.runBlocking
import no.nav.personbruker.dittnav.common.metrics.MetricsReporter
import no.nav.personbruker.dittnav.common.metrics.StubMetricsReporter
import no.nav.personbruker.dittnav.common.metrics.influx.InfluxMetricsReporter
import no.nav.personbruker.dittnav.common.metrics.influx.SensuConfig
import no.nav.personbruker.dittnav.common.cache.EvictingCache
import no.nav.personbruker.dittnav.common.cache.EvictingCacheConfig
import no.nav.personbruker.innloggingsstatus.auth.AuthTokenService
import no.nav.personbruker.innloggingsstatus.common.metrics.MetricsCollector
import no.nav.personbruker.innloggingsstatus.idporten.authentication.IdPortenService
import no.nav.personbruker.innloggingsstatus.idporten.authentication.IdportenClientInterceptor
import no.nav.personbruker.innloggingsstatus.idporten.authentication.OauthServerConfigurationMetadata
import no.nav.personbruker.innloggingsstatus.idporten.authentication.config.Idporten
import no.nav.personbruker.innloggingsstatus.oidc.OidcTokenService
import no.nav.personbruker.innloggingsstatus.oidc.OidcTokenValidator
import no.nav.personbruker.innloggingsstatus.openam.*
import no.nav.personbruker.innloggingsstatus.pdl.PdlConsumer
import no.nav.personbruker.innloggingsstatus.pdl.PdlService
import no.nav.personbruker.innloggingsstatus.sts.CachingStsService
import no.nav.personbruker.innloggingsstatus.sts.NonCachingStsService
import no.nav.personbruker.innloggingsstatus.sts.STSConsumer
import no.nav.personbruker.innloggingsstatus.sts.StsService
import no.nav.personbruker.innloggingsstatus.sts.cache.StsTokenCache
import no.nav.personbruker.innloggingsstatus.tokendings.TokendingsService
import no.nav.personbruker.innloggingsstatus.tokendings.TokendingsTokenValidator
import no.nav.personbruker.innloggingsstatus.user.SubjectNameService
import java.net.URL
import java.util.concurrent.TimeUnit

@KtorExperimentalAPI
internal class ApplicationContext(config: ApplicationConfig) {

    val environment = Environment()

    val httpClient = HttpClientBuilder.build()

    val oidcTokenValidator = OidcTokenValidator(config)
    val oidcValidationService = OidcTokenService(oidcTokenValidator, environment)

    val openAMConsumer = OpenAMConsumer(httpClient, environment)
    val openAMTokenInfoProvider = setupOpenAmTokenInfoProvider(openAMConsumer, environment)
    val openAMValidationService = OpenAMTokenService(openAMTokenInfoProvider)

    val stsConsumer = STSConsumer(httpClient, environment)
    val pdlConsumer = PdlConsumer(httpClient, environment)
    val stsService = resolveStsService(stsConsumer, environment)
    val pdlService = PdlService(pdlConsumer, stsService)

    val subjectNameCache = setupSubjectNameCache(environment)
    val subjectNameService = SubjectNameService(pdlService, subjectNameCache)

    val metricsReporter = resolveMetricsReporter(environment)
    val metricsCollector = MetricsCollector(metricsReporter)

    val idportenTokenCookieName = "innloggingstatus_idporten"
    val idportenMetadata = fetchMetadata(
        httpClient,
        environment.idportenWellKnownUrl
    )

    private val idportenClientInterceptor = createIdPortenClientInterceptor(environment, idportenMetadata)
    val oauth2ServerSettings = createOAuth2ServerSettings(
        environment,
        idportenMetadata,
        idportenClientInterceptor
    )
    val idportenJwkProvider = createJwkProvider(idportenMetadata)

    val idportenService = IdPortenService(idportenJwkProvider, idportenMetadata, environment)
    val tokendingsTokenValidator = TokendingsTokenValidator(config)
    val tokendingsService = TokendingsService(tokendingsTokenValidator, idportenService)

    val authTokenService = AuthTokenService(oidcValidationService, openAMValidationService, subjectNameService, tokendingsService, metricsCollector)

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
            eventsTopLevelName = "personbruker-innloggingsstatus",
            enableEventBatching = environment.sensuBatchingEnabled,
            eventBatchesPerSecond = environment.sensuBatchesPerSecond
        )

        InfluxMetricsReporter(sensuConfig)
    }
}

private fun resolveStsService(stsConsumer: STSConsumer, environment: Environment): StsService {

    return if (environment.stsCacheEnabled) {
        val stsTokenCache = StsTokenCache(stsConsumer, environment)
        CachingStsService(stsTokenCache)
    } else {
        NonCachingStsService(stsConsumer)
    }
}

private fun setupSubjectNameCache(environment: Environment): EvictingCache<String, String> {
    val cacheThreshold = environment.subjectNameCacheThreshold
    val cacheExpiryMinutes = environment.subjectNameCacheExpiryMinutes

    val evictingCacheConfig = EvictingCacheConfig(
        evictionThreshold = cacheThreshold,
        entryLifetimeMinutes = cacheExpiryMinutes
    )

    return EvictingCache(evictingCacheConfig)
}

private fun setupOpenAmTokenInfoProvider(openAMConsumer: OpenAMConsumer, environment: Environment): OpenAMTokenInfoProvider {
    return if (environment.openAmTokenInfoCacheEnabled) {
        val openAmTokenInfoCache = setupOpenAMTokenInfoCache(environment)
        CachingOpenAmTokenInfoProvider(openAMConsumer, openAmTokenInfoCache)
    } else {
        NonCachingOpenAmTokenInfoProvider(openAMConsumer)
    }
}

private fun setupOpenAMTokenInfoCache(environment: Environment): EvictingCache<String, OpenAMTokenInfo> {
    val cacheThreshold = environment.openAmTokenInfoCacheThreshold
    val cacheExpiryMinutes = environment.openAmTokenInfoCacheExpiryMinutes

    val evictingCacheConfig = EvictingCacheConfig(
        evictionThreshold = cacheThreshold,
        entryLifetimeMinutes = cacheExpiryMinutes
    )

    return EvictingCache(evictingCacheConfig)
}

private fun createOAuth2ServerSettings(
    environment: Environment,
    metadata: OauthServerConfigurationMetadata,
    idportenClientInterceptor: IdportenClientInterceptor
) = OAuthServerSettings.OAuth2ServerSettings(
    name = "IdPorten",
    authorizeUrl = metadata.authorizationEndpoint,
    accessTokenUrl = metadata.tokenEndpoint,
    clientId = environment.idportenClientId,
    clientSecret = "",
    accessTokenRequiresBasicAuth = false,
    requestMethod = HttpMethod.Post,
    defaultScopes = listOf(Idporten.scope),
    authorizeUrlInterceptor = createAuthorizeUrlInterceptor(),
    accessTokenInterceptor = idportenClientInterceptor.appendClientAssertion
)

private fun createAuthorizeUrlInterceptor(): URLBuilder.() -> Unit {
    return {
        parameters.append("response_mode", "query")
        parameters.append("acr_values", "Level4")       // SecurityLevel 4
//      parameters.append("acr_values", "Level3                     // SecurityLevel 3
//      {}                                                          // SecurityLevel unspecified
    }
}

private fun fetchMetadata(httpClient: HttpClient, idPortenUrl: String) = runBlocking {
    httpClient.getOAuthServerConfigurationMetadata(idPortenUrl)
}

private fun createJwkProvider(metadata: OauthServerConfigurationMetadata): JwkProvider = JwkProviderBuilder(URL(metadata.jwksUri))
    .cached(10, 24, TimeUnit.HOURS)
    .rateLimited(10, 1, TimeUnit.MINUTES)
    .build()

private fun createIdPortenClientInterceptor(environment: Environment, metadata: OauthServerConfigurationMetadata) = IdportenClientInterceptor(
    privateJwk = environment.idportenClientJwk,
    clientId = environment.idportenClientId,
    audience = metadata.issuer
)