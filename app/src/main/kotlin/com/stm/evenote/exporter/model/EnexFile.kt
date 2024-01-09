package com.stm.evenote.exporter.model

import com.stm.evenote.exporter.SimpleXmlWriter

class EnexFile(val writer: SimpleXmlWriter) {

    init {
        writer.writeText("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
        writer.writeIndentText("<!DOCTYPE en-export SYSTEM \"http://xml.evernote.com/pub/evernote-export3.dtd\">")
        writer.openTag("en-export", "export-date=\"${EnexDate()}\" application=\"Evernote\" " +
                "version=\"Evernote Exporter\""
        )
    }

    fun addNote(note : EnexNote) : EnexFile {
        note.writeXml(this.writer)
        return this
    }

    fun close() {
        writer.closeTag()
    }
}
