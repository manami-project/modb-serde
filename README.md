![Build](https://github.com/manami-project/modb-db-parser/workflows/Build/badge.svg) [![Coverage Status](https://coveralls.io/repos/github/manami-project/modb-db-parser/badge.svg)](https://coveralls.io/github/manami-project/modb-db-parser) ![jdk21](https://img.shields.io/badge/jdk-21-informational)
# modb-db-parser
_[modb](https://github.com/manami-project?tab=repositories&q=modb&type=source)_ stands for _**M**anami **O**ffline **D**ata**B**ase_. Repositories prefixed with this acronym are used to create the [manami-project/anime-offline-database](https://github.com/manami-project/anime-offline-database).

# What does this lib do?
This lib can parse both the anime database file as well as the files for the dead entries.
 
# Usage

Create an instance of the respective `JsonStringParser`:

```kotlin
// parse a the JSON String of the anime database file
val animeDatabaseJsonStringParser = AnimeDatabaseJsonStringParser()

// parse a the JSON String of a dead entries file
val deadEntriesJsonStringParser = DeadEntriesJsonStringParser()
```

Wrap the instance above in a `DatabaseFileParser` to be able to parse a `URL` or a `Path`

```kotlin
val animeDatabaseFileParser = DatabaseFileParser<Anime>(fileParser = AnimeDatabaseJsonStringParser())

val deadEntriesFileParser = DatabaseFileParser<AnimeId>(fileParser = DeadEntriesJsonStringParser())
```

Now you can either parse the anime database file or a dead entries file by using a URL, a file or a JSON string.
The parser can also handle zipped files, but the zip file must only contain a single JSON file.

*Example:*

```kotlin
val parser = DatabaseFileParser<Anime>(fileParser = AnimeDatabaseJsonStringParser())
val allAnime: List<Anime> = parser.parse(URL("https://raw.githubusercontent.com/manami-project/anime-offline-database/master/anime-offline-database.json"))
```