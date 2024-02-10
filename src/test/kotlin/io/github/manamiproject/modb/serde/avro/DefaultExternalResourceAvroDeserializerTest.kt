package io.github.manamiproject.modb.serde.avro

import com.github.tomakehurst.wiremock.WireMockServer
import io.github.manamiproject.modb.core.config.AnimeId
import io.github.manamiproject.modb.core.extensions.EMPTY
import io.github.manamiproject.modb.core.httpclient.HttpClient
import io.github.manamiproject.modb.core.httpclient.HttpResponse
import io.github.manamiproject.modb.core.models.Anime
import io.github.manamiproject.modb.serde.TestAvroDeserializer
import io.github.manamiproject.modb.serde.TestHttpClient
import io.github.manamiproject.modb.test.*
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import java.net.URI
import java.net.URL
import kotlin.test.Test


internal class DefaultExternalResourceAvroDeserializerTest : MockServerTestCase<WireMockServer> by WireMockServerCreator() {

    @Nested
    inner class DeserializeAnimeListUrlTests {

        @Test
        fun `throws exception if the response code is not 200`() {
            // given
            val testHttpClient = object: HttpClient by TestHttpClient {
                override suspend fun get(url: URL, headers: Map<String, Collection<String>>): HttpResponse = HttpResponse(500, "ERROR".toByteArray())
            }

            val externalResourceDeserializer = DefaultExternalResourceAvroDeserializer(
                httpClient = testHttpClient,
                deserializer = TestAvroDeserializer,
            )

            // when
            val result = exceptionExpected<IllegalStateException> {
                externalResourceDeserializer.deserializeAnimeList(URI("http://localhost$port/anime-offline-database.avro").toURL())
            }

            // then
            assertThat(result).hasMessage("Error downloading file: HTTP response code was: [500]")
        }

        @Test
        fun `throws exception if the response body is blank`() {
            // given
            val testHttpClient = object: HttpClient by TestHttpClient {
                override suspend fun get(url: URL, headers: Map<String, Collection<String>>): HttpResponse = HttpResponse(200, EMPTY.toByteArray())
            }

            val externalResourceDeserializer = DefaultExternalResourceAvroDeserializer(
                httpClient = testHttpClient,
                deserializer = TestAvroDeserializer,
            )

            // when
            val result = exceptionExpected<IllegalStateException> {
                externalResourceDeserializer.deserializeAnimeList(URI("http://localhost$port/anime-offline-database.avro").toURL())
            }

            // then
            assertThat(result).hasMessage("Error downloading file: The response body was blank.")
        }

        @Test
        fun `correctly download and deserialize dataset file`() {
            runBlocking {
                // given
                val testHttpClient = object: HttpClient by TestHttpClient {
                    override suspend fun get(url: URL, headers: Map<String, Collection<String>>): HttpResponse = HttpResponse(
                        code = 200,
                        body = loadTestResource<ByteArray>("avro/deserialization/test_dataset_for_deserialization.avro"),
                    )
                }

                val testDeserializer = object: AvroDeserializer by TestAvroDeserializer {
                    override suspend fun deserializeAnimeList(animeList: ByteArray): List<Anime> = listOf(
                        Anime(
                            _title = "a",
                            type = Anime.Type.OVA,
                        ),
                        Anime(
                            _title = "b",
                            type = Anime.Type.SPECIAL,
                        ),
                        Anime(
                            _title = "c",
                            type = Anime.Type.MOVIE,
                        ),
                    )
                }

                val externalResourceDeserializer = DefaultExternalResourceAvroDeserializer(
                    httpClient = testHttpClient,
                    deserializer = testDeserializer,
                )

                // when
                val result = externalResourceDeserializer.deserializeAnimeList(URI("http://localhost$port/anime-offline-database.json").toURL())

                // then
                assertThat(result).containsExactlyInAnyOrder(
                    Anime(
                        _title = "a",
                        type = Anime.Type.OVA,
                    ),
                    Anime(
                        _title = "b",
                        type = Anime.Type.SPECIAL,
                    ),
                    Anime(
                        _title = "c",
                        type = Anime.Type.MOVIE,
                    ),
                )
            }
        }
    }

    @Nested
    inner class DeserializeDeadEntriesUrlTests {

        @Test
        fun `throws exception if the response code is not 200`() {
            // given
            val testHttpClient = object: HttpClient by TestHttpClient {
                override suspend fun get(url: URL, headers: Map<String, Collection<String>>): HttpResponse = HttpResponse(500, "ERROR".toByteArray())
            }

            val externalResourceDeserializer = DefaultExternalResourceAvroDeserializer(
                httpClient = testHttpClient,
                deserializer = TestAvroDeserializer,
            )

            // when
            val result = exceptionExpected<IllegalStateException> {
                externalResourceDeserializer.deserializeDeadEntries(URI("http://localhost$port/anime-offline-database.avro").toURL())
            }

            // then
            assertThat(result).hasMessage("Error downloading file: HTTP response code was: [500]")
        }

        @Test
        fun `throws exception if the response body is blank`() {
            // given
            val testHttpClient = object: HttpClient by TestHttpClient {
                override suspend fun get(url: URL, headers: Map<String, Collection<String>>): HttpResponse = HttpResponse(200, EMPTY.toByteArray())
            }

            val externalResourceDeserializer = DefaultExternalResourceAvroDeserializer(
                httpClient = testHttpClient,
                deserializer = TestAvroDeserializer,
            )

            // when
            val result = exceptionExpected<IllegalStateException> {
                externalResourceDeserializer.deserializeDeadEntries(URI("http://localhost$port/anime-offline-database.avro").toURL())
            }

            // then
            assertThat(result).hasMessage("Error downloading file: The response body was blank.")
        }

        @Test
        fun `correctly download and deserialize dataset file`() {
            runBlocking {
                // given
                val testHttpClient = object: HttpClient by TestHttpClient {
                    override suspend fun get(url: URL, headers: Map<String, Collection<String>>): HttpResponse = HttpResponse(
                        code = 200,
                        body = loadTestResource<ByteArray>("avro/deserialization/test_dead_entries_for_deserialization.avro"),
                    )
                }

                val testDeserializer = object: AvroDeserializer by TestAvroDeserializer {
                    override suspend fun deserializeDeadEntries(deadEntries: ByteArray): List<AnimeId> = listOf(
                        "123456",
                        "789012",
                        "345678",
                    )
                }

                val externalResourceDeserializer = DefaultExternalResourceAvroDeserializer(
                    httpClient = testHttpClient,
                    deserializer = testDeserializer,
                )

                // when
                val result = externalResourceDeserializer.deserializeDeadEntries(URI("http://localhost$port/anime-offline-database.json").toURL())

                // then
                assertThat(result).containsExactlyInAnyOrder(
                    "123456",
                    "789012",
                    "345678",
                )
            }
        }
    }

    @Nested
    inner class DeserializeAnimeListRegularFileTests {

        @Test
        fun `throws exception if the given path is a directory`() {
            tempDirectory {
                // given
                val externalResourceDeserializer = DefaultExternalResourceAvroDeserializer(
                    httpClient = TestHttpClient,
                    deserializer = TestAvroDeserializer,
                )

                // when
                val result = exceptionExpected<IllegalArgumentException> {
                    externalResourceDeserializer.deserializeAnimeList(tempDir)
                }

                // then
                assertThat(result).hasMessage("The given path does not exist or is not a regular file: [${tempDir.toAbsolutePath()}]")
            }
        }

        @Test
        fun `throws exception if the given file does not exist`() {
            tempDirectory {
                // given
                val externalResourceDeserializer = DefaultExternalResourceAvroDeserializer(
                    httpClient = TestHttpClient,
                    deserializer = TestAvroDeserializer,
                )
                val testFile = tempDir.resolve("anime-offline-database.avro")

                // when
                val result = exceptionExpected<IllegalArgumentException> {
                    externalResourceDeserializer.deserializeAnimeList(testFile)
                }

                // then
                assertThat(result).hasMessage("The given path does not exist or is not a regular file: [${testFile.toAbsolutePath()}]")
            }
        }

        @Test
        fun `correctly deserialize dataset file`() {
            runBlocking {
                // given
                val externalResourceDeserializer = DefaultExternalResourceAvroDeserializer(
                    httpClient = TestHttpClient,
                )

                // when
                val result = externalResourceDeserializer.deserializeAnimeList(testResource("avro/deserialization/test_dataset_for_deserialization.avro"))

                // then
                assertThat(result).containsExactlyInAnyOrder(
                    Anime(
                        _title = "a",
                        type = Anime.Type.OVA,
                    ),
                    Anime(
                        _title = "b",
                        type = Anime.Type.SPECIAL,
                    ),
                    Anime(
                        _title = "c",
                        type = Anime.Type.MOVIE,
                    ),
                )
            }
        }
    }

    @Nested
    inner class DeserializeDeadEntriesRegularFileTests {

        @Test
        fun `throws exception if the given path is a directory`() {
            tempDirectory {
                // given
                val externalResourceDeserializer = DefaultExternalResourceAvroDeserializer(
                    httpClient = TestHttpClient,
                    deserializer = TestAvroDeserializer,
                )

                // when
                val result = exceptionExpected<IllegalArgumentException> {
                    externalResourceDeserializer.deserializeDeadEntries(tempDir)
                }

                // then
                assertThat(result).hasMessage("The given path does not exist or is not a regular file: [${tempDir.toAbsolutePath()}]")
            }
        }

        @Test
        fun `throws exception if the given file does not exist`() {
            tempDirectory {
                // given
                val externalResourceDeserializer = DefaultExternalResourceAvroDeserializer(
                    httpClient = TestHttpClient,
                    deserializer = TestAvroDeserializer,
                )
                val testFile = tempDir.resolve("anime-offline-database.avro")

                // when
                val result = exceptionExpected<IllegalArgumentException> {
                    externalResourceDeserializer.deserializeDeadEntries(testFile)
                }

                // then
                assertThat(result).hasMessage("The given path does not exist or is not a regular file: [${testFile.toAbsolutePath()}]")
            }
        }

        @Test
        fun `correctly deserialize dataset file`() {
            runBlocking {
                // given
                val externalResourceDeserializer = DefaultExternalResourceAvroDeserializer(
                    httpClient = TestHttpClient,
                )

                // when
                val result = externalResourceDeserializer.deserializeDeadEntries(testResource("avro/deserialization/test_dead_entries_for_deserialization.avro"))

                // then
                assertThat(result).containsExactlyInAnyOrder(
                    "123456",
                    "789012",
                    "345678",
                )
            }
        }
    }
}