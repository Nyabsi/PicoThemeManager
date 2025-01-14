package cc.sovellus.picothememanager.service

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import cc.sovellus.picothememanager.R
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException


class AudioService : Service() {

    private lateinit var mediaPlayer: MediaPlayer

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        mediaPlayer = MediaPlayer()

        val packageName = intent.getStringExtra("packageName")

        packageName?.let {
            playAudio(packageName)
        }

        val builder = NotificationCompat.Builder(this, "default_channel")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("AudioService")
            .setContentText("this service enables audio for themes.")
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                12345,
                builder.build(),
                FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(12345, builder.build())
        }

        return START_STICKY
    }

    private fun playAudio(packageName: String) {
        try {

            val fd = packageManager.getResourcesForApplication(packageName).assets.openFd("audio/audio.ogg")

            mediaPlayer.setDataSource(fd)
            mediaPlayer.prepare()

            fd.close()

            mediaPlayer.start()
            mediaPlayer.isLooping = true

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        mediaPlayer.stop()
        mediaPlayer.release()
    }
}