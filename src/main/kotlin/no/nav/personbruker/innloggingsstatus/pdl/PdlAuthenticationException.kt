package no.nav.personbruker.innloggingsstatus.pdl

class PdlAuthenticationException: PdlException {
    constructor() : super()
    constructor(message: String) : super(message)
}