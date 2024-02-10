package io.github.manamiproject.modb.serde.avro

import io.github.manamiproject.modb.core.config.AnimeId
import io.github.manamiproject.modb.core.extensions.RegularFile
import io.github.manamiproject.modb.core.models.Anime
import java.net.URL

/**
 * Parses external resources such as [RegularFile]s or [URL]s.
 * @since 5.0.0
 */
public interface ExternalResourceAvroDeserializer {

    /**
     * Deserializes the anime list retrieved from an [URL].
     * @since 5.0.0
     * @return Anime list of sorted by title, type and episodes in that order.
     */
    public suspend fun deserializeAnimeList(url: URL): List<Anime>

    /**
     * Deserializes the anime list retrieved from a [RegularFile] to a [List] of [Anime].
     * @since 5.0.0
     * @return Anime list of sorted by title, type and episodes in that order.
     */
    public suspend fun deserializeAnimeList(file: RegularFile): List<Anime>

    /**
     * Deserializes a dead entries file retrieved from an [URL].
     * @since 5.0.0
     * @return List of dead entries. Sorted and duplicate free.
     */
    public suspend fun deserializeDeadEntries(url: URL): List<AnimeId>

    /**
     * Deserializes a dead entries file retrieved from a [RegularFile].
     * @since 5.0.0
     * @return List of dead entries. Sorted and duplicate free.
     */
    public suspend fun deserializeDeadEntries(file: RegularFile): List<AnimeId>
}