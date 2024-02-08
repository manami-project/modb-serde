package io.github.manamiproject.modb.serde.json

import io.github.manamiproject.modb.core.collections.SortedList
import io.github.manamiproject.modb.core.config.AnimeId
import io.github.manamiproject.modb.core.models.Anime
import io.github.manamiproject.modb.serde.LICENSE_NAME
import io.github.manamiproject.modb.serde.LICENSE_URL
import io.github.manamiproject.modb.serde.REPO_URL

/**
 * JSON containing the complete dataset.
 * @since 5.0.0
 * @property license Contains the license as seen on the github repository
 * @property repository Link to the github repository
 * @property lastUpdate Day of the last update in the format `yyyy-mm-dd`
 * @property data [List] of the anime.
 */
internal data class JsonDataset(
    internal val license: JsonLicense = JsonLicense(),
    internal val repository: String = REPO_URL,
    internal val lastUpdate: String,
    internal val data: List<JsonDatasetEntry>,
)

/**
 * License info as seen in the github repository.
 * @since 5.0.0
 * @property name Name of the license.
 * @property url URL to the license file.
 */
internal data class JsonLicense(
    internal val name: String = LICENSE_NAME,
    internal val url: String = LICENSE_URL,
)

/**
 * Represents an anime.
 * @since 5.0.0
 * @property sources Duplicate-free list of sources from which this anime was created. Unlike in [Anime] sortation is not guaranteed.
 * @property title Main title.
 * @property synonyms Duplicate-free list of alternative titles. Unlike in [Anime] sortation is not guaranteed.
 * @property type Distribution type.
 * @property episodes Number of episodes.
 * @property status Publishing status.
 * @property picture URI to a (large) poster/cover.
 * @property thumbnail URI to a thumbnail poster/cover.
 * @property relations Duplicate-free list of related anime. Unlike in [Anime] sortation is not guaranteed.
 * @property tags Duplicate-free list of tags.
 * @property animeSeason In which season did the anime premiere
 */
internal data class JsonDatasetEntry(
    internal val sources: List<String>,
    internal val title: String,
    internal val type: String,
    internal val episodes: Int,
    internal val status: String,
    internal val animeSeason: JsonAnimeSeason,
    internal val picture: String,
    internal val thumbnail: String,
    internal var synonyms: List<String>,
    internal var relations: List<String>,
    internal var tags: List<String>,
)

/**
 * Defines the season in which an anime premiered or has been published.
 * @since 5.0.0
 * @property season Season in which the anime premiered.
 * @property year Year in which the anime premiered in the format `yyyy`.
 */
internal data class JsonAnimeSeason(
    internal val season: String,
    internal val year: Int?,
)

/**
 * JSON containing list of dead entries.
 * @since 5.0.0
 * @property deadEntries List of dead entries represented by their ID as [String].
 */
internal data class JsonDeadEntries(
    internal val deadEntries: SortedList<AnimeId> = SortedList()
)