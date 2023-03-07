package com.lincolnstewart.android.reachout.ui.setup.tabs

import android.app.TimePickerDialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.lincolnstewart.android.reachout.ReachOutNotificationService
import com.lincolnstewart.android.reachout.databinding.FragmentChildTwoBinding
import com.lincolnstewart.android.reachout.ui.alarm.AlarmItem
import com.lincolnstewart.android.reachout.ui.alarm.AndroidAlarmScheduler
import java.time.LocalDateTime
import java.util.*

private const val TAG = "ChildTwoFragment"

class ChildTwoFragment : Fragment() {

    //region Data members

    private var _binding: FragmentChildTwoBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ChildTwoViewModel

    //endregion

    //region Lifecycle Functions
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[ChildTwoViewModel::class.java]
        _binding = FragmentChildTwoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // For notification testing
        val service = ReachOutNotificationService(requireContext())
//        service.showNotification("Ree")
        service.scheduleNotifications(requireContext())

        initializeSpinners()

        binding.selectedTime.setOnClickListener {
            initializeTimePicker()
        }

        val shedular = AndroidAlarmScheduler(requireContext())
        binding.testButton.setOnClickListener {
            val alarmItem = AlarmItem(
                time = LocalDateTime.now()
                    .plusSeconds(5),
                message = "test"
            )
            alarmItem.let(shedular::schedule)
        }
    }

    // For testing purposes
    override fun onPause() {
        super.onPause()

        val sharedPreferences = context?.getSharedPreferences("ReminderPrefs", MODE_PRIVATE)

        val freqString = sharedPreferences?.getString("selected_frequency", "")
        val timeString = sharedPreferences?.getString("selected_time", "")
        val dayString = sharedPreferences?.getString("selected_day", "")
        Log.d(TAG, "Currently set in ReminderPrefs, Frequency: $freqString, Time: $timeString, Day: $dayString")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    //endregion

    private fun initializeSpinners() {
        val frequencySpinner = binding.frequencySpinner
        val daySpinner = binding.daySpinner

        frequencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedFrequency = parent.getItemAtPosition(position).toString()
                Log.d(TAG, "Selected Frequency: $selectedFrequency")

                // Save to shared prefs
                val sharedPrefs = context?.getSharedPreferences("ReminderPrefs", MODE_PRIVATE)
                val editor = sharedPrefs?.edit()
                editor?.putString("selected_frequency", selectedFrequency)
                editor?.apply()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        daySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedDay = parent.getItemAtPosition(position).toString()
                Log.d(TAG, "Selected Day: $selectedDay")

                // Save to shared prefs
                val sharedPrefs = context?.getSharedPreferences("ReminderPrefs", MODE_PRIVATE)
                val editor = sharedPrefs?.edit()
                editor?.putString("selected_day", selectedDay)
                editor?.apply()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun initializeTimePicker() {
        // Get the current time
        val currentTime = Calendar.getInstance()
        val hour = currentTime.get(Calendar.HOUR_OF_DAY)
        val minute = currentTime.get(Calendar.MINUTE)

        // Create a time picker dialog
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minuteOfHour ->
                // Create a new calendar instance and set to current time
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                selectedCalendar.set(Calendar.MINUTE, minuteOfHour)

//                // Get the AM/PM indicator
//                val amPm = if (selectedCalendar.get(Calendar.AM_PM) == Calendar.AM) "AM" else "PM"
//
//                // Update the selected time text view with the selected time and AM/PM indicator
//                binding.selectedTime.text = "$hourOfDay:$minuteOfHour $amPm"
//
//                Log.d(TAG, "Selected Time: $hourOfDay:$minuteOfHour $amPm")

                // Format time to a Simple Date Format
                val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
                val formattedTime = sdf.format(selectedCalendar.time)
                Log.d(TAG, "Formatted/Selected Time: $formattedTime")

                // Update UI
                binding.selectedTime.text = formattedTime

                // Save to shared prefs
                val sharedPrefs = context?.getSharedPreferences("ReminderPrefs", MODE_PRIVATE)
                val editor = sharedPrefs?.edit()
                editor?.putString("selected_time", formattedTime)
                editor?.apply()
            },
            hour,
            minute,
            false
        )
        // Show the time picker dialog
        timePickerDialog.show()
    }
}