package io.github.manamiproject.modb.dbparser

import io.github.manamiproject.modb.core.httpclient.HttpClient
import io.github.manamiproject.modb.core.httpclient.HttpResponse
import io.github.manamiproject.modb.core.httpclient.RequestBody
import io.github.manamiproject.modb.test.shouldNotBeInvoked
import java.net.URL

internal object TestHttpClient : HttpClient {
    @Deprecated("Use coroutine",
        ReplaceWith("shouldNotBeInvoked()", "io.github.manamiproject.modb.test.shouldNotBeInvoked")
    )
    override fun executeRetryable(retryWith: String, func: () -> HttpResponse): HttpResponse = shouldNotBeInvoked()
    @Deprecated("Will possibly be removed",
        ReplaceWith("shouldNotBeInvoked()", "io.github.manamiproject.modb.test.shouldNotBeInvoked")
    )
    override suspend fun executeRetryableSuspendable(retryWith: String, func: suspend () -> HttpResponse): HttpResponse = shouldNotBeInvoked()
    @Deprecated("Use coroutine",
        ReplaceWith("shouldNotBeInvoked()", "io.github.manamiproject.modb.test.shouldNotBeInvoked")
    )
    override fun get(url: URL, headers: Map<String, Collection<String>>, retryWith: String): HttpResponse = shouldNotBeInvoked()
    override suspend fun getSuspedable(url: URL, headers: Map<String, Collection<String>>, retryWith: String): HttpResponse = shouldNotBeInvoked()
    @Deprecated("Use coroutine",
        ReplaceWith("shouldNotBeInvoked()", "io.github.manamiproject.modb.test.shouldNotBeInvoked")
    )
    override fun post(url: URL, requestBody: RequestBody, headers: Map<String, Collection<String>>, retryWith: String): HttpResponse = shouldNotBeInvoked()
    override suspend fun postSuspendable(url: URL, requestBody: RequestBody, headers: Map<String, Collection<String>>, retryWith: String): HttpResponse = shouldNotBeInvoked()
}

internal object TestDatabaseFileParser : JsonParser<Int> {
    @Deprecated("Use coroutine instead",
        ReplaceWith("shouldNotBeInvoked()", "io.github.manamiproject.modb.test.shouldNotBeInvoked")
    )
    override fun parse(json: String): List<Int> = shouldNotBeInvoked()
    override suspend fun parseSuspendable(json: String): List<Int> = shouldNotBeInvoked()
}