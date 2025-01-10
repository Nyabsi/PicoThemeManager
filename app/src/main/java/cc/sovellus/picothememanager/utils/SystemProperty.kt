package cc.sovellus.picothememanager.utils

import java.io.BufferedReader
import java.io.InputStreamReader

fun getSystemProperty(propName: String): String? {
    return try {
        val process = Runtime.getRuntime().exec("getprop $propName")
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        reader.readLine()
    } catch (e: Exception) {
        null
    }
}