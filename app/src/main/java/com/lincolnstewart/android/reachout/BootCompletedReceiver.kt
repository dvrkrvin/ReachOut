package com.lincolnstewart.android.reachout

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.lincolnstewart.android.reachout.ui.alarm.AlarmItem
import com.lincolnstewart.android.reachout.ui.alarm.AndroidAlarmScheduler
import java.time.LocalDateTime

private const val TAG = "BootCompletedReceiver"

class BootCompletedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d(TAG, "Device booted")
            // TODO: Schedule notification alarm to default or users preferred time
            val scheduler = context?.let { AndroidAlarmScheduler(it) }
            val alarmItem = AlarmItem(
                time = LocalDateTime.now(),
                message = "a friend"
            )
            scheduler!!::schedule.let { alarmItem.let(it) }

            Log.d(TAG, "Alarm Scheduled upon boot")
        } else {
            Log.d(TAG, "Boot broadcast received but not the correct action")
        }
    }
}