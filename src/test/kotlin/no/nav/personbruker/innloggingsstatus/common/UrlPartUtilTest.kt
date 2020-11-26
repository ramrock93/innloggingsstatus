package no.nav.personbruker.innloggingsstatus.common

import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test

internal class UrlPartUtilTest {

    @Test
    fun `Should parse domain from origin url, leaving out scheme`() {
        val domain = "sub.test.com"

        val httpUrl = "http://$domain"
        val httpsUrl = "https://$domain"

        UrlPartUtil.parseDomain(httpUrl) `should be equal to` domain
        UrlPartUtil.parseDomain(httpsUrl) `should be equal to` domain
    }

    @Test
    fun `Should parse domain from referrer url, leaving out scheme, path and params`() {
        val domain = "sub.test.com"

        val url = "https://$domain/path/to/something?param=123"

        UrlPartUtil.parseDomain(url) `should be equal to` domain
    }

    @Test
    fun `Should parse path from referer`() {
        val path = "/path/to/something"

        val url = "https://sub.test.com$path"

        UrlPartUtil.parsePath(url) `should be equal to` path
    }

    @Test
    fun `Should ignore path parameters`() {
        val path = "/path/to/something"

        val url = "https://sub.test.com$path?param=123&other=321"

        UrlPartUtil.parsePath(url) `should be equal to` path
    }

    @Test
    fun `Should return slash when no path exists`() {
        val url = "https://sub.test.com"
        val urlWithTrailingSlash = "https://sub.test.com/"
        val urlWithParams = "https://sub.test.com?param=123&other=321"

        UrlPartUtil.parsePath(url) `should be equal to` "/"
        UrlPartUtil.parsePath(urlWithTrailingSlash) `should be equal to` "/"
        UrlPartUtil.parsePath(urlWithParams) `should be equal to` "/"
    }
}