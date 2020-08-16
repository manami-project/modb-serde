package io.github.manamiproject.modb.dbparser

/**
 * @since 1.0.0
 */
interface JsonStringParser<T> {

    fun parse(json: String): List<T>
}