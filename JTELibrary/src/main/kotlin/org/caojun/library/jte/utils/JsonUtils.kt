package org.caojun.library.jte.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

object JsonUtils {
    const val NULL = "NULL"

    private val gson = Gson()

    /**
     * 将空值替换成NULL，以免解析时出错
     */
    private fun markNullValue(json: String): String {
        return json.replace("\"\"", NULL)
    }

    inline fun <reified T> fromJson(json: String?): T? {
        val typeToken = object : TypeToken<T>() {}.type
        return fromJson(json, typeToken)
    }

    inline fun <reified T> fromJsonWithErrStr(json: String?): Pair<T?, String?> {
        val typeToken = object : TypeToken<T>() {}.type
        return fromJsonWithErrStr(json, typeToken)
    }

    @JvmStatic
    fun <T> fromJson(json: String?, classOfT: Class<T>): T? {
        return try {
            gson.fromJson(json, classOfT)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    @JvmStatic
    fun <T> fromJson(json: String?, typeOfT: Type): T? {
        return try {
            gson.fromJson(json, typeOfT)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    @JvmStatic
    fun <T> fromJsonWithErrStr(json: String?, typeOfT: Type): Pair<T?, String?> {
        return try {
            Pair(gson.fromJson(json, typeOfT), null)
        } catch (e: Exception) {
            e.printStackTrace()
            Pair(null, e.message)
        }
    }

    inline fun <reified T> toJsonT(data: T): String {
        val typeToken = object : TypeToken<T>() {}.type
        return toJson(data, typeToken)
    }

    @JvmStatic
    fun toJson(data: Any?): String {
        return try {
            gson.toJson(data)
        } catch (e: Exception) {
            ""
        }
    }

    @JvmStatic
    fun toJson(data: Any?, typeOfSrc: Type): String {
        return try {
            gson.toJson(data, typeOfSrc)
        } catch (e: Exception) {
            ""
        }
    }

    fun formatJson(data: Any?, all: Boolean = false): String {
        return format(toJson(data), all)
    }

    fun format(json: String, all: Boolean = false): String {
        if (all) {
            return json.replace(",", ",\n")
        }
        return json.replace("},{", "},\n{")
    }

    fun isJson(json: String?): Boolean {
        if (json.isNullOrEmpty()) {
            return false
        }
        if (json.startsWith("{") && json.endsWith("}")) {
            return true
        }
        return json.startsWith("[") && json.endsWith("]")
    }
}