package io.github.manamiproject.modb.dbparser

import io.github.manamiproject.modb.core.httpclient.HttpClient
import io.github.manamiproject.modb.core.httpclient.HttpResponse
import io.github.manamiproject.modb.core.httpclient.RequestBody
import io.github.manamiproject.modb.test.shouldNotBeInvoked
import java.net.URL

internal object TestHttpClient : HttpClient {
    override fun executeRetryable(retryWith: String, func: () -> HttpResponse): HttpResponse = shouldNotBeInvoked()
    override fun get(url: URL, headers: Map<String, Collection<String>>, retryWith: String): HttpResponse = shouldNotBeInvoked()
    override fun post(url: URL, requestBody: RequestBody, headers: Map<String, Collection<String>>, retryWith: String): HttpResponse = shouldNotBeInvoked()
}

internal object TestDatabaseFileParser : JsonParser<Int> {
    override fun parse(json: String): List<Int> = shouldNotBeInvoked()
}