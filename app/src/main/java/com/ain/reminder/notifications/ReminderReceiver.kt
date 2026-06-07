package com.ain.reminder.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.ain.reminder.MainActivity
import com.ain.reminder.R
import com.ain.reminder.data.AppDatabase
import com.ain.reminder.data.DoseGroup
import com.ain.reminder.data.MedicationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val database = AppDatabase.create(context.applicationContext)
                val repository = MedicationRepository(database.prescriptionDao())
                val date = intent.getStringExtra(AlarmScheduler.EXTRA_REMINDER_DATE)
                    ?.let(LocalDate::parse)
                    ?: LocalDate.now()
                val time = intent.getStringExtra(AlarmScheduler.EXTRA_REMINDER_TIME)
                    ?.let(LocalTime::parse)
                val group = repository.groupsForDate(date)
                    .firstOrNull { time == null || it.time == time }

                createChannel(context)
                val scheduler = AlarmScheduler(context)
                if (group != null && scheduler.notificationsAllowed()) {
                    NotificationManagerCompat.from(context).notify(
                        NOTIFICATION_ID,
                        notification(context, group)
                    )
                }
                scheduler.scheduleNext(repository)
                database.close()
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun notification(context: Context, group: DoseGroup): android.app.Notification {
        val content = if (group.items.size == 1) {
            val item = group.items.first()
            "${group.time} 需要服用：${item.medicineName} ${item.doseText}"
        } else {
            "${group.time} 有 ${group.items.size} 项药需要服用"
        }
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("服药提醒")
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setContentIntent(openAppIntent(context, group.date, group.time))
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
    }

    private fun openAppIntent(context: Context, date: LocalDate, time: LocalTime): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(AlarmScheduler.EXTRA_REMINDER_DATE, date.toString())
            putExtra(AlarmScheduler.EXTRA_REMINDER_TIME, time.toString())
        }
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "服药提醒",
                NotificationManager.IMPORTANCE_HIGH
            )
            context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    companion object {
        private const val CHANNEL_ID = "medication_reminders"
        private const val NOTIFICATION_ID = 1001
    }
}
