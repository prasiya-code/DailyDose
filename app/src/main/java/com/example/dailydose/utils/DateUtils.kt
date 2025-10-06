package com.example.dailydose.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    
    fun getCurrentDate(): Date = Date()
    
    fun getFormattedDate(date: Date, pattern: String): String {
        val formatter = SimpleDateFormat(pattern, Locale.getDefault())
        return formatter.format(date)
    }
    
    fun getDayOfWeek(date: Date): String {
        val formatter = SimpleDateFormat("EEE", Locale.getDefault())
        return formatter.format(date)
    }
    
    fun getDayOfMonth(date: Date): String {
        val formatter = SimpleDateFormat("dd", Locale.getDefault())
        return formatter.format(date)
    }
    
    fun getShortDateString(date: Date): String {
        return "${getDayOfWeek(date)}\n${getDayOfMonth(date)}"
    }
    
    fun getWeekDates(): List<Date> {
        val calendar = Calendar.getInstance()
        val dates = mutableListOf<Date>()
        
        // Start from Monday of current week
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        
        for (i in 0..6) {
            dates.add(calendar.time)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        
        return dates
    }
    
    fun isToday(date: Date): Boolean {
        val today = Calendar.getInstance()
        val checkDate = Calendar.getInstance().apply { time = date }
        
        return today.get(Calendar.YEAR) == checkDate.get(Calendar.YEAR) &&
               today.get(Calendar.DAY_OF_YEAR) == checkDate.get(Calendar.DAY_OF_YEAR)
    }
    
    fun getCurrentDateString(): String {
        val formatter = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        return formatter.format(Date())
    }
    
    fun getDateDaysAgo(daysAgo: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, -daysAgo)
        val formatter = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        return formatter.format(calendar.time)
    }
}
