package no.nav.personbruker.innloggingsstatus.common.metrics

import no.nav.personbruker.dittnav.common.metrics.MetricsReporter
import no.nav.personbruker.innloggingsstatus.auth.AuthInfo
import no.nav.personbruker.innloggingsstatus.auth.UserInfo
import no.nav.personbruker.innloggingsstatus.common.metrics.influx.USER_AUTH_INFO
import no.nav.personbruker.innloggingsstatus.common.metrics.prometheus.PrometheusMetricsCollector

class MetricsCollector(private val metricsReporter: MetricsReporter) {

    suspend fun recordAuthMetrics(authInfo: AuthInfo, userInfo: UserInfo) {
        buildMetricsAggregate(authInfo, userInfo).let { metrics ->
            reportMetrics(metrics)
            PrometheusMetricsCollector.registerAuthMetrics(metrics)
        }
    }

    private fun buildMetricsAggregate(authInfo: AuthInfo, userInfo: UserInfo): MetricsAggregate {
        return MetricsAggregate(
            authenticated = authInfo.authenticated,
            stable = authInfo.stable,
            foundSubjectName = userInfo.name != null,
            operatingAuthLevel = authInfo.authLevel ?: -1,
            oidcMetrics = OidcMetrics.fromAuthInfo(authInfo),
            openAMMetrics = OpenAMMetrics.fromAuthInfo(authInfo)
        )
    }

    private suspend fun reportMetrics(metrics: MetricsAggregate) {
        val fieldMap = listOf(
            "foundSubjectName" to metrics.foundSubjectName,
            "operatingAuthLevel" to metrics.operatingAuthLevel,
            "authenticatedWithOidc" to metrics.oidcMetrics.authenticated,
            "oidcAuthLevel" to metrics.oidcMetrics.authLevel,
            "oidcTokenAgeSeconds" to metrics.oidcMetrics.tokenAgeSeconds,
            "oidcTokenTimeToExpirySeconds" to metrics.oidcMetrics.tokenTimeToExpirySeconds,
            "authenticatedWithOpenAM" to metrics.openAMMetrics.authenticated,
            "openAMAuthLevel" to metrics.openAMMetrics.authLevel
        ).toMap()

        val tagMap = listOf("authenticationState" to metrics.authenticationState.name).toMap()

        metricsReporter.registerDataPoint(USER_AUTH_INFO, fieldMap, tagMap)
    }

}