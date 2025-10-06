package com.example.dailydose.models

import java.util.Date

data class HistoryDay(
    val date: String,
    val completedHabits: Int,
    val totalHabits: Int,
    val progressPercentage: Int,
    val habits: List<HistoryHabit>
)

data class HistoryHabit(
    val name: String,
    val emoji: String,
    val isCompleted: Boolean,
    val completedAt: String
)
