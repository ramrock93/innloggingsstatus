package no.nav.personbruker.innloggingsstatus.openam

class OpenAMException : Exception {
    constructor() : super()
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
}