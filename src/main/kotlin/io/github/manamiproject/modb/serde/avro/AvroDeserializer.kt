package io.github.manamiproject.modb.serde.avro

import io.github.manamiproject.modb.core.config.AnimeId
import io.github.manamiproject.modb.core.models.Anime

/**
 * Deserializes objects from [Apache Avro](https://avro.apache.org).
 * @since 5.0.0
 */
public interface AvroDeserializer {

    /**
     * Deserializes manami-project anime-offline-database file in avro format.
     * @since 5.0.0
     */
    public suspend fun deserializeAnimeList(animeList: ByteArray): List<Anime>

    /**
     * Deserializes one of the manami-project anime-offline-database dead entries files.
     * @since 5.0.0
     */
    public suspend fun deserializeDeadEntries(deadEntries: ByteArray): List<AnimeId>
}