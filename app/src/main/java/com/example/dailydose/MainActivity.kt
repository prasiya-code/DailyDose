package com.example.dailydose

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.dailydose.data.SharedPreferencesHelper
import com.example.dailydose.databinding.ActivityMainBinding
import com.example.dailydose.fragments.HabitTrackerFragment
import com.example.dailydose.fragments.HistoryFragment
import com.example.dailydose.fragments.MoodJournalFragment
import com.example.dailydose.fragments.SettingsFragment
import com.example.dailydose.utils.NotificationHelper

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var notificationHelper: NotificationHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferencesHelper = SharedPreferencesHelper(this)
        notificationHelper = NotificationHelper(this)

        // Schedule hydration reminders on app start
        scheduleHydrationReminders()

        // Default fragment
        replaceFragment(HabitTrackerFragment())

        // Bottom navigation
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_habits -> replaceFragment(HabitTrackerFragment())
                R.id.nav_mood -> replaceFragment(MoodJournalFragment())
                R.id.nav_history -> replaceFragment(HistoryFragment())
                R.id.nav_settings -> replaceFragment(SettingsFragment())
            }
            true
        }
    }

    fun getSharedPreferencesHelper(): SharedPreferencesHelper = sharedPreferencesHelper

    fun scheduleHydrationReminders() {
        val settings = sharedPreferencesHelper.getSettings()
        if (settings.hydrationReminderEnabled) {
            notificationHelper.scheduleHydrationReminders(
                settings.hydrationReminderInterval,
                settings.reminderTime
            )
        } else {
            notificationHelper.cancelHydrationReminders()
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
