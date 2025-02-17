package io.github.freshmag.core.yaml

import io.github.freshmag.core.Element
import io.github.freshmag.core.MapParser
import io.github.freshmag.core.toMapElement
import io.github.freshmag.utils.MapUtils.checkNulls
import net.mamoe.yamlkt.Yaml

class YamlParser : MapParser {
    override fun parse(source: String): Map<String, Element> {
        val rawMap = Yaml.decodeMapFromString(source)
        return rawMap.checkNulls().toMapElement()
    }
}
