package no.nav.personbruker.innloggingsstatus.pdl

open class PdlException : Exception {
    constructor() : super()
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
}