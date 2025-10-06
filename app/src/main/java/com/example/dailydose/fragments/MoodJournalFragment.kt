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
import com.example.dailydose.adapters.MoodLogAdapter
import com.example.dailydose.data.SharedPreferencesHelper
import com.example.dailydose.models.MoodLog
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MoodJournalFragment : Fragment() {

    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var moodLogRecyclerView: RecyclerView
    private lateinit var addMoodFab: FloatingActionButton
    private lateinit var moodLogAdapter: MoodLogAdapter
    private lateinit var emptyState: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mood_journal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get SharedPreferences helper from MainActivity
        sharedPreferencesHelper = (activity as MainActivity).getSharedPreferencesHelper()

        // Initialize views
        moodLogRecyclerView = view.findViewById(R.id.mood_log_recycler_view)
        addMoodFab = view.findViewById(R.id.add_mood_fab)
        emptyState = view.findViewById(R.id.empty_state)

        // Setup RecyclerView
        setupRecyclerView()

        // Setup FAB click listener
        addMoodFab.setOnClickListener {
            showAddMoodDialog()
        }

        // Load mood logs
        loadMoodLogs()
    }

    private fun setupRecyclerView() {
        moodLogAdapter = MoodLogAdapter(
            moodLogs = emptyList(),
            onMoodLogClick = { moodLog ->
                // TODO: Handle mood log click
            },
            onMoodLogEdit = { moodLog ->
                showEditMoodDialog(moodLog)
            },
            onMoodLogDelete = { moodLog ->
                deleteMoodLog(moodLog)
            }
        )

        moodLogRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = moodLogAdapter
        }
    }

    private fun loadMoodLogs() {
        val moodLogs = sharedPreferencesHelper.getMoodLogs().sortedByDescending { it.date }
        moodLogAdapter.updateMoodLogs(moodLogs)

        // Show/hide empty state
        if (moodLogs.isEmpty()) {
            emptyState.visibility = View.VISIBLE
            moodLogRecyclerView.visibility = View.GONE
        } else {
            emptyState.visibility = View.GONE
            moodLogRecyclerView.visibility = View.VISIBLE
        }
    }

    private fun deleteMoodLog(moodLog: MoodLog) {
        sharedPreferencesHelper.deleteMoodLog(moodLog)
        loadMoodLogs()
    }

    private fun showEditMoodDialog(moodLog: MoodLog) {
        val dialog = AddMoodDialogFragment()
        dialog.moodToEdit = moodLog
        dialog.moodAddedListener = object : AddMoodDialogFragment.OnMoodAddedListener {
            override fun onMoodAdded() {
                loadMoodLogs()
            }
        }
        dialog.show(parentFragmentManager, "EditMoodDialog")
    }

    private fun showAddMoodDialog() {
        val dialog = AddMoodDialogFragment()
        dialog.moodAddedListener = object : AddMoodDialogFragment.OnMoodAddedListener {
            override fun onMoodAdded() {
                loadMoodLogs()
            }
        }
        dialog.show(parentFragmentManager, "AddMoodDialog")
    }

    override fun onResume() {
        super.onResume()
        loadMoodLogs()
    }
}
