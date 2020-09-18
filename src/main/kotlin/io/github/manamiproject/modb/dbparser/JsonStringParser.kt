package io.github.manamiproject.modb.dbparser

/**
 * @since 1.0.0
 */
public interface JsonStringParser<T> {

    public fun parse(json: String): List<T>
}