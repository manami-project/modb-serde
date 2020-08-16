package io.github.manamiproject.modb.dbparser

import io.github.manamiproject.modb.core.Json
import io.github.manamiproject.modb.core.logging.LoggerDelegate
import io.github.manamiproject.modb.core.models.Anime
import io.github.manamiproject.modb.core.models.AnimeSeason
import java.net.URL

class AnimeDatabaseJsonStringParser : JsonStringParser<Anime> {

    override fun parse(json: String): List<Anime> {
        require(json.isNotBlank()) { "Given json string must not be blank." }

        log.info("Parsing database")

        return Json.parseJson<DatabaseData>(json)!!.data.map {
            Anime(
                _title = it.title,
                type = Anime.Type.valueOf(it.type),
                episodes = it.episodes,
                status = Anime.Status.valueOf(it.status),
                animeSeason = AnimeSeason(
                    season = AnimeSeason.Season.of(it.animeSeason.season),
                    _year = it.animeSeason.year ?: 0
                ),
                picture = URL(it.picture),
                thumbnail = URL(it.thumbnail)
            ).apply {
                addSources(it.sources.map { url -> URL(url) })
                addSynonyms(it.synonyms)
                addRelations(it.relations.map { url -> URL(url) })
                addTags(it.tags)
            }
        }
    }

    companion object {
        private val log by LoggerDelegate()
    }
}

internal data class DatabaseData(
    val data: List<DatabaseEntry>
)

internal data class DatabaseEntry(
    val sources: List<String>,
    val title: String,
    val type: String,
    val episodes: Int,
    val status: String,
    val animeSeason: DatabaseEntryAnimeSeason,
    val picture: String,
    val thumbnail: String,
    var synonyms: List<String>,
    var relations: List<String>,
    var tags: List<String>
)

internal data class DatabaseEntryAnimeSeason(
    val season: String,
    val year: Int?
)