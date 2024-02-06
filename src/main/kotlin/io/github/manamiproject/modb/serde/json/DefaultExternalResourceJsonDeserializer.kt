package io.github.manamiproject.modb.serde.json

import io.github.manamiproject.modb.core.coroutines.ModbDispatchers.LIMITED_CPU
import io.github.manamiproject.modb.core.extensions.RegularFile
import io.github.manamiproject.modb.core.extensions.fileSuffix
import io.github.manamiproject.modb.core.extensions.readFile
import io.github.manamiproject.modb.core.extensions.regularFileExists
import io.github.manamiproject.modb.core.httpclient.DefaultHttpClient
import io.github.manamiproject.modb.core.httpclient.HttpClient
import io.github.manamiproject.modb.core.logging.LoggerDelegate
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.net.URL
import java.util.zip.ZipFile

/**
 * # What it does
 * + Can download JSON files from manami-project anime-offline-database via HTTPS and deserialize them.
 * + Can deserialize the files from manami-project anime-offline-database as local JSON file.
 * + Can deserialize the files from manami-project anime-offline-database as local JSON file if it is provided as zip file.
 *
 * # Usage
 * Wrap an instance of [JsonDeserializer] in a [DefaultExternalResourceJsonDeserializer] to be able to parse a [URL] or a [RegularFile]
 * ```kotlin
 * val animeListDeserializer = DefaultExternalResourceJsonDeserializer<List<Anime>>(fileParser = AnimeDatabaseJsonStringDeserializer())
 * val deadEntriesDeserializer = DefaultExternalResourceJsonDeserializer<List<AnimeId>>(fileParser = DeadEntriesJsonStringDeserializer())
 * ```
 * Now you can either parse the anime database file or a dead entries file by using a [URL], a [RegularFile] or a JSON [String].
 * The deserializer can also handle zipped files, but the zip file must only contain a single JSON file.
 * **Example:**
 * ```kotlin
 * val deserializer = DefaultExternalResourceJsonDeserializer<List<Anime>>(fileParser = AnimeDatabaseJsonStringDeserializer())
 * val allAnime: List<Anime> = parser.parse(URL("https://raw.githubusercontent.com/manami-project/anime-offline-database/master/anime-offline-database-minified.json"))
 * ```
 * @since 5.0.0
 * @param httpClient Used to download given [URL]s
 * @param jsonDeserializer File parser for either the database or dead entries file.
 */
public class DefaultExternalResourceJsonDeserializer<out T>(
    private val httpClient: HttpClient = DefaultHttpClient(),
    private val jsonDeserializer: JsonDeserializer<T>,
) : ExternalResourceJsonDeserializer<T> {

    override suspend fun deserialize(url: URL): T = withContext(LIMITED_CPU) {
        log.info { "Downloading database file from [$url]" }

        val response = httpClient.get(url)

        return@withContext when {
            !response.isOk() -> throw IllegalStateException("Error downloading database file: HTTP response code was: [${response.code}]")
            response.bodyAsText.isBlank() -> throw IllegalStateException("Error downloading database file: The response body was blank.")
            else -> jsonDeserializer.deserialize(response.bodyAsText)
        }
    }

    override suspend fun deserialize(file: RegularFile): T = withContext(LIMITED_CPU) {
        require(file.regularFileExists()) { "The given path does not exist or is not a regular file: [${file.toAbsolutePath()}]" }

        val content =  when(file.fileSuffix()) {
            "json" -> file.readFile()
            "zip" -> readZip(file)
            else -> throw IllegalArgumentException("File is neither JSON nor zip file")
        }

        log.info { "Reading database file" }

        return@withContext jsonDeserializer.deserialize(content)
    }

    private suspend fun readZip(file: RegularFile): String = withContext(IO) {
        return@withContext ZipFile(file.toAbsolutePath().toString()).use { zip ->
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