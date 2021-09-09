package io.github.manamiproject.modb.dbparser

import io.github.manamiproject.modb.core.extensions.RegularFile
import io.github.manamiproject.modb.core.extensions.readFile
import io.github.manamiproject.modb.core.extensions.regularFileExists
import io.github.manamiproject.modb.core.httpclient.DefaultHttpClient
import io.github.manamiproject.modb.core.httpclient.HttpClient
import io.github.manamiproject.modb.core.logging.LoggerDelegate
import java.net.URL

public class DatabaseFileParser<T>(
    private val httpClient: HttpClient = DefaultHttpClient(),
    private val fileParser: JsonParser<T>
) : ExternalResourceParser<T>, JsonParser<T> by fileParser {

    override fun parse(url: URL): List<T> {
        log.info { "Downloading database file from [$url]" }

        val response = httpClient.get(url)

        return when {
            !response.isOk() -> throw IllegalStateException("Error downloading database file: HTTP response code was: [${response.code}]")
            response.body.isBlank() -> throw IllegalStateException("Error downloading database file: The response body was blank.")
            else -> fileParser.parse(response.body)
        }
    }

    override fun parse(file: RegularFile): List<T> {
        log.info { "Reading database file" }

        return when {
            !file.regularFileExists() -> throw IllegalStateException("The given path does not exist or is not a regular file: [${file.toAbsolutePath()}]")
            else -> fileParser.parse(file.readFile())
        }
    }

    private companion object {
        private val log by LoggerDelegate()
    }
}