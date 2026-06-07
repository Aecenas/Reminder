package com.ain.reminder.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class ScheduleType {
    Daily,
    Alternating
}

enum class MealTiming(val label: String) {
    BeforeMeal("饭前"),
    AfterMeal("饭后"),
    EmptyStomach("空腹");

    companion object {
        fun fromStored(value: String?): MealTiming =
            entries.firstOrNull { it.name == value } ?: AfterMeal
    }
}

@Entity(tableName = "prescriptions")
data class PrescriptionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val startDate: String,
    val endDate: String?,
    val enabled: Boolean,
    val scheduleType: String,
    val anchorDate: String?,
    val anchorDoseText: String?,
    val alternateDoseText: String?
)

@Entity(
    tableName = "prescription_times",
    foreignKeys = [
        ForeignKey(
            entity = PrescriptionEntity::class,
            parentColumns = ["id"],
            childColumns = ["prescriptionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("prescriptionId")]
)
data class PrescriptionTimeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val prescriptionId: Long,
    val time: String,
    val doseText: String,
    val mealTiming: String = MealTiming.AfterMeal.name
)

@Entity(
    tableName = "intake_records",
    primaryKeys = ["prescriptionId", "date", "time"],
    indices = [Index("date"), Index("prescriptionId")]
)
data class IntakeRecordEntity(
    val prescriptionId: Long,
    val date: String,
    val time: String,
    val medicineNameSnapshot: String,
    val doseTextSnapshot: String,
    val taken: Boolean,
    val confirmedAtMillis: Long?
)
