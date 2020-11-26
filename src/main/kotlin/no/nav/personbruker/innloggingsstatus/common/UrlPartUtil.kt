package no.nav.personbruker.innloggingsstatus.common

object UrlPartUtil {

    fun parseDomain(origin: String): String {
        val originRegex = "^(.+)://([^/\\?]+)(.+)?$".toRegex()

        return originRegex.find(origin)?.destructured?.let { (_, domain) ->
            domain
        }?: ""
    }

    fun parsePath(referer: String): String {
        val originRegex = "^(.+)://([^/]+)(/[^\\?]*)(\\?.*)?$".toRegex()

        return originRegex.find(referer)?.destructured?.let { (_, _, path) ->
            path
        }?: "/"
    }
}