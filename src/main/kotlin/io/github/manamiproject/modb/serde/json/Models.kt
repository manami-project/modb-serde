package io.github.manamiproject.modb.serde.json

import io.github.manamiproject.modb.core.collections.SortedList
import io.github.manamiproject.modb.core.config.AnimeId

internal data class DatasetJsonObject(
    val license: LicenseJsonObject = LicenseJsonObject(),
    val repository: String = "https://github.com/manami-project/anime-offline-database",
    val lastUpdate: String,
    val data: List<DatasetEntryJsonObject>,
)

internal data class LicenseJsonObject(
    val name: String = "GNU Affero General Public License v3.0",
    val url: String = "https://github.com/manami-project/anime-offline-database/blob/master/LICENSE",
)

internal data class DatasetEntryJsonObject(
    val sources: List<String>,
    val title: String,
    val type: String,
    val episodes: Int,
    val status: String,
    val animeSeason: AnimeSeasonJsonObject,
    val picture: String,
    val thumbnail: String,
    var synonyms: List<String>,
    var relations: List<String>,
    var tags: List<String>,
)

internal data class AnimeSeasonJsonObject(
    val season: String,
    val year: Int?,
)

internal data class DeadEntriesDocumentJsonObject(
    val deadEntries: SortedList<AnimeId> = SortedList()
)