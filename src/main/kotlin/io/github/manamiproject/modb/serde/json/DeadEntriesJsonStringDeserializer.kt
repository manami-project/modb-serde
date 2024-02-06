package io.github.manamiproject.modb.serde.json

import io.github.manamiproject.modb.core.Json
import io.github.manamiproject.modb.core.config.AnimeId
import io.github.manamiproject.modb.core.coroutines.ModbDispatchers.LIMITED_CPU
import io.github.manamiproject.modb.core.logging.LoggerDelegate
import kotlinx.coroutines.withContext

/**
 * Can deserialize dead entry files from manami-project anime-offline-database.
 * @since 5.0.0
 */
public class DeadEntriesJsonStringDeserializer : JsonDeserializer<List<AnimeId>> {

    override suspend fun deserialize(json: String): List<AnimeId> = withContext(LIMITED_CPU) {
        require(json.isNotBlank()) { "Given json string must not be blank." }

        log.info { "Parsing dead entries" }

        val jsonDocument: DeadEntriesDocument = Json.parseJson(json)!!

        return@withContext jsonDocument.deadEntries
    }

    private companion object {
        private val log by LoggerDelegate()
    }
}

private data class DeadEntriesDocument(
    val deadEntries: MutableList<String>
)