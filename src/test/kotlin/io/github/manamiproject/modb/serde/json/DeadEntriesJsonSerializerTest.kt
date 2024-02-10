package io.github.manamiproject.modb.serde.json

import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import kotlin.test.Test

internal class DeadEntriesJsonSerializerTest {

    @Test
    fun `correctly serialize minified`() {
        runBlocking {
            // given
            val serializer = DeadEntriesJsonSerializer()
            val list = setOf(
                "1234",
                "5678",
            )

            // when
            val result = serializer.serialize(list)

            // then
            assertThat(result).isEqualTo("""{"deadEntries":["1234","5678"]}""")
        }
    }

    @Test
    fun `correctly serialize pretty print`() {
        runBlocking {
            // given
            val serializer = DeadEntriesJsonSerializer()
            val list = setOf(
                "1234",
                "5678",
            )

            // when
            val result = serializer.serialize(list, minify = false)

            // then
            assertThat(result).isEqualTo("""
                {
                  "deadEntries": [
                    "1234",
                    "5678"
                  ]
                }
            """.trimIndent())
        }
    }

    @Test
    fun `results are sorted`() {
        runBlocking {
            // given
            val serializer = DeadEntriesJsonSerializer()
            val list = setOf(
                "5678",
                "1234",
            )

            // when
            val result = serializer.serialize(list)

            // then
            assertThat(result).isEqualTo("""{"deadEntries":["1234","5678"]}""")
        }
    }
}