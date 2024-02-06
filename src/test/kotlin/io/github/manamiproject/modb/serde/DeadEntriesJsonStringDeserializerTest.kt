package io.github.manamiproject.modb.serde

import io.github.manamiproject.modb.core.extensions.EMPTY
import io.github.manamiproject.modb.serde.json.DeadEntriesJsonStringDeserializer
import io.github.manamiproject.modb.test.exceptionExpected
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import kotlin.test.Test

internal class DeadEntriesJsonStringDeserializerTest {

    @Test
    fun `throws exception if the json string is empty`() {
        // given
        val deadEntriesFileParser = DeadEntriesJsonStringDeserializer()

        // when
        val result = exceptionExpected<IllegalArgumentException> {
            deadEntriesFileParser.deserialize(EMPTY)
        }

        // then
        assertThat(result).hasMessage("Given json string must not be blank.")
    }

    @Test
    fun `throws exception if the json string is blank`() {
        // given
        val deadEntriesFileParser = DeadEntriesJsonStringDeserializer()

        // when
        val result = exceptionExpected<IllegalArgumentException> {
            deadEntriesFileParser.deserialize("    ")
        }

        // then
        assertThat(result).hasMessage("Given json string must not be blank.")
    }

    @Test
    fun `return empty list of the json array is empty`() {
        runBlocking {
            // given
            val deadEntriesFileParser = DeadEntriesJsonStringDeserializer()
            val json = """
            {
                "deadEntries": []
            }
        """.trimIndent()

            // when
            val result = deadEntriesFileParser.deserialize(json)

            // then
            assertThat(result).isEmpty()
        }
    }

    @Test
    fun `correctly parse list of String`() {
        runBlocking {
            // given
            val deadEntriesFileParser = DeadEntriesJsonStringDeserializer()
            val json = """
            {
                "deadEntries": [
                    "kj42fc5--",
                    "lkn6--k44",
                    "l2ht33--1",
                    "1kj5g--41",
                    "3jl253vv9"
                ]
            }
        """.trimIndent()

            // when
            val result = deadEntriesFileParser.deserialize(json)

            // then
            assertThat(result).containsExactlyInAnyOrder(
                "kj42fc5--",
                "lkn6--k44",
                "l2ht33--1",
                "1kj5g--41",
                "3jl253vv9",
            )
        }
    }
}