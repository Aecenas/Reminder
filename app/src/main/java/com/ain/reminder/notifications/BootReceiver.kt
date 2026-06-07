package com.ain.reminder.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ain.reminder.data.AppDatabase
import com.ain.reminder.data.MedicationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (
            intent.action != Intent.ACTION_BOOT_COMPLETED &&
            intent.action != Intent.ACTION_MY_PACKAGE_REPLACED
        ) {
            return
        }

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val database = AppDatabase.create(context.applicationContext)
                val repository = MedicationRepository(database.prescriptionDao())
                AlarmScheduler(context.applicationContext).scheduleNext(repository)
                database.close()
            } finally {
                pendingResult.finish()
            }
        }
    }
}
