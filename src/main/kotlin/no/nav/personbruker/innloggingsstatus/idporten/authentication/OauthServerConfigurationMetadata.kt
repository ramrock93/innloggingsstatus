package no.nav.personbruker.innloggingsstatus.idporten.authentication

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

// This object holds info returned from idporten's well-known-url
@JsonIgnoreProperties(ignoreUnknown = true)
data class OauthServerConfigurationMetadata(
        @JsonProperty("issuer") val issuer: String,
        @JsonProperty("token_endpoint") val tokenEndpoint: String,
        @JsonProperty("jwks_uri") val jwksUri: String,
        @JsonProperty("authorization_endpoint") var authorizationEndpoint: String = "",
        @JsonProperty("end_session_endpoint") var logoutEndpoint: String = "",
)
