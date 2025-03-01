package io.github.freshmag.conversion.dsl

import io.github.freshmag.core.Element
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class VisitorUnitSpec : StringSpec({

    "should accept any of multiple names" {
        val root =
            Element.Object("root", null) {
                mapOf(
                    "name" to Element.Text("name", null, "Alice"),
                    "id" to Element.Text("id", null, "123"),
                )
            }

        root visit {
            acceptAnyOfOrThrow("name", "id") { elem ->
                elem.toText().value shouldBe "Alice"
            }
        }
    }

    "should accept alternative keys" {
        val root =
            Element.Object("root", null) {
                mapOf(
                    "ID" to Element.Text("ID", null, "456"),
                )
            }

        root visit {
            acceptAnyOfOrThrow("id", "ID") { elem ->
                elem.toText().value shouldBe "456"
            }
        }
    }

    "should throw if required element is missing" {
        val root = Element.Object("root", null) { emptyMap() }

        shouldThrow<IllegalArgumentException> {
            root visit {
                acceptAnyOfOrThrow("missing") { }
            }
        }
    }

    "should return null when converting non-text to text" {
        val obj = Element.Object("object", null) { mapOf("key" to (Element.Object("key", it) { emptyMap() })) }

        obj visit {
            accept("key") { elem ->
                elem.toTextOrNull() shouldBe null
            }
        }
    }

    "should convert text element properly" {
        val obj = Element.Object("object", null) { mapOf("key" to Element.Text("key", null, "Hello")) }

        obj visit {
            accept("key") { elem ->
                elem.toText().value shouldBe "Hello"
            }
        }
    }

    "should throw when converting non-text to text" {
        val obj = Element.Object("root", null) { mapOf("key" to Element.Object("key", null) { emptyMap() }) }

        obj visit {
            accept("key") { elem ->
                shouldThrow<IllegalArgumentException> { elem.toText() }
            }
        }
    }

    "should convert single text element to array if acceptDegenerate is true" {
        val obj = Element.Object("root", null) { mapOf("key" to Element.Text("key", null, "Item")) }

        obj visit {
            accept("key") { elem ->
                val arr = elem.toArrayOrNull(acceptDegenerate = true)
                arr?.value?.size shouldBe 1
                arr?.value[0]?.toText()?.value shouldBe "Item"
            }
        }
    }

    "should not convert single text element to array if acceptDegenerate is false" {
        val obj = Element.Object("root", null) { mapOf("key" to Element.Text("key", null, "Item")) }

        obj visit {
            accept("key") { elem ->
                val arr = elem.toArrayOrNull(acceptDegenerate = false)
                arr shouldBe null
            }
        }
    }

    "should convert an object to itself" {
        val obj = Element.Object("root", null) { mapOf("key" to Element.Object("key", null) { emptyMap() }) }

        obj visit {
            accept("key") { elem ->
                elem.toObjectOrNull() shouldBe elem
            }
        }
    }

    "should throw when converting non-object to object" {
        val obj = Element.Object("root", null) { mapOf("key" to Element.Text("key", null, "Hello")) }

        obj visit {
            accept("key") { elem ->
                shouldThrow<IllegalArgumentException> { elem.toObject() }
            }
        }
    }

    "should iterate over an array" {
        val obj =
            Element.Object("root", null) {
                mapOf(
                    "key" to
                        Element.Array("key", null) {
                            listOf(
                                Element.Text("key", null, "One"),
                                Element.Text("key", null, "Two"),
                            )
                        },
                )
            }

        obj visit {
            accept("key") { elem ->
                val values = mutableListOf<String>()
                elem.acceptAll { values.add(it.toText().value) }

                values shouldBe listOf("One", "Two")
            }
        }
    }

    "should throw when trying to acceptAll on an object without iterateObj" {
        val obj = Element.Object("root", null) { mapOf("key" to Element.Object("key", null) { emptyMap() }) }

        obj visit {
            accept("key") { elem ->
                shouldThrow<IllegalArgumentException> { elem.acceptAll { } }
            }
        }
    }

    val testObj =
        Element.Object("root", null) {
            mapOf(
                "key" to
                    Element.Object("key", null) {
                        mapOf(
                            "key1" to Element.Text("key1", null, "A"),
                            "key2" to Element.Text("key2", null, "B"),
                        )
                    },
            )
        }

    "should acceptAll on an object when iterateObj is true" {
        val obj = testObj

        obj visit {
            accept("key") { elem ->
                val values = mutableListOf<String>()
                elem.acceptAll(iterateObj = true) { values.add(it.toText().value) }

                values shouldBe listOf("A", "B")
            }
        }
    }

    "should be able to visit the parent" {
        val obj = testObj

        obj visit {
            accept("key") { elem ->
                elem.parent?.visit {
                    acceptAnyOfOrThrow("key") {
                        it.toObjectOrNull() shouldBe elem
                    }
                }
            }
        }
    }
})
