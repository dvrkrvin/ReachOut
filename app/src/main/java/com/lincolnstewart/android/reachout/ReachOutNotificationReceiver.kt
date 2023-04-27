package com.lincolnstewart.android.reachout

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ReachOutNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val service = ReachOutNotificationService(context)
        service.showNotification()
    }
}