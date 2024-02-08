package io.github.manamiproject.modb.serde.json

import io.github.manamiproject.modb.core.Json
import io.github.manamiproject.modb.core.JsonSerializationOptions.DEACTIVATE_PRETTY_PRINT
import io.github.manamiproject.modb.core.JsonSerializationOptions.DEACTIVATE_SERIALIZE_NULL
import io.github.manamiproject.modb.core.config.AnimeId
import io.github.manamiproject.modb.core.coroutines.ModbDispatchers.LIMITED_CPU
import kotlinx.coroutines.withContext

/**
 * Can serialize a [Collection] of dead entries files from manami-project anime-offline-database.
 * The resulting lists is duplicate free and sorted.
 * @since 5.0.0
 */
public class DeadEntriesJsonSerializer : JsonSerializer<Collection<AnimeId>> {

    override suspend fun serialize(obj: Collection<AnimeId>, minify: Boolean): String = withContext(LIMITED_CPU) {
        val deadEntriesDocument = JsonDeadEntries()

        obj.toSet().forEach {
            deadEntriesDocument.deadEntries.add(it)
        }

        return@withContext if (minify) {
            Json.toJson(deadEntriesDocument, DEACTIVATE_PRETTY_PRINT, DEACTIVATE_SERIALIZE_NULL)
        } else {
            Json.toJson(deadEntriesDocument)
        }
    }
}