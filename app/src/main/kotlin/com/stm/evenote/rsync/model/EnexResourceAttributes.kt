package com.stm.evenote.rsync.model

import com.stm.evenote.rsync.SimpleXmlWriter

class EnexResourceAttributes(
    val fileName: String,
    val sourceURL: String,
    val latitude: Double,
    val longitude: Double,
    val altitude: Double,
    val attachment: Boolean
) {

    fun writeXml(writer: SimpleXmlWriter) {
        writer.openTag("resource-attributes")
        writer.writeText("file-name", fileName)
        if (sourceURL.isNotBlank()) {
            writer.writeText("source-url", sourceURL)
        }
        if (latitude != 0.0) {
            writer.writeText("latitude", latitude.toString())
        }
        if (longitude != 0.0) {
            writer.writeText("longitude", longitude.toString())
        }
        if (altitude != 0.0) {
            writer.writeText("altitude", altitude.toString())
        }
        writer.writeText("attachment", attachment.toString().lowercase())
        writer.closeTag()
    }

}