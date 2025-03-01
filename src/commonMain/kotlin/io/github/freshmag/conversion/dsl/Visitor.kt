package io.github.freshmag.conversion.dsl

import io.github.freshmag.conversion.variationsOf
import io.github.freshmag.core.Element

infix fun Element.visit(block: Visitor.() -> Unit) {
    Visitor(this).apply(block)
}

class Visitor(
    private val e: Element,
) {
    private fun Element?.visitOrThrow(
        optional: Boolean = false,
        message: String = "Required element missing",
        block: (Element) -> Unit,
    ) {
        if (this != null) {
            block(this)
        } else {
            if (!optional) throw IllegalArgumentException(message)
        }
    }

    fun acceptAnyOf(
        vararg names: String,
        optional: Boolean = false,
        variations: Boolean = true,
        block: (Element) -> Unit,
    ) {
        require(names.isNotEmpty()) { "At least one name must be provided" }
        val namesToSearch = if (variations) variationsOf(names.toList()) else names.toList()
        val match = namesToSearch.firstNotNullOfOrNull { e.find(it) }
        match.visitOrThrow(optional, "Required element missing: ${names.joinToString()}", block)
    }

    fun accept(
        name: String,
        optional: Boolean = false,
        block: (Element) -> Unit,
    ) {
        val match = e.find(name)
        match.visitOrThrow(optional, "Required element missing: $name", block)
    }

    fun Element.toTextOrNull(): Element.Text? = this as? Element.Text

    fun Element.toText(): Element.Text =
        toTextOrNull() ?: throw IllegalArgumentException("Expected text but found $this")

    fun Element.toTextOrElse(errorHandler: () -> Element.Text): Element.Text = toTextOrNull() ?: errorHandler()

    fun Element.toArrayOrNull(acceptDegenerate: Boolean = true): Element.Array? =
        when (this) {
            is Element.Array -> this
            is Element.Text, is Element.Object ->
                if (acceptDegenerate) {
                    Element.Array(this.name, this.parent) { listOf(this) }
                } else {
                    null
                }

            else -> null
        }

    fun Element.toArray(acceptDegenerate: Boolean = true): Element.Array =
        toArrayOrNull(acceptDegenerate) ?: throw IllegalArgumentException("Expected array but found $this")

    fun Element.toArrayOrElse(
        acceptDegenerate: Boolean = true,
        errorHandler: () -> Element.Array,
    ): Element.Array = toArrayOrNull(acceptDegenerate) ?: errorHandler()

    fun Element.toObjectOrNull(): Element.Object? = this as? Element.Object

    fun Element.toObject(): Element.Object =
        toObjectOrNull() ?: throw IllegalArgumentException("Expected object but found $this")

    fun Element.toObjOrElse(errorHandler: () -> Element.Object): Element.Object = toObjectOrNull() ?: errorHandler()

    fun Element.acceptAll(
        iterateObj: Boolean = false,
        block: (Element) -> Unit,
    ) {
        when (this) {
            is Element.Array -> this.value.forEach(block)
            is Element.Object ->
                if (iterateObj) {
                    this.value.values.forEach(block)
                } else {
                    throw IllegalArgumentException("acceptAll() called on object type: $this")
                }

            else -> throw IllegalArgumentException("acceptAll() called on non-iterable type: $this")
        }
    }

    private fun Element.find(name: String): Element? =
        when (this) {
            is Element.Object -> this.value[name]
            else -> null
        }
}
