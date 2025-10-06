package com.example.dailydose.fragments

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.dailydose.MainActivity
import com.example.dailydose.R
import com.example.dailydose.data.SharedPreferencesHelper
import com.example.dailydose.models.Settings
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SettingsFragment : Fragment() {

    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var hydrationReminderSwitch: SwitchMaterial
    private lateinit var notificationsSwitch: SwitchMaterial
    private lateinit var waterIntakeInput: TextInputEditText
    private lateinit var reminderIntervalInput: TextInputEditText
    private lateinit var reminderTimeInput: TextInputEditText

    private val calendar = Calendar.getInstance()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferencesHelper = (activity as MainActivity).getSharedPreferencesHelper()

        initializeViews(view)
        loadSettings()
        setupListeners()
    }

    private fun initializeViews(view: View) {
        hydrationReminderSwitch = view.findViewById(R.id.hydration_reminder_switch)
        notificationsSwitch = view.findViewById(R.id.notifications_switch)
        waterIntakeInput = view.findViewById(R.id.water_intake_input)
        reminderIntervalInput = view.findViewById(R.id.reminder_interval_input)
        reminderTimeInput = view.findViewById(R.id.reminder_time_input)
    }

    private fun loadSettings() {
        val settings = sharedPreferencesHelper.getSettings()
        hydrationReminderSwitch.isChecked = settings.hydrationReminderEnabled
        notificationsSwitch.isChecked = settings.notificationsEnabled
        waterIntakeInput.setText(settings.dailyGoalWaterIntake.toString())
        reminderIntervalInput.setText(settings.hydrationReminderInterval.toString())
        updateReminderTimeText(settings.reminderTime)
    }

    private fun setupListeners() {
        hydrationReminderSwitch.setOnCheckedChangeListener { _, _ -> saveSettings() }
        notificationsSwitch.setOnCheckedChangeListener { _, _ -> saveSettings() }

        waterIntakeInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) validateAndSaveWaterIntake()
        }

        reminderIntervalInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) validateAndSaveReminderInterval()
        }

        reminderTimeInput.setOnClickListener {
            showTimePickerDialog()
        }
    }

    private fun showTimePickerDialog() {
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            val newTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)
            updateReminderTimeText(newTime)
            saveSettings()
        }

        val currentTime = reminderTimeInput.text.toString()
        val parts = currentTime.split(":")
        val hour = if (parts.size == 2) parts[0].toIntOrNull() ?: calendar.get(Calendar.HOUR_OF_DAY) else calendar.get(Calendar.HOUR_OF_DAY)
        val minute = if (parts.size == 2) parts[1].toIntOrNull() ?: calendar.get(Calendar.MINUTE) else calendar.get(Calendar.MINUTE)

        TimePickerDialog(requireContext(), timeSetListener, hour, minute, true).show()
    }

    private fun updateReminderTimeText(time: String) {
        reminderTimeInput.setText(time)
        val parts = time.split(":")
        if (parts.size == 2) {
            calendar.set(Calendar.HOUR_OF_DAY, parts[0].toInt())
            calendar.set(Calendar.MINUTE, parts[1].toInt())
        }
    }

    private fun validateAndSaveWaterIntake() {
        val text = waterIntakeInput.text.toString()
        if (text.isEmpty() || text.toIntOrNull() == null) {
            waterIntakeInput.setText(sharedPreferencesHelper.getSettings().dailyGoalWaterIntake.toString())
            Toast.makeText(context, "Invalid water intake goal", Toast.LENGTH_SHORT).show()
        } else {
            saveSettings()
        }
    }

    private fun validateAndSaveReminderInterval() {
        val text = reminderIntervalInput.text.toString()
        if (text.isEmpty() || text.toIntOrNull() == null) {
            reminderIntervalInput.setText(sharedPreferencesHelper.getSettings().hydrationReminderInterval.toString())
            Toast.makeText(context, "Invalid reminder interval", Toast.LENGTH_SHORT).show()
        } else {
            saveSettings()
        }
    }

    private fun saveSettings() {
        val currentSettings = sharedPreferencesHelper.getSettings()

        val newSettings = currentSettings.copy(
            hydrationReminderEnabled = hydrationReminderSwitch.isChecked,
            notificationsEnabled = notificationsSwitch.isChecked,
            dailyGoalWaterIntake = waterIntakeInput.text.toString().toIntOrNull() ?: currentSettings.dailyGoalWaterIntake,
            hydrationReminderInterval = reminderIntervalInput.text.toString().toIntOrNull() ?: currentSettings.hydrationReminderInterval,
            reminderTime = reminderTimeInput.text.toString().ifEmpty { currentSettings.reminderTime }
        )

        sharedPreferencesHelper.saveSettings(newSettings)

        // Reschedule hydration reminders with updated settings
        (activity as MainActivity).scheduleHydrationReminders()

        Toast.makeText(context, "Settings saved", Toast.LENGTH_SHORT).show()
    }
}
