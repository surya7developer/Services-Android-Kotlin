package com.suresh.androidservices.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import com.suresh.androidservices.MainActivity.Companion.LOG_TAG
import com.suresh.androidservices.R

class ForegroundService : Service() {

    var i = 1
    private var mediaPlayer: MediaPlayer? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI)
        mediaPlayer?.isLooping = true

        mediaPlayer?.setOnCompletionListener {
            //stopSelf()
        }
    }

    @SuppressLint("ForegroundServiceType")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(LOG_TAG, "onStartCommand: MyForegroundService = ${intent?.action}")

        when (intent?.action) {
            "Play" -> {
                mediaPlayer?.start()
            }

            "Pause" -> {
                mediaPlayer?.pause()
            }

            "Stop" -> {
                mediaPlayer?.stop()
                stopSelf()
            }

            else -> {
                val notification = createNotification()
                startForeground(1001, notification)
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        Log.d(LOG_TAG, "onDestroy: MyForegroundService")
        mediaPlayer?.stop()
        super.onDestroy()
    }

    private fun createNotification(): Notification {
        val channelId = "ch1"
        val channelName = "channel 1"

        // Create notification channel (required for Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }


        val pIntent = Intent(this, ForegroundService::class.java)
        pIntent.action = "Play"
        val playIntent = PendingIntent.getService(this, 100, pIntent, PendingIntent.FLAG_IMMUTABLE)

        val psIntent = Intent(this, ForegroundService::class.java)
        psIntent.action = "Pause"
        val pauseIntent =
            PendingIntent.getService(this, 100, psIntent, PendingIntent.FLAG_IMMUTABLE)

        val sIntent = Intent(this, ForegroundService::class.java)
        sIntent.action = "Stop"
        val stopIntent = PendingIntent.getService(this, 100, sIntent, PendingIntent.FLAG_IMMUTABLE)


        // Build the notification
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Foreground Service Running")
            .setContentText("Playing Music....")
            .setSmallIcon(R.drawable.ic_notification_one)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .addAction(R.drawable.ic_play, "Play", playIntent)
            .addAction(R.drawable.ic_pause, "Pause", pauseIntent)
            .addAction(R.drawable.ic_stop, "Stop", stopIntent)
            .setAutoCancel(false)
            .setOngoing(true)
            .build()
    }
}