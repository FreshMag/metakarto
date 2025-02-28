package io.github.freshmag.core

/**
 * Main interface for parsing a map from a string. Implementations should be able to parse YAML, JSON, and other
 * formats.
 */
interface MapParser {
    /**
     * Parses the source string and returns a map of elements.
     */
    fun parse(source: String): Element.Object
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
 * Converts a map of any values to a Object element with name "root".
 */
fun Map<String, Any?>.toElement(parent: Element? = null): Element.Object =
    Element.Object("root", parent) { this.toMapElement(parent) }

/**
 * Converts a map of elements to a map of strings. Note: null elements are converted to null values.
 */
fun Map<String, Any?>.toMapElement(parent: Element? = null): Map<String, Element> =
    mapValues { (name, value) ->
        when (value) {
            is String -> Element.Text(name, parent, value)
            is List<*> ->
                Element.Array(name, parent) { parentArray ->
                    value.map {
                        when (it) {
                            is String -> Element.Text(name, parentArray, it)
                            is Map<*, *> ->
                                Element.Object(
                                    name,
                                    parentArray,
                                ) { parentObj -> it.mapKeys { it.key.toString() }.toMapElement(parentObj) }

                            null -> Element.NullElement(name, parentArray)
                            else -> throw IllegalArgumentException("Unsupported type: ${it::class.simpleName}")
                        }
                    }
                }

            is Map<*, *> ->
                Element.Object(name, parent) { parentObj ->
                    value.mapKeys { it.key.toString() }.toMapElement(parentObj)
                }

            null -> Element.NullElement(name, parent)
            else -> throw IllegalArgumentException("Unsupported type: ${value::class.simpleName}")
        }
    }
