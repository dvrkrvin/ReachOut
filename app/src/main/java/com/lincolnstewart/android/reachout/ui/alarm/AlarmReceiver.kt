package com.lincolnstewart.android.reachout.ui.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.lincolnstewart.android.reachout.ReachOutNotificationService

class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val message = intent?.getStringExtra("EXTRA_MESSAGE") ?: return
        println("Alarm triggered: $message")

        val service = ReachOutNotificationService(context)
        service.showNotification("Test123")
    }
}