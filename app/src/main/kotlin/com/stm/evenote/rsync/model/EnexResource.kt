package com.stm.evenote.rsync.model

import com.stm.evenote.rsync.SimpleXmlWriter
import java.util.Base64

class EnexResource(val data: ByteArray, val mime: String, val width: Int, val height: Int, val resourceAttributes:
EnexResourceAttributes) {

    fun writeXml(writer: SimpleXmlWriter) {
        writer.openTag("resource")
        writer.writeText("data", Base64.getEncoder().encodeToString(data), "encoding=\"base64\"")
        writer.writeText("mime", mime)
        if (width != 0) {
            writer.writeText("width", width.toString())
        }
        if (height != 0) {
            writer.writeText("height", height.toString())
        }
        resourceAttributes.writeXml(writer)
        writer.closeTag()
    }

}