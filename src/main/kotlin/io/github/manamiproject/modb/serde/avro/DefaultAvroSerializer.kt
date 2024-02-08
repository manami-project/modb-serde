package io.github.manamiproject.modb.serde.avro

import com.github.avrokotlin.avro4k.Avro
import com.github.avrokotlin.avro4k.io.AvroEncodeFormat
import io.github.manamiproject.modb.core.config.AnimeId
import io.github.manamiproject.modb.core.coroutines.ModbDispatchers.LIMITED_CPU
import io.github.manamiproject.modb.core.logging.LoggerDelegate
import io.github.manamiproject.modb.core.models.Anime
import io.github.manamiproject.modb.core.models.AnimeSeason
import io.github.manamiproject.modb.serde.LICENSE_NAME
import io.github.manamiproject.modb.serde.LICENSE_URL
import io.github.manamiproject.modb.serde.REPO_URL
import kotlinx.coroutines.withContext
import org.apache.avro.file.CodecFactory
import java.io.ByteArrayOutputStream
import java.time.Clock
import java.time.LocalDate
import java.time.format.DateTimeFormatter.ISO_DATE

/**
 * Serializes objects to [Apache Avro](https://avro.apache.org).
 * Uses zstandard codec 16.
 * @since 5.0.0
 */
public class DefaultAvroSerializer(
    private val clock: Clock = Clock.systemDefaultZone(),
) : AvroSerializer {

    override suspend fun serializeAnimeList(animeList: Collection<Anime>): ByteArray = withContext(LIMITED_CPU) {
        val avroAnime = animeList.map { anime ->
            AvroDatasetEntry(
                title = anime.title,
                sources = anime.sources.map { it.toString() },
                episodes = anime.episodes,
                picture = anime.picture.toString(),
                thumbnail = anime.thumbnail.toString(),
                synonyms = anime.synonyms.toList(),
                relations = anime.relatedAnime.map { it.toString() },
                tags = anime.tags.toList(),
                animeSeason = AvroAnimeSeason(
                    season = when (anime.animeSeason.season) {
                        AnimeSeason.Season.UNDEFINED -> AvroSeason.UNDEFINED
                        AnimeSeason.Season.SPRING -> AvroSeason.SPRING
                        AnimeSeason.Season.SUMMER -> AvroSeason.SUMMER
                        AnimeSeason.Season.FALL -> AvroSeason.FALL
                        AnimeSeason.Season.WINTER -> AvroSeason.WINTER
                    },
                    year = anime.animeSeason.year,
                ),
                status = when (anime.status) {
                    Anime.Status.FINISHED -> AvroStatus.FINISHED
                    Anime.Status.ONGOING -> AvroStatus.ONGOING
                    Anime.Status.UPCOMING -> AvroStatus.UPCOMING
                    Anime.Status.UNKNOWN -> AvroStatus.UNKNOWN
                },
                type = when (anime.type) {
                    Anime.Type.TV -> AvroType.TV
                    Anime.Type.MOVIE -> AvroType.MOVIE
                    Anime.Type.OVA -> AvroType.OVA
                    Anime.Type.ONA -> AvroType.ONA
                    Anime.Type.SPECIAL -> AvroType.SPECIAL
                    Anime.Type.UNKNOWN -> AvroType.UNKNOWN
                }
            )
        }

        val dataSet = AvroDataset(
            license = AvroDatasetLicense(
                name = LICENSE_NAME,
                url = LICENSE_URL,
            ),
            repository = REPO_URL,
            lastUpdate = LocalDate.now(clock).format(ISO_DATE),
            data = avroAnime,
        )

        val outputStream = ByteArrayOutputStream()
        val datasetSchema = Avro.default.schema(AvroDataset.serializer())

        log.debug { "Serializing dataset into avro format." }

        Avro.default.openOutputStream(AvroDataset.serializer()) {
            encodeFormat = AvroEncodeFormat.Data(CodecFactory.zstandardCodec(16))
            schema = datasetSchema
        }.to(outputStream).use {
            it.write(dataSet)
            it.flush()
        }

        return@withContext outputStream.toByteArray()
    }

    override suspend fun serializeDeadEntries(deadEntries: Collection<AnimeId>): ByteArray = withContext(LIMITED_CPU) {
        val avroDeadEntries = AvroDeadEntries(deadEntries.distinct())

        val outputStream = ByteArrayOutputStream()
        val datasetSchema = Avro.default.schema(AvroDeadEntries.serializer())

        log.debug { "Serializing dead entries into avro format." }

        Avro.default.openOutputStream(AvroDeadEntries.serializer()) {
            encodeFormat = AvroEncodeFormat.Data(CodecFactory.zstandardCodec(16))
            schema = datasetSchema
        }.to(outputStream).use {
            it.write(avroDeadEntries)
            it.flush()
        }

        return@withContext outputStream.toByteArray()
    }

    private companion object {
        private val log by LoggerDelegate()
    }
}