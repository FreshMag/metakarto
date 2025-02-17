package io.github.freshmag.utils

object MapUtils {
    /**
     * Checks that the map does not contain null keys, and throws an [IllegalArgumentException] if it does.
     */
    @Throws(IllegalArgumentException::class)
    internal fun <K, V> Map<K?, V?>.checkNulls(): Map<K, V?> =
        map { (key, value) ->
            require(key != null) { "Found null key" }
            key to value
        }.toMap()
}
