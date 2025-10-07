package com.example.dailydose.models

import com.google.gson.annotations.SerializedName

data class Settings(
    @SerializedName("hydrationReminderEnabled")
    val hydrationReminderEnabled: Boolean = true,
    
    @SerializedName("hydrationReminderInterval")
    val hydrationReminderInterval: Int = 2, // hours
    
    @SerializedName("dailyGoalWaterIntake")
    val dailyGoalWaterIntake: Int = 8, // glasses
    
    @SerializedName("currentWaterIntake")
    val currentWaterIntake: Int = 0,
    
    @SerializedName("theme")
    val theme: String = "light", // light, dark, auto
    
    @SerializedName("notificationsEnabled")
    val notificationsEnabled: Boolean = true,
    
    @SerializedName("reminderTime")
    val reminderTime: String = "09:00", // 24-hour format
    
    @SerializedName("weekStartDay")
    val weekStartDay: Int = 1, // 1 = Monday, 0 = Sunday
    
    @SerializedName("moodReminderEnabled")
    val moodReminderEnabled: Boolean = true,
    
    @SerializedName("moodReminderTime")
    val moodReminderTime: String = "20:00", // 24-hour format, evening reminder
)
