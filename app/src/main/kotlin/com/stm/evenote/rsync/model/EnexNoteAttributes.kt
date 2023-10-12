package com.stm.evenote.rsync.model

import com.stm.evenote.rsync.SimpleXmlWriter

data class EnexNoteAttributes(
    val latitude: Double,
    val longitude: Double,
    val altitude: Double,
    val author: String,
    val contentClass: String?,
    val source: String?,
    val sourceURL: String?,
    val sourceApplication: String?,
    val reminderDoneTime: Long,
    val reminderTime: Long,
    val reminderOrder: Long,
) {

    fun writeXml(writer: SimpleXmlWriter) {
        writer.openTag("note-attributes")
        if (!contentClass.isNullOrBlank()) {
            writer.writeText("content-class", contentClass)
        }
        if (!source.isNullOrBlank()) {
            writer.writeText("source", source)
        }
        if (!sourceURL.isNullOrBlank()) {
            writer.writeText("source-url", sourceURL)
        }
        if (!sourceApplication.isNullOrBlank()) {
            writer.writeText("source-application", sourceApplication)
        }
        if (reminderTime != 0L) {
            writer.writeText("reminder-time", EnexDate(reminderTime).toString())
        }
        if (reminderDoneTime != 0L) {
            writer.writeText("reminder-done-time", EnexDate(reminderDoneTime).toString().lowercase())
        }
        if (reminderOrder != 0L) {
            writer.writeText("reminder-order", reminderOrder.toString())
        }
        if (latitude != 0.0){
            writer.writeText("latitude", latitude.toString())
        }
        if (longitude != 0.0) {
            writer.writeText("longitude", longitude.toString())
        }
        if (altitude != 0.0) {
            writer.writeText("altitude", altitude.toString())
        }
        if (author.isNotEmpty()) {
            writer.writeText("author", author)
        }
        writer.closeTag()
    }

}
