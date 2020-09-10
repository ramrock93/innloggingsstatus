package no.nav.personbruker.innloggingsstatus.config

data class Environment(
    val applicationName: String = "innloggingsstatus",
    val openAMServiceUrl: String = getEnvVar("OPEN_AM_REST_SERVICE_URL"),
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
    val stsCacheExpiryMarginMinutes: String  = getEnvVar("STS_CACHE_EXPIRY_MARGIN_MINUTES"),
    val corsAllowedOrigins: String = getEnvVar("CORS_ALLOWED_ORIGINS"),
    val corsAllowedSchemes: List<String> = getEnvVarAsList("CORS_ALLOWED_SCHEMES", listOf("https")),
    val corsAllowedSubdomains: List<String> = getEnvVarAsList("CORS_ALLOWED_SUBDOMAINS", emptyList()),
    val subjectNameCacheThreshold: String = getEnvVar("SUBJECT_NAME_CACHE_THRESHOLD", "4096"),
    val subjectNameCacheExpiryMinutes: String = getEnvVar("SUBJECT_NAME_CACHE_EXPIRY_MINUTES", "30")
)

private fun getEnvVar(varName: String, default: String? = null): String {
    return System.getenv(varName)
        ?: default
        ?: throw IllegalArgumentException("Appen kan ikke starte uten at miljøvariabelen $varName er satt.")
}

private fun getEnvVarAsList(varName: String, default: List<String>? = null): List<String> {
    return System.getenv(varName)
        ?.split(",")
        ?: default
        ?: throw IllegalArgumentException("Appen kan ikke starte uten at miljøvariabelen $varName er satt eller default er gitt.")
}

