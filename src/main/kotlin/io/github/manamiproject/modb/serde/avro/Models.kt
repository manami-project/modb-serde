package io.github.manamiproject.modb.serde.avro

import com.github.avrokotlin.avro4k.AvroName
import com.github.avrokotlin.avro4k.AvroNamespace
import kotlinx.serialization.Serializable
import io.github.manamiproject.modb.core.models.Anime

/**
 * Avro file containing the complete dataset.
 * @since 5.0.0
 * @property license Contains the license as seen on the github repository
 * @property repository Link to the github repository
 * @property lastUpdate Day of the last update in the format `yyyy-mm-dd`
 * @property data [List] of the anime.
 */
@Serializable
@AvroName("Dataset")
@AvroNamespace("io.github.manamiproject.modb.serde.avro")
internal data class AvroDataset(
    internal val license: AvroDatasetLicense,
    internal val repository: String,
    internal val lastUpdate: String,
    internal val data: List<AvroDatasetEntry>
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
@Serializable
@AvroName("Anime")
@AvroNamespace("io.github.manamiproject.modb.serde.avro")
internal data class AvroDatasetEntry(
    internal val sources: List<String>,
    internal val title: String,
    internal val type: AvroType,
    internal val episodes: Int,
    internal val status: AvroStatus,
    internal val animeSeason: AvroAnimeSeason,
    internal val picture: String,
    internal val thumbnail: String,
    internal val synonyms: List<String>,
    internal val relations: List<String>,
    internal val tags: List<String>,
)

/**
 * Defines the season in which an anime premiered or has been published.
 * @since 5.0.0
 * @property season Season in which the anime premiered.
 * @property year Year in which the anime premiered in the format `yyyy`.
 */
@Serializable
@AvroName("AnimeSeason")
@AvroNamespace("io.github.manamiproject.modb.serde.avro")
internal data class AvroAnimeSeason(
    internal val season: AvroSeason,
    internal val year: Int,
)

/**
 * License info as seen in the github repository.
 * @since 5.0.0
 * @property name Name of the license.
 * @property url URL to the license file.
 */
@Serializable
@AvroName("License")
@AvroNamespace("io.github.manamiproject.modb.serde.avro")
internal data class AvroDatasetLicense(
    internal val name: String,
    internal val url: String,
)

/**
 * Season in which an anime premiered.
 * @since 5.0.0
 */
@Serializable
@AvroName("Season")
@AvroNamespace("io.github.manamiproject.modb.serde.avro")
internal enum class AvroSeason {
    UNDEFINED,
    SPRING,
    SUMMER,
    FALL,
    WINTER;
}

/**
 * Distribution status of an anime.
 * @since 5.0.0
 */
@Serializable
@AvroName("Status")
@AvroNamespace("io.github.manamiproject.modb.serde.avro")
internal enum class AvroStatus {
    FINISHED,
    ONGOING,
    UPCOMING,
    UNKNOWN;
}

/**
 * Distribution type of an anime.
 * @since 5.0.0
 */
@Serializable
@AvroName("Type")
@AvroNamespace("io.github.manamiproject.modb.serde.avro")
internal enum class AvroType {
    TV,
    MOVIE,
    OVA,
    ONA,
    SPECIAL,
    UNKNOWN;
}

/**
 * Avro file containing dead entries.
 * @since 5.0.0
 * @property deadEntries List of dead entries represented by their ID as [String].
 */
@Serializable
@AvroName("DeadEntries")
@AvroNamespace("io.github.manamiproject.modb.serde.avro")
internal data class AvroDeadEntries(
    internal val deadEntries: List<String>,
)