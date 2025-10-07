package com.example.dailydose.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.dailydose.data.SharedPreferencesHelper
import com.example.dailydose.utils.NotificationHelper

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val sharedPreferencesHelper = SharedPreferencesHelper(context)
            val notificationHelper = NotificationHelper(context)

            // Reschedule hydration reminders
            val settings = sharedPreferencesHelper.getSettings()
            if (settings.hydrationReminderEnabled) {
                notificationHelper.scheduleHydrationReminders(
                    settings.hydrationReminderInterval,
                    settings.reminderTime
                )
            }

            // Reschedule mood reminders
            if (settings.moodReminderEnabled) {
                notificationHelper.scheduleMoodReminders(settings.moodReminderTime)
            }
        }
    }
}
