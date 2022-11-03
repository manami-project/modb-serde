package io.github.manamiproject.modb.dbparser

import io.github.manamiproject.modb.core.Json
import io.github.manamiproject.modb.core.config.AnimeId
import io.github.manamiproject.modb.core.coroutines.ModbDispatchers.LIMITED_CPU
import io.github.manamiproject.modb.core.logging.LoggerDelegate
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * Can parse dead entry files from manami-project anime-offline-database.
 * @since 1.0.0
 */
public class DeadEntriesJsonStringParser : JsonParser<AnimeId> {

    override suspend fun parse(json: String): List<AnimeId> = withContext(LIMITED_CPU) {
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