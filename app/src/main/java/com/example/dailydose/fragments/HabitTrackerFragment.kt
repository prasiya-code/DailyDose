package com.example.dailydose.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dailydose.adapters.HabitAdapter
import com.example.dailydose.data.SharedPreferencesHelper
import com.example.dailydose.databinding.FragmentHabitTrackerBinding
import com.example.dailydose.models.Habit
import com.example.dailydose.models.HabitCompletion
import com.example.dailydose.utils.DateUtils
import java.text.SimpleDateFormat
import java.util.*

class HabitTrackerFragment : Fragment() {

    private var _binding: FragmentHabitTrackerBinding? = null
    private val binding get() = _binding!!

    private lateinit var habitAdapter: HabitAdapter
    private val habitList = mutableListOf<Habit>()
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHabitTrackerBinding.inflate(inflater, container, false)
        
        sharedPreferencesHelper = (activity as com.example.dailydose.MainActivity).getSharedPreferencesHelper()
        
        setupRecyclerView()
        setupCurrentDate()
        
        // Load saved habits
        refreshHabits()

        // FAB click opens dialog
        binding.addHabitFab.setOnClickListener {
            showAddHabitDialog()
        }

        return binding.root
    }

    private fun setupCurrentDate() {
        val currentDate = SimpleDateFormat("EEEE, MMMM dd", Locale.getDefault()).format(Date())
        binding.currentDateText.text = "Today, $currentDate"
        
        // Set greeting based on current time
        setupGreeting()
    }
    
    private fun setupGreeting() {
        val calendar = Calendar.getInstance()
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        
        val greeting = when (hourOfDay) {
            in 5..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            in 17..20 -> "Good Evening"
            else -> "Good Night"
        }
        
        binding.greetingText.text = greeting
    }

    private fun setupRecyclerView() {
        habitAdapter = HabitAdapter(
            habitList, 
            sharedPreferencesHelper,
            ::onEditHabit, 
            ::onDeleteHabit,
            ::onCompleteHabit
        )
        binding.habitRecyclerView.adapter = habitAdapter
        binding.habitRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun showAddHabitDialog(habitToEdit: Habit? = null) {
        val dialog = com.example.dailydose.fragments.AddHabitDialogFragment()
        // Set callback
        dialog.habitAddedListener = object : com.example.dailydose.fragments.AddHabitDialogFragment.OnHabitAddedListener {
            override fun onHabitAdded() {
                refreshHabits()
            }
        }

        // Pass habit to edit if any
        dialog.habitToEdit = habitToEdit

        dialog.show(parentFragmentManager, "AddHabitDialog")
    }

    private fun onEditHabit(habit: Habit) {
        showAddHabitDialog(habit)
    }

    private fun onDeleteHabit(habit: Habit) {
        sharedPreferencesHelper.deleteHabit(habit)
        refreshHabits()
        Toast.makeText(requireContext(), "Habit deleted", Toast.LENGTH_SHORT).show()
    }

    private fun onCompleteHabit(habit: Habit) {
        val currentDateString = DateUtils.getCurrentDateString()
        val existingCompletion = sharedPreferencesHelper.getHabitCompletion(habit.id, currentDateString)
        
        val newCompletedCount = (existingCompletion?.completedCount ?: 0) + 1
        
        val habitCompletion = HabitCompletion(
            habitId = habit.id,
            date = DateUtils.getCurrentDate(),
            completedCount = newCompletedCount.coerceAtMost(habit.target),
            notes = ""
        )
        
        sharedPreferencesHelper.saveHabitCompletion(habitCompletion)
        refreshHabits()
        
        if (newCompletedCount >= habit.target) {
            Toast.makeText(requireContext(), "Habit completed for today! 🎉", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Progress: $newCompletedCount/${habit.target}", Toast.LENGTH_SHORT).show()
        }
    }

    fun refreshHabits() {
        val savedHabits = sharedPreferencesHelper.getHabits()
        habitList.clear()
        habitList.addAll(savedHabits)
        habitAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
