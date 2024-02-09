package io.github.manamiproject.modb.serde

import com.github.avrokotlin.avro4k.Avro
import org.assertj.core.api.Assertions.assertThat
import kotlin.test.Test

internal class AvroModelsKtTest {

    @Test
    fun `schema test AvroDataset`() {
        // when
        val result = Avro.default.schema(DatasetModel.serializer()).toString(true)

        // then
        assertThat(result).isEqualTo("""
        {
          "type" : "record",
          "name" : "Dataset",
          "namespace" : "io.github.manamiproject.modb.serde.avro",
          "fields" : [ {
            "name" : "license",
            "type" : {
              "type" : "record",
              "name" : "License",
              "fields" : [ {
                "name" : "name",
                "type" : "string"
              }, {
                "name" : "url",
                "type" : "string"
              } ]
            }
          }, {
            "name" : "repository",
            "type" : "string"
          }, {
            "name" : "lastUpdate",
            "type" : "string"
          }, {
            "name" : "data",
            "type" : {
              "type" : "array",
              "items" : {
                "type" : "record",
                "name" : "Anime",
                "fields" : [ {
                  "name" : "sources",
                  "type" : {
                    "type" : "array",
                    "items" : "string"
                  }
                }, {
                  "name" : "title",
                  "type" : "string"
                }, {
                  "name" : "type",
                  "type" : {
                    "type" : "enum",
                    "name" : "Type",
                    "symbols" : [ "TV", "MOVIE", "OVA", "ONA", "SPECIAL", "UNKNOWN" ]
                  }
                }, {
                  "name" : "episodes",
                  "type" : "int"
                }, {
                  "name" : "status",
                  "type" : {
                    "type" : "enum",
                    "name" : "Status",
                    "symbols" : [ "FINISHED", "ONGOING", "UPCOMING", "UNKNOWN" ]
                  }
                }, {
                  "name" : "animeSeason",
                  "type" : {
                    "type" : "record",
                    "name" : "AnimeSeason",
                    "fields" : [ {
                      "name" : "season",
                      "type" : {
                        "type" : "enum",
                        "name" : "Season",
                        "symbols" : [ "UNDEFINED", "SPRING", "SUMMER", "FALL", "WINTER" ]
                      }
                    }, {
                      "name" : "year",
                      "type" : [ "null", "int" ]
                    } ]
                  }
                }, {
                  "name" : "picture",
                  "type" : "string"
                }, {
                  "name" : "thumbnail",
                  "type" : "string"
                }, {
                  "name" : "synonyms",
                  "type" : {
                    "type" : "array",
                    "items" : "string"
                  }
                }, {
                  "name" : "relations",
                  "type" : {
                    "type" : "array",
                    "items" : "string"
                  }
                }, {
                  "name" : "tags",
                  "type" : {
                    "type" : "array",
                    "items" : "string"
                  }
                } ]
              }
            }
          } ]
        }""".trimIndent())
    }

    @Test
    fun `schema test AvroAnime`() {
        // when
        val result = Avro.default.schema(DatasetEntryModel.serializer()).toString(true)

        // then
        assertThat(result).isEqualTo("""
        {
          "type" : "record",
          "name" : "Anime",
          "namespace" : "io.github.manamiproject.modb.serde.avro",
          "fields" : [ {
            "name" : "sources",
            "type" : {
              "type" : "array",
              "items" : "string"
            }
          }, {
            "name" : "title",
            "type" : "string"
          }, {
            "name" : "type",
            "type" : {
              "type" : "enum",
              "name" : "Type",
              "symbols" : [ "TV", "MOVIE", "OVA", "ONA", "SPECIAL", "UNKNOWN" ]
            }
          }, {
            "name" : "episodes",
            "type" : "int"
          }, {
            "name" : "status",
            "type" : {
              "type" : "enum",
              "name" : "Status",
              "symbols" : [ "FINISHED", "ONGOING", "UPCOMING", "UNKNOWN" ]
            }
          }, {
            "name" : "animeSeason",
            "type" : {
              "type" : "record",
              "name" : "AnimeSeason",
              "fields" : [ {
                "name" : "season",
                "type" : {
                  "type" : "enum",
                  "name" : "Season",
                  "symbols" : [ "UNDEFINED", "SPRING", "SUMMER", "FALL", "WINTER" ]
                }
              }, {
                "name" : "year",
                "type" : [ "null", "int" ]
              } ]
            }
          }, {
            "name" : "picture",
            "type" : "string"
          }, {
            "name" : "thumbnail",
            "type" : "string"
          }, {
            "name" : "synonyms",
            "type" : {
              "type" : "array",
              "items" : "string"
            }
          }, {
            "name" : "relations",
            "type" : {
              "type" : "array",
              "items" : "string"
            }
          }, {
            "name" : "tags",
            "type" : {
              "type" : "array",
              "items" : "string"
            }
          } ]
        }""".trimIndent())
    }

    @Test
    fun `schema test AvroAnimeSeason`() {
        // when
        val result = Avro.default.schema(AnimeSeasonModel.serializer()).toString(true)

        // then
        assertThat(result).isEqualTo("""
        {
          "type" : "record",
          "name" : "AnimeSeason",
          "namespace" : "io.github.manamiproject.modb.serde.avro",
          "fields" : [ {
            "name" : "season",
            "type" : {
              "type" : "enum",
              "name" : "Season",
              "symbols" : [ "UNDEFINED", "SPRING", "SUMMER", "FALL", "WINTER" ]
            }
          }, {
            "name" : "year",
            "type" : [ "null", "int" ]
          } ]
        }""".trimIndent())
    }

    @Test
    fun `schema test AvroLicense`() {
        // when
        val result = Avro.default.schema(DatasetLicenseModel.serializer()).toString(true)

        // then
        assertThat(result).isEqualTo("""
        {
          "type" : "record",
          "name" : "License",
          "namespace" : "io.github.manamiproject.modb.serde.avro",
          "fields" : [ {
            "name" : "name",
            "type" : "string"
          }, {
            "name" : "url",
            "type" : "string"
          } ]
        }""".trimIndent())
    }

    @Test
    fun `schema test AvroSeason`() {
        // when
        val result = Avro.default.schema(SeasonModel.serializer()).toString(true)

        // then
        assertThat(result).isEqualTo("""
        {
          "type" : "enum",
          "name" : "Season",
          "namespace" : "io.github.manamiproject.modb.serde.avro",
          "symbols" : [ "UNDEFINED", "SPRING", "SUMMER", "FALL", "WINTER" ]
        }""".trimIndent())
    }

    @Test
    fun `schema test AvroStatus`() {
        // when
        val result = Avro.default.schema(StatusModel.serializer()).toString(true)

        // then
        assertThat(result).isEqualTo("""
        {
          "type" : "enum",
          "name" : "Status",
          "namespace" : "io.github.manamiproject.modb.serde.avro",
          "symbols" : [ "FINISHED", "ONGOING", "UPCOMING", "UNKNOWN" ]
        }""".trimIndent())
    }

    @Test
    fun `schema test AvroType`() {
        // when
        val result = Avro.default.schema(TypeModel.serializer()).toString(true)

        // then
        assertThat(result).isEqualTo("""
        {
          "type" : "enum",
          "name" : "Type",
          "namespace" : "io.github.manamiproject.modb.serde.avro",
          "symbols" : [ "TV", "MOVIE", "OVA", "ONA", "SPECIAL", "UNKNOWN" ]
        }""".trimIndent())
    }

    @Test
    fun `schema test AvroDeadEntries`() {
        // when
        val result = Avro.default.schema(DeadEntriesModel.serializer()).toString(true)

        // then
        assertThat(result).isEqualTo("""
        {
          "type" : "record",
          "name" : "DeadEntries",
          "namespace" : "io.github.manamiproject.modb.serde.avro",
          "fields" : [ {
            "name" : "deadEntries",
            "type" : {
              "type" : "array",
              "items" : "string"
            }
          } ]
        }""".trimIndent())
    }
}