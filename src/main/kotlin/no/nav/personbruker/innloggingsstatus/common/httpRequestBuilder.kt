package no.nav.personbruker.innloggingsstatus.common

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import java.util.*

fun HttpRequestBuilder.basicAuth(username: String, password: String) {
    val encodedCredentials = "$username:$password".toBase64()
    header(HttpHeaders.Authorization, "Basic $encodedCredentials")
}

fun HttpRequestBuilder.bearerAuth(token: String) {
    header(HttpHeaders.Authorization, "Bearer $token")
}

fun HttpRequestBuilder.apiKeyHeader(apiKey: String) {
    header("x-nav-apiKey", apiKey)
}