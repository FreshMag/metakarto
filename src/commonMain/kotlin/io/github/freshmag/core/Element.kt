package io.github.freshmag.core

/**
 * Represents a Element inside a Metakarto Map.
 */
sealed class Element(
    open val name: String,
    open val parent: Element?,
) {
    /**
     * Represents a text element.
     */
    class Text(
        override val name: String,
        override val parent: Element?,
        val value: String,
    ) : Element(name, parent) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Text) return false

            if (name != other.name) return false
            if (value != other.value) return false

            return true
        }

        override fun hashCode(): Int {
            var result = name.hashCode()
            result = 31 * result + value.hashCode()
            return result
        }
    }

    /**
     * Represents an array element.
     */
    class Array(
        override val name: String,
        override val parent: Element?,
        valueInit: (Element) -> List<Element>,
    ) : Element(name, parent) {
        val value: List<Element> = valueInit(this)

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Array) return false

            if (name != other.name) return false
            if (value != other.value) return false

            return true
        }

        override fun hashCode(): Int {
            var result = name.hashCode()
            result = 31 * result + value.hashCode()
            return result
        }
    }

    /**
     * Represents a nested object element.
     */
    class Object(
        override val name: String,
        override val parent: Element?,
        valueInit: (Element) -> Map<String, Element>,
    ) : Element(name, parent) {
        val value: Map<String, Element> = valueInit(this)

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Object) return false

            if (name != other.name) return false
            if (value != other.value) return false

            return true
        }

        override fun hashCode(): Int {
            var result = name.hashCode()
            result = 31 * result + value.hashCode()
            return result
        }
    }

    /**
     * Represents a null element.
     */
    data class NullElement(
        override val name: String,
        override val parent: Element?,
    ) : Element(name, parent)

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
