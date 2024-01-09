package com.stm.evenote.exporter

import java.io.Writer
import java.util.*

class SimpleXmlWriter(val pw: Writer, val prettyPrint: Boolean) : AutoCloseable {

    var level = 0
    var tag = Stack<String>()

    fun openTag(tagName: String, attribute: String = ""): SimpleXmlWriter {
        newline()
        writeOpenTag(tagName, attribute)
        tag.push(tagName)
        level++
        return this
    }

    fun closeTag(): SimpleXmlWriter {
        if (tag.size > 0) {
            level--
            newline()
            val tagName = tag.pop()
            writeCloseTag(tagName)
        }
        return this
    }

    fun writeText(text: String): SimpleXmlWriter {
        pw.write(text)
        return this
    }

    fun writeIndentText(text: String): SimpleXmlWriter {
        newline()
        pw.write(text)
        return this
    }

    fun writeText(tag: String, data: String, attribute: String = ""): SimpleXmlWriter {
        newline()
        return writeOpenTag(tag, attribute)
            .writeText(data)
            .writeCloseTag(tag)
    }

    private fun writeCloseTag(tagName: String): SimpleXmlWriter {
        pw.write("</")
        pw.write(tagName)
        pw.write(">")
        return this
    }

    private fun writeOpenTag(tagName: String, attribute: String): SimpleXmlWriter {
        pw.write("<")
        pw.write(tagName)
        if (attribute.isNotBlank()) {
            pw.write(" ")
            pw.write(attribute)
        }
        pw.write(">")
        return this
    }

    private fun newline() {
        if (prettyPrint) {
            pw.write("\n")
            for (i in 0..level - 1) {
                pw.write("\t")
            }
        }
    }

    override fun close() {
        this.pw.close()
    }
}
