package io.github.manamiproject.modb.dbparser

/**
 * @since 2.0.0
 */
public interface JsonParser<T> {

    /**
     * Parses a valid json [String] to a list of objects of type [T].
     * Requires the JSON the have an array as root object.
     * @since 4.0.0
     * @return [List] of objects of type [T]
     */
    public suspend fun parse(json: String): List<T>
}