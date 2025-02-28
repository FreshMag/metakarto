package io.github.freshmag.core

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class MapParserSpec : StringSpec({

    val testMapElements =
        mapOf(
            "key1" to Element.Text("key1", null, "value1"),
            "key2" to Element.Array("key2", null) { listOf(Element.Text("key2", it, "value2")) },
            "key3" to Element.Object("key3", null) { mapOf("key4" to Element.Text("key4", it, "value3")) },
        )

    val testMapAny =
        mapOf(
            "key1" to "value1",
            "key2" to listOf("value2"),
            "key3" to mapOf("key4" to "value3"),
        )

    "A map of elements should be converted to a map of any values" {
        testMapElements.toMapAny() shouldBe testMapAny
    }

    "A map of any should be converted to a map of elements" {
        testMapAny.toMapElement() shouldBe testMapElements
    }

    "A map of elements should be converted to a map of elements and back to a map of any values" {
        testMapElements.toMapAny().toMapElement() shouldBe testMapElements
    }
})
