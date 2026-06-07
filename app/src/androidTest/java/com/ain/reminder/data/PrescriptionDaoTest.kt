package com.ain.reminder.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.LocalTime

@RunWith(AndroidJUnit4::class)
class PrescriptionDaoTest {
    private lateinit var database: AppDatabase
    private lateinit var repository: MedicationRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        repository = MedicationRepository(database.prescriptionDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun dailyPrescriptionGeneratesItemsInsideDateRange() = runTest {
        repository.savePrescription(
            PrescriptionInput(
                name = "维生素",
                startDate = LocalDate.of(2026, 6, 1),
                endDate = LocalDate.of(2026, 6, 10),
                enabled = true,
                scheduleType = ScheduleType.Daily,
                times = listOf(
                    PrescriptionTimeInput(LocalTime.of(8, 0), "1片"),
                    PrescriptionTimeInput(LocalTime.of(20, 0), "1片")
                ),
                anchorDate = null,
                anchorDoseText = null,
                alternateDoseText = null
            )
        )

        assertEquals(0, repository.groupsForDate(LocalDate.of(2026, 5, 31)).size)
        assertEquals(2, repository.groupsForDate(LocalDate.of(2026, 6, 5)).size)
        assertEquals(0, repository.groupsForDate(LocalDate.of(2026, 6, 11)).size)
    }

    @Test
    fun permanentPrescriptionKeepsGeneratingAndDisabledDoesNotGenerate() = runTest {
        val id = repository.savePrescription(
            PrescriptionInput(
                name = "长期药",
                startDate = LocalDate.of(2026, 6, 1),
                endDate = null,
                enabled = true,
                scheduleType = ScheduleType.Daily,
                times = listOf(PrescriptionTimeInput(LocalTime.of(8, 0), "10mg")),
                anchorDate = null,
                anchorDoseText = null,
                alternateDoseText = null
            )
        )

        val prescriptions = database.prescriptionDao().getPrescriptions()
        assertTrue(repository.groupsForDate(LocalDate.of(2027, 1, 1)).isNotEmpty())

        repository.setPrescriptionEnabled(prescriptions.first { it.id == id }, false)

        assertTrue(repository.groupsForDate(LocalDate.of(2027, 1, 1)).isEmpty())
    }

    @Test
    fun alternatingDoseUsesAnchorParityAcrossPreviousAndNextDates() = runTest {
        repository.savePrescription(
            PrescriptionInput(
                name = "交替药",
                startDate = LocalDate.of(2026, 1, 1),
                endDate = null,
                enabled = true,
                scheduleType = ScheduleType.Alternating,
                times = listOf(PrescriptionTimeInput(LocalTime.of(8, 0), "2片")),
                anchorDate = LocalDate.of(2026, 6, 3),
                anchorDoseText = "2片",
                alternateDoseText = "2.5片"
            )
        )

        assertEquals("2.5片", repository.groupsForDate(LocalDate.of(2026, 6, 2)).single().items.single().doseText)
        assertEquals("2片", repository.groupsForDate(LocalDate.of(2026, 6, 3)).single().items.single().doseText)
        assertEquals("2.5片", repository.groupsForDate(LocalDate.of(2026, 6, 4)).single().items.single().doseText)
    }

    @Test
    fun mealTimingIsStoredAndReturnedWithDailyPlans() = runTest {
        repository.savePrescription(
            PrescriptionInput(
                name = "餐别药",
                startDate = LocalDate.of(2026, 6, 1),
                endDate = null,
                enabled = true,
                scheduleType = ScheduleType.Daily,
                times = listOf(
                    PrescriptionTimeInput(LocalTime.of(7, 30), "1片", MealTiming.EmptyStomach),
                    PrescriptionTimeInput(LocalTime.of(12, 30), "2片", MealTiming.AfterMeal),
                    PrescriptionTimeInput(LocalTime.of(18, 30), "半片", MealTiming.BeforeMeal)
                ),
                anchorDate = null,
                anchorDoseText = null,
                alternateDoseText = null
            )
        )

        val items = repository.groupsForDate(LocalDate.of(2026, 6, 5)).flatMap { it.items }

        assertEquals(
            listOf(MealTiming.EmptyStomach, MealTiming.AfterMeal, MealTiming.BeforeMeal),
            items.map { it.mealTiming }
        )
        assertEquals(listOf("1片", "2片", "半片"), items.map { it.doseText })
    }

    @Test
    fun sameTimePrescriptionsMergeIntoOneGroupAndConfirmTogether() = runTest {
        listOf("药A", "药B").forEach { name ->
            repository.savePrescription(
                PrescriptionInput(
                    name = name,
                    startDate = LocalDate.of(2026, 6, 1),
                    endDate = null,
                    enabled = true,
                    scheduleType = ScheduleType.Daily,
                    times = listOf(PrescriptionTimeInput(LocalTime.of(8, 0), "1片")),
                    anchorDate = null,
                    anchorDoseText = null,
                    alternateDoseText = null
                )
            )
        }

        val group = repository.groupsForDate(LocalDate.of(2026, 6, 5)).single()

        assertEquals(LocalTime.of(8, 0), group.time)
        assertEquals(2, group.items.size)
        assertFalse(group.allTaken)

        repository.confirmGroup(group, confirmedAtMillis = 123L)
        val confirmed = repository.groupsForDate(LocalDate.of(2026, 6, 5)).single()

        assertTrue(confirmed.allTaken)
        assertEquals(listOf(123L, 123L), confirmed.items.map { it.confirmedAtMillis })
    }
}
