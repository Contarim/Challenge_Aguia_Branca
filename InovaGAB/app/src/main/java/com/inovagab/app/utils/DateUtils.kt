package com.inovagab.app.utils

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object DateUtils {
    private const val DATE_FORMAT = "dd/MM/yyyy"

    fun getCurrentDate(): String {
        val sdf = SimpleDateFormat(DATE_FORMAT, Locale("pt", "BR"))
        return sdf.format(Date())
    }

    fun addDaysToCurrentDate(days: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, days)
        val sdf = SimpleDateFormat(DATE_FORMAT, Locale("pt", "BR"))
        return sdf.format(calendar.time)
    }

    fun daysBetween(startStr: String, endStr: String): Int {
        try {
            val sdf = SimpleDateFormat(DATE_FORMAT, Locale("pt", "BR"))
            val start = sdf.parse(startStr)
            val end = sdf.parse(endStr)
            if (start != null && end != null) {
                val diffInMillis = end.time - start.time
                return TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS).toInt()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }

    fun calculateDaysRemaining(endDateStr: String): Int {
        return daysBetween(getCurrentDate(), endDateStr)
    }
}
