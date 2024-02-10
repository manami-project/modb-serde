package io.github.manamiproject.modb.serde.json

import io.github.manamiproject.modb.core.Json
import io.github.manamiproject.modb.core.coroutines.ModbDispatchers.LIMITED_CPU
import io.github.manamiproject.modb.core.logging.LoggerDelegate
import io.github.manamiproject.modb.core.models.Anime
import io.github.manamiproject.modb.core.models.AnimeSeason
import io.github.manamiproject.modb.serde.DatasetModel
import kotlinx.coroutines.withContext
import java.net.URI

/**
 * Can deserialize the manami-project anime-offline-database JSON provided as [String].
 * @since 5.0.0
 */
public class AnimeListJsonStringDeserializer : JsonDeserializer<List<Anime>> {

    override suspend fun deserialize(json: String): List<Anime> = withContext(LIMITED_CPU) {
        require(json.isNotBlank()) { "Given JSON string must not be blank." }

        log.info { "Deserializing dataset" }

        return@withContext Json.parseJson<DatasetModel>(json)!!.data.map {
            Anime(
                _title = it.title,
                type = Anime.Type.valueOf(it.type.toString()),
                episodes = it.episodes,
                status = Anime.Status.valueOf(it.status.toString()),
                animeSeason = AnimeSeason(
                    season = AnimeSeason.Season.of(it.animeSeason.season.toString()),
                    year = it.animeSeason.year ?: AnimeSeason.UNKNOWN_YEAR,
                ),
                picture = URI(it.picture),
                thumbnail = URI(it.thumbnail),
            ).apply {
                addSources(it.sources.map { uri -> URI(uri) })
                addSynonyms(it.synonyms)
                addRelations(it.relations.map { uri -> URI(uri) })
                addTags(it.tags)
            }
        }
    }

    private companion object {
        private val log by LoggerDelegate()
    }
}