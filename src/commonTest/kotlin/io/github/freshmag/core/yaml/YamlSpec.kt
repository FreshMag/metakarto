package io.github.freshmag.core.yaml

import io.github.freshmag.core.Element
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class YamlSpec : StringSpec({

    "A simple YAML string should be parsed correctly" {
        val yaml =
            """
            |key: value
            """.trimMargin()
        val parser = YamlParser()
        val result = parser.parse(yaml)
        result.value["key"] shouldBe Element.Text("key", null, "value")
    }

    "A YAML string with an array should be parsed correctly" {
        val yaml =
            """
            |key:
            |  - value1
            |  - value2
            """.trimMargin()
        val parser = YamlParser()
        val result = parser.parse(yaml)
        val expected =
            Element.Array(
                "key",
                null,
            ) { p ->
                listOf(
                    Element.Text("key", p, "value1"),
                    Element.Text("key", p, "value2"),
                )
            }
        result.value["key"] shouldBe expected
    }

    "A YAML string with an object should be parsed correctly" {
        val yaml =
            """
            |key:
            |  subkey1: value1
            |  subkey2: value2
            """.trimMargin()
        val parser = YamlParser()
        val result = parser.parse(yaml)
        result.value["key"] shouldBe
            Element.Object(
                "key",
                null,
            ) { p ->
                mapOf(
                    "subkey1" to Element.Text("subkey1", p, "value1"),
                    "subkey2" to Element.Text("subkey2", p, "value2"),
                )
            }
    }

    "A YAML string with a null value should be parsed correctly" {
        val yaml =
            """
            |key: null
            """.trimMargin()
        val parser = YamlParser()
        val result = parser.parse(yaml)
        result.value["key"] shouldBe Element.NullElement("key", null)
    }

    "A YAML string with a nested object should be parsed correctly" {
        val yaml =
            """
            |key:
            |  subkey1:
            |    subsubkey1: value1
            |    subsubkey2: value2
            |  subkey2:
            |    subsubkey3: value3
            """.trimMargin()
        val parser = YamlParser()
        val result = parser.parse(yaml)
        result.value["key"] shouldBe
            Element.Object(
                "key",
                null,
            ) { p ->
                mapOf(
                    "subkey1" to
                        Element.Object(
                            "subkey1",
                            p,
                        ) { p2 ->
                            mapOf(
                                "subsubkey1" to Element.Text("subsubkey1", p2, "value1"),
                                "subsubkey2" to Element.Text("subsubkey2", p2, "value2"),
                            )
                        },
                    "subkey2" to
                        Element.Object(
                            "subkey2",
                            p,
                        ) { p2 ->
                            mapOf(
                                "subsubkey3" to Element.Text("subsubkey3", p2, "value3"),
                            )
                        },
                )
            }
    }

    "A YAML string with a nested array should be parsed correctly" {
        val yaml =
            """
            |key:
            |  - value1
            |  - value2:
            |      subkey1: value3
            |      subkey2: value4
            """.trimMargin()
        val parser = YamlParser()
        val result = parser.parse(yaml)
        result.value["key"] shouldBe
            Element.Array(
                "key",
                null,
            ) { p ->
                listOf(
                    Element.Text("key", p, "value1"),
                    Element.Object(
                        "key",
                        p,
                    ) { p2 ->
                        mapOf(
                            "value2" to
                                Element.Object(
                                    "value2",
                                    p2,
                                ) { p3 ->
                                    mapOf(
                                        "subkey1" to Element.Text("subkey1", p3, "value3"),
                                        "subkey2" to Element.Text("subkey2", p3, "value4"),
                                    )
                                },
                        )
                    },
                )
            }
    }
})
