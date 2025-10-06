package com.example.dailydose.data

import android.content.Context
import android.content.SharedPreferences
import com.example.dailydose.models.Habit
import com.example.dailydose.models.HabitCompletion
import com.example.dailydose.models.MoodLog
import com.example.dailydose.models.Settings
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class SharedPreferencesHelper(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("DailyDosePrefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    // ------------------- HABIT MANAGEMENT -------------------

    fun getHabits(): List<Habit> {
        val json = prefs.getString("habits_list", null)
        return if (!json.isNullOrEmpty()) {
            val type = object : TypeToken<List<Habit>>() {}.type
            gson.fromJson(json, type)
        } else emptyList()
    }

    fun saveHabits(habits: List<Habit>) {
        val json = gson.toJson(habits)
        prefs.edit().putString("habits_list", json).apply()
    }

    fun addHabit(habit: Habit) {
        val habits = getHabits().toMutableList()
        habits.add(habit)
        saveHabits(habits)
    }

    fun deleteHabit(habitId: String) {
        val habits = getHabits().filter { it.id != habitId }
        saveHabits(habits)
    }

    fun deleteHabit(habit: Habit) {
        deleteHabit(habit.id)
    }

    fun updateHabit(updatedHabit: Habit) {
        val habits = getHabits().toMutableList()
        val index = habits.indexOfFirst { it.id == updatedHabit.id }
        if (index != -1) {
            habits[index] = updatedHabit
            saveHabits(habits)
        }
    }

    // ------------------- HABIT COMPLETIONS -------------------

    private fun getDateKey(date: Date): String {
        return SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(date)
    }

    fun getHabitCompletionsForDate(date: Date): List<HabitCompletion> {
        val key = "completions_${getDateKey(date)}"
        val json = prefs.getString(key, null)
        return if (!json.isNullOrEmpty()) {
            val type = object : TypeToken<List<HabitCompletion>>() {}.type
            gson.fromJson(json, type)
        } else emptyList()
    }

    fun saveHabitCompletionsForDate(date: Date, completions: List<HabitCompletion>) {
        val key = "completions_${getDateKey(date)}"
        val json = gson.toJson(completions)
        prefs.edit().putString(key, json).apply()
    }

    fun addHabitCompletion(habitCompletion: HabitCompletion) {
        val completions = getHabitCompletionsForDate(habitCompletion.date).toMutableList()
        completions.removeAll { it.habitId == habitCompletion.habitId } // replace if exists
        completions.add(habitCompletion)
        saveHabitCompletionsForDate(habitCompletion.date, completions)
    }

    fun getHabitCompletion(habitId: String, dateString: String): HabitCompletion? {
        // Parse the date string to Date object
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val date = try {
            dateFormat.parse(dateString) ?: Date()
        } catch (e: Exception) {
            Date()
        }
        
        val completions = getHabitCompletionsForDate(date)
        return completions.find { it.habitId == habitId }
    }

    fun saveHabitCompletion(habitCompletion: HabitCompletion) {
        addHabitCompletion(habitCompletion)
    }

    // ------------------- MOOD LOGS -------------------

    fun getMoodLogs(): List<MoodLog> {
        val json = prefs.getString("mood_logs", null)
        return if (!json.isNullOrEmpty()) {
            val type = object : TypeToken<List<MoodLog>>() {}.type
            gson.fromJson(json, type)
        } else emptyList()
    }

    fun saveMoodLogs(moodLogs: List<MoodLog>) {
        val json = gson.toJson(moodLogs)
        prefs.edit().putString("mood_logs", json).apply()
    }

    fun addMoodLog(moodLog: MoodLog) {
        val logs = getMoodLogs().toMutableList()
        logs.add(moodLog)
        saveMoodLogs(logs)
    }

    fun deleteMoodLog(moodLog: MoodLog) {
        val logs = getMoodLogs().filter { it.id != moodLog.id }
        saveMoodLogs(logs)
    }

    fun updateMoodLog(updatedLog: MoodLog) {
        val logs = getMoodLogs().toMutableList()
        val index = logs.indexOfFirst { it.id == updatedLog.id }
        if (index != -1) {
            logs[index] = updatedLog
            saveMoodLogs(logs)
        }
    }

    // ------------------- SETTINGS -------------------

    fun getSettings(): Settings {
        val json = prefs.getString("app_settings", null)
        return if (!json.isNullOrEmpty()) {
            gson.fromJson(json, Settings::class.java)
        } else {
            Settings() // return default if none saved
        }
    }

    fun saveSettings(settings: Settings) {
        val json = gson.toJson(settings)
        prefs.edit().putString("app_settings", json).apply()
    }
}
