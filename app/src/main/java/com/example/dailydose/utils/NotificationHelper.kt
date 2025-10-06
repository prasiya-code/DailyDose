package com.example.dailydose.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.work.*
import com.example.dailydose.workers.HydrationReminderWorker
import java.util.concurrent.TimeUnit

class NotificationHelper(private val context: Context) {
    
    private val workManager = WorkManager.getInstance(context)
    
    fun scheduleHydrationReminders(intervalHours: Int, startTime: String) {
        // Cancel existing work
        workManager.cancelUniqueWork("hydration_reminder")
        
        // Parse start time (format: "HH:MM")
        val timeParts = startTime.split(":")
        val hour = timeParts[0].toInt()
        val minute = timeParts[1].toInt()
        
        // Create constraints
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresBatteryNotLow(true)
            .build()
        
        // Create periodic work request
        val hydrationWork = PeriodicWorkRequestBuilder<HydrationReminderWorker>(
            intervalHours.toLong(), TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .setInitialDelay(calculateInitialDelay(hour, minute), TimeUnit.MINUTES)
            .build()
        
        // Enqueue the work
        workManager.enqueueUniquePeriodicWork(
            "hydration_reminder",
            ExistingPeriodicWorkPolicy.REPLACE,
            hydrationWork
        )
    }
    
    fun cancelHydrationReminders() {
        workManager.cancelUniqueWork("hydration_reminder")
    }
    
    private fun calculateInitialDelay(targetHour: Int, targetMinute: Int): Long {
        val now = java.util.Calendar.getInstance()
        val target = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, targetHour)
            set(java.util.Calendar.MINUTE, targetMinute)
            set(java.util.Calendar.SECOND, 0)
        }
        
        // If the target time has passed today, schedule for tomorrow
        if (target.timeInMillis <= now.timeInMillis) {
            target.add(java.util.Calendar.DAY_OF_MONTH, 1)
        }
        
        return target.timeInMillis - now.timeInMillis
    }
}
