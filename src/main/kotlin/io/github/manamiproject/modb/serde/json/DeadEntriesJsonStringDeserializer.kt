package io.github.manamiproject.modb.serde.json

import io.github.manamiproject.modb.core.coroutines.ModbDispatchers.LIMITED_CPU
import io.github.manamiproject.modb.core.json.Json
import io.github.manamiproject.modb.core.logging.LoggerDelegate
import kotlinx.coroutines.withContext

/**
 * Can deserialize dead entry files from [manami-project/anime-offline-database](https://github.com/manami-project/anime-offline-database).
 * @since 5.0.0
 */
public class DeadEntriesJsonStringDeserializer : JsonDeserializer<DeadEntriesModel> {

    override suspend fun deserialize(json: String): DeadEntriesModel = withContext(LIMITED_CPU) {
        require(json.isNotBlank()) { "Given JSON string must not be blank." }

        log.info { "Parsing dead entries" }

        return@withContext Json.parseJson(json)!!
    }

    private companion object {
        private val log by LoggerDelegate()
    }
}