package io.github.manamiproject.modb.dbparser

import io.github.manamiproject.modb.core.Json
import io.github.manamiproject.modb.core.config.AnimeId
import io.github.manamiproject.modb.core.logging.LoggerDelegate

class DeadEntriesJsonStringParser : JsonStringParser<AnimeId> {

    override fun parse(json: String): List<AnimeId> {
        require(json.isNotBlank()) { "Given json string must not be blank." }

        log.info("Parsing dead entries")

        val jsonDocument: DeadEntriesDocument = Json.parseJson(json)!!
        val deadEntries = jsonDocument.deadEntries

        if (deadEntries.isEmpty()) {
            return emptyList()
        }

        return deadEntries
    }

    companion object {
        private val log by LoggerDelegate()
    }
}

private data class DeadEntriesDocument(
    val deadEntries: MutableList<String>
)