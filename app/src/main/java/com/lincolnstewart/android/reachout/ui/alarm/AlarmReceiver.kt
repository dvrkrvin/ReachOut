package com.lincolnstewart.android.reachout.ui.alarm

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.lincolnstewart.android.reachout.ReachOutNotificationService

class AlarmReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_VIEW) {
            val message = intent?.getStringExtra("EXTRA_MESSAGE") ?: return
            println("Alarm triggered: $message")

            val service = ReachOutNotificationService(context)
            service.showNotification(message)
        } else {
            println("AlarmReceiver received but incorrect Intent.action")
        }
    }
}