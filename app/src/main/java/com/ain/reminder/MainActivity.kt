package com.ain.reminder

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import com.ain.reminder.notifications.AlarmScheduler
import com.ain.reminder.ui.ReminderApp
import com.ain.reminder.ui.theme.ReminderTheme
import java.time.LocalDate
import java.time.LocalTime

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as ReminderApplication

        setContent {
            ReminderTheme {
                val context = LocalContext.current
                val notificationPermissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission(),
                    onResult = {}
                )

                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val granted = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED
                        if (!granted) {
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                    val preferences = context.getSharedPreferences("permission_prompts", MODE_PRIVATE)
                    val exactAlarmPrompted = preferences.getBoolean("exact_alarm_prompted", false)
                    val exactAlarmIntent = app.alarmScheduler.exactAlarmSettingsIntent()
                    if (!exactAlarmPrompted && !app.alarmScheduler.canScheduleExactAlarms() && exactAlarmIntent != null) {
                        preferences.edit().putBoolean("exact_alarm_prompted", true).apply()
                        runCatching { context.startActivity(exactAlarmIntent) }
                    }
                }

                ReminderApp(
                    medicationRepository = app.medicationRepository,
                    alarmScheduler = app.alarmScheduler,
                    initialReminderDate = intent.getStringExtra(AlarmScheduler.EXTRA_REMINDER_DATE)
                        ?.let { runCatching { LocalDate.parse(it) }.getOrNull() },
                    initialReminderTime = intent.getStringExtra(AlarmScheduler.EXTRA_REMINDER_TIME)
                        ?.let { runCatching { LocalTime.parse(it) }.getOrNull() }
                )
            }
        }
    }
}
