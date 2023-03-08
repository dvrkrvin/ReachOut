package com.lincolnstewart.android.reachout.ui.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.*

private const val TAG = "AndroidAlarmScheduler"

class AndroidAlarmScheduler(
    private val context: Context
): AlarmScheduler {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    override fun schedule(item: AlarmItem) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("EXTRA_MESSAGE", item.message)
        }
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val sharedPreferences = context.getSharedPreferences("ReminderPrefs", Context.MODE_PRIVATE)
        val selectedFrequency = sharedPreferences?.getString("selected_frequency", "")
        val selectedDay = sharedPreferences?.getString("selected_day", "")

        // Attempt to parse selected time
        val selectedTime = sharedPreferences.getString("selected_time", "")

        val sdf = SimpleDateFormat("hh:mm aa", Locale.getDefault())
        var selectedHour = 12
        var selectedMinute = 0
        try {
            val date = sdf.parse(selectedTime ?: "12:00 pm")
            val conversionCalendar = Calendar.getInstance()
            conversionCalendar.time = date!!

            selectedHour = conversionCalendar.get(Calendar.HOUR_OF_DAY)
            selectedMinute = conversionCalendar.get(Calendar.MINUTE)

            Log.d(TAG, "Selected Hour $selectedHour Selected Minute: $selectedMinute")
        } catch (e: ParseException) {
            Log.d(TAG, "Failed to parse selected_time")
        }

        // Calculate interval of notifications based on selected frequency
        var intervalMillis: Long = 0
        when (selectedFrequency) {
            "Weekly" -> intervalMillis = (AlarmManager.INTERVAL_DAY * 7)
            "Biweekly" -> intervalMillis = (AlarmManager.INTERVAL_DAY * 14)
//            "Monthly" -> intervalMillis = AlarmManager.INTERVAL_DAY
        }

        // Set the time at which we trigger the notification into a Calendar instance
        val calendar = Calendar.getInstance()

        // This block of code sets repeating notifications at the time saved into the users
        // ReminderPrefs shared preferences using the key "selected_time"
        // NOTE: Currently able to schedule the time and frequency of the notifications,
        // still haven't figured out how to set a specific day
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
        calendar.set(Calendar.MINUTE, selectedMinute)
        calendar.set(Calendar.SECOND, 0)
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis, // Time at which the we trigger the notification
            intervalMillis, // Interval between notifications
            pendingIntent
        )

        // This block of code will set a repeating notification at the set time,
        // it currently does not use either of the data members received with the item: AlarmItem
//        val calendar = Calendar.getInstance()
//        calendar.timeInMillis = System.currentTimeMillis()
//        calendar.set(Calendar.HOUR_OF_DAY, 0)
//        calendar.set(Calendar.MINUTE, 55)
//        calendar.set(Calendar.SECOND, 0)
//        alarmManager.setRepeating(
//            AlarmManager.RTC_WAKEUP,
//            calendar.timeInMillis,
//            AlarmManager.INTERVAL_DAY,
//            pendingIntent
//        )

            // This block will send the notification every 15 minutes for testing
//        alarmManager.setRepeating(
//            AlarmManager.RTC_WAKEUP,
//            calendar.timeInMillis,
//            AlarmManager.INTERVAL_FIFTEEN_MINUTES,
//            pendingIntent
//        )

        // This will send it at the time specified in the call which is in C2Fragment
//        alarmManager.setExactAndAllowWhileIdle(
//            AlarmManager.RTC_WAKEUP,
//            item.time.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000,
//            PendingIntent.getBroadcast(
//                context,
//                item.hashCode(),
//                intent,
//                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//            )
//        )
    }

    override fun cancel(item: AlarmItem) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                item.hashCode(),
                Intent(context, AlarmReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }
}