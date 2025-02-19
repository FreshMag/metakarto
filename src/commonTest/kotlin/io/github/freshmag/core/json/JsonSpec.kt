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
        result["key"] shouldBe Element.Text("value")
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
        result["key"] shouldBe
            Element.Array(
                listOf(
                    Element.Text("value1"),
                    Element.Text("value2"),
                ),
            )
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
                mapOf(
                    "subkey1" to Element.Text("value1"),
                    "subkey2" to Element.Text("value2"),
                ),
            )
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
        result["key"] shouldBe Element.NullElement()
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
                mapOf(
                    "subkey1" to
                        Element.Object(
                            mapOf(
                                "subsubkey1" to Element.Text("value1"),
                            ),
                        ),
                ),
            )
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
                listOf(
                    Element.Object(
                        mapOf(
                            "subkey1" to Element.Text("value1"),
                        ),
                    ),
                    Element.Object(
                        mapOf(
                            "subkey2" to Element.Text("value2"),
                        ),
                    ),
                ),
            )
    }
})
