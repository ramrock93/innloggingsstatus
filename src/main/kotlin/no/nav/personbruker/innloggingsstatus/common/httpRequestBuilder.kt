package no.nav.personbruker.innloggingsstatus.common

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import java.util.*

fun HttpRequestBuilder.basicAuth(username: String, password: String) {
    val encodedCredentials = "$username:$password".toBase64()
    header(HttpHeaders.Authorization, "Basic $encodedCredentials")
}

fun HttpRequestBuilder.apiKeyHeader(apiKey: String) {
    header("x-nav-apiKey", apiKey)
}

private fun encode64(string: String): String {
    return string.toByteArray(charset("UTF-8")).let { bytes ->
        Base64.getEncoder().encodeToString(bytes)
    }
}

fun String.toBase64() = encode64(this)