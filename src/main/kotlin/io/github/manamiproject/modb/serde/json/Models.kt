package io.github.manamiproject.modb.serde.json

import io.github.manamiproject.modb.core.config.AnimeId
import io.github.manamiproject.modb.core.models.Anime


/**
 * Contains the complete dataset.
 * @since 5.0.0
 * @property license Contains the license as seen on the github repository.
 * @property repository Link to the github repository.
 * @property lastUpdate Day of the last update in the format `yyyy-mm-dd`.
 * @property data [List] of the anime. It's a [List], because it is sorted.
 */
public data class DatasetModel(
    val license: LicenseModel = LicenseModel(),
    val repository: String = "https://github.com/manami-project/anime-offline-database",
    val lastUpdate: String,
    val data: List<Anime>
)

/**
 * License info as seen in the github repository.
 * @since 5.0.0
 * @property name Name of the license.
 * @property url URL to the license file.
 */
public data class LicenseModel(
    val name: String = "GNU Affero General Public License v3.0",
    val url: String = "https://github.com/manami-project/anime-offline-database/blob/master/LICENSE",
)


/**
 * Contains dead entries.
 * @since 5.0.0
 * @property license Contains the license as seen on the github repository.
 * @property repository Link to the github repository.
 * @property lastUpdate Day of the last update in the format `yyyy-mm-dd`.
 * @property deadEntries [List] of dead entries represented by their ID as [String]. It's a [List], because it is sorted.
 */
public data class DeadEntriesModel(
    val license: LicenseModel = LicenseModel(),
    val repository: String = "https://github.com/manami-project/anime-offline-database",
    val lastUpdate: String,
    val deadEntries: List<AnimeId>,
)