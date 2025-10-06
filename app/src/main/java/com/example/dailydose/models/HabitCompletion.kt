package com.example.dailydose.models

import com.google.gson.annotations.SerializedName
import java.util.*

data class HabitCompletion(
    @SerializedName("habitId")
    val habitId: String,

    @SerializedName("date")
    val date: Date,

    @SerializedName("completedCount")
    val completedCount: Int = 1,

    @SerializedName("notes")
    val notes: String = ""
)
