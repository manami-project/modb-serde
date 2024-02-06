package io.github.manamiproject.modb.serde

import com.github.tomakehurst.wiremock.WireMockServer
import io.github.manamiproject.modb.core.extensions.EMPTY
import io.github.manamiproject.modb.core.httpclient.HttpClient
import io.github.manamiproject.modb.core.httpclient.HttpResponse
import io.github.manamiproject.modb.core.models.Anime
import io.github.manamiproject.modb.core.models.AnimeSeason
import io.github.manamiproject.modb.serde.json.AnimeListJsonStringDeserializer
import io.github.manamiproject.modb.serde.json.DefaultExternalResourceJsonDeserializer
import io.github.manamiproject.modb.serde.json.JsonDeserializer
import io.github.manamiproject.modb.test.*
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import java.net.URI
import java.net.URL
import kotlin.test.Test

internal class DefaultExternalResourceJsonDeserializerTest : MockServerTestCase<WireMockServer> by WireMockServerCreator() {

    @Nested
    inner class DeserializeUrlTests {

        @Test
        fun `throws exception if the response code is not 200`() {
            // given
            val testHttpClient = object: HttpClient by TestHttpClient {
                override suspend fun get(url: URL, headers: Map<String, Collection<String>>): HttpResponse = HttpResponse(500, "ERROR".toByteArray())
            }

            val defaultDatabaseFileParser = DefaultExternalResourceJsonDeserializer(
                httpClient = testHttpClient,
                jsonDeserializer = TestJsonDeserializer,
            )

            // when
            val result = exceptionExpected<IllegalStateException> {
                defaultDatabaseFileParser.deserialize(URI("http://localhost$port/anime-offline-database.json").toURL())
            }

            // then
            assertThat(result).hasMessage("Error downloading database file: HTTP response code was: [500]")
        }

        @Test
        fun `throws exception if the response body is blank`() {
            // given
            val testHttpClient = object: HttpClient by TestHttpClient {
                override suspend fun get(url: URL, headers: Map<String, Collection<String>>): HttpResponse = HttpResponse(200, EMPTY.toByteArray())
            }

            val defaultDatabaseFileParser = DefaultExternalResourceJsonDeserializer(
                httpClient = testHttpClient,
                jsonDeserializer = TestJsonDeserializer,
            )

            // when
            val result = exceptionExpected<IllegalStateException> {
                defaultDatabaseFileParser.deserialize(URI("http://localhost$port/anime-offline-database.json").toURL())
            }

            // then
            assertThat(result).hasMessage("Error downloading database file: The response body was blank.")
        }

        @Test
        fun `correctly download and parse database file`() {
            runBlocking {
                // given
                val testHttpClient = object: HttpClient by TestHttpClient {
                    override suspend fun get(url: URL, headers: Map<String, Collection<String>>): HttpResponse = HttpResponse(
                        code = 200,
                        body = loadTestResource<ByteArray>("json/deserialization/test_db_for_deserialization.json"),
                    )
                }

                val testDatabaseFileParser = object: JsonDeserializer<List<Int>> by TestJsonDeserializer {
                    override suspend fun deserialize(json: String): List<Int> = listOf(1, 2, 3, 4, 5)
                }

                val defaultDatabaseFileParser = DefaultExternalResourceJsonDeserializer(
                    httpClient = testHttpClient,
                    jsonDeserializer = testDatabaseFileParser,
                )

                // when
                val result = defaultDatabaseFileParser.deserialize(URI("http://localhost$port/anime-offline-database.json").toURL())

                // then
                assertThat(result).containsExactlyInAnyOrder(1, 2, 3, 4, 5)
            }
        }
    }

    @Nested
    inner class DeserializeRegularFileTests {

        @Test
        fun `throws exception if the given path is a directory`() {
            tempDirectory {
                // given
                val defaultDatabaseFileParser = DefaultExternalResourceJsonDeserializer(
                    httpClient = TestHttpClient,
                    jsonDeserializer = TestJsonDeserializer,
                )

                // when
                val result = exceptionExpected<IllegalArgumentException> {
                    defaultDatabaseFileParser.deserialize(tempDir)
                }

                // then
                assertThat(result).hasMessage("The given path does not exist or is not a regular file: [${tempDir.toAbsolutePath()}]")
            }
        }

        @Test
        fun `throws exception if the given file does not exist`() {
            tempDirectory {
                // given
                val defaultDatabaseFileParser = DefaultExternalResourceJsonDeserializer(
                    httpClient = TestHttpClient,
                    jsonDeserializer = TestJsonDeserializer,
                )
                val testFile = tempDir.resolve("anime-offline-database.json")

                // when
                val result = exceptionExpected<IllegalArgumentException> {
                    defaultDatabaseFileParser.deserialize(testFile)
                }

                // then
                assertThat(result).hasMessage("The given path does not exist or is not a regular file: [${testFile.toAbsolutePath()}]")
            }
        }

        @Test
        fun `throws exception if the given file is neither json nor zip file`() {
            tempDirectory {
                // given
                val defaultDatabaseFileParser = DefaultExternalResourceJsonDeserializer(
                    httpClient = TestHttpClient,
                    jsonDeserializer = TestJsonDeserializer,
                )

                // when
                val result = exceptionExpected<IllegalArgumentException> {
                    defaultDatabaseFileParser.deserialize(testResource("logback-test.xml"))
                }

                // then
                assertThat(result).hasMessage("File is neither JSON nor zip file")
            }
        }

        @Test
        fun `throws exception if the given zip file doesn't contain a JSON file`() {
            tempDirectory {
                // given
                val defaultDatabaseFileParser = DefaultExternalResourceJsonDeserializer(
                    httpClient = TestHttpClient,
                    jsonDeserializer = TestJsonDeserializer,
                )

                // when
                val result = exceptionExpected<IllegalArgumentException> {
                    defaultDatabaseFileParser.deserialize(testResource("json/deserialization/non-json.zip"))
                }

                // then
                assertThat(result).hasMessage("File inside zip archive is not a JSON file.")
            }
        }

        @Test
        fun `throws exception if the given zip file contains more than one file`() {
            tempDirectory {
                // given
                val defaultDatabaseFileParser = DefaultExternalResourceJsonDeserializer(
                    httpClient = TestHttpClient,
                    jsonDeserializer = TestJsonDeserializer,
                )

                // when
                val result = exceptionExpected<IllegalArgumentException> {
                    defaultDatabaseFileParser.deserialize(testResource("json/deserialization/2_files.zip"))
                }

                // then
                assertThat(result).hasMessage("The zip file contains more than one file.")
            }
        }

        @Test
        fun `correctly parse database file`() {
            runBlocking {
                // given
                val testDatabaseFileParser = object : JsonDeserializer<List<Int>> by TestJsonDeserializer {
                    override suspend fun deserialize(json: String): List<Int> = listOf(1, 2, 4, 5)
                }

                val defaultDatabaseFileParser = DefaultExternalResourceJsonDeserializer(
                    httpClient = TestHttpClient,
                    jsonDeserializer = testDatabaseFileParser,
                )

                // when
                val result = defaultDatabaseFileParser.deserialize(testResource("json/deserialization/test_db_for_deserialization.json"))

                // then
                assertThat(result).containsExactlyInAnyOrder(1, 2, 4, 5)
            }
        }

        @Test
        fun `correctly parse zipped database file`() {
            runBlocking {
                // given
                val expectedEntries = listOf(
                    Anime(
                        _title = "Seikai no Monshou",
                        type = Anime.Type.TV,
                        episodes = 13,
                        picture = URI("https://cdn.anidb.net/images/main/224618.jpg"),
                        thumbnail = URI("https://cdn.anidb.net/images/main/224618.jpg-thumb.jpg"),
                        status = Anime.Status.FINISHED,
                        animeSeason = AnimeSeason(
                            season = AnimeSeason.Season.UNDEFINED,
                            year = 1999,
                        )
                    ).apply {
                        addSources(listOf(URI("https://anidb.net/anime/1")))
                        addSynonyms(
                            "CotS",
                            "Crest of the Stars",
                            "Hvězdný erb",
                            "SnM",
                            "星界の紋章",
                            "星界之纹章",
                        )
                        addRelations(
                            URI("https://anidb.net/anime/1623"),
                            URI("https://anidb.net/anime/4"),
                            URI("https://anidb.net/anime/6"),
                        )
                        addTags(
                            "action",
                            "adventure",
                            "genetic modification",
                            "novel",
                            "science fiction",
                            "space travel",
                        )
                    },
                    Anime(
                        _title = "Cowboy Bebop",
                        type = Anime.Type.TV,
                        episodes = 26,
                        picture = URI("https://cdn.myanimelist.net/images/anime/4/19644.jpg"),
                        thumbnail = URI("https://cdn.myanimelist.net/images/anime/4/19644t.jpg"),
                        status = Anime.Status.FINISHED,
                        animeSeason = AnimeSeason(
                            season = AnimeSeason.Season.SPRING,
                            year = 1998,
                        )
                    ).apply {
                        addSources(listOf(URI("https://myanimelist.net/anime/1")))
                        addSynonyms(listOf("カウボーイビバップ"))
                        addRelations(
                            URI("https://myanimelist.net/anime/17205"),
                            URI("https://myanimelist.net/anime/4037"),
                            URI("https://myanimelist.net/anime/5"),
                        )
                        addTags(
                            "action",
                            "adventure",
                            "comedy",
                            "drama",
                            "sci-fi",
                            "space",
                        )
                    },
                    Anime(
                        _title = "Cowboy Bebop: Tengoku no Tobira",
                        type = Anime.Type.MOVIE,
                        episodes = 1,
                        picture = URI("https://cdn.myanimelist.net/images/anime/1439/93480.jpg"),
                        thumbnail = URI("https://cdn.myanimelist.net/images/anime/1439/93480t.jpg"),
                        status = Anime.Status.FINISHED,
                        animeSeason = AnimeSeason(
                            season = AnimeSeason.Season.SPRING,
                            year = 1998,
                        )
                    ).apply {
                        addSources(URI("https://myanimelist.net/anime/5"))
                        addRelations(URI("https://myanimelist.net/anime/1"))
                        addSynonyms(
                            "Cowboy Bebop: Knockin' on Heaven's Door",
                            "Cowboy Bebop: The Movie", "カウボーイビバップ 天国の扉",
                        )
                        addTags(
                            "action",
                            "drama",
                            "mystery",
                            "sci-fi",
                            "space",
                        )
                    },
                    Anime(
                        _title = "11 Eyes",
                        type = Anime.Type.TV,
                        episodes = 12,
                        picture = URI("https://cdn.anidb.net/images/main/32901.jpg"),
                        thumbnail = URI("https://cdn.anidb.net/images/main/32901.jpg-thumb.jpg"),
                        status = Anime.Status.FINISHED,
                        animeSeason = AnimeSeason(
                            season = AnimeSeason.Season.UNDEFINED,
                            year = 2009,
                        )
                    ).apply {
                        addSources(listOf(URI("https://anidb.net/anime/6751")))
                        addSynonyms(
                            "11 akių",
                            "11 глаз",
                            "11 چشم",
                            "11eyes",
                            "11eyes -罪與罰與贖的少女-",
                            "11eyes: Tsumi to Batsu to Aganai no Shoujo",
                            "أحد عشر عيناً",
                            "イレブンアイズ",
                            "罪与罚与赎的少女",
                        )
                        addTags(
                            "action",
                            "angst",
                            "contemporary fantasy",
                            "ecchi",
                            "erotic game",
                            "fantasy",
                            "female student",
                            "seinen",
                            "super power",
                            "swordplay",
                            "visual novel",
                        )
                    },
                    Anime(
                        _title = "11eyes",
                        type = Anime.Type.TV,
                        episodes = 12,
                        picture = URI("https://s4.anilist.co/file/anilistcdn/media/anime/cover/medium/bx6682-ZptgLsCCNHjL.jpg"),
                        thumbnail = URI("https://s4.anilist.co/file/anilistcdn/media/anime/cover/medium/default.jpg"),
                        status = Anime.Status.FINISHED,
                        animeSeason = AnimeSeason(
                            season = AnimeSeason.Season.FALL,
                            year = 2009,
                        )
                    ).apply {
                        addSources(
                            URI("https://anilist.co/anime/6682"),
                            URI("https://myanimelist.net/anime/6682"),
                        )
                        addSynonyms(
                            "11eyes -Tsumi to Batsu to Aganai no Shoujo-",
                            "11eyes イレブンアイズ",
                            "イレブンアイズ",
                        )
                        addRelations(
                            URI("https://anilist.co/anime/110465"),
                            URI("https://anilist.co/anime/7739"),
                            URI("https://myanimelist.net/anime/20557"),
                            URI("https://myanimelist.net/anime/7739"),
                        )
                        addTags(
                            "action",
                            "demons",
                            "ecchi",
                            "ensemble cast",
                            "gore",
                            "magic",
                            "male protagonist",
                            "memory manipulation",
                            "revenge",
                            "super power",
                            "supernatural",
                            "survival",
                            "swordplay",
                            "tragedy",
                            "witch",
                        )
                    }
                )

                val defaultDatabaseFileParser = DefaultExternalResourceJsonDeserializer(
                    httpClient = TestHttpClient,
                    jsonDeserializer = AnimeListJsonStringDeserializer(),
                )

                // when
                val result = defaultDatabaseFileParser.deserialize(testResource("json/deserialization/test_db_for_deserialization.zip"))

                // then
                assertThat(result).containsAll(expectedEntries)
            }
        }
    }
}