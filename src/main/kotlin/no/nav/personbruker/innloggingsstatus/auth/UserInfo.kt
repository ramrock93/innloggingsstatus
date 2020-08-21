package no.nav.personbruker.innloggingsstatus.auth

data class UserInfo private constructor(
    val authenticated: Boolean,
    val name: String?,
    val authLevel: Int?
) {
    companion object {
        fun authenticated(name: String, authLevel: Int): UserInfo = UserInfo(true, name, authLevel)
        fun unAuthenticated(): UserInfo = UserInfo(false, null, null)
    }
}
