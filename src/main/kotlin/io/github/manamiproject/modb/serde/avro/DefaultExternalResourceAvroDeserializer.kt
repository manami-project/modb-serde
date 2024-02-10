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
 * # What it does
 * + Can download [Apache Avro](https://avro.apache.org) files from manami-project anime-offline-database via HTTPS and deserialize them.
 * + Can deserialize the files from [manami-project/anime-offline-database](https://github.com/manami-project/anime-offline-database) as local [Apache Avro](https://avro.apache.org) files.
 *
 * You can either deserialize the anime dataset file or a dead entries file by using a [URL] or a [RegularFile].
 * To deserialize a simple [ByteArray] use [DefaultAvroDeserializer].
 * @since 5.0.0
 * @param httpClient Used to download given [URL]s
 * @param deserializer Deserializer for either the dataset or dead entries file.
 */
public class DefaultExternalResourceAvroDeserializer(
    private val httpClient: HttpClient = DefaultHttpClient(),
    private val deserializer: AvroDeserializer = DefaultAvroDeserializer(),
) : ExternalResourceAvroDeserializer {

    override suspend fun deserializeAnimeList(url: URL): List<Anime> = withContext(LIMITED_CPU) {
        log.info { "Downloading dataset file from [$url]" }

        val response = httpClient.get(url)

        return@withContext when {
            !response.isOk() -> throw IllegalStateException("Error downloading file: HTTP response code was: [${response.code}]")
            response.body.isEmpty() -> throw IllegalStateException("Error downloading file: The response body was blank.")
            else -> deserializer.deserializeAnimeList(response.body)
        }
    }

    override suspend fun deserializeAnimeList(file: RegularFile): List<Anime> = withContext(LIMITED_FS) {
        require(file.regularFileExists()) { "The given path does not exist or is not a regular file: [${file.toAbsolutePath()}]" }

        log.info { "Reading dataset file." }

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