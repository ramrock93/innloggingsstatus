package no.nav.personbruker.innloggingsstatus.common.metrics

data class MetricsAggregate (
    val authenticated: Boolean,
    val stable: Boolean,
    val foundSubjectName: Boolean,
    val operatingAuthLevel: Int,
    val oidcMetrics: OidcMetrics,
    val openAMMetrics: OpenAMMetrics,
    val requestDomain: String,
    val originDomain: String
) {
    val authenticationState get() =
        if (oidcMetrics.authenticated && openAMMetrics.authenticated) {
            if (stable) {
                AuthenticationState.OIDC_AND_OPEN_AM
            } else {
                AuthenticationState.UNSTABLE_OIDC_AND_OPEN_AM
            }
        } else if (oidcMetrics.authenticated && !openAMMetrics.authenticated) {
            AuthenticationState.OIDC
        } else if (!oidcMetrics.authenticated && openAMMetrics.authenticated) {
            AuthenticationState.OPEN_AM
        } else {
            AuthenticationState.NONE
        }
}

enum class AuthenticationState {
    NONE, OIDC, OPEN_AM, OIDC_AND_OPEN_AM, UNSTABLE_OIDC_AND_OPEN_AM
}