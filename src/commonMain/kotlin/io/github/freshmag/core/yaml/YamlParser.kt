package io.github.freshmag.core.yaml

import io.github.freshmag.core.Element
import io.github.freshmag.core.MapParser
import io.github.freshmag.core.toElement
import io.github.freshmag.utils.MapUtils.checkNulls
import net.mamoe.yamlkt.Yaml

/**
 * A parser that converts a YAML string into a map of (String, Element).
 */
class YamlParser : MapParser {
    override fun parse(source: String): Element.Object {
        val rawMap = Yaml.decodeMapFromString(source)
        return rawMap.checkNulls().toElement()
    }
}
