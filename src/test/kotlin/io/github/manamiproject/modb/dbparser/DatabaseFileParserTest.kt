package io.github.manamiproject.modb.dbparser

import com.github.tomakehurst.wiremock.WireMockServer
import io.github.manamiproject.modb.core.extensions.EMPTY
import io.github.manamiproject.modb.core.httpclient.HttpClient
import io.github.manamiproject.modb.core.httpclient.HttpResponse
import io.github.manamiproject.modb.test.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.net.URL

internal class DatabaseFileParserTest : MockServerTestCase<WireMockServer> by WireMockServerCreator() {

    @Nested
    inner class ParseUrlTests {

        @Test
        fun `throws exception if the response code is not 200`() {
            // given
            val testHttpClient = object: HttpClient by TestHttpClient {
                override fun get(url: URL, headers: Map<String, List<String>>, retryWith: String): HttpResponse = HttpResponse(500, "ERROR")
            }

            val defaultDatabaseFileParser = DatabaseFileParser(
                httpClient = testHttpClient,
                fileParser = TestDatabaseFileParser
            )

            // when
            val result = org.junit.jupiter.api.assertThrows<IllegalStateException> {
                defaultDatabaseFileParser.parse(URL("http://localhost$port/anime-offline-database.json"))
            }

            // then
            assertThat(result).hasMessage("Error downloading database file: HTTP response code was: [500]")
        }

        @Test
        fun `throws exception if the response body is blank`() {
            // given
            val testHttpClient = object: HttpClient by TestHttpClient {
                override fun get(url: URL, headers: Map<String, List<String>>, retryWith: String): HttpResponse = HttpResponse(200, EMPTY)
            }

            val defaultDatabaseFileParser = DatabaseFileParser(
                httpClient = testHttpClient,
                fileParser = TestDatabaseFileParser
            )

            // when
            val result = org.junit.jupiter.api.assertThrows<IllegalStateException> {
                defaultDatabaseFileParser.parse(URL("http://localhost$port/anime-offline-database.json"))
            }

            // then
            assertThat(result).hasMessage("Error downloading database file: The response body was blank.")
        }

        @Test
        fun `correctly download and parse database file`() {
            // given
            val testHttpClient = object: HttpClient by TestHttpClient {
                override fun get(url: URL, headers: Map<String, List<String>>, retryWith: String): HttpResponse = HttpResponse(
                    code = 200,
                    body = loadTestResource("test_db_for_deserialization.json")
                )
            }

            val testDatabaseFileParser = object: JsonStringParser<Int> by TestDatabaseFileParser {
                override fun parse(json: String): List<Int> = listOf(1, 2, 3, 4, 5)
            }

            val defaultDatabaseFileParser = DatabaseFileParser(
                httpClient = testHttpClient,
                fileParser = testDatabaseFileParser
            )

            // when
            val result = defaultDatabaseFileParser.parse(URL("http://localhost$port/anime-offline-database.json"))

            // then
            assertThat(result).containsExactlyInAnyOrder(1, 2, 3, 4, 5)
        }
    }

    @Nested
    inner class ParseRegularFileTests {

        @Test
        fun `throws exception if the given path is a directory`() {
            tempDirectory {
                // given
                val defaultDatabaseFileParser = DatabaseFileParser(
                    httpClient = TestHttpClient,
                    fileParser = TestDatabaseFileParser
                )

                // when
                val result = org.junit.jupiter.api.assertThrows<IllegalStateException> {
                    defaultDatabaseFileParser.parse(tempDir)
                }

                // then
                assertThat(result).hasMessage("The given path does not exist or is not a regular file: [${tempDir.toAbsolutePath()}]")
            }
        }

        @Test
        fun `throws exception if the given file does not exist`() {
            tempDirectory {
                // given
                val defaultDatabaseFileParser = DatabaseFileParser(
                    httpClient = TestHttpClient,
                    fileParser = TestDatabaseFileParser
                )
                val testFile = tempDir.resolve("anime-offline-database.json")

                // when
                val result = org.junit.jupiter.api.assertThrows<IllegalStateException> {
                    defaultDatabaseFileParser.parse(testFile)
                }

                // then
                assertThat(result).hasMessage("The given path does not exist or is not a regular file: [${testFile.toAbsolutePath()}]")
            }
        }

        @Test
        fun `correctly parse database file`() {
            // given
            val testDatabaseFileParser = object: JsonStringParser<Int> by TestDatabaseFileParser {
                override fun parse(json: String): List<Int> = listOf(1, 2, 4, 5)
            }

            val defaultDatabaseFileParser = DatabaseFileParser(
                httpClient = TestHttpClient,
                fileParser = testDatabaseFileParser
            )

            // when
            val result = defaultDatabaseFileParser.parse(testResource("test_db_for_deserialization.json"))

            // then
            assertThat(result).containsExactlyInAnyOrder(1, 2, 4, 5)
        }
    }
}