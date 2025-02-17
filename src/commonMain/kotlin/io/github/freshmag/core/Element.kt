package io.github.freshmag.core

sealed class Element {
    data class Text(
        val value: String,
    ) : Element()

    data class Array(
        val value: List<Element>,
    ) : Element()

    data class Object(
        val value: Map<String, Element>,
    ) : Element()

    class NullElement : Element() {
        override fun equals(other: Any?): Boolean = other is NullElement

        override fun hashCode(): Int = 0
    }

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
