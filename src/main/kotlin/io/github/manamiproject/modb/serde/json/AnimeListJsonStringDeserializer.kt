package io.github.manamiproject.modb.serde.json

import io.github.manamiproject.modb.core.coroutines.ModbDispatchers.LIMITED_CPU
import io.github.manamiproject.modb.core.json.Json
import io.github.manamiproject.modb.core.logging.LoggerDelegate
import io.github.manamiproject.modb.serde.json.models.Dataset
import kotlinx.coroutines.withContext

/**
 * Can deserialize the [manami-project/anime-offline-database](https://github.com/manami-project/anime-offline-database) JSON provided as [String].
 * @since 5.0.0
 */
public class AnimeListJsonStringDeserializer : JsonDeserializer<Dataset> {

    override suspend fun deserialize(json: String): Dataset = withContext(LIMITED_CPU) {
        require(json.isNotBlank()) { "Given JSON string must not be blank." }

        log.info { "Deserializing dataset" }

        return@withContext Json.parseJson<Dataset>(json)!!
    }

    private companion object {
        private val log by LoggerDelegate()
    }
}