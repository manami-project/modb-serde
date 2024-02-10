package io.github.manamiproject.modb.serde.avro

import io.github.manamiproject.modb.core.config.AnimeId
import io.github.manamiproject.modb.core.models.Anime

/**
 * Serializes objects to [Apache Avro](https://avro.apache.org).
 * @since 5.0.0
 */
public interface AvroSerializer {

    /**
     * Serializes an anime list to the manami-project anime-offline-database file.
     * @since 5.0.0
     * @param animeList Anime list to be serialized.
     * @return Serialized anime dataset in avro format.
     */
    public suspend fun serializeAnimeList(animeList: Collection<Anime>): ByteArray

    /**
     * Serializes a list of dead entries to one of the manami-project anime-offline-database dead entries files.
     * @since 5.0.0
     * @param deadEntries List of anime IDs to be serialized.
     * @return Serialized dead entries list in avro format.
     */
    public suspend fun serializeDeadEntries(deadEntries: Collection<AnimeId>): ByteArray
}