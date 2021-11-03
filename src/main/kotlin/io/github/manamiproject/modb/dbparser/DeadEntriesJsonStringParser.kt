package io.github.manamiproject.modb.dbparser

import io.github.manamiproject.modb.core.Json
import io.github.manamiproject.modb.core.config.AnimeId
import io.github.manamiproject.modb.core.logging.LoggerDelegate

/**
 * Can parse dead entry files from manami-project anime-offline-database.
 * @since 1.0.0
 */
public class DeadEntriesJsonStringParser : JsonParser<AnimeId> {

    override fun parse(json: String): List<AnimeId> {
        require(json.isNotBlank()) { "Given json string must not be blank." }

        log.info { "Parsing dead entries" }

        val jsonDocument: DeadEntriesDocument = Json.parseJson(json)!!
        val deadEntries = jsonDocument.deadEntries

        if (deadEntries.isEmpty()) {
            return emptyList()
        }

        return deadEntries
    }

    private companion object {
        private val log by LoggerDelegate()
    }
}

private data class DeadEntriesDocument(
    val deadEntries: MutableList<String>
)