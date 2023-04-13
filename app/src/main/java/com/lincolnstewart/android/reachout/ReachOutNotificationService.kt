package com.lincolnstewart.android.reachout

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder

class ReachOutNotificationService(
    private val context: Context
) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun showNotification() {
        // Build the notification so that when a user taps on it, it takes them to the Reach Fragment
        val deepLink = NavDeepLinkBuilder(context)
            .setGraph(R.navigation.mobile_navigation)
            .setDestination(R.id.navigation_reach)
            .createPendingIntent()
        val notification = NotificationCompat.Builder(context, REACHOUT_CHANNEL_ID)
            .setSmallIcon(R.drawable.r_o_logo2)
            .setContentTitle("Reach Out")
            .setContentText("Let's reach out to a friend")
            .setContentIntent(deepLink)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }

    // For testing purposes
    fun scheduleNotifications(context: Context) {
        val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReachOutNotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE )

        // Trigger the alarm every 60 seconds
        val interval = 10 * 1000 // 60 seconds in milliseconds
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + interval,
            interval.toLong(),
            pendingIntent
        )
    }


    companion object {
        const val REACHOUT_CHANNEL_ID = "reachout_channel"
    }
}