package com.example.dailydose.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dailydose.MainActivity
import com.example.dailydose.R
import com.example.dailydose.adapters.HistoryAdapter
import com.example.dailydose.data.SharedPreferencesHelper
import com.example.dailydose.models.Habit
import com.example.dailydose.models.HabitCompletion
import com.example.dailydose.models.HistoryDay
import com.example.dailydose.models.HistoryHabit
import com.example.dailydose.utils.DateUtils

class HistoryFragment : Fragment() {

    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var emptyState: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get SharedPreferences helper from MainActivity
        sharedPreferencesHelper = (activity as MainActivity).getSharedPreferencesHelper()

        // Initialize views
        historyRecyclerView = view.findViewById(R.id.history_recycler_view)
        emptyState = view.findViewById(R.id.empty_state)

        // Setup RecyclerView
        setupRecyclerView()

        // Load history data
        loadHistoryData()
    }

    private fun setupRecyclerView() {
        historyAdapter = HistoryAdapter(
            historyData = emptyList(),
            onHabitClick = { habit, date ->
                // Handle habit click - show details or edit if needed
            }
        )

        historyRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyAdapter
        }
    }

    private fun loadHistoryData() {
        val historyDays = mutableListOf<HistoryDay>()
        val habits = sharedPreferencesHelper.getHabits()

        if (habits.isEmpty()) {
            emptyState.visibility = View.VISIBLE
            historyRecyclerView.visibility = View.GONE
            return
        }

        // Get last 7 days of data (excluding today - start from day 1)
        for (i in 1 until 8) {
            val dateString = DateUtils.getDateDaysAgo(i)
            val dateFormat = java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.getDefault())
            val date = dateFormat.parse(dateString) ?: java.util.Date()
            val dayName = DateUtils.getDayOfWeek(date)
            val monthDay = DateUtils.getDayOfMonth(date)

            val historyHabits = mutableListOf<HistoryHabit>()
            var completedCount = 0

            // Only process habits that have completion data for this specific date
            for (habit in habits) {
                val completion = sharedPreferencesHelper.getHabitCompletion(habit.id, dateString)

                // Skip habits that have no completion data for this date
                if (completion == null) continue

                val isCompleted = completion.completedCount >= habit.target
                val completedAmount = completion.completedCount

                historyHabits.add(
                    HistoryHabit(
                        name = habit.name,
                        emoji = habit.category.emoji,
                        isCompleted = isCompleted,
                        completedAt = if (isCompleted) "Completed" else "$completedAmount/${habit.target}"
                    )
                )

                if (isCompleted) completedCount++
            }

            // Only add days that have some habit data
            if (historyHabits.isNotEmpty()) {
                val progressPercentage = if (habits.isNotEmpty()) {
                    (completedCount * 100) / habits.size
                } else 0

                historyDays.add(
                    HistoryDay(
                        date = "$dayName, $monthDay",
                        completedHabits = completedCount,
                        totalHabits = habits.size,
                        progressPercentage = progressPercentage,
                        habits = historyHabits
                    )
                )
            }
        }

        if (historyDays.isEmpty()) {
            // No history data available
            emptyState.visibility = View.VISIBLE
            historyRecyclerView.visibility = View.GONE
        } else {
            // History data available
            historyAdapter.updateHistory(historyDays)
            emptyState.visibility = View.GONE
            historyRecyclerView.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        loadHistoryData()
    }
}
