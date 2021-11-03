package no.nav.personbruker.innloggingsstatus.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// This object holds info returned from idporten's well-known-url
@Serializable
data class OauthServerConfigurationMetadata(
        @SerialName("issuer") val issuer: String,
        //@SerialName("token_endpoint") val tokenEndpoint: String,
        @SerialName("jwks_uri") val jwksUri: String,
        //@SerialName("authorization_endpoint") var authorizationEndpoint: String = "",
        //@SerialName("end_session_endpoint") var logoutEndpoint: String = "",
)
