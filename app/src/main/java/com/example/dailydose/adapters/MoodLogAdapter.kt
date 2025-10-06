package com.example.dailydose.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dailydose.R
import com.example.dailydose.models.MoodLog
import com.example.dailydose.models.MoodType
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import java.text.SimpleDateFormat
import java.util.*

class MoodLogAdapter(
    private var moodLogs: List<MoodLog>,
    private val onMoodLogClick: (MoodLog) -> Unit,
    private val onMoodLogEdit: (MoodLog) -> Unit,
    private val onMoodLogDelete: (MoodLog) -> Unit
) : RecyclerView.Adapter<MoodLogAdapter.MoodLogViewHolder>() {

    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    class MoodLogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: MaterialCardView = itemView.findViewById(R.id.mood_log_card)
        val emojiContainer: MaterialCardView = itemView.findViewById(R.id.mood_emoji_container)
        val emojiTextView: TextView = itemView.findViewById(R.id.mood_emoji)
        val moodTextView: TextView = itemView.findViewById(R.id.mood_type)
        val dateTextView: TextView = itemView.findViewById(R.id.mood_date)
        val timeChip: Chip = itemView.findViewById(R.id.mood_time_chip)
        val noteTextView: TextView = itemView.findViewById(R.id.mood_note)
        val energyLevelTextView: TextView = itemView.findViewById(R.id.energy_level)
        val stressLevelTextView: TextView = itemView.findViewById(R.id.stress_level)
        val editButton: View = itemView.findViewById(R.id.edit_button)
        val deleteButton: View = itemView.findViewById(R.id.delete_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodLogViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mood_log, parent, false)
        return MoodLogViewHolder(view)
    }

    override fun onBindViewHolder(holder: MoodLogViewHolder, position: Int) {
        val moodLog = moodLogs[position]

        holder.emojiTextView.text = moodLog.emoji
        holder.moodTextView.text = moodLog.mood.name.replace("_", " ").lowercase()
            .replaceFirstChar { it.uppercase() }
        holder.dateTextView.text = dateFormat.format(moodLog.date)
        holder.timeChip.text = timeFormat.format(moodLog.date)

        // Handle note visibility
        if (moodLog.note.isNotEmpty()) {
            holder.noteTextView.text = moodLog.note
            holder.noteTextView.visibility = View.VISIBLE
        } else {
            holder.noteTextView.visibility = View.GONE
        }

        holder.energyLevelTextView.text = "${moodLog.energyLevel}/10"
        holder.stressLevelTextView.text = "${moodLog.stressLevel}/10"

        // Set mood color for the emoji container background
        val moodColor = when (moodLog.mood) {
            MoodType.VERY_HAPPY -> R.color.mood_very_happy
            MoodType.HAPPY -> R.color.mood_happy
            MoodType.NEUTRAL -> R.color.mood_neutral
            MoodType.SAD -> R.color.mood_sad
            MoodType.VERY_SAD -> R.color.mood_very_sad
            MoodType.ANXIOUS -> R.color.mood_anxious
            MoodType.EXCITED -> R.color.mood_excited
            MoodType.TIRED -> R.color.mood_tired
            MoodType.FRUSTRATED -> R.color.mood_frustrated
            MoodType.PEACEFUL -> R.color.mood_peaceful
        }
        holder.emojiContainer.setCardBackgroundColor(holder.itemView.context.getColor(moodColor))

        holder.cardView.setOnClickListener { onMoodLogClick(moodLog) }
        holder.editButton.setOnClickListener { onMoodLogEdit(moodLog) }
        holder.deleteButton.setOnClickListener { onMoodLogDelete(moodLog) }
    }

    override fun getItemCount(): Int = moodLogs.size

    fun updateMoodLogs(newMoodLogs: List<MoodLog>) {
        moodLogs = newMoodLogs
        notifyDataSetChanged()
    }
}
