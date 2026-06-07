package com.ain.reminder.data

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class ScheduleRulesTest {
    private val anchor = LocalDate.of(2026, 6, 3)

    @Test
    fun anchorDateUsesAnchorDose() {
        assertEquals(
            "2片",
            ScheduleRules.alternatingDoseForDate(anchor, "2片", "2.5片", anchor)
        )
    }

    @Test
    fun nextDayUsesAlternateDose() {
        assertEquals(
            "2.5片",
            ScheduleRules.alternatingDoseForDate(anchor, "2片", "2.5片", anchor.plusDays(1))
        )
    }

    @Test
    fun previousDayUsesAlternateDose() {
        assertEquals(
            "2.5片",
            ScheduleRules.alternatingDoseForDate(anchor, "2片", "2.5片", anchor.minusDays(1))
        )
    }

    @Test
    fun monthSpanningDatesKeepAlternating() {
        assertEquals(
            "2片",
            ScheduleRules.alternatingDoseForDate(anchor, "2片", "2.5片", LocalDate.of(2026, 7, 1))
        )
        assertEquals(
            "2.5片",
            ScheduleRules.alternatingDoseForDate(anchor, "2片", "2.5片", LocalDate.of(2026, 7, 2))
        )
    }
}
