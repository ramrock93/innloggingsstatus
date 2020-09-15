package no.nav.personbruker.innloggingsstatus.common

import com.fasterxml.jackson.databind.ObjectMapper

inline fun <reified T> ObjectMapper.readObject(string: String): T = readValue(string, T::class.java)
