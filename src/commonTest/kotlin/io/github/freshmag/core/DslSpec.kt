package io.github.freshmag.core

import io.github.freshmag.core.DSL.el
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class DslSpec : FreeSpec({
    "should create a simple text element" {
        val e = el("title") { txt("Hello, World!") }
        e shouldBe Element.Text("title", null, "Hello, World!")
    }

    "should create an object with nested elements" {
        val e =
            el("root") {
                obj {
                    "name" to { txt("Alice") }
                    "age" to { txt("30") }
                }
            }

        e shouldBe
            Element.Object("root", null) {
                mapOf(
                    "name" to Element.Text("name", e, "Alice"),
                    "age" to Element.Text("age", e, "30"),
                )
            }
    }

    "should create an array with text elements" {
        val e =
            el("items") {
                arr {
                    +"Item 1"
                    +"Item 2"
                }
            }

        e shouldBe
            Element.Array("items", null) {
                listOf(
                    Element.Text("items", e, "Item 1"),
                    Element.Text("items", e, "Item 2"),
                )
            }
    }

    "should create an object containing an array" {
        val e =
            el("container") {
                obj {
                    "list" to {
                        arr {
                            +"A"
                            +"B"
                        }
                    }
                }
            }

        e shouldBe
            Element.Object("container", null) {
                mapOf(
                    "list" to
                        Element.Array("list", e) {
                            listOf(
                                Element.Text("list", (e as Element.Object).value["list"], "A"),
                                Element.Text("list", e.value["list"], "B"),
                            )
                        },
                )
            }
    }

    "should create an empty object as a NullElement" {
        val e = el("empty") { }
        e shouldBe Element.NullElement("empty", null)
    }

    "should create an empty array as an empty list" {
        val e =
            el("array") {
                arr { }
            }

        e shouldBe Element.Array("array", null) { emptyList() }
    }
})
