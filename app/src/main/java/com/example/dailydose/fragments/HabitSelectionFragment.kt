package com.example.dailydose.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.gridlayout.widget.GridLayout
import com.example.dailydose.R
import com.example.dailydose.data.SharedPreferencesHelper
import com.example.dailydose.databinding.FragmentHabitSelectionBinding
import com.example.dailydose.models.Habit

class HabitSelectionFragment : Fragment() {

    private var _binding: FragmentHabitSelectionBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var habitGrid: GridLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHabitSelectionBinding.inflate(inflater, container, false)
        sharedPreferencesHelper =
            (activity as com.example.dailydose.MainActivity).getSharedPreferencesHelper()

        habitGrid = binding.habitGrid

        // Load existing habits dynamically
        refreshHabitGrid()

        // Optional: click listener for creating custom habit
        binding.createCustomButton.setOnClickListener {
            // You can trigger your AddHabitDialogFragment here
            val dialog = com.example.dailydose.fragments.AddHabitDialogFragment()
            dialog.show(parentFragmentManager, "AddHabitDialog")
        }

        return binding.root
    }

    // Function to dynamically add habit cards to GridLayout
    private fun refreshHabitGrid() {
        habitGrid.removeAllViews() // Clear existing views

        val habits = sharedPreferencesHelper.getHabits()

        for (habit in habits) {
            val habitView = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_habit_card, habitGrid, false)

            val habitNameTextView = habitView.findViewById<TextView>(R.id.habit_name)
            habitNameTextView.text = habit.name

            // You can set other habit info, colors, click listeners here

            habitGrid.addView(habitView)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
