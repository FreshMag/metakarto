package io.github.freshmag.core

/**
 * Main interface for parsing a map from a string. Implementations should be able to parse YAML, JSON, and other
 * formats.
 */
interface MapParser {
    /**
     * Parses the source string and returns a map of elements.
     */
    fun parse(source: String): Map<String, Element>
}

/**
 * Converts a map of elements to a map of any values. Note: null elements are converted to null values.
 */
fun Map<String, Element>.toMapAny(): Map<String, Any?> =
    mapValues { (_, value) ->
        when (value) {
            is Element.Text -> value.value
            is Element.Array -> value.value.map { it.toDynamic() }
            is Element.Object -> value.value.toMapAny()
            is Element.NullElement -> null
        }
    }

/**
 * Converts a map of elements to a map of strings. Note: null elements are converted to null values.
 */
fun Map<String, Any?>.toMapElement(): Map<String, Element> =
    mapValues { (_, value) ->
        when (value) {
            is String -> Element.Text(value)
            is List<*> ->
                Element.Array(
                    value.map {
                        when (it) {
                            is String -> Element.Text(it)
                            is Map<*, *> -> Element.Object(it.mapKeys { it.key.toString() }.toMapElement())
                            null -> Element.NullElement()
                            else -> throw IllegalArgumentException("Unsupported type: ${it::class.simpleName}")
                        }
                    },
                )
            is Map<*, *> -> Element.Object(value.mapKeys { it.key.toString() }.toMapElement())
            null -> Element.NullElement()
            else -> throw IllegalArgumentException("Unsupported type: ${value::class.simpleName}")
        }
    }
