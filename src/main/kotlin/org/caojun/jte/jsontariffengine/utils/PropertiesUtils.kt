package org.caojun.jte.jsontariffengine.utils

import java.io.File
import java.util.*

object PropertiesUtils {

    private val file = File("data.properties")
    private val properties = Properties()

    fun save(key: String, value: String = "") {
        try {
            properties.setProperty(key, value)
            file.outputStream().use { properties.store(it, null) }
        } catch (e: Exception) {
            println("PropertiesUtils.save: $e")
        }
    }

    fun load(key: String, defValue: String = ""): String {
        return try {
            file.inputStream().use { properties.load(it) }
            properties.getProperty(key, defValue)
        } catch (e: Exception) {
            println("PropertiesUtils.load: $e")
            ""
        }
    }
}