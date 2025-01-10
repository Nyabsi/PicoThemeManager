package cc.sovellus.picothememanager.utils

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import cc.sovellus.picothememanager.Constants
import cc.sovellus.picothememanager.Constants.APP_PERMISSIONS_ACTIVITY_LEGACY
import cc.sovellus.picothememanager.Constants.APP_PERMISSIONS_ACTIVITY_SPARROW
import cc.sovellus.picothememanager.Constants.PICO_PERMISSION_CONTROLLER
import cc.sovellus.picothememanager.R
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
fun Context.checkSecurePermission(callback: () -> Unit) {
    if (ActivityCompat.checkSelfPermission(
            this, Constants.ANDROID_PERMISSION_SECURE_SETTINGS
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        callback()
    } else {
        Toast.makeText(
            this,
            getString(R.string.toast_no_permission),
            Toast.LENGTH_LONG
        ).show()
    }
}

fun Context.requestPicoDeletion(pkg: String, name: String) {
    Intent().apply {
        setComponent(
            ComponentName(
                PICO_PERMISSION_CONTROLLER,
                if (getSystemProperty("ro.product.name").contentEquals("sparrow")) {
                    APP_PERMISSIONS_ACTIVITY_SPARROW
                } else {
                    APP_PERMISSIONS_ACTIVITY_LEGACY
                }
            )
        )
        putExtra(
            "android.intent.extra.PACKAGE_NAME",
            pkg
        )
        putExtra(
            "pico_permission_app_name", name
        )
    }.run {
        startActivity(this)
    }
}