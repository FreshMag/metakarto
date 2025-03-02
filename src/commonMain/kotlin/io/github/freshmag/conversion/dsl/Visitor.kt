package io.github.freshmag.conversion.dsl

import io.github.freshmag.conversion.variationsOf
import io.github.freshmag.core.Element

/**
 * Visits an [Element] tree and provides a DSL for extracting values from it.
 */
infix fun <T> Element.visit(block: Visitor.() -> T): T = Visitor(this).run(block)

/**
 * Shortcut for accepting any of the provided [names] and calling `visit` on the result, opening a new visiting scope.
 * Throws an [IllegalArgumentException] if any of the [names] provided are missing.
 */
fun <T> Visitor.acceptAnyAndVisit(
    vararg names: String,
    block: Visitor.() -> T,
): T? = acceptAnyOfOrThrow(names.toList()) { it.visit(block) }

/**
 * Shortcut for accepting any of the provided [names] and calling `visit` on the result, opening a new visiting scope.
 * Returns null if any of the [names] provided are missing.
 */
fun <T> Visitor.acceptAnyAndVisitOrNull(
    vararg names: String,
    block: Visitor.() -> T,
): T? = acceptAnyOfOrNull(names.toList()) { it.visit(block) }

/**
 * Shortcut for accepting any of the provided [names] and calling `visit` on the result, opening a new visiting scope.
 */
fun <T> Visitor.acceptAnyAndVisit(
    names: List<String>,
    block: Visitor.() -> T,
): T? = acceptAnyOfOrThrow(names) { it.visit(block) }

/**
 * Class that holds DSL functions for extracting values from an [Element] tree.
 */
class Visitor(
    internal val e: Element,
) {
    /**
     * Visit an element or return null if it is missing. Returns the result of [block] if the element is present.
     */
    internal fun <T> Element?.visitOrNull(block: (Element) -> T): T? = this?.let { block(it) }

    /**
     * Visits the first element with any of the provided [names]. If [variations] is true, variations of the names are
     * also accepted. Returns the result of [block] if the element is present, otherwise null.
     */
    fun <T> Visitor.acceptAnyOfOrNull(
        names: List<String>,
        variations: Boolean = true,
        block: (Element) -> T,
    ): T? {
        require(names.isNotEmpty()) { "At least one name must be provided" }
        val namesToSearch = if (variations) variationsOf(names.toList()) else names.toList()
        val match = e.findAny(namesToSearch)
        return match.visitOrNull(block)
    }

    /**
     * Visits all elements with the provided lists of [names]. Each list is treated as an alternative name for the same
     * element. If [variations] is true, variations of the names are also accepted. Finally, the [block] is called with
     * the list of elements. If any of the elements are missing, null is returned.
     */
    fun <T> Visitor.acceptAllOfOrNull(
        names: List<List<String>>,
        variations: Boolean = false,
        block: (List<Element>) -> T,
    ): T? {
        require(names.isNotEmpty()) { "At least one name must be provided" }
        val namesToSearch = if (variations) names.map { variationsOf(it) } else names.toList()
        val matches = namesToSearch.mapNotNull { e.findAny(it) }
        return if (matches.size == names.size) {
            block(matches)
        } else {
            null
        }
    }

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
    internal fun Element.find(name: String): Element? =
        when (this) {
            is Element.Object -> this.value[name]
            else -> null
        }

    /**
     * Searches for an element with any of the provided [names] in an object. If the element is not found, null is
     * returned.
     */
    internal fun Element.findAny(names: Collection<String>): Element? = names.firstNotNullOfOrNull { find(it) }
}
