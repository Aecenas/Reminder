package com.ain.reminder

import android.app.Application
import com.ain.reminder.data.AppDatabase
import com.ain.reminder.data.MedicationRepository
import com.ain.reminder.notifications.AlarmScheduler

class ReminderApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.create(this) }
    val medicationRepository: MedicationRepository by lazy {
        MedicationRepository(database.prescriptionDao())
    }
    val alarmScheduler: AlarmScheduler by lazy { AlarmScheduler(this) }
}
