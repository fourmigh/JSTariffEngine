package org.caojun.library.jte.utils

class JsonTimestamp internal constructor(val timestamp: Long) {
    val format = TimeUtils.getTime(timestamp)
}
