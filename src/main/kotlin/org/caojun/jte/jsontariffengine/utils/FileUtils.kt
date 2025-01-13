package org.caojun.jte.jsontariffengine.utils

import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException

object FileUtils {

    fun readFileContent(file: File?): String {
        if (file == null) {
            return ""
        }
        val sb = StringBuilder()
        try {
            BufferedReader(FileReader(file)).use { reader ->
                var line: String?
                while ((reader.readLine().also { line = it }) != null) {
                    sb.append(line)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return sb.toString()
    }
}