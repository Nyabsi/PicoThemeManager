// PicoThemeManager
// Copyright (C) 2025 Nyabsi <nyabsi@sovellus.cc>
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.


package cc.sovellus.picothememanager.service

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import cc.sovellus.picothememanager.Constants.NOTIFICATION_CHANNEL_DEFAULT
import cc.sovellus.picothememanager.Constants.PICO_VRSHELL
import cc.sovellus.picothememanager.Constants.PROP_MRSERVICE
import cc.sovellus.picothememanager.Constants.PROP_SCREEN_STATE
import cc.sovellus.picothememanager.R
import cc.sovellus.picothememanager.utils.getSystemProperty
import java.io.IOException
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


class AudioService : Service() {

    private lateinit var mediaPlayer: MediaPlayer
    private var isPlaying: Boolean = false
    private var lastPackageName: String = ""

    private val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)

    private var checkShouldWePlayAudio: Runnable = Runnable {
        val currentApplication = getSystemProperty(PROP_MRSERVICE)
        if (currentApplication != PICO_VRSHELL && isPlaying)
        {
            mediaPlayer.pause()
            isPlaying = false
        } else if (currentApplication == PICO_VRSHELL && !isPlaying)
        {
            mediaPlayer.start()
            isPlaying = true
        }

        val screenState = getSystemProperty(PROP_SCREEN_STATE)?.toInt() ?: -1

        if (screenState == 1 && isPlaying) {
            mediaPlayer.pause()
            isPlaying = false
        }
        else if (screenState == 2 && !isPlaying) {
            mediaPlayer.start()
            isPlaying = true
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        mediaPlayer = MediaPlayer()

        val packageName = intent.getStringExtra("packageName")

        packageName?.let {
            lastPackageName = packageName
            playAudio(packageName)
        }

        scheduler.scheduleWithFixedDelay(checkShouldWePlayAudio, 1000, 1000, TimeUnit.MILLISECONDS)

        val builder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_DEFAULT)
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

            val fd = packageManager.getResourcesForApplication(packageName).assets.openFd("audio/background.ogg")

            mediaPlayer.reset()
            mediaPlayer.setDataSource(fd)
            mediaPlayer.prepare()

            fd.close()

            mediaPlayer.start()
            mediaPlayer.isLooping = true

            isPlaying = true

        } catch (e: IOException) {
            e.printStackTrace()
            isPlaying = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        mediaPlayer.stop()
        mediaPlayer.release()
        isPlaying = false
    }
}