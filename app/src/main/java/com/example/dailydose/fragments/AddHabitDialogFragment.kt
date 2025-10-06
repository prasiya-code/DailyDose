package com.example.dailydose.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.dailydose.data.SharedPreferencesHelper
import com.example.dailydose.databinding.DialogAddHabitBinding
import com.example.dailydose.models.Habit
import com.example.dailydose.models.HabitCategory
import java.util.*

class AddHabitDialogFragment : DialogFragment() {

    private var _binding: DialogAddHabitBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    // Callback interface
    interface OnHabitAddedListener {
        fun onHabitAdded()
    }
    var habitAddedListener: OnHabitAddedListener? = null

    // Optional habit to edit
    var habitToEdit: Habit? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddHabitBinding.inflate(inflater, container, false)
        sharedPreferencesHelper = (activity as com.example.dailydose.MainActivity).getSharedPreferencesHelper()
        
        // Make dialog background transparent
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        setupCategorySpinner()

        // Pre-fill if editing
        habitToEdit?.let {
            binding.inputHabitName.setText(it.name)
            binding.inputHabitDescription.setText(it.description)
            binding.inputHabitTarget.setText(it.target.toString())
            binding.spinnerHabitCategory.setSelection(it.category.ordinal)
        }

        binding.buttonSave.setOnClickListener {
            saveHabit()
        }

        binding.buttonCancel.setOnClickListener {
            dismiss()
        }

        return binding.root
    }

    private fun setupCategorySpinner() {
        val categories = HabitCategory.values().map { "${it.emoji} ${it.name}" }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerHabitCategory.adapter = adapter
    }

    private fun saveHabit() {
        val name = binding.inputHabitName.text.toString().trim()
        val description = binding.inputHabitDescription.text.toString().trim()
        val targetText = binding.inputHabitTarget.text.toString().trim()

        // Validate habit name
        if (name.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter a habit name", Toast.LENGTH_SHORT).show()
            return
        }

        // Validate target
        val target = targetText.toIntOrNull()
        if (targetText.isNotEmpty() && (target == null || target <= 0)) {
            Toast.makeText(requireContext(), "Please enter a valid target (positive number)", Toast.LENGTH_SHORT).show()
            return
        }

        val finalTarget = target ?: 1
        val category = HabitCategory.values()[binding.spinnerHabitCategory.selectedItemPosition]

        // Check for duplicate habit name (only for new habits)
        if (habitToEdit == null) {
            val existingHabits = sharedPreferencesHelper.getHabits()
            val duplicateHabit = existingHabits.find { it.name.equals(name, ignoreCase = true) }
            if (duplicateHabit != null) {
                Toast.makeText(requireContext(), "Habit with this name already exists!", Toast.LENGTH_SHORT).show()
                return
            }
        }

        try {
            if (habitToEdit == null) {
                // Add new habit
                val newHabit = Habit(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    description = description,
                    target = finalTarget,
                    category = category,
                    streak = 0
                )
                sharedPreferencesHelper.addHabit(newHabit)
                Toast.makeText(requireContext(), "Habit added successfully!", Toast.LENGTH_SHORT).show()
            } else {
                // Update existing habit
                val updatedHabit = habitToEdit!!.copy(
                    name = name,
                    description = description,
                    target = finalTarget,
                    category = category
                )
                sharedPreferencesHelper.updateHabit(updatedHabit)
                Toast.makeText(requireContext(), "Habit updated successfully!", Toast.LENGTH_SHORT).show()
            }

            // Notify parent fragment BEFORE dismissing
            habitAddedListener?.onHabitAdded()

            // Small delay to ensure callback is processed
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                dismiss()
            }, 100)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Failed to save habit. Please try again.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
