package com.lincolnstewart.android.reachout.ui.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.lincolnstewart.android.reachout.ReachOutNotificationService

class AlarmReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_VIEW) {
            println("Alarm triggered")

            val service = ReachOutNotificationService(context)
            service.showNotification()
        } else {
            println("AlarmReceiver received but incorrect Intent.action")
        }
    }
}