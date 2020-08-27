package no.nav.personbruker.innloggingsstatus.config

data class Environment(
    val applicationName: String = "innloggingsstatus",
    val openAMServiceUrl: String = getEnvVar("OPENAM_REST_SERVICE_URL"),
    val oidcIssuer: String = getEnvVar("OIDC_ISSUER"),
    val oidcDiscoveryUrl: String = getEnvVar("OIDC_DISCOVERY_URL"),
    val oidcAcceptetAudience: String = getEnvVar("OIDC_ACCEPTED_AUDIENCE"),
    val securityTokenServiceUrl: String = getEnvVar("SECURITY_TOKEN_SERVICE_URL"),
    val stsApiGWKey: String = getEnvVar("STS_API_GW_KEY"),
    val pdlApiUrl: String = getEnvVar("PDL_API_URL"),
    val pdlApiGWKey: String = getEnvVar("PDL_API_GW_KEY"),
    val serviceUsername: String = getEnvVar("SERVICEUSER_USERNAME"),
    val servicePassword: String = getEnvVar("SERVICEUSER_PASSWORD"),
    val clusterName: String = getEnvVar("NAIS_CLUSTER_NAME"),
    val namespace: String = getEnvVar("NAIS_NAMESPACE"),
    val sensuHost: String = getEnvVar("SENSU_HOST"),
    val sensuPort: String = getEnvVar("SENSU_PORT"),
    val stsCacheEnabled: String  = getEnvVar("STS_CACHE_ENABLED"),
    val stsCacheExpiryMarginMinutes: String  = getEnvVar("STS_CACHE_EXPIRY_MARGIN_MINUTES")
)

private fun getEnvVar(varName: String): String {
    return System.getenv(varName)
            ?: throw IllegalArgumentException("Appen kan ikke starte uten at miljøvariabelen $varName er satt.")
}
