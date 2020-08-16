![Build](https://github.com/manami-project/modb-db-parser/workflows/Build/badge.svg)
# modb-db-parser
_[modb](https://github.com/manami-project?tab=repositories&q=modb&type=source)_ stands for _**M**anami **O**ffline **D**ata**B**ase_. Repositories prefixed with this acronym are used to create the [manami-project/anime-offline-database](https://github.com/manami-project/anime-offline-database).

# What does this lib do?
This lib can parse both the anime database file as well as the files for the dead entries.
 
# Usage
Add repository and dependency
```kotlin
repositories {
    maven {
        url = uri("https://dl.bintray.com/manami-project/maven")
    }
}

dependencies {
    implementation("io.github.manamiproject:modb-db-parser:$version")
}
```

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

*Example:*

```kotlin
val parser = DatabaseFileParser<Anime>(fileParser = AnimeDatabaseJsonStringParser())
parser.parse(URL("https://raw.githubusercontent.com/manami-project/anime-offline-database/master/anime-offline-database.json"))
```