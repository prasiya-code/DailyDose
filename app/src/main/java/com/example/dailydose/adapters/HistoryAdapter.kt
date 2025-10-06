package com.example.dailydose.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dailydose.R
import com.example.dailydose.models.HistoryDay
import com.example.dailydose.models.HistoryHabit
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.*

class HistoryAdapter(
    private var historyData: List<HistoryDay>,
    private val onHabitClick: (HistoryHabit, String) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    private val dateFormat = SimpleDateFormat("EEEE, MMM dd", Locale.getDefault())

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: MaterialCardView = itemView.findViewById(R.id.history_day_card)
        val dateTextView: TextView = itemView.findViewById(R.id.history_date)
        val progressTextView: TextView = itemView.findViewById(R.id.history_progress)
        val habitsRecyclerView: RecyclerView = itemView.findViewById(R.id.history_habits_recycler)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history_day, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val historyDay = historyData[position]

        // Set date
        holder.dateTextView.text = historyDay.date

        // Set progress
        val progressText = "${historyDay.completedHabits}/${historyDay.totalHabits} habits completed"
        holder.progressTextView.text = progressText

        // Setup habits RecyclerView
        val habitsAdapter = HistoryHabitsAdapter(
            habits = historyDay.habits,
            onHabitClick = { habit -> onHabitClick(habit, historyDay.date) }
        )

        holder.habitsRecyclerView.apply {
            layoutManager = LinearLayoutManager(holder.itemView.context)
            adapter = habitsAdapter
        }

        // Set card background based on completion
        val completionPercentage = historyDay.progressPercentage / 100f

        val backgroundRes = when {
            completionPercentage >= 1.0f -> R.drawable.history_complete_gradient
            completionPercentage >= 0.5f -> R.drawable.history_partial_gradient
            else -> R.drawable.history_incomplete_gradient
        }
        holder.cardView.setBackgroundResource(backgroundRes)
    }

    override fun getItemCount(): Int = historyData.size

    fun updateHistory(newHistoryData: List<HistoryDay>) {
        historyData = newHistoryData
        notifyDataSetChanged()
    }
}

class HistoryHabitsAdapter(
    private val habits: List<HistoryHabit>,
    private val onHabitClick: (HistoryHabit) -> Unit
) : RecyclerView.Adapter<HistoryHabitsAdapter.HistoryHabitViewHolder>() {

    class HistoryHabitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: MaterialCardView = itemView.findViewById(R.id.history_habit_card)
        val nameTextView: TextView = itemView.findViewById(R.id.history_habit_name)
        val emojiTextView: TextView = itemView.findViewById(R.id.history_habit_emoji)
        val statusTextView: TextView = itemView.findViewById(R.id.history_habit_status)
        val timeTextView: TextView = itemView.findViewById(R.id.history_habit_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryHabitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history_habit, parent, false)
        return HistoryHabitViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryHabitViewHolder, position: Int) {
        val historyHabit = habits[position]

        holder.nameTextView.text = historyHabit.name
        holder.emojiTextView.text = historyHabit.emoji

        if (historyHabit.isCompleted) {
            holder.statusTextView.text = "Completed"
            holder.statusTextView.setTextColor(holder.itemView.context.getColor(R.color.success_color))
            holder.timeTextView.text = historyHabit.completedAt
            holder.timeTextView.visibility = View.VISIBLE
        } else {
            holder.statusTextView.text = historyHabit.completedAt
            holder.statusTextView.setTextColor(holder.itemView.context.getColor(R.color.text_secondary))
            holder.timeTextView.visibility = View.GONE
        }

        // Set card background based on completion
        val backgroundRes = if (historyHabit.isCompleted) {
            R.drawable.history_habit_completed
        } else {
            R.drawable.history_habit_incomplete
        }
        holder.cardView.setBackgroundResource(backgroundRes)

        holder.cardView.setOnClickListener { onHabitClick(historyHabit) }
    }

    override fun getItemCount(): Int = habits.size
}
