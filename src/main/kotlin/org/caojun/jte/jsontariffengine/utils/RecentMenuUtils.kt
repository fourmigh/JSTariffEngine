package org.caojun.jte.jsontariffengine.utils

import javafx.scene.control.MenuItem
import org.caojun.jte.jsontariffengine.enums.RecentFile
import java.io.File

object RecentMenuUtils {

    private val files = ArrayList<File>()
    private val list = ArrayList<Pair<MenuItem, File>>()

    private fun getKey(key: String): String {
        return "RecentFile_$key"
    }

    fun load(): Array<Pair<MenuItem, File>> {
        files.clear()
        list.clear()
        val listRecentFile = RecentFile.entries.toTypedArray()
        for (i in 0 until listRecentFile.size - 1) {
            val key = getKey(listRecentFile[i].name)
            val filePath = PropertiesUtils.load(key)
            if (filePath.isEmpty()) {
                continue
            }
            val file = File(filePath)
            files.add(file)
            val menuItem = MenuItem("${listRecentFile[i].text}${file.name}")
            list.add(Pair(menuItem, file))
        }
        if (list.isNotEmpty()) {
            val menuItem = MenuItem(listRecentFile[listRecentFile.size - 1].text)
            list.add(Pair(menuItem, File("clear")))
        }
        return list.toTypedArray()
    }

    fun clear(): Array<Pair<MenuItem, File>> {
        val listRecentFile = RecentFile.entries.toTypedArray()
        for (i in 0 until listRecentFile.size - 1) {
            val key = getKey(listRecentFile[i].name)
            PropertiesUtils.save(key)
        }
        return load()
    }

    fun add(file: File): Array<Pair<MenuItem, File>> {
        for (i in 0 until files.size) {
            if (files[i].absolutePath == file.absolutePath) {
                files.removeAt(i)
                break
            }
        }
        files.add(0, file)
        val listRecentFile = RecentFile.entries.toTypedArray()
        for (i in listRecentFile.indices) {
            val key = getKey(listRecentFile[i].name)
            val value = if (i < files.size) {
                files[i].absolutePath
            } else {
                ""
            }
            PropertiesUtils.save(key, value)
        }
        return load()
    }
}