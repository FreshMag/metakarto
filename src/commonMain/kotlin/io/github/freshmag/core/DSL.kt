package io.github.freshmag.core

object DSL {
    fun el(
        name: String,
        p: Element? = null,
        init: Builder.() -> Unit,
    ): Element {
        val builder = Builder(name, p)
        builder.init()
        return builder.build()
    }

    class Builder(
        val n: String,
        val p: Element?,
    ) {
        private var t: String? = null
        private var l: MutableList<Element>? = null
        private var m: MutableMap<String, Element>? = null

        fun txt(v: String) {
            t = v
        }

        fun arr(init: ArrBuilder.() -> Unit) {
            l = ArrBuilder(this).apply(init).build()
        }

        fun obj(init: ObjBuilder.() -> Unit) {
            m = ObjBuilder(this).apply(init).build()
        }

        fun build(): Element =
            when {
                t != null -> Element.Text(n, p, t!!)
                l != null -> Element.Array(n, p) { l!! }
                m != null -> Element.Object(n, p) { m!! }
                else -> Element.NullElement(n, p)
            }
    }

    // Simplified object builder
    class ObjBuilder(
        private val builder: Builder,
    ) {
        private val m = mutableMapOf<String, Element>()

        infix fun String.to(init: Builder.() -> Unit) {
            m[this] = el(this, builder.build(), init)
        }

        fun build(): MutableMap<String, Element> = m
    }

    // Simplified array builder
    class ArrBuilder(
        private val builder: Builder,
    ) {
        private val l = mutableListOf<Element>()

        operator fun Element.unaryPlus() {
            l.add(this)
        }

        operator fun String.unaryPlus() {
            l.add(Element.Text(builder.n, builder.build(), this))
        }

        fun build(): MutableList<Element> = l
    }
}
