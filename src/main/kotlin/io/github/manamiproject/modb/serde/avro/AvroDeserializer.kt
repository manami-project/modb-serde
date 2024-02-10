package io.github.manamiproject.modb.serde.avro

import io.github.manamiproject.modb.core.config.AnimeId
import io.github.manamiproject.modb.core.models.Anime

/**
 * Deserializes objects from [Apache Avro](https://avro.apache.org) presented as [ByteArray].
 * @since 5.0.0
 */
public interface AvroDeserializer {

    /**
     * Deserializes manami-project anime-offline-database file in avro format.
     * @since 5.0.0
     * @param animeList Anime dataset file in avro format as [ByteArray].
     * @return List of anime.
     */
    public suspend fun deserializeAnimeList(animeList: ByteArray): List<Anime>

    /**
     * Deserializes one of the manami-project anime-offline-database dead entries files.
     * @since 5.0.0
     * @param deadEntries Dead entries file in avro format as [ByteArray].
     * @return List of anime IDs.
     */
    public suspend fun deserializeDeadEntries(deadEntries: ByteArray): List<AnimeId>
}