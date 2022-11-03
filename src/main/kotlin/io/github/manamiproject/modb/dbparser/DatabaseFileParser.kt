package io.github.manamiproject.modb.dbparser

import io.github.manamiproject.modb.core.coroutines.ModbDispatchers.LIMITED_CPU
import io.github.manamiproject.modb.core.extensions.*
import io.github.manamiproject.modb.core.httpclient.DefaultHttpClient
import io.github.manamiproject.modb.core.httpclient.HttpClient
import io.github.manamiproject.modb.core.logging.LoggerDelegate
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.net.URL
import java.util.zip.ZipFile

/**
 * # What it does
 * + Can download JSON files from manami-project anime-offline-database via HTTPS and parse them.
 * + Can parse the files from manami-project anime-offline-database as local JSON file.
 * + Can parse the files from manami-project anime-offline-database as local JSON file if it is provided as zip file.
 *
 * # Usage
 * Wrap an instance of [JsonParser] in a [DatabaseFileParser] to be able to parse a [URL] or a [RegularFile]
 * ```
 * val animeDatabaseFileParser = DatabaseFileParser<Anime>(fileParser = AnimeDatabaseJsonStringParser())
 * val deadEntriesFileParser = DatabaseFileParser<AnimeId>(fileParser = DeadEntriesJsonStringParser())
 * ```
 * Now you can either parse the anime database file or a dead entries file by using a URL, a file or a JSON string.
 * The parser can also handle zipped files, but the zip file must only contain a single JSON file.
 * **Example:**
 * ```
 * val parser = DatabaseFileParser<Anime>(fileParser = AnimeDatabaseJsonStringParser())
 * val allAnime: List<Anime> = parser.parse(URL("https://raw.githubusercontent.com/manami-project/anime-offline-database/master/anime-offline-database-minified.json"))
 * ```
 * @since 1.0.0
 */
public class DatabaseFileParser<T>(
    private val httpClient: HttpClient = DefaultHttpClient(),
    private val fileParser: JsonParser<T>,
) : ExternalResourceParser<T>, JsonParser<T> by fileParser {

    override suspend fun parse(url: URL): List<T> = withContext(LIMITED_CPU) {
        log.info { "Downloading database file from [$url]" }

        val response = httpClient.get(url)

        return@withContext when {
            !response.isOk() -> throw IllegalStateException("Error downloading database file: HTTP response code was: [${response.code}]")
            response.body.isBlank() -> throw IllegalStateException("Error downloading database file: The response body was blank.")
            else -> fileParser.parse(response.body)
        }
    }

    override suspend fun parse(file: RegularFile): List<T> = withContext(LIMITED_CPU) {
        require(file.regularFileExists()) { "The given path does not exist or is not a regular file: [${file.toAbsolutePath()}]" }

        val content =  when(file.fileSuffix()) {
            "json" -> file.readFile()
            "zip" -> readZip(file)
            else -> throw IllegalArgumentException("File is neither JSON nor zip file")
        }

        log.info { "Reading database file" }

        return@withContext fileParser.parse(content)
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