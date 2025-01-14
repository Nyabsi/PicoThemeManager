package cc.sovellus.picothememanager.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.ContextWrapper.MODE_PRIVATE
import android.content.Intent
import cc.sovellus.picothememanager.extension.lastUsedTheme
import cc.sovellus.picothememanager.service.AudioService


class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            val preferences = context.getSharedPreferences("pico_theme_manager_prefs", MODE_PRIVATE)
            if (preferences.lastUsedTheme.isNotEmpty()) {
                val hasAudio = context.packageManager.getResourcesForApplication(preferences.lastUsedTheme).assets.list("audio")?.isNotEmpty()
                if (hasAudio == true) {
                    val serviceIntent = Intent(context, AudioService::class.java)
                    serviceIntent.putExtra("packageName", preferences.lastUsedTheme)
                    context.startForegroundService(serviceIntent)
                }
            }

        }
    }
}