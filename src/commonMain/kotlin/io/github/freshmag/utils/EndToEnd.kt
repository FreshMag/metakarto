package io.github.freshmag.utils

import io.github.freshmag.core.Element
import io.github.freshmag.core.json.JsonParser
import io.github.freshmag.core.yaml.YamlParser

/**
 * Parse JSON string to Element.Object
 */
fun parseJson(json: String): Element.Object = JsonParser().parse(json)

/**
 * Parse YAML string to Element.Object
 */
fun parseYaml(yaml: String): Element.Object = YamlParser().parse(yaml)
