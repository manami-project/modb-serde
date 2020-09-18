package io.github.manamiproject.modb.dbparser

import io.github.manamiproject.modb.core.extensions.RegularFile
import java.net.URL

public interface ExternalResourceParser<T> {

    public fun parse(url: URL): List<T>

    public fun parse(file: RegularFile): List<T>
}