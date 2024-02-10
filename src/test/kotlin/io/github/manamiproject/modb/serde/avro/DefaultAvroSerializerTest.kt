package io.github.manamiproject.modb.serde.avro

import io.github.manamiproject.modb.core.config.AnimeId
import io.github.manamiproject.modb.core.models.Anime
import io.github.manamiproject.modb.core.models.AnimeSeason
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import java.net.URI
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset.UTC

internal class DefaultAvroSerializerTest {

    @Nested
    inner class AnimeListTests {

        @Test
        fun `correctly serialize empty list`() {
            runBlocking {
                // given
                val clock = Clock.fixed(Instant.parse("2020-01-01T16:02:42.00Z"), UTC)
                val serializer = DefaultAvroSerializer(clock)
                val deserializer = DefaultAvroDeserializer()
                val list = emptyList<Anime>()

                // when
                val result = serializer.serializeAnimeList(list)

                // then
                assertThat(deserializer.deserializeAnimeList(result)).isEmpty()
            }
        }

        @Test
        fun `correctly serialize default anime`() {
            runBlocking {
                // given
                val clock = Clock.fixed(Instant.parse("2020-01-01T16:02:42.00Z"), UTC)
                val serializer = DefaultAvroSerializer(clock)
                val deserializer = DefaultAvroDeserializer()
                val list = listOf(
                    Anime("Death Note"),
                )

                // when
                val result = serializer.serializeAnimeList(list)

                // then
                assertThat(deserializer.deserializeAnimeList(result)).isEqualTo(list)
            }
        }

        @Test
        fun `correctly serialze anime with all properties`() {
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

                // when
                val result = serializer.serializeAnimeList(list)

                // then
                assertThat(deserializer.deserializeAnimeList(result)).isEqualTo(list)
            }
        }
    }

    @Nested
    inner class DeadEntriesTests {

        @Test
        fun `correctly serialize empty list`() {
            runBlocking {
                // given
                val clock = Clock.fixed(Instant.parse("2020-01-01T16:02:42.00Z"), UTC)
                val serializer = DefaultAvroSerializer(clock)
                val deserializer = DefaultAvroDeserializer()
                val list = emptyList<AnimeId>()

                // when
                val result = serializer.serializeDeadEntries(list)

                // then
                assertThat(deserializer.deserializeDeadEntries(result)).isEmpty()
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

                // when
                val result = serializer.serializeDeadEntries(list)

                // then
                assertThat(deserializer.deserializeDeadEntries(result)).isEqualTo(list)
            }
        }
    }
}