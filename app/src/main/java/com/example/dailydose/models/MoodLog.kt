package com.example.dailydose.models

import com.google.gson.annotations.SerializedName
import java.util.Date

data class MoodLog(
    @SerializedName("id")
    val id: String,

    @SerializedName("date")
    val date: Date,

    @SerializedName("mood")
    val mood: MoodType,

    @SerializedName("emoji")
    val emoji: String,

    @SerializedName("note")
    val note: String = "",

    @SerializedName("energyLevel")
    val energyLevel: Int = 5, // 1-10 scale

    @SerializedName("stressLevel")
    val stressLevel: Int = 5, // 1-10 scale

    @SerializedName("activities")
    val activities: List<String> = emptyList()
)

enum class MoodType {
    VERY_HAPPY,
    HAPPY,
    NEUTRAL,
    SAD,
    VERY_SAD,
    ANXIOUS,
    EXCITED,
    TIRED,
    FRUSTRATED,
    PEACEFUL
}
