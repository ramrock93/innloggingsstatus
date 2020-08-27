package no.nav.personbruker.innloggingsstatus.sts

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class StsTokenResponse (
    @JsonProperty("access_token")
    val accessToken: String
)