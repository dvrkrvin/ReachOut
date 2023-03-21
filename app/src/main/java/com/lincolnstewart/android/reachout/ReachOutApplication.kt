package com.lincolnstewart.android.reachout

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import com.lincolnstewart.android.reachout.ui.alarm.AlarmItem
import com.lincolnstewart.android.reachout.ui.alarm.AlarmScheduler
import com.lincolnstewart.android.reachout.ui.alarm.AndroidAlarmScheduler
import java.time.LocalDateTime

private const val TAG = "ReachOutApplication"

class ReachOutApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ContactRepository.initialize(this)
        createNotificationChannel()

        scheduleNotifications()
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

    private fun scheduleNotifications() {
        val scheduler = AndroidAlarmScheduler(this)
        val alarmItem = AlarmItem(
            time = LocalDateTime.now(),
            message = "a Friend"
        )
        alarmItem.let(scheduler::schedule)

        Log.d(TAG, "Alarm Scheduled")
    }

}