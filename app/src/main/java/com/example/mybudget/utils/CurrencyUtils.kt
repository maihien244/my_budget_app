package com.example.mybudget.utils

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object CurrencyUtils {
    private val localeVi = Locale.forLanguageTag("vi-VN")

    fun formatVnd(amount: Double): String {
        val formatter = NumberFormat.getCurrencyInstance(localeVi)
        return formatter.format(amount)
    }

    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm dd/MM/yyyy", localeVi)
        return sdf.format(Date(timestamp))
    }

    fun formatDateShort(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM", localeVi)
        return sdf.format(Date(timestamp))
    }

    fun formatMonthYear(calendar: Calendar): String {
        val sdf = SimpleDateFormat("MM/yyyy", localeVi)
        return "Kỳ tháng " + sdf.format(calendar.time)
    }

    fun formatDateRange(startMillis: Long, endMillis: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", localeVi)
        return "${sdf.format(Date(startMillis))} - ${sdf.format(Date(endMillis))}"
    }

    fun getStartOfMonth(calendar: Calendar): Long {
        val cal = calendar.clone() as Calendar
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    fun getEndOfMonth(calendar: Calendar): Long {
        val cal = calendar.clone() as Calendar
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        return cal.timeInMillis
    }

    fun getSalaryCyclePeriod(calendar: Calendar, startDay: Int): Pair<Long, Long> {
        if (startDay <= 1) {
            return Pair(getStartOfMonth(calendar), getEndOfMonth(calendar))
        }

        val calStart = calendar.clone() as Calendar
        val maxDaysInMonth = calStart.getActualMaximum(Calendar.DAY_OF_MONTH)
        val validStartDay = startDay.coerceAtMost(maxDaysInMonth)

        calStart.set(Calendar.DAY_OF_MONTH, validStartDay)
        calStart.set(Calendar.HOUR_OF_DAY, 0)
        calStart.set(Calendar.MINUTE, 0)
        calStart.set(Calendar.SECOND, 0)
        calStart.set(Calendar.MILLISECOND, 0)

        val calEnd = calStart.clone() as Calendar
        calEnd.add(Calendar.MONTH, 1)
        calEnd.add(Calendar.DAY_OF_MONTH, -1)
        calEnd.set(Calendar.HOUR_OF_DAY, 23)
        calEnd.set(Calendar.MINUTE, 59)
        calEnd.set(Calendar.SECOND, 59)
        calEnd.set(Calendar.MILLISECOND, 999)

        return Pair(calStart.timeInMillis, calEnd.timeInMillis)
    }

    fun getInitialCalendarForToday(startDay: Int): Calendar {
        val today = Calendar.getInstance()
        val currentDay = today.get(Calendar.DAY_OF_MONTH)
        val cal = today.clone() as Calendar
        if (startDay > 1 && currentDay < startDay) {
            cal.add(Calendar.MONTH, -1)
        }
        return cal
    }
}