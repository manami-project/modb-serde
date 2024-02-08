package io.github.manamiproject.modb.serde.avro

import com.github.avrokotlin.avro4k.Avro
import com.github.avrokotlin.avro4k.io.AvroDecodeFormat
import io.github.manamiproject.modb.core.collections.SortedList
import io.github.manamiproject.modb.core.config.AnimeId
import io.github.manamiproject.modb.core.coroutines.ModbDispatchers.LIMITED_CPU
import io.github.manamiproject.modb.core.models.Anime
import io.github.manamiproject.modb.core.models.AnimeSeason
import kotlinx.coroutines.withContext
import java.net.URI

/**
 * Deserializes objects to [Apache Avro](https://avro.apache.org).
 * @since 5.0.0
 */
public class DefaultAvroDeserializer : AvroDeserializer {

    override suspend fun deserializeAnimeList(animeList: ByteArray): List<Anime> = withContext(LIMITED_CPU) {
        require(animeList.isNotEmpty()) { "Given ByteArray must not be empty." }

        val serializer = AvroDataset.serializer()
        val datasetSchema = Avro.default.schema(serializer)
        val content = mutableListOf<AvroDataset>()

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
                    AvroType.TV -> Anime.Type.TV
                    AvroType.MOVIE -> Anime.Type.MOVIE
                    AvroType.OVA -> Anime.Type.OVA
                    AvroType.ONA -> Anime.Type.ONA
                    AvroType.SPECIAL -> Anime.Type.SPECIAL
                    AvroType.UNKNOWN -> Anime.Type.UNKNOWN
                },
                status = when (entry.status) {
                    AvroStatus.FINISHED -> Anime.Status.FINISHED
                    AvroStatus.ONGOING -> Anime.Status.ONGOING
                    AvroStatus.UPCOMING -> Anime.Status.UPCOMING
                    AvroStatus.UNKNOWN -> Anime.Status.UNKNOWN
                },
                animeSeason = AnimeSeason(
                    year = entry.animeSeason.year,
                    season = when (entry.animeSeason.season) {
                        AvroSeason.UNDEFINED -> AnimeSeason.Season.UNDEFINED
                        AvroSeason.SPRING -> AnimeSeason.Season.SPRING
                        AvroSeason.SUMMER -> AnimeSeason.Season.SUMMER
                        AvroSeason.FALL -> AnimeSeason.Season.FALL
                        AvroSeason.WINTER -> AnimeSeason.Season.WINTER
                    },
                ),
            )
        }.sortedWith(compareBy({ it.title.lowercase() }, {it.type}, { it.episodes }))
    }

    override suspend fun deserializeDeadEntries(deadEntries: ByteArray): List<AnimeId> = withContext(LIMITED_CPU) {
        require(deadEntries.isNotEmpty()) { "Given ByteArray must not be empty." }

        val serializer = AvroDeadEntries.serializer()
        val datasetSchema = Avro.default.schema(serializer)
        val content = mutableListOf<AvroDeadEntries>()

        Avro.default.openInputStream(serializer) {
            decodeFormat = AvroDecodeFormat.Data(datasetSchema, datasetSchema)
        }.from(deadEntries).use { stream ->
            stream.iterator().asSequence().forEach { item -> content.add(item) }
        }

        return@withContext content.first().deadEntries.sorted()
    }
}