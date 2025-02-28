package io.github.freshmag.core.json

import io.github.freshmag.core.Element
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class JsonSpec : StringSpec({

    "A simple JSON string should be parsed correctly" {
        val json =
            """
            |{
            |  "key": "value"
            |}
            """.trimMargin()
        val parser = JsonParser()
        val result = parser.parse(json)
        result["key"] shouldBe Element.Text("key", null, "value")
    }

    "A JSON string with an array should be parsed correctly" {
        val json =
            """
            |{
            |  "key": [
            |    "value1",
            |    "value2"
            |  ]
            |}
            """.trimMargin()
        val parser = JsonParser()
        val result = parser.parse(json)
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
        result["key"] shouldBe expected
    }

    "A JSON string with an object should be parsed correctly" {
        val json =
            """
            |{
            |  "key": {
            |    "subkey1": "value1",
            |    "subkey2": "value2"
            |  }
            |}
            """.trimMargin()
        val parser = JsonParser()
        val result = parser.parse(json)
        result["key"] shouldBe
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

    "A JSON string with a null value should be parsed correctly" {
        val json =
            """
            |{
            |  "key": null
            |}
            """.trimMargin()
        val parser = JsonParser()
        val result = parser.parse(json)
        result["key"] shouldBe Element.NullElement("key", null)
    }

    "A JSON string with a nested object should be parsed correctly" {
        val json =
            """
            |{
            |  "key": {
            |    "subkey1": {
            |      "subsubkey1": "value1"
            |    }
            |  }
            |}
            """.trimMargin()
        val parser = JsonParser()
        val result = parser.parse(json)
        result["key"] shouldBe
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
                            )
                        },
                )
            }
    }

    "A JSON string with a nested array should be parsed correctly" {
        val json =
            """
            |{
            |  "key": [
            |    {
            |      "subkey1": "value1"
            |    },
            |    {
            |      "subkey2": "value2"
            |    }
            |  ]
            |}
            """.trimMargin()
        val parser = JsonParser()
        val result = parser.parse(json)
        result["key"] shouldBe
            Element.Array(
                "key",
                null,
            ) { p ->
                listOf(
                    Element.Object(
                        "key",
                        p,
                    ) { p2 ->
                        mapOf(
                            "subkey1" to Element.Text("subkey1", p2, "value1"),
                        )
                    },
                    Element.Object(
                        "key",
                        p,
                    ) { p2 ->
                        mapOf(
                            "subkey2" to Element.Text("subkey2", p2, "value2"),
                        )
                    },
                )
            }
    }
})
