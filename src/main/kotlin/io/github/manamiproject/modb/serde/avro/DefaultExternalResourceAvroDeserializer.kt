package io.github.manamiproject.modb.serde.avro

import io.github.manamiproject.modb.core.config.AnimeId
import io.github.manamiproject.modb.core.coroutines.ModbDispatchers.LIMITED_CPU
import io.github.manamiproject.modb.core.coroutines.ModbDispatchers.LIMITED_FS
import io.github.manamiproject.modb.core.extensions.RegularFile
import io.github.manamiproject.modb.core.extensions.regularFileExists
import io.github.manamiproject.modb.core.httpclient.DefaultHttpClient
import io.github.manamiproject.modb.core.httpclient.HttpClient
import io.github.manamiproject.modb.core.logging.LoggerDelegate
import io.github.manamiproject.modb.core.models.Anime
import kotlinx.coroutines.withContext
import java.net.URL
import kotlin.io.path.readBytes

/**
 * Deserializes the anime dataset file and dead entries files in  [Apache Avro](https://avro.apache.org) format either as [URL] or [RegularFile].
 * @since 5.0.0
 */
public class DefaultExternalResourceAvroDeserializer(
    private val httpClient: HttpClient = DefaultHttpClient(),
    private val deserializer: AvroDeserializer = DefaultAvroDeserializer(),
) : ExternalResourceAvroDeserializer {

    override suspend fun deserializeAnimeList(url: URL): List<Anime> = withContext(LIMITED_CPU) {
        log.info { "Downloading database file from [$url]" }

        val response = httpClient.get(url)

        return@withContext when {
            !response.isOk() -> throw IllegalStateException("Error downloading file: HTTP response code was: [${response.code}]")
            response.body.isEmpty() -> throw IllegalStateException("Error downloading file: The response body was blank.")
            else -> deserializer.deserializeAnimeList(response.body)
        }
    }

    override suspend fun deserializeAnimeList(file: RegularFile): List<Anime> = withContext(LIMITED_FS) {
        require(file.regularFileExists()) { "The given path does not exist or is not a regular file: [${file.toAbsolutePath()}]" }

        log.info { "Reading database file." }

        return@withContext deserializer.deserializeAnimeList(file.readBytes())
    }

    override suspend fun deserializeDeadEntries(url: URL): List<AnimeId> = withContext(LIMITED_CPU) {
        log.info { "Downloading dead entries file from [$url]" }

        val response = httpClient.get(url)

        return@withContext when {
            !response.isOk() -> throw IllegalStateException("Error downloading file: HTTP response code was: [${response.code}]")
            response.body.isEmpty() -> throw IllegalStateException("Error downloading file: The response body was blank.")
            else -> deserializer.deserializeDeadEntries(response.body)
        }
    }

    override suspend fun deserializeDeadEntries(file: RegularFile): List<AnimeId> = withContext(LIMITED_FS) {
        require(file.regularFileExists()) { "The given path does not exist or is not a regular file: [${file.toAbsolutePath()}]" }

        log.info { "Reading dead entries file." }

        return@withContext deserializer.deserializeDeadEntries(file.readBytes())
    }

    private companion object {
        private val log by LoggerDelegate()
    }
}