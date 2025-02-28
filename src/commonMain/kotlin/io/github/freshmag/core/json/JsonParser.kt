package io.github.freshmag.core.json

import io.github.freshmag.core.Element
import io.github.freshmag.core.MapParser
import io.github.freshmag.core.json.JsonUtils.toAnyMap
import io.github.freshmag.core.json.JsonUtils.toJsonMap
import io.github.freshmag.core.toElement
import io.github.freshmag.utils.MapUtils.checkNulls

/**
 * A parser that converts a JSON string into a map of (String, Element).
 */
class JsonParser : MapParser {
    override fun parse(source: String): Element.Object =
        source
            .toJsonMap()
            .toAnyMap()
            .checkNulls()
            .toElement()
}
