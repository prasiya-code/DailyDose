package com.example.dailydose.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.example.dailydose.MainActivity
import com.example.dailydose.R
import com.example.dailydose.data.SharedPreferencesHelper
import com.example.dailydose.databinding.DialogAddMoodBinding
import com.example.dailydose.models.MoodLog
import com.example.dailydose.models.MoodType
import java.util.*

class AddMoodDialogFragment : DialogFragment() {

    private var _binding: DialogAddMoodBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    // Mood emojis mapped to MoodType
    private val moodEmojis = mapOf(
        MoodType.VERY_HAPPY to "😄",
        MoodType.HAPPY to "😊",
        MoodType.NEUTRAL to "😐",
        MoodType.SAD to "😢",
        MoodType.VERY_SAD to "😭",
        MoodType.ANXIOUS to "😰",
        MoodType.EXCITED to "🤩",
        MoodType.TIRED to "😴",
        MoodType.FRUSTRATED to "😤",
        MoodType.PEACEFUL to "😌"
    )

    private var energyLevel = 5
    private var stressLevel = 5

    // Callback interface
    interface OnMoodAddedListener {
        fun onMoodAdded()
    }
    var moodAddedListener: OnMoodAddedListener? = null

    // Optional mood to edit
    var moodToEdit: MoodLog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddMoodBinding.inflate(inflater, container, false)
        sharedPreferencesHelper = (activity as MainActivity).getSharedPreferencesHelper()
        
        // Make dialog background transparent
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        setupMoodSpinner()
        setupSeekBars()
        setupButtons()

        // Pre-fill if editing
        moodToEdit?.let {
            binding.moodSpinner.setSelection(it.mood.ordinal)
            binding.moodNoteInput.setText(it.note)
            energyLevel = it.energyLevel
            stressLevel = it.stressLevel
            binding.energySeekbar.progress = it.energyLevel
            binding.stressSeekbar.progress = it.stressLevel
            binding.energyText.text = "${it.energyLevel}/10"
            binding.stressText.text = "${it.stressLevel}/10"
        }

        return binding.root
    }

    private fun setupMoodSpinner() {
        val moodOptions = MoodType.values().map { moodType ->
            "${moodEmojis[moodType]} ${moodType.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }}"
        }

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, moodOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.moodSpinner.adapter = adapter
    }

    private fun setupSeekBars() {
        // Energy SeekBar
        binding.energySeekbar.progress = energyLevel
        binding.energyText.text = "$energyLevel/10"
        
        binding.energySeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                energyLevel = progress
                binding.energyText.text = "$energyLevel/10"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Stress SeekBar
        binding.stressSeekbar.progress = stressLevel
        binding.stressText.text = "$stressLevel/10"
        
        binding.stressSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                stressLevel = progress
                binding.stressText.text = "$stressLevel/10"
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun setupButtons() {
        binding.saveButton.setOnClickListener {
            saveMoodLog()
        }

        binding.cancelButton.setOnClickListener {
            dismiss()
        }
    }

    private fun saveMoodLog() {
        val selectedMoodType = MoodType.values()[binding.moodSpinner.selectedItemPosition]
        val note = binding.moodNoteInput.text.toString().trim()
        val emoji = moodEmojis[selectedMoodType] ?: "😊"

        if (moodToEdit == null) {
            // Add new mood log
            val moodLog = MoodLog(
                id = UUID.randomUUID().toString(),
                date = Date(),
                mood = selectedMoodType,
                emoji = emoji,
                note = note,
                energyLevel = energyLevel,
                stressLevel = stressLevel,
                activities = emptyList()
            )
            sharedPreferencesHelper.addMoodLog(moodLog)
            Toast.makeText(requireContext(), "Mood logged successfully!", Toast.LENGTH_SHORT).show()
        } else {
            // Update existing mood log
            val updatedMoodLog = moodToEdit!!.copy(
                mood = selectedMoodType,
                emoji = emoji,
                note = note,
                energyLevel = energyLevel,
                stressLevel = stressLevel
            )
            sharedPreferencesHelper.updateMoodLog(updatedMoodLog)
            Toast.makeText(requireContext(), "Mood updated successfully!", Toast.LENGTH_SHORT).show()
        }

        // Notify parent fragment
        moodAddedListener?.onMoodAdded()

        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
