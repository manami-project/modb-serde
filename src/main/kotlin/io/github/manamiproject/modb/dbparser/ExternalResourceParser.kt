package io.github.manamiproject.modb.dbparser

import io.github.manamiproject.modb.core.extensions.RegularFile
import java.net.URL

/**
 * Parses external resources such as [RegularFile]s or [URL]s.
 * @since 1.0.0
 */
public interface ExternalResourceParser<T> {

    /**
     * Parses content retrieved from an [URL] to a [List] of objects of type [T].
     * @since 4.0.0
     * @return [List] of objects of type [T]
     */
    public suspend fun parse(url: URL): List<T>

    /**
     * Parses content retrieved from an [RegularFile] to a [List] of objects of type [T].
     * @since 4.0.0
     * @return [List] of objects of type [T]
     */
    public suspend fun parse(file: RegularFile): List<T>
}