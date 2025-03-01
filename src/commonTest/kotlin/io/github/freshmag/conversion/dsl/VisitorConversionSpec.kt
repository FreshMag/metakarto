package io.github.freshmag.conversion.dsl

import io.github.freshmag.core.DSL.el
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class VisitorConversionSpec : StringSpec({

    data class ExampleModel(val name: String, val age: Int, val tags: List<String>)

    "should convert an Element tree into an ExampleModel" {
        val root =
            el("root") {
                obj {
                    "name" to { txt("Alice") }
                    "age" to { txt("30") }
                    "tags" to {
                        arr {
                            +"Kotlin"
                            +"Programming"
                        }
                    }
                }
            }

        val model =
            root.visit {
                val name = acceptAnyOfOrThrow("name") { it.toText().value }
                val age = acceptAnyOfOrThrow("age") { it.toText().value.toInt() }
                val tags = acceptAnyOfOrThrow("tags") { it.toArray().acceptAll { it.toText().value } }
                ExampleModel(name, age, tags.toList())
            }

        model shouldBe ExampleModel("Alice", 30, listOf("Kotlin", "Programming"))
    }

    "should handle missing optional fields gracefully" {
        val root =
            el("root") {
                obj {
                    "name" to { txt("Bob") }
                }
            }

        val model =
            root.visit {
                val name = acceptAnyOfOrThrow("name") { it.toText().value }
                val age = acceptAnyOfOrNull("age") { it.toText().value.toInt() } ?: 0
                val tags = acceptAnyOfOrNull("tags") { it.toArray().acceptAll { it.toText().value } } ?: emptyList()
                ExampleModel(name, age, tags.toList())
            }

        model shouldBe ExampleModel("Bob", 0, emptyList())
    }

    "should convert nested structures correctly" {
        data class User(val id: String, val details: ExampleModel)

        val root =
            el("user") {
                obj {
                    "id" to { txt("1234") }
                    "details" to {
                        obj {
                            "name" to { txt("Charlie") }
                            "Age" to { txt("25") }
                            "tags" to {
                                arr {
                                    +"Gamer"
                                    +"Streamer"
                                }
                            }
                        }
                    }
                }
            }

        val user =
            root.visit {
                val id = acceptAnyOfOrThrow("id") { it.toText().value }
                val details =
                    acceptAnyAndVisit("details") {
                        val name = acceptAnyOfOrThrow("name") { it.toText().value }
                        val age = acceptAnyOfOrThrow("Age") { it.toText().value.toInt() }
                        val tags = acceptAnyOfOrThrow("tags") { it.toArray().acceptAll { it.toText().value } }
                        ExampleModel(name, age, tags.toList())
                    }
                User(id, details!!)
            }

        user shouldBe User("1234", ExampleModel("Charlie", 25, listOf("Gamer", "Streamer")))
    }
})
