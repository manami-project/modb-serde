package io.github.manamiproject.modb.dbparser

import io.github.manamiproject.modb.core.extensions.EMPTY
import io.github.manamiproject.modb.test.exceptionExpected
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class DeadEntriesJsonParserTest {

    @Test
    fun `throws exception if the json string is empty`() {
        // given
        val deadEntriesFileParser = DeadEntriesJsonStringParser()

        // when
        val result = exceptionExpected<IllegalArgumentException> {
            deadEntriesFileParser.parseSuspendable(EMPTY)
        }

        // then
        assertThat(result).hasMessage("Given json string must not be blank.")
    }

    @Test
    fun `throws exception if the json string is blank`() {
        // given
        val deadEntriesFileParser = DeadEntriesJsonStringParser()

        // when
        val result = exceptionExpected<IllegalArgumentException> {
            deadEntriesFileParser.parseSuspendable("    ")
        }

        // then
        assertThat(result).hasMessage("Given json string must not be blank.")
    }

    @Test
    fun `return empty list of the json array is empty`() {
        // given
        val deadEntriesFileParser = DeadEntriesJsonStringParser()
        val json = """
            {
                "deadEntries": []
            }
        """.trimIndent()

        // when
        val result = runBlocking {
            deadEntriesFileParser.parseSuspendable(json)
        }

        // then
        assertThat(result).isEmpty()
    }

    @Test
    fun `correctly parse list of String`() {
        // given
        val deadEntriesFileParser = DeadEntriesJsonStringParser()
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
        val result = runBlocking {
            deadEntriesFileParser.parseSuspendable(json)
        }

        // then
        assertThat(result).containsExactlyInAnyOrder("kj42fc5--", "lkn6--k44", "l2ht33--1", "1kj5g--41", "3jl253vv9")
    }
}