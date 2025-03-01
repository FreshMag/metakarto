package io.github.freshmag.conversion.dsl

import io.github.freshmag.conversion.variationsOf
import io.github.freshmag.core.Element

/**
 * Visits an [Element] tree and provides a DSL for extracting values from it.
 */
infix fun <T> Element.visit(block: Visitor.() -> T): T = Visitor(this).run(block)

/**
 * Class that holds DSL functions for extracting values from an [Element] tree.
 */
class Visitor(
    private val e: Element,
) {
    /**
     * Visit an element or throw an exception if it is missing. If [optional] is true, the element is allowed to be
     * missing. [message] is used in the exception. Returns the result of [block] if the element is present, otherwise
     * null.
     */
    private fun <T> Element?.visitOrThrow(
        optional: Boolean = false,
        message: String = "Required element missing",
        block: (Element) -> T,
    ): T? =
        if (this != null) {
            block(this)
        } else {
            if (!optional) throw IllegalArgumentException(message) else null
        }

    /**
     * Visits the first element with any of the provided [names]. If [variations] is true, variations of the names are
     * also accepted. If [optional] is true, the element is allowed to be missing and the block is not called.
     * Returns the result of [block] if the element is present, otherwise null.
     */
    fun <T> acceptAnyOf(
        vararg names: String,
        optional: Boolean = false,
        variations: Boolean = true,
        block: (Element) -> T,
    ): T? {
        require(names.isNotEmpty()) { "At least one name must be provided" }
        val namesToSearch = if (variations) variationsOf(names.toList()) else names.toList()
        val match = namesToSearch.firstNotNullOfOrNull { e.find(it) }
        return match.visitOrThrow(optional, "Required element missing: ${names.joinToString()}", block)
    }

    /**
     * Visits the element with the provided [name]. If [optional] is true, the element is allowed to be missing and the
     * block is not called.
     */
    fun <T> accept(
        name: String,
        optional: Boolean = false,
        block: (Element) -> T,
    ): T? = e.find(name).visitOrThrow(optional, "Required element missing: $name", block)

    /**
     * Converts a text element to a text value. If the element is not a text element, null is returned.
     */
    fun Element.toTextOrNull(): Element.Text? = this as? Element.Text

    /**
     * Converts a text element to a text value. If the element is not a text element, an [IllegalArgumentException] is
     * thrown.
     */
    fun Element.toText(): Element.Text =
        toTextOrNull() ?: throw IllegalArgumentException("Expected text but found $this")

    /**
     * Converts a text element to a text value. If the element is not a text element, the [errorHandler] is called.
     */
    fun Element.toTextOrElse(errorHandler: () -> Element.Text): Element.Text = toTextOrNull() ?: errorHandler()

    /**
     * Converts an element to an array. If the element is not an array, null is returned. If [acceptDegenerate] is true,
     * a single text element is converted to an array with one element.
     */
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

    /**
     * Converts an element to an array. If the element is not an array, an [IllegalArgumentException] is thrown. If
     * [acceptDegenerate] is true, a single text element is converted to an array with one element.
     */
    fun Element.toArray(acceptDegenerate: Boolean = true): Element.Array =
        toArrayOrNull(acceptDegenerate) ?: throw IllegalArgumentException("Expected array but found $this")

    /**
     * Converts an element to an array. If the element is not an array, the [errorHandler] is called. If [acceptDegenerate]
     * is true, a single text element is converted to an array with one element.
     */
    fun Element.toArrayOrElse(
        acceptDegenerate: Boolean = true,
        errorHandler: () -> Element.Array,
    ): Element.Array = toArrayOrNull(acceptDegenerate) ?: errorHandler()

    /**
     * Converts an element to an object. If the element is not an object, null is returned.
     */
    fun Element.toObjectOrNull(): Element.Object? = this as? Element.Object

    /**
     * Converts an element to an object. If the element is not an object, an [IllegalArgumentException] is thrown.
     */
    fun Element.toObject(): Element.Object =
        toObjectOrNull() ?: throw IllegalArgumentException("Expected object but found $this")

    /**
     * Converts an element to an object. If the element is not an object, the [errorHandler] is called.
     */
    fun Element.toObjOrElse(errorHandler: () -> Element.Object): Element.Object = toObjectOrNull() ?: errorHandler()

    /**
     * Iterates over an array or object and calls [block] for each element. If [iterateObj] is true, objects are
     * iterated over as well (i.e., the values of their keys are iterated), otherwise an [IllegalArgumentException] is
     * thrown.
     */
    fun <T> Element.acceptAll(
        iterateObj: Boolean = false,
        block: (Element) -> T,
    ): Iterable<T> =
        when (this) {
            is Element.Array -> this.value.map(block)
            is Element.Object ->
                if (iterateObj) {
                    this.value.values.map(block)
                } else {
                    throw IllegalArgumentException("acceptAll() called on object type: $this")
                }

            else -> throw IllegalArgumentException("acceptAll() called on non-iterable type: $this")
        }

    /**
     * Searches for an element with the provided [name] in an object. If the element is not found, null is returned.
     */
    private fun Element.find(name: String): Element? =
        when (this) {
            is Element.Object -> this.value[name]
            else -> null
        }
}
