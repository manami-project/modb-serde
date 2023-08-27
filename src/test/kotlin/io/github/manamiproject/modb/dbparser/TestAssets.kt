package io.github.manamiproject.modb.dbparser

import io.github.manamiproject.modb.core.httpclient.HttpClient
import io.github.manamiproject.modb.core.httpclient.HttpResponse
import io.github.manamiproject.modb.core.httpclient.RequestBody
import io.github.manamiproject.modb.test.shouldNotBeInvoked
import java.net.URL

internal object TestHttpClient : HttpClient {
    override suspend fun get(url: URL, headers: Map<String, Collection<String>>): HttpResponse = shouldNotBeInvoked()
    override suspend fun post(url: URL, requestBody: RequestBody, headers: Map<String, Collection<String>>): HttpResponse = shouldNotBeInvoked()
}

internal object TestDatabaseFileParser : JsonParser<Int> {
    override suspend fun parse(json: String): List<Int> = shouldNotBeInvoked()
}