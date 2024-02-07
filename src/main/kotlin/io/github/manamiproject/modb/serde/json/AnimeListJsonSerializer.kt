package io.github.manamiproject.modb.serde.json

import io.github.manamiproject.modb.core.Json
import io.github.manamiproject.modb.core.JsonSerializationOptions.DEACTIVATE_PRETTY_PRINT
import io.github.manamiproject.modb.core.JsonSerializationOptions.DEACTIVATE_SERIALIZE_NULL
import io.github.manamiproject.modb.core.coroutines.ModbDispatchers.LIMITED_CPU
import io.github.manamiproject.modb.core.logging.LoggerDelegate
import io.github.manamiproject.modb.core.models.Anime
import kotlinx.coroutines.withContext
import java.time.Clock
import java.time.LocalDate
import java.time.format.DateTimeFormatter.ISO_DATE

/**
 * Can serialize a [Collection] of [Anime] to the final anime-offline-database JSON file.
 * The resulting list will be sorted by title, type and episodes in that order.
 * @since 5.0.0
 */
public class AnimeListJsonSerializer(
    private val clock: Clock = Clock.systemDefaultZone(),
) : JsonSerializer<Collection<Anime>> {

    override suspend fun serialize(obj: Collection<Anime>, minify: Boolean): String = withContext(LIMITED_CPU) {
        log.info { "Sorting dataset by title, type and episodes" }

        val sortedList = obj.map {
            DatasetEntryJsonObject(
                sources = it.sources.map { source -> source.toString() },
                title = it.title,
                type = it.type.toString(),
                episodes = it.episodes,
                status = it.status.toString(),
                animeSeason = AnimeSeasonJsonObject(
                    year = if (it.animeSeason.year != 0) it.animeSeason.year else null,
                    season = it.animeSeason.season.toString()
                ),
                picture = it.picture.toString(),
                thumbnail = it.thumbnail.toString(),
                synonyms = it.synonyms,
                relations = it.relatedAnime.map { relation -> relation.toString() },
                tags = it.tags
            )
        }.sortedWith(compareBy({ it.title.lowercase() }, {it.type}, { it.episodes }))

        val data = DatasetJsonObject(
            data = sortedList,
            lastUpdate = LocalDate.now(clock).format(ISO_DATE),
        )

        return@withContext if (minify) {
            Json.toJson(data, DEACTIVATE_PRETTY_PRINT, DEACTIVATE_SERIALIZE_NULL)
        } else {
            Json.toJson(data)
        }
    }

    private companion object {
        private val log by LoggerDelegate()
    }
}