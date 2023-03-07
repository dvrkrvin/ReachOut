package com.lincolnstewart.android.reachout

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

class ReachOutApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ContactRepository.initialize(this)
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                ReachOutNotificationService.REACHOUT_CHANNEL_ID,
                "ReachOut",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "Used for ReachOut notifications"

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}