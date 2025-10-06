package com.example.dailydose.models

import com.google.gson.annotations.SerializedName
import java.util.UUID

// Enum class for Habit categories with emojis
enum class HabitCategory(val emoji: String) {
    HEALTH("💪"),
    FITNESS("🏋️‍♂️"),
    STUDY("📚"),
    WORK("💼"),
    MIND("🧘‍♂️"),
    HOBBY("🎨"),
    OTHER("✨")
}

// Habit data class
data class Habit(
    @SerializedName("id")
    val id: String = UUID.randomUUID().toString(),  // unique id

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String = "",

    @SerializedName("category")
    val category: HabitCategory = HabitCategory.OTHER,  // default category

    @SerializedName("target")
    val target: Int = 1,  // target completions per day/week

    @SerializedName("streak")
    val streak: Int = 0  // current streak count
)
