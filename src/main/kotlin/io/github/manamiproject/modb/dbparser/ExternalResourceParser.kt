package io.github.manamiproject.modb.dbparser

import io.github.manamiproject.modb.core.extensions.RegularFile
import java.net.URL

interface ExternalResourceParser<T> {

    fun parse(url: URL): List<T>

    fun parse(file: RegularFile): List<T>
}