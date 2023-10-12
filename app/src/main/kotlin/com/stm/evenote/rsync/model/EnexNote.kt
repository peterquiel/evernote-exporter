package com.stm.evenote.rsync.model

import com.stm.evenote.rsync.SimpleXmlWriter
import java.util.*

data class EnexNote(
    val title: String,
    val content: String,
    val created: Long,
    val updated: Long,
    val tags: List<String>?,
    val noteAttributes: EnexNoteAttributes,
    val resources : List<EnexResource>
) {

    private val createdDate = EnexDate(created)
    private val updatedDate = EnexDate(updated)

    fun writeXml(writer: SimpleXmlWriter) {

        writer.openTag("note")
        writer.writeText("title", title)
        writeContent(writer)
        writer.writeText("created", createdDate.toString())
        writer.writeText("updated", updatedDate.toString())
        if (tags != null) {
            for (tag in tags!!) {
                writer.writeText("tag", tag)
            }
        }
        noteAttributes.writeXml(writer)

        for (resource in resources) {
            resource.writeXml(writer)
        }

        writer.closeTag()
    }

    private fun writeContent(writer: SimpleXmlWriter) {
        writer.openTag("content")
        writer.writeIndentText("<![CDATA[")
        writer.writeIndentText(content)
        writer.writeIndentText("]]>")
        writer.closeTag()
    }
}
