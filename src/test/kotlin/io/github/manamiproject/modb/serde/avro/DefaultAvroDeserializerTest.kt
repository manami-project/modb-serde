package io.github.manamiproject.modb.serde.avro

import io.github.manamiproject.modb.core.config.AnimeId
import io.github.manamiproject.modb.core.models.Anime
import io.github.manamiproject.modb.core.models.AnimeSeason
import io.github.manamiproject.modb.test.exceptionExpected
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import java.net.URI
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset.UTC

internal class DefaultAvroDeserializerTest {

    @Nested
    inner class AnimeListDeserializationTests {

        @Test
        fun `throws exception if the byte array is empty`() {
            runBlocking {
                // given
                val deserializer = DefaultAvroDeserializer()

                // when
                val result = exceptionExpected<IllegalArgumentException> {
                    deserializer.deserializeAnimeList("".toByteArray())
                }

                // then
                assertThat(result).hasMessage("Given ByteArray must not be empty.")
            }
        }

        @Test
        fun `correctly deserialize empty list`() {
            runBlocking {
                // given
                val clock = Clock.fixed(Instant.parse("2020-01-01T16:02:42.00Z"), UTC)
                val serializer = DefaultAvroSerializer(clock)
                val deserializer = DefaultAvroDeserializer()
                val list = emptyList<Anime>()
                val input = serializer.serializeAnimeList(list)

                // when
                val result = deserializer.deserializeAnimeList(input)

                // then
                assertThat(result).isEmpty()
            }
        }

        @Test
        fun `correctly deserialize default anime`() {
            runBlocking {
                // given
                val clock = Clock.fixed(Instant.parse("2020-01-01T16:02:42.00Z"), UTC)
                val serializer = DefaultAvroSerializer(clock)
                val deserializer = DefaultAvroDeserializer()
                val list = listOf(
                    Anime("Death Note"),
                )
                val input = serializer.serializeAnimeList(list)

                // when
                val result = deserializer.deserializeAnimeList(input)

                // then
                assertThat(result).isEqualTo(list)
            }
        }

        @Test
        fun `correctly deserialze anime with all properties`() {
            runBlocking {
                // given
                val clock = Clock.fixed(Instant.parse("2020-01-01T16:02:42.00Z"), UTC)
                val serializer = DefaultAvroSerializer(clock)
                val deserializer = DefaultAvroDeserializer()
                val list = listOf(
                    Anime(
                        _title = "Clannad: After Story - Mou Hitotsu no Sekai, Kyou-hen",
                        type = Anime.Type.SPECIAL,
                        episodes = 1,
                        status = Anime.Status.FINISHED,
                        animeSeason = AnimeSeason(
                            season = AnimeSeason.Season.SUMMER,
                            year = 2009
                        ),
                        picture = URI("https://cdn.myanimelist.net/images/anime/10/19621.jpg"),
                        thumbnail = URI("https://cdn.myanimelist.net/images/anime/10/19621t.jpg")
                    ).apply {
                        addSources(listOf(URI("https://myanimelist.net/anime/6351")))
                        addSynonyms(
                            listOf(
                                "Clannad ~After Story~: Another World, Kyou Chapter",
                                "Clannad: After Story OVA",
                                "クラナド　アフターストーリー　もうひとつの世界　杏編"
                            )
                        )
                        addRelations(listOf(URI("https://myanimelist.net/anime/2167")))
                        addTags(
                            listOf(
                                "comedy",
                                "drama",
                                "romance",
                                "school",
                                "slice of life",
                                "supernatural"
                            )
                        )
                    }
                )
                val input = serializer.serializeAnimeList(list)

                // when
                val result = deserializer.deserializeAnimeList(input)

                // then
                assertThat(result).isEqualTo(list)
            }
        }
    }

    @Nested
    inner class DeadEntriesDeserializationTests {

        @Test
        fun `throws exception if the byte array is empty`() {
            runBlocking {
                // given
                val deserializer = DefaultAvroDeserializer()

                // when
                val result = exceptionExpected<IllegalArgumentException> {
                    deserializer.deserializeDeadEntries("".toByteArray())
                }

                // then
                assertThat(result).hasMessage("Given ByteArray must not be empty.")
            }
        }

        @Test
        fun `correctly serialize empty list`() {
            runBlocking {
                // given
                val clock = Clock.fixed(Instant.parse("2020-01-01T16:02:42.00Z"), UTC)
                val serializer = DefaultAvroSerializer(clock)
                val deserializer = DefaultAvroDeserializer()
                val list = emptyList<AnimeId>()
                val input = serializer.serializeDeadEntries(list)

                // when
                val result = deserializer.deserializeDeadEntries(input)

                // then
                assertThat(result).isEmpty()
            }
        }

        @Test
        fun `correctly serialize dead entries`() {
            runBlocking {
                // given
                val clock = Clock.fixed(Instant.parse("2020-01-01T16:02:42.00Z"), UTC)
                val serializer = DefaultAvroSerializer(clock)
                val deserializer = DefaultAvroDeserializer()
                val list = listOf(
                    "1234",
                    "5678",
                )
                val input = serializer.serializeDeadEntries(list)

                // when
                val result = deserializer.deserializeDeadEntries(input)

                // then
                assertThat(result).isEqualTo(list)
            }
        }
    }

    @Nested
    inner class AnimeListSortingTests {

        @Test
        fun `correctly sort prio 1 - sort by title`() {
            runBlocking {
                // given
                val clock = Clock.fixed(Instant.parse("2020-01-01T16:02:42.00Z"), UTC)
                val serializer = DefaultAvroSerializer(clock)
                val deserializer = DefaultAvroDeserializer()
                val list = listOf(
                    Anime("B"),
                    Anime("C"),
                    Anime("A"),
                )

                // when
                val result = serializer.serializeAnimeList(list)

                // then
                assertThat(deserializer.deserializeAnimeList(result)).containsExactlyInAnyOrder(
                    Anime("A"),
                    Anime("B"),
                    Anime("C"),
                )
            }
        }

        @Test
        fun `correctly sort prio 2 - sort by type`() {
            runBlocking {
                // given
                val clock = Clock.fixed(Instant.parse("2020-01-01T16:02:42.00Z"), UTC)
                val serializer = DefaultAvroSerializer(clock)
                val deserializer = DefaultAvroDeserializer()
                val list = listOf(
                    Anime(
                        _title = "test",
                        type = Anime.Type.OVA,
                    ),
                    Anime(
                        _title = "test",
                        type = Anime.Type.SPECIAL,
                    ),
                    Anime(
                        _title = "test",
                        type = Anime.Type.MOVIE,
                    ),
                )

                // when
                val result = serializer.serializeAnimeList(list)

                // then
                assertThat(deserializer.deserializeAnimeList(result)).containsExactlyInAnyOrder(
                    Anime(
                        _title = "test",
                        type = Anime.Type.MOVIE,
                    ),
                    Anime(
                        _title = "test",
                        type = Anime.Type.OVA,
                    ),
                    Anime(
                        _title = "test",
                        type = Anime.Type.SPECIAL,
                    ),
                )
            }
        }

        @Test
        fun `correctly sort prio 3 - sort by episodes`() {
            runBlocking {
                // given
                val clock = Clock.fixed(Instant.parse("2020-01-01T16:02:42.00Z"), UTC)
                val serializer = DefaultAvroSerializer(clock)
                val deserializer = DefaultAvroDeserializer()
                val list = listOf(
                    Anime(
                        _title = "test",
                        type = Anime.Type.TV,
                        episodes = 24,
                    ),
                    Anime(
                        _title = "test",
                        type = Anime.Type.TV,
                        episodes = 12,
                    ),
                    Anime(
                        _title = "test",
                        type = Anime.Type.TV,
                        episodes = 13,
                    ),
                )

                // when
                val result = serializer.serializeAnimeList(list)

                // then
                assertThat(deserializer.deserializeAnimeList(result)).containsExactlyInAnyOrder(
                    Anime(
                        _title = "test",
                        type = Anime.Type.TV,
                        episodes = 12,
                    ),
                    Anime(
                        _title = "test",
                        type = Anime.Type.TV,
                        episodes = 13,
                    ),
                    Anime(
                        _title = "test",
                        type = Anime.Type.TV,
                        episodes = 24,
                    ),
                )
            }
        }
    }

    @Nested
    inner class DeadEntriesSortingTests {

        @Test
        fun `correctly sort dead entries`() {
            runBlocking {
                // given
                val clock = Clock.fixed(Instant.parse("2020-01-01T16:02:42.00Z"), UTC)
                val serializer = DefaultAvroSerializer(clock)
                val deserializer = DefaultAvroDeserializer()
                val list = listOf(
                    "56789",
                    "89745",
                    "12345",
                )
                val input = serializer.serializeDeadEntries(list)

                // when
                val result = deserializer.deserializeDeadEntries(input)

                // then
                assertThat(result).containsExactlyInAnyOrder(
                    "12345",
                    "56789",
                    "89745",
                )
            }
        }
    }
}