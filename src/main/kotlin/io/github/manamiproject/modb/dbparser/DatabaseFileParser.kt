package io.github.manamiproject.modb.dbparser

import io.github.manamiproject.modb.core.extensions.RegularFile
import io.github.manamiproject.modb.core.extensions.fileSuffix
import io.github.manamiproject.modb.core.extensions.readFile
import io.github.manamiproject.modb.core.extensions.regularFileExists
import io.github.manamiproject.modb.core.httpclient.DefaultHttpClient
import io.github.manamiproject.modb.core.httpclient.HttpClient
import io.github.manamiproject.modb.core.logging.LoggerDelegate
import java.net.URL
import java.util.zip.ZipFile

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
        require(file.regularFileExists()) { "The given path does not exist or is not a regular file: [${file.toAbsolutePath()}]" }

        val content =  when(file.fileSuffix()) {
            "json" -> file.readFile()
            "zip" -> readZip(file)
            else -> throw IllegalArgumentException("File is neither JSON nor zip file")
        }

        log.info { "Reading database file" }

        return fileParser.parse(content)
    }

    private fun readZip(file: RegularFile): String {
        return ZipFile(file.toAbsolutePath().toString()).use { zip ->
            val zipEntries = zip.entries().toList()
            require(zipEntries.size == 1) { "The zip file contains more than one file." }

            val entry = zipEntries.first()
            require(entry.name.endsWith("json")) { "File inside zip archive is not a JSON file." }

            zip.getInputStream(entry).bufferedReader().use { reader ->
                reader.readText()
            }
        }
    }

    private companion object {
        private val log by LoggerDelegate()
    }
}