package io.github.manamiproject.modb.serde.avro

import com.github.avrokotlin.avro4k.Avro
import com.github.avrokotlin.avro4k.io.AvroDecodeFormat
import io.github.manamiproject.modb.core.collections.SortedList
import io.github.manamiproject.modb.core.config.AnimeId
import io.github.manamiproject.modb.core.coroutines.ModbDispatchers.LIMITED_CPU
import io.github.manamiproject.modb.core.models.Anime
import io.github.manamiproject.modb.core.models.AnimeSeason
import io.github.manamiproject.modb.serde.DatasetModel
import io.github.manamiproject.modb.serde.DeadEntriesModel
import io.github.manamiproject.modb.serde.SeasonModel
import io.github.manamiproject.modb.serde.StatusModel
import io.github.manamiproject.modb.serde.TypeModel
import kotlinx.coroutines.withContext
import java.net.URI

/**
 * Deserializes objects from [Apache Avro](https://avro.apache.org).
 * For the anime listtThe resulting list will be sorted by title, type and episodes in that order.
 * The list of [AnimeId] will be sorted as well.
 * @since 5.0.0
 */
public class DefaultAvroDeserializer : AvroDeserializer {

    override suspend fun deserializeAnimeList(animeList: ByteArray): List<Anime> = withContext(LIMITED_CPU) {
        require(animeList.isNotEmpty()) { "Given ByteArray must not be empty." }

        val serializer = DatasetModel.serializer()
        val datasetSchema = Avro.default.schema(serializer)
        val content = mutableListOf<DatasetModel>()

        Avro.default.openInputStream(serializer) {
            decodeFormat = AvroDecodeFormat.Data(datasetSchema, datasetSchema)
        }.from(animeList).use { stream ->
            stream.iterator().asSequence().forEach { item -> content.add(item) }
        }

        return@withContext content.first().data.map { entry ->
            Anime(
                _title = entry.title,
                sources = SortedList(entry.sources.map { urlString -> URI(urlString) }.toMutableList()),
                synonyms = SortedList(entry.synonyms.toMutableList()),
                tags = SortedList(entry.tags.toMutableList()),
                relatedAnime = SortedList(entry.relations.map { urlString -> URI(urlString) }.toMutableList()),
                episodes = entry.episodes,
                picture = URI(entry.picture),
                thumbnail = URI(entry.thumbnail),
                type = when (entry.type) {
                    TypeModel.TV -> Anime.Type.TV
                    TypeModel.MOVIE -> Anime.Type.MOVIE
                    TypeModel.OVA -> Anime.Type.OVA
                    TypeModel.ONA -> Anime.Type.ONA
                    TypeModel.SPECIAL -> Anime.Type.SPECIAL
                    TypeModel.UNKNOWN -> Anime.Type.UNKNOWN
                },
                status = when (entry.status) {
                    StatusModel.FINISHED -> Anime.Status.FINISHED
                    StatusModel.ONGOING -> Anime.Status.ONGOING
                    StatusModel.UPCOMING -> Anime.Status.UPCOMING
                    StatusModel.UNKNOWN -> Anime.Status.UNKNOWN
                },
                animeSeason = AnimeSeason(
                    year = entry.animeSeason.year ?: 0,
                    season = when (entry.animeSeason.season) {
                        SeasonModel.UNDEFINED -> AnimeSeason.Season.UNDEFINED
                        SeasonModel.SPRING -> AnimeSeason.Season.SPRING
                        SeasonModel.SUMMER -> AnimeSeason.Season.SUMMER
                        SeasonModel.FALL -> AnimeSeason.Season.FALL
                        SeasonModel.WINTER -> AnimeSeason.Season.WINTER
                    },
                ),
            )
        }.sortedWith(compareBy({ it.title.lowercase() }, {it.type}, { it.episodes }))
    }

    override suspend fun deserializeDeadEntries(deadEntries: ByteArray): List<AnimeId> = withContext(LIMITED_CPU) {
        require(deadEntries.isNotEmpty()) { "Given ByteArray must not be empty." }

        val serializer = DeadEntriesModel.serializer()
        val datasetSchema = Avro.default.schema(serializer)
        val content = mutableListOf<DeadEntriesModel>()

        Avro.default.openInputStream(serializer) {
            decodeFormat = AvroDecodeFormat.Data(datasetSchema, datasetSchema)
        }.from(deadEntries).use { stream ->
            stream.iterator().asSequence().forEach { item -> content.add(item) }
        }

        return@withContext content.first().deadEntries.sorted()
    }
}