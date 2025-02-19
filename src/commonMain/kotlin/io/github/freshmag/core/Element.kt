package io.github.freshmag.core

/**
 * Represents a Element inside a Metakarto Map.
 */
sealed class Element {
    /**
     * Represents a text element.
     */
    data class Text(
        val value: String,
    ) : Element()

    /**
     * Represents an array element.
     */
    data class Array(
        val value: List<Element>,
    ) : Element()

    /**
     * Represents a nested object element.
     */
    data class Object(
        val value: Map<String, Element>,
    ) : Element()

    /**
     * Represents a null element.
     */
    class NullElement : Element() {
        override fun equals(other: Any?): Boolean = other is NullElement

        override fun hashCode(): Int = 0
    }

    /**
     * Converts this element to a dynamic type.
     */
    fun toDynamic(): Any? =
        when (this) {
            is Text -> value
            is Array -> value.map { it.toDynamic() }
            is Object -> value.mapValues { (_, value) -> value.toDynamic() }
            is NullElement -> null
        }

    override fun toString(): String =
        when (this) {
            is Text -> value
            is Array -> value.joinToString(prefix = "[", postfix = "]", separator = ", ")
            is Object ->
                value.entries.joinToString(prefix = "{", postfix = "}", separator = ", ") {
                    "\"${it.key}\": ${it.value}"
                }
            is NullElement -> "null"
        }
}
