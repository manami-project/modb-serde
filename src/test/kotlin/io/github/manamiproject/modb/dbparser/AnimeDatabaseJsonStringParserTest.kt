package io.github.manamiproject.modb.dbparser

import io.github.manamiproject.modb.core.extensions.EMPTY
import io.github.manamiproject.modb.core.models.Anime
import io.github.manamiproject.modb.core.models.AnimeSeason
import io.github.manamiproject.modb.test.loadTestResource
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.net.URL

internal class AnimeDatabaseJsonStringParserTest {

    @Test
    fun `throws exception if the given string is empty`() {
        // given
        val defaultDatabaseFileParser = AnimeDatabaseJsonStringParser()

        // when
        val result = assertThrows<IllegalArgumentException> {
            defaultDatabaseFileParser.parse(EMPTY)
        }

        // then
        assertThat(result).hasMessage("Given json string must not be blank.")
    }

    @Test
    fun `throws exception if the given string is blank`() {
        // given
        val defaultDatabaseFileParser = AnimeDatabaseJsonStringParser()

        // when
        val result = assertThrows<IllegalArgumentException> {
            defaultDatabaseFileParser.parse("    ")
        }

        // then
        assertThat(result).hasMessage("Given json string must not be blank.")
    }

    @Test
    fun `correctly parse database string`() {
        // given
        val defaultDatabaseFileParser = AnimeDatabaseJsonStringParser()

        val expectedEntries = listOf(
            Anime(
                _title = "Seikai no Monshou",
                type = Anime.Type.TV,
                episodes = 13,
                picture = URL("https://cdn.anidb.net/images/main/224618.jpg"),
                thumbnail = URL("https://cdn.anidb.net/images/main/224618.jpg-thumb.jpg"),
                status = Anime.Status.FINISHED,
                animeSeason = AnimeSeason(
                    season = AnimeSeason.Season.UNDEFINED,
                    _year = 1999
                )
            ).apply {
                addSources(listOf(URL("https://anidb.net/anime/1")))
                addSynonyms(
                    listOf(
                        "CotS",
                        "Crest of the Stars",
                        "Hvězdný erb",
                        "SnM",
                        "星界の紋章",
                        "星界之纹章"
                    )
                )
                addRelations(
                    listOf(
                        URL("https://anidb.net/anime/1623"),
                        URL("https://anidb.net/anime/4"),
                        URL("https://anidb.net/anime/6")
                    )
                )
                addTags(
                    listOf(
                        "action",
                        "adventure",
                        "genetic modification",
                        "novel",
                        "science fiction",
                        "space travel"
                    )
                )
            },
            Anime(
                _title = "Cowboy Bebop",
                type = Anime.Type.TV,
                episodes = 26,
                picture = URL("https://cdn.myanimelist.net/images/anime/4/19644.jpg"),
                thumbnail = URL("https://cdn.myanimelist.net/images/anime/4/19644t.jpg"),
                status = Anime.Status.FINISHED,
                animeSeason = AnimeSeason(
                    season = AnimeSeason.Season.SPRING,
                    _year = 1998
                )
            ).apply {
                addSources(listOf(URL("https://myanimelist.net/anime/1")))
                addSynonyms(listOf("カウボーイビバップ"))
                addRelations(
                    listOf(
                        URL("https://myanimelist.net/anime/17205"),
                        URL("https://myanimelist.net/anime/4037"),
                        URL("https://myanimelist.net/anime/5")
                    )
                )
                addTags(
                    listOf(
                        "action",
                        "adventure",
                        "comedy",
                        "drama",
                        "sci-fi",
                        "space"
                    )
                )
            },
            Anime(
                _title = "Cowboy Bebop: Tengoku no Tobira",
                type = Anime.Type.Movie,
                episodes = 1,
                picture = URL("https://cdn.myanimelist.net/images/anime/1439/93480.jpg"),
                thumbnail = URL("https://cdn.myanimelist.net/images/anime/1439/93480t.jpg"),
                status = Anime.Status.FINISHED,
                animeSeason = AnimeSeason(
                    season = AnimeSeason.Season.SPRING,
                    _year = 1998
                )
            ).apply {
                addSources(listOf(URL("https://myanimelist.net/anime/5")))
                addSynonyms(
                    listOf(
                        "Cowboy Bebop: Knockin' on Heaven's Door",
                        "Cowboy Bebop: The Movie", "カウボーイビバップ 天国の扉"
                    )
                )
                addRelations(listOf(URL("https://myanimelist.net/anime/1")))
                addTags(
                    listOf(
                        "action",
                        "drama",
                        "mystery",
                        "sci-fi",
                        "space"
                    )
                )
            },
            Anime(
                _title = "11 Eyes",
                type = Anime.Type.TV,
                episodes = 12,
                picture = URL("https://cdn.anidb.net/images/main/32901.jpg"),
                thumbnail = URL("https://cdn.anidb.net/images/main/32901.jpg-thumb.jpg"),
                status = Anime.Status.FINISHED,
                animeSeason = AnimeSeason(
                    season = AnimeSeason.Season.UNDEFINED,
                    _year = 2009
                )
            ).apply {
                addSources(listOf(URL("https://anidb.net/anime/6751")))
                addSynonyms(
                    listOf(
                        "11 akių",
                        "11 глаз",
                        "11 چشم",
                        "11eyes",
                        "11eyes -罪與罰與贖的少女-",
                        "11eyes: Tsumi to Batsu to Aganai no Shoujo",
                        "أحد عشر عيناً",
                        "イレブンアイズ",
                        "罪与罚与赎的少女"
                    )
                )
                addTags(
                    listOf(
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
                        "visual novel"
                    )
                )
            },
            Anime(
                _title = "11eyes",
                type = Anime.Type.TV,
                episodes = 12,
                picture = URL("https://s4.anilist.co/file/anilistcdn/media/anime/cover/medium/bx6682-ZptgLsCCNHjL.jpg"),
                thumbnail = URL("https://s4.anilist.co/file/anilistcdn/media/anime/cover/medium/default.jpg"),
                status = Anime.Status.FINISHED,
                animeSeason = AnimeSeason(
                    season = AnimeSeason.Season.FALL,
                    _year = 2009
                )
            ).apply {
                addSources(
                    listOf(
                        URL("https://anilist.co/anime/6682"),
                        URL("https://myanimelist.net/anime/6682")
                    )
                )
                addSynonyms(
                    listOf(
                        "11eyes -Tsumi to Batsu to Aganai no Shoujo-",
                        "11eyes イレブンアイズ",
                        "イレブンアイズ"
                    )
                )
                addRelations(
                    listOf(
                        URL("https://anilist.co/anime/110465"),
                        URL("https://anilist.co/anime/7739"),
                        URL("https://myanimelist.net/anime/20557"),
                        URL("https://myanimelist.net/anime/7739")
                    )
                )
                addTags(
                    listOf(
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
                        "witch"
                    )
                )
            }
        )

        // when
        val result = defaultDatabaseFileParser.parse(loadTestResource("test_db_for_deserialization.json"))

        // then
        assertThat(result).containsAll(expectedEntries)
    }
}