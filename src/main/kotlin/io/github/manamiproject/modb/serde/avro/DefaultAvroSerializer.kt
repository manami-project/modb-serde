package io.github.manamiproject.modb.serde.avro

import com.github.avrokotlin.avro4k.Avro
import com.github.avrokotlin.avro4k.io.AvroEncodeFormat
import io.github.manamiproject.modb.core.config.AnimeId
import io.github.manamiproject.modb.core.coroutines.ModbDispatchers.LIMITED_CPU
import io.github.manamiproject.modb.core.logging.LoggerDelegate
import io.github.manamiproject.modb.core.models.Anime
import io.github.manamiproject.modb.core.models.AnimeSeason
import io.github.manamiproject.modb.serde.AnimeSeasonModel
import io.github.manamiproject.modb.serde.DatasetModel
import io.github.manamiproject.modb.serde.DatasetEntryModel
import io.github.manamiproject.modb.serde.DatasetLicenseModel
import io.github.manamiproject.modb.serde.DeadEntriesModel
import io.github.manamiproject.modb.serde.SeasonModel
import io.github.manamiproject.modb.serde.StatusModel
import io.github.manamiproject.modb.serde.TypeModel
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
            DatasetEntryModel(
                title = anime.title,
                sources = anime.sources.map { it.toString() },
                episodes = anime.episodes,
                picture = anime.picture.toString(),
                thumbnail = anime.thumbnail.toString(),
                synonyms = anime.synonyms.toList(),
                relations = anime.relatedAnime.map { it.toString() },
                tags = anime.tags.toList(),
                animeSeason = AnimeSeasonModel(
                    season = when (anime.animeSeason.season) {
                        AnimeSeason.Season.UNDEFINED -> SeasonModel.UNDEFINED
                        AnimeSeason.Season.SPRING -> SeasonModel.SPRING
                        AnimeSeason.Season.SUMMER -> SeasonModel.SUMMER
                        AnimeSeason.Season.FALL -> SeasonModel.FALL
                        AnimeSeason.Season.WINTER -> SeasonModel.WINTER
                    },
                    year = anime.animeSeason.year,
                ),
                status = when (anime.status) {
                    Anime.Status.FINISHED -> StatusModel.FINISHED
                    Anime.Status.ONGOING -> StatusModel.ONGOING
                    Anime.Status.UPCOMING -> StatusModel.UPCOMING
                    Anime.Status.UNKNOWN -> StatusModel.UNKNOWN
                },
                type = when (anime.type) {
                    Anime.Type.TV -> TypeModel.TV
                    Anime.Type.MOVIE -> TypeModel.MOVIE
                    Anime.Type.OVA -> TypeModel.OVA
                    Anime.Type.ONA -> TypeModel.ONA
                    Anime.Type.SPECIAL -> TypeModel.SPECIAL
                    Anime.Type.UNKNOWN -> TypeModel.UNKNOWN
                }
            )
        }

        val dataSet = DatasetModel(
            license = DatasetLicenseModel(
                name = "GNU Affero General Public License v3.0",
                url = "https://github.com/manami-project/anime-offline-database/blob/master/LICENSE",
            ),
            repository = "https://github.com/manami-project/anime-offline-database",
            lastUpdate = LocalDate.now(clock).format(ISO_DATE),
            data = avroAnime,
        )

        val outputStream = ByteArrayOutputStream()
        val datasetSchema = Avro.default.schema(DatasetModel.serializer())

        log.debug { "Serializing dataset into avro format." }

        Avro.default.openOutputStream(DatasetModel.serializer()) {
            encodeFormat = AvroEncodeFormat.Data(CodecFactory.zstandardCodec(16, true, true))
            schema = datasetSchema
        }.to(outputStream).use {
            it.write(dataSet)
            it.flush()
        }

        return@withContext outputStream.toByteArray()
    }

    override suspend fun serializeDeadEntries(deadEntries: Collection<AnimeId>): ByteArray = withContext(LIMITED_CPU) {
        val avroDeadEntries = DeadEntriesModel(deadEntries.distinct())

        val outputStream = ByteArrayOutputStream()
        val datasetSchema = Avro.default.schema(DeadEntriesModel.serializer())

        log.debug { "Serializing dead entries into avro format." }

        Avro.default.openOutputStream(DeadEntriesModel.serializer()) {
            encodeFormat = AvroEncodeFormat.Data(CodecFactory.zstandardCodec(16, true, true))
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