![Build](https://github.com/manami-project/modb-db-parser/actions/workflows/build.yml/badge.svg) [![Coverage Status](https://coveralls.io/repos/github/manami-project/modb-db-parser/badge.svg)](https://coveralls.io/github/manami-project/modb-db-parser) ![jdk21](https://img.shields.io/badge/jdk-21-informational)
# modb-serde
_[modb](https://github.com/manami-project?tab=repositories&q=modb&type=source)_ stands for _**M**anami **O**ffline **D**ata**B**ase_. Repositories prefixed with this acronym are used to create the [manami-project/anime-offline-database](https://github.com/manami-project/anime-offline-database).

# What does this lib do?
This lib can serialize and deserialize (serde) both the anime dataset file as well as the files for the dead entries.
 
# Usage

Create an instance of the respective `JsonSerializer`:

```kotlin
// deserialize a the JSON String of the anime database file
val animeListDeserializer = AnimeListJsonStringDeserializer()

// deserialize a the JSON String of a dead entries file
val deadEntriesDeserializer = DeadEntriesJsonStringDeserializer()
```

Wrap the instance above in a `ExternalResourceJsonDeserializer` to be able to deserialize a `URL` or a `Path`

```kotlin
val animeDatasetFileDeserializer = DefaultExternalResourceJsonDeserializer<Anime>(deserializer = AnimeListJsonStringDeserializer())

val deadEntriesFileDeserializer = DefaultExternalResourceJsonDeserializer<AnimeId>(deserializer = DeadEntriesJsonStringDeserializer())
```

Now you can either deserialize the anime dataset file or a dead entries file by using a `URL` or a `Path`.
The `DefaultExternalResourceJsonDeserializer` can also handle zipped files, but the zip file must only contain a single JSON file.

*Example:*

```kotlin
val deserializer = DefaultExternalResourceJsonDeserializer<Anime>(deserializer = AnimeListJsonStringDeserializer())
val allAnime: List<Anime> = deserializer.deserialize(URI("https://raw.githubusercontent.com/manami-project/anime-offline-database/master/anime-offline-database.json").toURL())
```