package com.example.dailydose.adapters
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dailydose.databinding.ItemHabitBinding
import com.example.dailydose.models.Habit
import com.example.dailydose.data.SharedPreferencesHelper
import com.example.dailydose.utils.DateUtils

class HabitAdapter(
    private var habitList: MutableList<Habit>,
    private val sharedPreferencesHelper: SharedPreferencesHelper,
    private val onEditClick: (Habit) -> Unit,
    private val onDeleteClick: (Habit) -> Unit,
    private val onCompleteClick: (Habit) -> Unit
) : RecyclerView.Adapter<HabitAdapter.HabitViewHolder>() {

    inner class HabitViewHolder(val binding: ItemHabitBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val binding = ItemHabitBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HabitViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habit = habitList[position]
        val currentDateString = DateUtils.getCurrentDateString()
        
        // Get today's completion for this habit
        val todayCompletion = sharedPreferencesHelper.getHabitCompletion(habit.id, currentDateString)
        val completedCount = todayCompletion?.completedCount ?: 0
        val progressPercentage = ((completedCount.toFloat() / habit.target) * 100).toInt().coerceAtMost(100)

        // Bind data to views
        holder.binding.textHabitName.text = habit.name
        holder.binding.textHabitDesc.text = if (habit.description.isNotEmpty()) habit.description else "No description"
        holder.binding.textHabitTarget.text = "Target: ${habit.target}/day"
        holder.binding.textHabitCategory.text = "${habit.category.emoji} ${habit.category.name}"
        holder.binding.textHabitProgress.text = "Progress: $completedCount/${habit.target}"
        holder.binding.progressBar.progress = progressPercentage

        // Update complete button state
        if (completedCount >= habit.target) {
            holder.binding.buttonComplete.text = "COMPLETED ✓"
            holder.binding.buttonComplete.isEnabled = false
            holder.binding.buttonComplete.alpha = 0.6f
        } else {
            holder.binding.buttonComplete.text = "COMPLETE"
            holder.binding.buttonComplete.isEnabled = true
            holder.binding.buttonComplete.alpha = 1.0f
        }

        // Click listeners
        holder.itemView.setOnClickListener {
            onEditClick(habit)
        }

        holder.binding.buttonDelete.setOnClickListener {
            onDeleteClick(habit)
        }

        holder.binding.buttonComplete.setOnClickListener {
            if (completedCount < habit.target) {
                onCompleteClick(habit)
            }
        }
    }

    override fun getItemCount(): Int = habitList.size

    fun updateHabits(newHabits: List<Habit>) {
        habitList.clear()
        habitList.addAll(newHabits)
        notifyDataSetChanged()
    }
}
