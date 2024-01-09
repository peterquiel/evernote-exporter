package com.stm.evenote.exporter.model

import java.text.SimpleDateFormat
import java.util.*

class EnexDate(timestamp: Long) {

    constructor() : this(System.currentTimeMillis())

    val date = Date(timestamp)

    override fun toString(): String {
        return SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'").format(this.date)
    }
}
