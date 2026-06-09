package com.ain.reminder.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.AlarmManagerCompat
import com.ain.reminder.data.DoseGroup
import com.ain.reminder.data.MedicationRepository
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

class AlarmScheduler(private val context: Context) {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    suspend fun scheduleNext(repository: MedicationRepository, from: ZonedDateTime = ZonedDateTime.now()): DoseGroup? {
        val group = repository.nextReminderGroup(from) ?: return null
        val trigger = group.date.atTime(group.time).atZone(from.zone)
        val pendingIntent = reminderPendingIntent(context, group.date, group.time)
        if (canScheduleExactAlarms()) {
            AlarmManagerCompat.setExactAndAllowWhileIdle(
                alarmManager,
                AlarmManager.RTC_WAKEUP,
                trigger.toInstant().toEpochMilli(),
                pendingIntent
            )
        } else {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                trigger.toInstant().toEpochMilli(),
                pendingIntent
            )
        }
        return group
    }

    fun canScheduleExactAlarms(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()

    fun exactAlarmSettingsIntent(): Intent? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                data = Uri.parse("package:${context.packageName}")
            }
        } else {
            null
        }

    fun notificationsAllowed(): Boolean = ReminderNotifications.notificationsAllowed(context)

    companion object {
        const val EXTRA_REMINDER_DATE = "reminder_date"
        const val EXTRA_REMINDER_TIME = "reminder_time"
        private const val REQUEST_CODE = 42

        fun reminderPendingIntent(context: Context, date: LocalDate, time: LocalTime): PendingIntent {
            val intent = Intent(context, ReminderReceiver::class.java).apply {
                putExtra(EXTRA_REMINDER_DATE, date.toString())
                putExtra(EXTRA_REMINDER_TIME, time.toString())
            }
            return PendingIntent.getBroadcast(
                context,
                REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        fun millisFor(date: LocalDate, hour: Int, minute: Int): Long =
            date.atTime(hour, minute)
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
    }
}
