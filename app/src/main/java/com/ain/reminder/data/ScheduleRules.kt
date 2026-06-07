package com.ain.reminder.data

import java.time.LocalDate
import java.time.temporal.ChronoUnit

object ScheduleRules {
    fun alternatingDoseForDate(
        anchorDate: LocalDate,
        anchorDoseText: String,
        alternateDoseText: String,
        date: LocalDate
    ): String {
        val offset = ChronoUnit.DAYS.between(anchorDate, date)
        return if (Math.floorMod(offset, 2L) == 0L) anchorDoseText else alternateDoseText
    }
}
