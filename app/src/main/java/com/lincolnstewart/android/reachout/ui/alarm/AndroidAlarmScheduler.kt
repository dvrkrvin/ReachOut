package com.lincolnstewart.android.reachout.ui.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.navigation.NavDeepLinkBuilder
import com.lincolnstewart.android.reachout.MainActivity
import com.lincolnstewart.android.reachout.R
import com.lincolnstewart.android.reachout.ui.reach.ReachFragment
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.util.*

private const val TAG = "AndroidAlarmScheduler"

class AndroidAlarmScheduler(
    private val context: Context
): AlarmScheduler {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    // Get users preferred time from shared preferences and schedule a repeating alarm
    override fun schedule(item: AlarmItem) {
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.action = Intent.ACTION_VIEW
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val sharedPreferences = context.getSharedPreferences("ReminderPrefs", Context.MODE_PRIVATE)
        val selectedFrequency = sharedPreferences?.getString("selected_frequency", "")
        val selectedDay = sharedPreferences?.getString("selected_day", "")
        val selectedTime = sharedPreferences.getString("selected_time", "")

        // If we don't have a preferred time, set default time
        if (selectedFrequency != null && selectedDay != null && selectedTime != null) {
            if (selectedFrequency.isEmpty() || selectedDay.isEmpty() || selectedTime.isEmpty()) {

                Log.d(TAG, "Preferred notification schedule does not exist, setting default alarm schedule")

                val defaultHour = 19
                val defaultMinute = 0
                val defaultFrequency = AlarmManager.INTERVAL_DAY * 7
                val defaultDay = Calendar.MONDAY

                val calendar = Calendar.getInstance()
                calendar.set(Calendar.DAY_OF_WEEK, defaultDay)
                calendar.set(Calendar.HOUR_OF_DAY, defaultHour)
                calendar.set(Calendar.MINUTE, defaultMinute)
                calendar.set(Calendar.SECOND, 0)
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis, // Time at which the we trigger the notification
                    defaultFrequency, // Interval between notifications
                    pendingIntent
                )
                return
            }
        }

        Log.d(TAG, "Preferred notification schedule exists, parsing and setting to alarm")

        // Attempt to parse selected time
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

        Log.d(TAG, "Selected Frequency: $selectedFrequency")
        Log.d(TAG, "Selected Day: $selectedDay")

        // Calculate interval of notifications based on selected frequency
        var intervalMillis: Long = 0
        when (selectedFrequency) {
            "Weekly" -> intervalMillis = (AlarmManager.INTERVAL_DAY * 7)
            "Biweekly" -> intervalMillis = (AlarmManager.INTERVAL_DAY * 14)
//            "Monthly" -> intervalMillis = AlarmManager.INTERVAL_DAY
        }

        // Set the time at which we trigger the notification into a Calendar instance
        val calendar = Calendar.getInstance()

        // This block should theoretically set the notification to go off on the next occurrence of
        // the selected day if the day has already passed.
        // It appears to be working, though testing hasn't been thorough.
        val today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        val dayOfWeekMap = mapOf(
            "Monday" to Calendar.MONDAY,
            "Tuesday" to Calendar.TUESDAY,
            "Wednesday" to Calendar.WEDNESDAY,
            "Thursday" to Calendar.THURSDAY,
            "Friday" to Calendar.FRIDAY,
            "Saturday" to Calendar.SATURDAY,
            "Sunday" to Calendar.SUNDAY
        )

        val selectedDayOfWeek = dayOfWeekMap[selectedDay]

        if (selectedDayOfWeek != null) {
            if (today > selectedDayOfWeek) {
                calendar.add(Calendar.WEEK_OF_YEAR, 1)
            }
            calendar.set(Calendar.DAY_OF_WEEK, selectedDayOfWeek)
        }

        // This block of code sets repeating notifications at the day, time, and frequency of
        // coinsurance saved into the users ReminderPrefs shared preferences

        calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
        calendar.set(Calendar.MINUTE, selectedMinute)
        calendar.set(Calendar.SECOND, 0)
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis, // Time at which the we trigger the notification
            intervalMillis, // Interval between notifications
            pendingIntent
        )
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