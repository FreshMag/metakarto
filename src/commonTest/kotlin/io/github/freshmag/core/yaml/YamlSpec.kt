package io.github.freshmag.core.yaml

import io.kotest.core.spec.style.StringSpec

class YamlSpec : StringSpec({

//    "A simple YAML string should be parsed correctly" {
//        val yaml =
//            """
//            |key: value
//            """.trimMargin()
//        val parser = YamlParser()
//        val result = parser.parse(yaml)
//        result["key"] shouldBe Element.Text("value")
//    }
//
//    "A YAML string with an array should be parsed correctly" {
//        val yaml =
//            """
//            |key:
//            |  - value1
//            |  - value2
//            """.trimMargin()
//        val parser = YamlParser()
//        val result = parser.parse(yaml)
//        result["key"] shouldBe
//            Element.Array(
//                listOf(
//                    Element.Text("value1"),
//                    Element.Text("value2"),
//                ),
//            )
//    }
//
//    "A YAML string with an object should be parsed correctly" {
//        val yaml =
//            """
//            |key:
//            |  subkey1: value1
//            |  subkey2: value2
//            """.trimMargin()
//        val parser = YamlParser()
//        val result = parser.parse(yaml)
//        result["key"] shouldBe
//            Element.Object(
//                mapOf(
//                    "subkey1" to Element.Text("value1"),
//                    "subkey2" to Element.Text("value2"),
//                ),
//            )
//    }
//
//    "A YAML string with a null value should be parsed correctly" {
//        val yaml =
//            """
//            |key: null
//            """.trimMargin()
//        val parser = YamlParser()
//        val result = parser.parse(yaml)
//        result["key"] shouldBe Element.NullElement()
//    }
//
//    "A YAML string with a nested object should be parsed correctly" {
//        val yaml =
//            """
//            |key:
//            |  subkey1:
//            |    subsubkey1: value1
//            |    subsubkey2: value2
//            |  subkey2:
//            |    subsubkey3: value3
//            """.trimMargin()
//        val parser = YamlParser()
//        val result = parser.parse(yaml)
//        result["key"] shouldBe
//            Element.Object(
//                mapOf(
//                    "subkey1" to
//                        Element.Object(
//                            mapOf(
//                                "subsubkey1" to Element.Text("value1"),
//                                "subsubkey2" to Element.Text("value2"),
//                            ),
//                        ),
//                    "subkey2" to
//                        Element.Object(
//                            mapOf(
//                                "subsubkey3" to Element.Text("value3"),
//                            ),
//                        ),
//                ),
//            )
//    }
//
//    "A YAML string with a nested array should be parsed correctly" {
//        val yaml =
//            """
//            |key:
//            |  - value1
//            |  - value2:
//            |      subkey1: value3
//            |      subkey2: value4
//            """.trimMargin()
//        val parser = YamlParser()
//        val result = parser.parse(yaml)
//        result["key"] shouldBe
//            Element.Array(
//                listOf(
//                    Element.Text("value1"),
//                    Element.Object(
//                        mapOf(
//                            "value2" to
//                                Element.Object(
//                                    mapOf(
//                                        "subkey1" to Element.Text("value3"),
//                                        "subkey2" to Element.Text("value4"),
//                                    ),
//                                ),
//                        ),
//                    ),
//                ),
//            )
//    }
})
