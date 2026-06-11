package com.ain.reminder.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.ain.reminder.R
import com.ain.reminder.data.DayMedicationStatus
import com.ain.reminder.data.DoseGroup
import com.ain.reminder.data.DueMedicationStatus
import com.ain.reminder.data.MealTiming
import com.ain.reminder.data.MedicationRepository
import com.ain.reminder.data.PlannedIntake
import com.ain.reminder.data.PrescriptionInput
import com.ain.reminder.data.PrescriptionTimeInput
import com.ain.reminder.data.PrescriptionWithTimes
import com.ain.reminder.data.ScheduleType
import com.ain.reminder.notifications.AlarmScheduler
import com.ain.reminder.notifications.ReminderNotifications
import com.ain.reminder.updates.GitHubUpdateService
import com.ain.reminder.updates.DownloadProgress
import com.ain.reminder.updates.UpdateCheckResult
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.PI
import kotlin.math.min
import kotlin.math.sin

private enum class Screen(val label: String, val icon: ImageVector) {
    Reminder("服药", Icons.Default.Medication),
    Prescription("药方", Icons.AutoMirrored.Filled.Assignment),
    Settings("设置", Icons.Default.Settings)
}

private enum class MedicationSubPage(val label: String) {
    Reminder("提醒"),
    Calendar("日历")
}

private data class VisualTheme(
    val name: String,
    val backgroundRes: Int,
    val ringRes: Int,
    val accent: Color,
    val accentDeep: Color,
    val text: Color,
    val mutedText: Color,
    val glass: Color,
    val navGlass: Color
)

private val VisualThemes = listOf(
    VisualTheme(
        name = "春叶",
        backgroundRes = R.drawable.theme_green_bg,
        ringRes = R.drawable.theme_green_ring,
        accent = Color(0xFF5FA65E),
        accentDeep = Color(0xFF2F7E48),
        text = Color(0xFF4E8A4E),
        mutedText = Color(0xFF6E9B6A),
        glass = Color.White.copy(alpha = 0.68f),
        navGlass = Color.White.copy(alpha = 0.76f)
    )
)

private val DateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy年M月d日")
private val IsoDateFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE
private val MonthFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy年M月")
private val TimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
private val WeekLabels = listOf("日", "一", "二", "三", "四", "五", "六")
private val ReminderWeekdayLabels = mapOf(
    DayOfWeek.MONDAY to "星期一",
    DayOfWeek.TUESDAY to "星期二",
    DayOfWeek.WEDNESDAY to "星期三",
    DayOfWeek.THURSDAY to "星期四",
    DayOfWeek.FRIDAY to "星期五",
    DayOfWeek.SATURDAY to "星期六",
    DayOfWeek.SUNDAY to "星期日"
)

@Composable
fun ReminderApp(
    medicationRepository: MedicationRepository,
    alarmScheduler: AlarmScheduler,
    initialReminderDate: LocalDate?,
    initialReminderTime: LocalTime?
) {
    val theme = remember { themeForDate() }
    var screen by remember { mutableStateOf(Screen.Reminder) }
    val sounds = rememberAppSounds()

    LaunchedEffect(Unit) {
        alarmScheduler.scheduleNext(medicationRepository)
    }

    CompositionLocalProvider(LocalAppSounds provides sounds) {
        ThemeFrame(theme = theme, frosted = false) {
            Scaffold(
                containerColor = Color.Transparent,
                bottomBar = {
                    GlassNav(
                        current = screen,
                        theme = theme,
                        onSelect = {
                            if (screen != it) sounds.play(SoundCue.SwitchLeaf)
                            screen = it
                        }
                    )
                }
            ) { padding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    when (screen) {
                        Screen.Reminder -> ReminderScreen(
                            medicationRepository = medicationRepository,
                            alarmScheduler = alarmScheduler,
                            theme = theme,
                            initialReminderDate = initialReminderDate,
                            initialReminderTime = initialReminderTime
                        )

                        Screen.Prescription -> PrescriptionScreen(
                            medicationRepository = medicationRepository,
                            alarmScheduler = alarmScheduler,
                            theme = theme
                        )

                        Screen.Settings -> SettingsScreen(
                            medicationRepository = medicationRepository,
                            alarmScheduler = alarmScheduler,
                            theme = theme
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ThemeFrame(theme: VisualTheme, frosted: Boolean, content: @Composable () -> Unit) {
    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(theme.backgroundRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .then(if (frosted) Modifier.blur(18.dp) else Modifier)
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = if (frosted) 0.22f else 0.08f),
                            Color.White.copy(alpha = if (frosted) 0.16f else 0.02f),
                            Color.White.copy(alpha = if (frosted) 0.34f else 0.18f)
                        )
                    )
                )
        )
        content()
    }
}

@Composable
private fun GlassNav(
    current: Screen,
    theme: VisualTheme,
    onSelect: (Screen) -> Unit
) {
    val barShape = RoundedCornerShape(30.dp)
    val selectedShape = RoundedCornerShape(28.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 10.dp)
            .background(Color(0xFFFFFBEA).copy(alpha = 0.42f), barShape)
            .border(1.dp, Color.White.copy(alpha = 0.58f), barShape)
            .padding(7.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Screen.entries.forEach { screen ->
                GlassNavItem(
                    screen = screen,
                    selected = current == screen,
                    theme = theme,
                    selectedShape = selectedShape,
                    onClick = { onSelect(screen) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun GlassNavItem(
    screen: Screen,
    selected: Boolean,
    theme: VisualTheme,
    selectedShape: RoundedCornerShape,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val contentColor = if (selected) theme.accentDeep else Color(0xFF5F7144)
    Box(
        modifier = modifier
            .fillMaxSize()
            .then(
                if (selected) {
                    Modifier
                        .background(Color(0xFFFFFBEA).copy(alpha = 0.62f), selectedShape)
                        .border(1.dp, Color.White.copy(alpha = 0.74f), selectedShape)
                        .border(1.dp, Color(0xFFD7CB82).copy(alpha = 0.28f), selectedShape)
                } else {
                    Modifier
                }
            )
            .softClickable(onClick = onClick)
            .padding(horizontal = if (selected) 8.dp else 4.dp),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    screen.icon,
                    contentDescription = screen.label,
                    tint = contentColor,
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    screen.label,
                    color = contentColor,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Icon(
                    screen.icon,
                    contentDescription = screen.label,
                    tint = contentColor.copy(alpha = 0.92f),
                    modifier = Modifier.size(28.dp)
                )
                Text(
                    screen.label,
                    color = contentColor,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun ReminderScreen(
    medicationRepository: MedicationRepository,
    alarmScheduler: AlarmScheduler,
    theme: VisualTheme,
    initialReminderDate: LocalDate?,
    initialReminderTime: LocalTime?
) {
    val sounds = LocalAppSounds.current
    val today = remember { LocalDate.now() }
    val groups by remember(today) {
        medicationRepository.observeGroupsForDate(today)
    }.collectAsStateWithLifecycle(initialValue = emptyList())
    val scope = rememberCoroutineScope()
    var selectedIndex by remember { mutableIntStateOf(0) }
    var initialSelectionApplied by remember { mutableStateOf(false) }
    var locallyCompletedGroupKey by remember { mutableStateOf<String?>(null) }
    var detailGroup by remember { mutableStateOf<DoseGroup?>(null) }
    var subPage by remember { mutableStateOf(MedicationSubPage.Reminder) }

    LaunchedEffect(groups.size) {
        selectedIndex = selectedIndex.coerceIn(0, (groups.size - 1).coerceAtLeast(0))
    }

    LaunchedEffect(groups, initialReminderDate, initialReminderTime) {
        if (groups.isNotEmpty() && !initialSelectionApplied) {
            selectedIndex = preferredReminderIndex(groups, today, initialReminderDate, initialReminderTime)
            initialSelectionApplied = true
        }
    }

    val group = groups.getOrNull(selectedIndex)
    val completed = group?.let { it.allTaken || locallyCompletedGroupKey == it.key } ?: false
    val totalToday = groups.sumOf { it.items.size }
    val completedToday = groups.sumOf { doseGroup ->
        doseGroup.items.count { it.taken } + if (doseGroup.key == locallyCompletedGroupKey) {
            doseGroup.items.count { !it.taken }
        } else {
            0
        }
    }.coerceAtMost(totalToday)

    Box(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 28.dp, bottom = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MedicationSubPageSwitch(
                current = subPage,
                theme = theme,
                modifier = Modifier.fillMaxWidth(),
                onSelect = { next ->
                    if (subPage != next) sounds.play(SoundCue.SwitchLeaf)
                    subPage = next
                }
            )

            when (subPage) {
                MedicationSubPage.Reminder -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(Modifier.height(26.dp))

                        ReminderDateLabel(date = today, theme = theme)

                        Spacer(Modifier.height(30.dp))

                        ReminderTimeHeader(
                            group = group,
                            theme = theme,
                            onShowDetails = {
                                sounds.play(SoundCue.OpenBloom)
                                detailGroup = it
                            }
                        )

                        Spacer(Modifier.height(10.dp))

                        ReminderHoldTarget(
                            group = group,
                            theme = theme,
                            completed = completed,
                            hasPrevious = groups.size > 1,
                            hasNext = groups.size > 1,
                            onPrevious = {
                                if (groups.isNotEmpty()) {
                                    sounds.play(SoundCue.SwitchLeaf)
                                    selectedIndex = if (selectedIndex == 0) groups.lastIndex else selectedIndex - 1
                                }
                            },
                            onNext = {
                                if (groups.isNotEmpty()) {
                                    sounds.play(SoundCue.SwitchLeaf)
                                    selectedIndex = if (selectedIndex == groups.lastIndex) 0 else selectedIndex + 1
                                }
                            },
                            onConfirm = { doseGroup ->
                                locallyCompletedGroupKey = doseGroup.key
                                scope.launch {
                                    medicationRepository.confirmGroup(doseGroup)
                                    alarmScheduler.scheduleNext(medicationRepository)
                                }
                            }
                        )

                        Spacer(Modifier.height(20.dp))

                        ReminderTodayProgress(total = totalToday, completed = completedToday, theme = theme)

                        Spacer(Modifier.height(26.dp))
                    }
                }

                MedicationSubPage.Calendar -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(Modifier.height(18.dp))

                        MedicationCalendarContent(
                            medicationRepository = medicationRepository,
                            theme = theme
                        )

                        Spacer(Modifier.height(24.dp))
                    }
                }
            }
        }
    }

    detailGroup?.let { doseGroup ->
        ReminderGroupDetailDialog(
            group = doseGroup,
            theme = theme,
            onDismiss = {
                sounds.play(SoundCue.CloseSoft)
                detailGroup = null
            }
        )
    }
}

private fun preferredReminderIndex(
    groups: List<DoseGroup>,
    today: LocalDate,
    initialReminderDate: LocalDate?,
    initialReminderTime: LocalTime?
): Int {
    if (initialReminderDate == today && initialReminderTime != null) {
        groups.indexOfFirst { it.time == initialReminderTime }.takeIf { it >= 0 }?.let { return it }
    }
    val now = LocalTime.now()
    groups.indexOfFirst { !it.allTaken && !it.time.isAfter(now) }.takeIf { it >= 0 }?.let { return it }
    groups.indexOfFirst { !it.allTaken }.takeIf { it >= 0 }?.let { return it }
    return 0
}

@Composable
private fun ReminderDateLabel(date: LocalDate, theme: VisualTheme, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.shadow(
            elevation = 6.dp,
            shape = RoundedCornerShape(22.dp),
            ambientColor = Color(0xFF7C9D51).copy(alpha = 0.08f),
            spotColor = Color(0xFF7C9D51).copy(alpha = 0.10f)
        ),
        color = Color(0xFFFFFBEA).copy(alpha = 0.36f),
        contentColor = theme.text,
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.32f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DateLeafDecor(modifier = Modifier.size(width = 25.dp, height = 23.dp))
            Text(
                text = "${date.format(DateFormatter)}  ${ReminderWeekdayLabels.getValue(date.dayOfWeek)}",
                color = theme.text,
                fontFamily = FontFamily.Serif,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            DateLeafDecor(
                mirrored = true,
                modifier = Modifier.size(width = 25.dp, height = 23.dp)
            )
        }
    }
}

@Composable
private fun DateLeafDecor(mirrored: Boolean = false, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.date_leaf),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = modifier.graphicsLayer { scaleX = if (mirrored) -1f else 1f }
    )
}

@Composable
private fun MedicationSubPageSwitch(
    current: MedicationSubPage,
    theme: VisualTheme,
    modifier: Modifier = Modifier,
    onSelect: (MedicationSubPage) -> Unit
) {
    val shape = RoundedCornerShape(36.dp)
    Surface(
        modifier = modifier
            .height(72.dp)
            .shadow(
                elevation = 10.dp,
                shape = shape,
                ambientColor = Color(0xFF7C9D51).copy(alpha = 0.10f),
                spotColor = Color(0xFF7C9D51).copy(alpha = 0.14f)
            ),
        color = Color.White.copy(alpha = 0.30f),
        contentColor = theme.text,
        shape = shape,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.66f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(7.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MedicationSubPage.entries.forEach { page ->
                val selected = page == current
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .then(
                            if (selected) {
                                Modifier
                                    .shadow(
                                        elevation = 5.dp,
                                        shape = RoundedCornerShape(30.dp),
                                        ambientColor = Color(0xFF7C9D51).copy(alpha = 0.12f),
                                        spotColor = Color(0xFF7C9D51).copy(alpha = 0.16f)
                                    )
                                    .background(Color(0xFFFFFEF4).copy(alpha = 0.88f), RoundedCornerShape(30.dp))
                                    .border(1.dp, theme.accent.copy(alpha = 0.22f), RoundedCornerShape(30.dp))
                            } else {
                                Modifier.background(Color.White.copy(alpha = 0.18f), RoundedCornerShape(30.dp))
                            }
                        )
                        .clickable { onSelect(page) },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (page == MedicationSubPage.Reminder) {
                        Icon(
                            Icons.Default.NotificationsNone,
                            contentDescription = null,
                            tint = if (selected) Color(0xFF315D31) else Color(0xFF6D746A).copy(alpha = 0.80f),
                            modifier = Modifier.size(25.dp)
                        )
                    } else {
                        Icon(
                            Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = if (selected) Color(0xFF315D31) else Color(0xFF6D746A).copy(alpha = 0.80f),
                            modifier = Modifier.size(25.dp)
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Text(
                        page.label,
                        color = if (selected) Color(0xFF315D31) else Color(0xFF6D746A).copy(alpha = 0.86f),
                        fontFamily = FontFamily.Serif,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsScreen(
    medicationRepository: MedicationRepository,
    alarmScheduler: AlarmScheduler,
    theme: VisualTheme
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    val sounds = LocalAppSounds.current
    var updateStatus by remember { mutableStateOf("尚未检查更新") }
    var checkingUpdate by remember { mutableStateOf(false) }
    var downloadingUpdate by remember { mutableStateOf(false) }
    var pendingImportJson by remember { mutableStateOf<String?>(null) }
    var aboutOpen by remember { mutableStateOf(false) }
    var helpOpen by remember { mutableStateOf(false) }
    var versionOpen by remember { mutableStateOf(false) }
    var notificationsAllowed by remember { mutableStateOf(ReminderNotifications.notificationsAllowed(context)) }
    var exactAlarmAllowed by remember { mutableStateOf(alarmScheduler.canScheduleExactAlarms()) }
    val currentVersion = remember { GitHubUpdateService.currentVersionName(context) }
    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        sounds.play(SoundCue.SelectDrop)
        scope.launch {
            runCatching {
                val backup = withContext(Dispatchers.IO) { medicationRepository.exportBackupJson() }
                withContext(Dispatchers.IO) {
                    context.contentResolver.openOutputStream(uri)?.use { stream ->
                        stream.writer(Charsets.UTF_8).use { writer -> writer.write(backup) }
                    } ?: error("无法写入备份文件。")
                }
            }.onSuccess {
                Toast.makeText(context, "数据已导出。", Toast.LENGTH_SHORT).show()
            }.onFailure {
                Toast.makeText(context, "导出失败：${it.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        sounds.play(SoundCue.SelectDrop)
        scope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    context.contentResolver.openInputStream(uri)?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }
                        ?: error("无法读取备份文件。")
                }
            }.onSuccess {
                pendingImportJson = it
            }.onFailure {
                Toast.makeText(context, "读取失败：${it.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun refreshPermissionState() {
        notificationsAllowed = ReminderNotifications.notificationsAllowed(context)
        exactAlarmAllowed = alarmScheduler.canScheduleExactAlarms()
    }

    LaunchedEffect(Unit) {
        refreshPermissionState()
    }

    DisposableEffect(lifecycleOwner, alarmScheduler) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) refreshPermissionState()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp),
            contentPadding = PaddingValues(top = 22.dp, bottom = 24.dp)
        ) {
            item {
                SettingsSection(title = "数据管理", theme = theme) {
                    SettingsActionRow(
                        icon = Icons.Default.FileUpload,
                        title = "数据导入",
                        subtitle = "从备份文件恢复本地数据",
                        theme = theme,
                        onClick = { importLauncher.launch(arrayOf("application/json", "text/*", "*/*")) }
                    )
                    SettingsRowDivider(theme)
                    SettingsActionRow(
                        icon = Icons.Default.FileDownload,
                        title = "数据导出",
                        subtitle = "导出处方、提醒时间与服药记录",
                        theme = theme,
                        onClick = { exportLauncher.launch("reminder-backup-${System.currentTimeMillis()}.json") }
                    )
                }
            }

            item {
                SettingsSection(title = "通知与提醒", theme = theme) {
                    SettingsActionRow(
                        icon = Icons.Default.NotificationsNone,
                        title = "服药提醒",
                        subtitle = "打开系统通知设置",
                        value = if (notificationsAllowed) "已开启" else "未开启",
                        theme = theme,
                        onClick = {
                            sounds.play(SoundCue.SelectDrop)
                            context.startActivity(notificationSettingsIntent(context))
                        }
                    )
                    SettingsRowDivider(theme)
                    SettingsActionRow(
                        icon = Icons.Default.AccessTime,
                        title = "闹钟提醒",
                        subtitle = "开启准点提醒权限",
                        value = if (exactAlarmAllowed) "已允许" else "建议开启",
                        theme = theme,
                        onClick = {
                            sounds.play(SoundCue.SelectDrop)
                            alarmScheduler.exactAlarmSettingsIntent()
                                ?.let { runCatching { context.startActivity(it) } }
                        }
                    )
                    SettingsRowDivider(theme)
                    SettingsActionRow(
                        icon = Icons.Default.ChatBubbleOutline,
                        title = "消息通知",
                        subtitle = "发送一条测试通知",
                        theme = theme,
                        onClick = {
                            sounds.play(SoundCue.SelectDrop)
                            val sent = ReminderNotifications.showTestNotification(context)
                            Toast.makeText(
                                context,
                                if (sent) "已发送测试通知。" else "通知权限未开启，无法发送测试通知。",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                }
            }

            item {
                SettingsSection(title = "关于与帮助", theme = theme) {
                    SettingsActionRow(
                        icon = Icons.Default.Info,
                        title = "当前版本",
                        subtitle = currentVersion,
                        theme = theme,
                        onClick = { versionOpen = true }
                    )
                    SettingsRowDivider(theme)
                    SettingsActionRow(
                        icon = Icons.Default.SystemUpdate,
                        title = when {
                            checkingUpdate -> "检查中..."
                            downloadingUpdate -> "更新中"
                            else -> "版本更新"
                        },
                        subtitle = updateStatus,
                        enabled = !checkingUpdate && !downloadingUpdate,
                        theme = theme,
                        onClick = {
                            sounds.play(SoundCue.SelectDrop)
                            checkingUpdate = true
                            updateStatus = "正在连接 GitHub..."
                            scope.launch {
                                when (val result = GitHubUpdateService.checkLatest(context)) {
                                    is UpdateCheckResult.Available -> {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
                                            !context.packageManager.canRequestPackageInstalls()
                                        ) {
                                            updateStatus = "请先允许安装未知应用"
                                            GitHubUpdateService.installPermissionIntent(context)
                                                ?.let { runCatching { context.startActivity(it) } }
                                            checkingUpdate = false
                                            return@launch
                                        }
                                        checkingUpdate = false
                                        downloadingUpdate = true
                                        updateStatus = "更新中（0/-- MB）"
                                        runCatching {
                                            GitHubUpdateService.enqueueApkDownload(context, result.update)
                                        }.onSuccess { downloadId ->
                                            GitHubUpdateService.observeDownload(context, downloadId).collect { progress ->
                                                when (progress) {
                                                    is DownloadProgress.Running -> {
                                                        updateStatus = "更新中（${formatBytes(progress.downloadedBytes)}/${formatBytes(progress.totalBytes)}）"
                                                    }
                                                    DownloadProgress.Complete -> {
                                                        downloadingUpdate = false
                                                        updateStatus = "更新完成"
                                                        delay(3000)
                                                        updateStatus = "完成后点击下载通知安装"
                                                    }
                                                    is DownloadProgress.Failed -> {
                                                        downloadingUpdate = false
                                                        updateStatus = progress.reason
                                                    }
                                                }
                                            }
                                        }.onFailure {
                                            downloadingUpdate = false
                                            updateStatus = "下载失败，请稍后重试。"
                                        }
                                    }
                                    is UpdateCheckResult.UpToDate -> {
                                        updateStatus = "无需更新"
                                    }
                                    is UpdateCheckResult.Failed -> {
                                        updateStatus = result.message
                                    }
                                }
                                checkingUpdate = false
                            }
                        }
                    )
                    SettingsRowDivider(theme)
                    SettingsActionRow(
                        icon = Icons.Default.AccountCircle,
                        title = "关于我们",
                        subtitle = "查看应用说明和项目来源",
                        theme = theme,
                        onClick = { aboutOpen = true }
                    )
                    SettingsRowDivider(theme)
                    SettingsActionRow(
                        icon = Icons.AutoMirrored.Filled.HelpOutline,
                        title = "使用帮助",
                        subtitle = "查看备份、提醒和服药记录说明",
                        theme = theme,
                        onClick = { helpOpen = true }
                    )
                }
            }
        }

        pendingImportJson?.let { json ->
            SettingsConfirmImportDialog(
                theme = theme,
                onDismiss = { pendingImportJson = null },
                onConfirm = {
                    pendingImportJson = null
                    scope.launch {
                        runCatching {
                            withContext(Dispatchers.IO) { medicationRepository.importBackupJson(json) }
                            alarmScheduler.scheduleNext(medicationRepository)
                        }.onSuccess {
                            Toast.makeText(context, "数据已导入。", Toast.LENGTH_SHORT).show()
                        }.onFailure {
                            Toast.makeText(context, "导入失败：${it.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            )
        }
        if (aboutOpen) {
            SettingsInfoDialog(
                title = "关于我们",
                body = settingsAboutText(currentVersion, theme),
                theme = theme,
                onDismiss = { aboutOpen = false }
            )
        }
        if (versionOpen) {
            SettingsInfoDialog(
                title = "当前版本",
                body = settingsVersionText(currentVersion, theme),
                theme = theme,
                onDismiss = { versionOpen = false }
            )
        }
        if (helpOpen) {
            SettingsInfoDialog(
                title = "使用帮助",
                body = settingsHelpText(theme),
                theme = theme,
                onDismiss = { helpOpen = false }
            )
        }
    }
}

private fun settingsAboutText(version: String, theme: VisualTheme): AnnotatedString = buildAnnotatedString {
    appendBullet("应用定位", "本地优先的服药提醒与药方管理工具。", theme.accentDeep)
    appendBullet("核心能力", "记录药方、提醒时间、服药状态与日历完成情况。", Color(0xFF7A8F2E))
    appendBullet("数据方式", "数据保存在本机，可通过设置页导入/导出备份。", Color(0xFFB07A26))
    appendBullet("当前版本", version, theme.accentDeep)
    appendBullet("开发者", "Ain", Color(0xFF9F5B31))
}

private fun settingsVersionText(version: String, theme: VisualTheme): AnnotatedString = buildAnnotatedString {
    appendBullet("当前版本", version, theme.accentDeep)
    appendBullet("更新方式", "在设置页点击“版本更新”会连接 GitHub Release 检查新版。", Color(0xFF7A8F2E))
    appendBullet("安装说明", "下载完成后，点击系统下载通知安装新版 APK。", Color(0xFFB07A26))
}

private fun settingsHelpText(theme: VisualTheme): AnnotatedString = buildAnnotatedString {
    appendBullet("服药提醒", "在“服药-提醒”中查看今日提醒，长按中心区域确认服药。", theme.accentDeep)
    appendBullet("服用日历", "在“服药-日历”中查看每天的完成、部分完成或待服用状态。", Color(0xFF7A8F2E))
    appendBullet("药方管理", "在“药方”页新增、编辑、停用或删除药方和服药时间。", theme.accentDeep)
    appendBullet("数据备份", "“数据导出”会生成 JSON 备份文件，建议定期保存。", Color(0xFFB07A26))
    appendBullet("恢复提醒", "“数据导入”会替换当前本地数据，导入前请先确认备份来源。", Color(0xFF9F352F))
}

private fun AnnotatedString.Builder.appendBullet(label: String, body: String, color: Color) {
    append("• ")
    withStyle(SpanStyle(color = color, fontWeight = FontWeight.Bold)) {
        append(label)
    }
    append("：")
    append(body)
    append("\n")
}

@Composable
private fun SettingsConfirmImportDialog(
    theme: VisualTheme,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("确认导入", color = theme.accentDeep, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消", color = theme.mutedText, fontWeight = FontWeight.Bold)
            }
        },
        title = {
            Text("导入数据", color = theme.text, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold)
        },
        text = {
            Text(
                "导入会用备份文件替换当前本地药方、提醒时间和服药记录。建议先导出当前数据作为备份。",
                color = Color(0xFF4E5F42),
                fontWeight = FontWeight.Bold
            )
        },
        containerColor = Color(0xFFFFFDF0),
        titleContentColor = theme.text,
        textContentColor = Color(0xFF4E5F42)
    )
}

@Composable
private fun SettingsInfoDialog(
    title: String,
    body: AnnotatedString,
    theme: VisualTheme,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .padding(horizontal = 28.dp)
                .fillMaxWidth()
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(30.dp),
                    ambientColor = Color(0xFF7C9D51).copy(alpha = 0.16f),
                    spotColor = Color(0xFF7C9D51).copy(alpha = 0.22f)
                ),
            color = Color.Transparent,
            contentColor = theme.text,
            shape = RoundedCornerShape(30.dp),
            shadowElevation = 0.dp
        ) {
            Box {
                Image(
                    painter = painterResource(R.drawable.detail_editor_bg),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.TopCenter,
                    modifier = Modifier.matchParentSize()
                )
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 22.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        LeafMark(theme, modifier = Modifier.size(22.dp))
                        Spacer(Modifier.width(10.dp))
                        Text(
                            title,
                            color = Color(0xFF315D31),
                            fontFamily = FontFamily.Serif,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.width(10.dp))
                        LeafMark(theme, modifier = Modifier.size(22.dp))
                    }
                    Text(
                        body,
                        color = Color(0xFF4E5F42),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.18f
                    )
                    SettingsFooterButton(
                        text = "知道了",
                        icon = Icons.Default.CheckCircle,
                        theme = theme,
                        onClick = onDismiss
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    theme: VisualTheme,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(start = 2.dp)
        ) {
            LeafMark(theme, modifier = Modifier.size(18.dp))
            Text(
                title,
                color = Color(0xFF315D31),
                fontFamily = FontFamily.Serif,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 7.dp,
                    shape = RoundedCornerShape(24.dp),
                    ambientColor = Color(0xFF8AA36C).copy(alpha = 0.12f),
                    spotColor = Color(0xFF8AA36C).copy(alpha = 0.16f)
                ),
            color = Color(0xFFFFFDF3).copy(alpha = 0.80f),
            contentColor = theme.text,
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, Color(0xFFD9D7B8).copy(alpha = 0.46f))
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                content = content
            )
        }
    }
}

@Composable
private fun SettingsProfileRow(theme: VisualTheme, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 76.dp)
            .softClickable(onClick = onClick)
            .padding(horizontal = 2.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = Color(0xFFEAF4C7),
            contentColor = theme.text,
            border = BorderStroke(1.dp, theme.accent.copy(alpha = 0.32f))
        ) {
            Icon(
                Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier
                    .padding(9.dp)
                    .size(42.dp)
            )
        }
        Column(Modifier.weight(1f)) {
            Text(
                "本地用户",
                color = Color(0xFF203320),
                fontFamily = FontFamily.Serif,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                "ID：本机数据",
                color = Color(0xFF6E6457),
                fontFamily = FontFamily.Serif,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = theme.text,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun SettingsActionRow(
    icon: ImageVector,
    title: String,
    theme: VisualTheme,
    subtitle: String? = null,
    value: String? = null,
    enabled: Boolean = true,
    showChevron: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    val sounds = LocalAppSounds.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = if (subtitle == null) 58.dp else 72.dp)
            .alpha(if (enabled) 1f else 0.55f)
            .then(
                if (onClick != null) {
                    Modifier.softClickable(enabled = enabled) {
                        sounds.play(SoundCue.SelectDrop)
                        onClick()
                    }
                } else {
                    Modifier
                }
            )
            .padding(horizontal = 2.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = theme.text,
            modifier = Modifier.size(25.dp)
        )
        Column(Modifier.weight(1f)) {
            Text(
                title,
                color = Color(0xFF213021),
                fontFamily = FontFamily.Serif,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (subtitle != null) {
                Text(
                    subtitle,
                    color = Color(0xFF6E6457),
                    fontFamily = FontFamily.Serif,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        if (value != null) {
            Text(
                value,
                color = theme.text,
                fontFamily = FontFamily.Serif,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (showChevron) {
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = theme.text,
                modifier = Modifier.size(23.dp)
            )
        }
    }
}

@Composable
private fun SettingsFooterButton(
    text: String,
    icon: ImageVector,
    theme: VisualTheme,
    onClick: () -> Unit
) {
    val sounds = LocalAppSounds.current
    val shape = RoundedCornerShape(24.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .selectedSoftPill(shape)
            .border(1.dp, Color.White.copy(alpha = 0.70f), shape)
            .softClickable {
                sounds.play(SoundCue.SelectDrop)
                onClick()
            }
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(icon, contentDescription = null, tint = Color(0xFF315D31), modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(12.dp))
        Text(
            text,
            color = Color(0xFF315D31),
            fontFamily = FontFamily.Serif,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SettingsRowDivider(theme: VisualTheme) {
    HorizontalDivider(
        modifier = Modifier.padding(start = 42.dp),
        color = theme.text.copy(alpha = 0.14f),
        thickness = 0.7.dp
    )
}

private fun SettingsComingSoonToast(context: android.content.Context, name: String) {
    Toast.makeText(context, "${name}暂未开放。", Toast.LENGTH_SHORT).show()
}

private fun formatBytes(bytes: Long): String {
    if (bytes <= 0L) return "-- MB"
    return String.format(Locale.US, "%.1f MB", bytes / 1024f / 1024f)
}

private fun notificationSettingsIntent(context: android.content.Context): Intent =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        }
    } else {
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:${context.packageName}")
        }
    }

private fun openUrl(context: android.content.Context, url: String) {
    runCatching {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }
}

@Composable
private fun ReminderTimeHeader(
    group: DoseGroup?,
    theme: VisualTheme,
    onShowDetails: (DoseGroup) -> Unit
) {
    Row(
        modifier = Modifier.height(46.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            group?.time?.format(TimeFormatter) ?: "暂无提醒",
            color = theme.text,
            fontFamily = FontFamily.Serif,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
        if (group != null && group.items.size > 1) {
            IconButton(
                onClick = { onShowDetails(group) },
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(40.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Assignment,
                    contentDescription = "查看详情",
                    tint = theme.text,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}

@Composable
private fun ReminderTodayProgress(total: Int, completed: Int, theme: VisualTheme) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            if (total == 0) "今日暂无提醒" else "今日提醒已完成",
            color = theme.text,
            fontFamily = FontFamily.Serif,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            "$completed/$total",
            color = theme.text.copy(alpha = 0.86f),
            fontFamily = FontFamily.Serif,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ReminderHoldTarget(
    group: DoseGroup?,
    theme: VisualTheme,
    completed: Boolean,
    hasPrevious: Boolean,
    hasNext: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onConfirm: (DoseGroup) -> Unit
) {
    val sounds = LocalAppSounds.current
    var holding by remember(group?.key) { mutableStateOf(false) }
    var confirmedInThisHold by remember(group?.key) { mutableStateOf(false) }
    val fillProgress = remember(group?.key) { Animatable(if (completed) 1f else 0f) }
    val completionGlow = remember(group?.key) { Animatable(0f) }
    val canConfirm = group != null && !completed

    LaunchedEffect(group?.key, completed) {
        if (completed) {
            fillProgress.snapTo(1f)
        } else if (!holding) {
            fillProgress.snapTo(0f)
        }
    }

    LaunchedEffect(holding, canConfirm, group?.key) {
        when {
            holding && canConfirm -> {
                val currentGroup = group ?: return@LaunchedEffect
                fillProgress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 2000, easing = LinearEasing)
                )
                if (!confirmedInThisHold) {
                    confirmedInThisHold = true
                    sounds.play(SoundCue.DoseComplete)
                    launch {
                        completionGlow.snapTo(0f)
                        completionGlow.animateTo(
                            targetValue = 1f,
                            animationSpec = tween(durationMillis = 900, easing = LinearEasing)
                        )
                        completionGlow.snapTo(0f)
                    }
                    onConfirm(currentGroup)
                }
            }

            !completed -> {
                fillProgress.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 240, easing = LinearEasing)
                )
            }
        }
    }

    val waveTransition = rememberInfiniteTransition(label = "dose-wave")
    val waveOffset by waveTransition.animateFloat(
        initialValue = 0f,
        targetValue = (PI * 2).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (holding) 700 else 2200, easing = LinearEasing)
        ),
        label = "wave-offset"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(330.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(330.dp)
                .zIndex(0f)
                .pointerInput(group?.key, completed) {
                    detectTapGestures(
                        onPress = { offset ->
                            val center = Offset(size.width / 2f, size.height / 2f)
                            val innerRadius = min(size.width, size.height) * 0.34f
                            val insideInnerRing = (offset - center).getDistance() <= innerRadius
                            if (canConfirm && insideInnerRing) {
                                sounds.play(SoundCue.HoldWater)
                                holding = true
                                try {
                                    tryAwaitRelease()
                                } finally {
                                    if (!confirmedInThisHold && fillProgress.value < 0.98f) {
                                        sounds.play(SoundCue.HoldCancel)
                                    }
                                    holding = false
                                }
                            }
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            DoseSunlightFill(
                progress = fillProgress.value,
                holding = holding,
                waveOffset = waveOffset,
                theme = theme
            )
            RotatingThemeRing(theme = theme, holding = holding)
            DoseCompletionGlow(progress = completionGlow.value, theme = theme)
            ReminderCenterText(group = group, completed = completed, theme = theme)
        }

        if (hasPrevious) {
            IconButton(
                onClick = onPrevious,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(56.dp)
                    .zIndex(1f)
            ) {
                Icon(Icons.Default.ChevronLeft, contentDescription = "上一组", tint = theme.text)
            }
        }

        if (hasNext) {
            IconButton(
                onClick = onNext,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(56.dp)
                    .zIndex(1f)
            ) {
                Icon(Icons.Default.ChevronRight, contentDescription = "下一组", tint = theme.text)
            }
        }
    }
}

@Composable
private fun DoseSunlightFill(
    progress: Float,
    holding: Boolean,
    waveOffset: Float,
    theme: VisualTheme
) {
    Canvas(Modifier.fillMaxSize()) {
        val clampedProgress = progress.coerceIn(0f, 1f)
        if (clampedProgress <= 0.001f && !holding) {
            return@Canvas
        }

        val center = Offset(size.width / 2f, size.height / 2f)
        val innerRadius = size.minDimension * 0.285f
        val sunlightAlpha = if (holding) 0.34f else 0.16f * clampedProgress

        drawLine(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = sunlightAlpha),
                    Color(0xFFFFF1A6).copy(alpha = sunlightAlpha * 0.58f),
                    Color.Transparent
                ),
                start = Offset(size.width * 0.92f, size.height * 0.04f),
                end = Offset(size.width * 0.36f, size.height * 0.58f)
            ),
            start = Offset(size.width * 0.88f, -size.height * 0.04f),
            end = Offset(size.width * 0.32f, size.height * 0.64f),
            strokeWidth = 42.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color.White.copy(alpha = sunlightAlpha * 0.54f),
            start = Offset(size.width * 0.98f, size.height * 0.10f),
            end = Offset(size.width * 0.45f, size.height * 0.62f),
            strokeWidth = 18.dp.toPx(),
            cap = StrokeCap.Round
        )

        val circlePath = Path().apply {
            addOval(
                Rect(
                    left = center.x - innerRadius,
                    top = center.y - innerRadius,
                    right = center.x + innerRadius,
                    bottom = center.y + innerRadius
                )
            )
        }
        val fillTop = center.y + innerRadius - innerRadius * 2f * clampedProgress
        val waveAmplitude = (if (holding) 9.dp else 5.dp).toPx()
        val liquidPath = Path().apply {
            moveTo(center.x - innerRadius, center.y + innerRadius)
            lineTo(center.x - innerRadius, fillTop)
            val steps = 24
            for (step in 0..steps) {
                val x = center.x - innerRadius + innerRadius * 2f * step / steps
                val wave = sin(waveOffset + step / steps.toFloat() * PI.toFloat() * 2f) * waveAmplitude
                lineTo(x, fillTop + wave)
            }
            lineTo(center.x + innerRadius, center.y + innerRadius)
            close()
        }

        clipPath(circlePath) {
            drawPath(
                path = liquidPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFF8C9).copy(alpha = 0.64f),
                        Color(0xFFBEE46F).copy(alpha = 0.74f),
                        Color(0xFF6FAE38).copy(alpha = 0.58f)
                    ),
                    startY = center.y - innerRadius,
                    endY = center.y + innerRadius
                )
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.34f * clampedProgress),
                        Color.Transparent
                    ),
                    center = Offset(center.x + innerRadius * 0.30f, center.y - innerRadius * 0.38f),
                    radius = innerRadius * 1.10f
                ),
                radius = innerRadius,
                center = center
            )
        }
        drawCircle(
            color = Color.White.copy(alpha = 0.24f * clampedProgress),
            radius = innerRadius,
            center = center,
            style = Stroke(width = 1.dp.toPx())
        )
    }
}

@Composable
private fun DoseCompletionGlow(progress: Float, theme: VisualTheme) {
    Canvas(Modifier.fillMaxSize()) {
        val p = progress.coerceIn(0f, 1f)
        if (p <= 0.001f) return@Canvas

        val center = Offset(size.width / 2f, size.height / 2f)
        val innerRadius = size.minDimension * 0.285f
        val ringRadius = size.minDimension * 0.43f
        val bloom = sin((p * PI).toFloat()).coerceAtLeast(0f)
        val fadeOut = (1f - p).coerceIn(0f, 1f)

        drawLine(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.46f * bloom),
                    Color(0xFFFFE58A).copy(alpha = 0.34f * bloom),
                    Color.Transparent
                ),
                start = Offset(size.width * 0.90f, -size.height * 0.02f),
                end = Offset(size.width * 0.38f, size.height * 0.57f)
            ),
            start = Offset(size.width * 0.88f, -size.height * 0.04f),
            end = Offset(size.width * 0.34f, size.height * 0.62f),
            strokeWidth = (58.dp.toPx() * (0.65f + 0.35f * fadeOut)),
            cap = StrokeCap.Round
        )

        val rippleRadius = innerRadius * (0.18f + 0.92f * p)
        drawCircle(
            color = Color(0xFFFFF2A8).copy(alpha = 0.42f * fadeOut),
            radius = rippleRadius,
            center = center,
            style = Stroke(width = (2.4.dp.toPx() * fadeOut).coerceAtLeast(0.5f))
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.28f * fadeOut),
            radius = rippleRadius * 0.72f,
            center = center,
            style = Stroke(width = (1.4.dp.toPx() * fadeOut).coerceAtLeast(0.4f))
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFF6B6).copy(alpha = 0.18f * bloom),
                    Color.Transparent
                ),
                center = center,
                radius = innerRadius * 1.18f
            ),
            radius = innerRadius,
            center = center
        )
        drawCircle(
            color = Color(0xFFFFE28C).copy(alpha = 0.36f * bloom),
            radius = ringRadius,
            center = center,
            style = Stroke(width = 4.dp.toPx() * bloom)
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.30f * bloom),
            radius = ringRadius * 0.98f,
            center = center,
            style = Stroke(width = 1.2.dp.toPx())
        )

        val sparkleAlpha = 0.24f * fadeOut
        val sparkles = listOf(
            Offset(center.x - innerRadius * 0.42f, center.y - innerRadius * 0.18f),
            Offset(center.x + innerRadius * 0.28f, center.y - innerRadius * 0.34f),
            Offset(center.x + innerRadius * 0.12f, center.y + innerRadius * 0.22f),
            Offset(center.x - innerRadius * 0.18f, center.y + innerRadius * 0.32f),
            Offset(center.x + innerRadius * 0.52f, center.y + innerRadius * 0.02f)
        )
        sparkles.forEachIndexed { index, point ->
            val pulse = sin((p * PI * 1.3f + index * 0.7f).toFloat()).coerceAtLeast(0f)
            drawCircle(
                color = Color.White.copy(alpha = sparkleAlpha * pulse),
                radius = (2.4f + index % 2) * density,
                center = point
            )
        }
    }
}

@Composable
private fun RotatingThemeRing(theme: VisualTheme, holding: Boolean) {
    var rotation by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(holding) {
        var lastFrameNanos: Long? = null
        while (true) {
            val frameNanos = withFrameNanos { it }
            val lastFrame = lastFrameNanos
            if (lastFrame != null) {
                val elapsedSeconds = (frameNanos - lastFrame) / 1_000_000_000f
                val secondsPerTurn = if (holding) 2f else 8f
                rotation = (rotation + elapsedSeconds * 360f / secondsPerTurn) % 360f
            }
            lastFrameNanos = frameNanos
        }
    }

    Image(
        painter = painterResource(theme.ringRes),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { rotationZ = rotation }
    )
}

@Composable
private fun ReminderCenterText(
    group: DoseGroup?,
    completed: Boolean,
    theme: VisualTheme
) {
    val firstLine = when {
        group == null -> "暂无提醒"
        completed -> "已全服"
        group.items.size == 1 -> {
            val item = group.items.first()
            "${item.medicineName} · ${item.mealTiming.label}"
        }
        else -> "${group.items.size}项药"
    }
    val showDetailHint = group != null && !completed && group.items.size > 1
    val secondLine = when {
        group == null -> "请到药方添加"
        completed -> "${group.items.size}项已完成"
        group.items.size == 1 -> group.items.first().doseText
        else -> null
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.width(172.dp)
    ) {
        Text(
            firstLine,
            color = theme.text,
            style = if (group != null && group.items.size == 1 && !completed) {
                MaterialTheme.typography.titleLarge
            } else {
                MaterialTheme.typography.headlineSmall
            },
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (showDetailHint) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "点击",
                    color = theme.text.copy(alpha = 0.82f),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Icon(
                    Icons.AutoMirrored.Filled.Assignment,
                    contentDescription = "查看详情",
                    tint = theme.text.copy(alpha = 0.82f),
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .size(23.dp)
                )
                Text(
                    "查看详情",
                    color = theme.text.copy(alpha = 0.82f),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
        } else {
            Text(
                secondLine.orEmpty(),
                color = theme.text.copy(alpha = 0.82f),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun GroupSummaryPanel(group: DoseGroup?, completed: Boolean, theme: VisualTheme) {
    if (group == null) {
        Surface(
            color = theme.glass,
            contentColor = theme.text,
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.50f)),
            shape = CircleShape,
            shadowElevation = 0.dp
        ) {
            Text(
                "暂无药方",
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 11.dp),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
        return
    }

    GlassPanel(theme) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                if (completed) "本组已服" else "待服药",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(group.time.format(TimeFormatter), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        group.items.forEach { item ->
            MedicineLine(item = item, theme = theme)
        }
    }
}

@Composable
private fun MedicineLine(item: PlannedIntake, theme: VisualTheme) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(item.medicineName, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            Text(item.doseText, style = MaterialTheme.typography.bodyMedium, color = theme.mutedText)
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            if (item.taken) {
                Icon(Icons.Default.CheckCircle, contentDescription = "已服", tint = theme.accentDeep, modifier = Modifier.size(18.dp))
            }
            Text(
                if (item.taken) "已服" else "未服",
                color = if (item.taken) theme.accentDeep else theme.mutedText,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ReminderGroupDetailDialog(
    group: DoseGroup,
    theme: VisualTheme,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.88f)
                .padding(horizontal = 6.dp),
            color = Color(0xFFFFFBEA).copy(alpha = 0.94f),
            contentColor = theme.text,
            shape = RoundedCornerShape(28.dp),
            border = BorderStroke(1.dp, Color(0xFFB8C58C).copy(alpha = 0.70f))
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            group.time.format(TimeFormatter),
                            color = theme.text,
                            fontFamily = FontFamily.Serif,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "${group.items.size}项药需要服用",
                            color = theme.mutedText,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "关闭", tint = theme.text)
                    }
                }

                HorizontalDivider(color = Color(0xFFB8C58C).copy(alpha = 0.46f))

                group.items.forEachIndexed { index, item ->
                    if (index > 0) {
                        HorizontalDivider(color = Color(0xFFB8C58C).copy(alpha = 0.28f))
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(42.dp),
                            color = Color(0xFFEAF4CE).copy(alpha = 0.78f),
                            contentColor = theme.text,
                            shape = CircleShape,
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.70f))
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    (index + 1).toString(),
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Serif
                                )
                            }
                        }
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Text(
                                item.medicineName,
                                color = theme.text,
                                fontFamily = FontFamily.Serif,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                "${item.mealTiming.label} · ${item.doseText}",
                                color = theme.mutedText,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        ReminderStatusPill(taken = item.taken || group.allTaken, theme = theme)
                    }
                }
            }
        }
    }
}

@Composable
private fun ReminderStatusPill(taken: Boolean, theme: VisualTheme) {
    val color = if (taken) theme.accentDeep else Color(0xFF9A6C18)
    Surface(
        color = if (taken) Color(0xFFEAF4CE).copy(alpha = 0.78f) else Color(0xFFFFF1C6).copy(alpha = 0.78f),
        contentColor = color,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.30f))
    ) {
        Text(
            if (taken) "已服" else "未服",
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}

@Composable
private fun MedicationCalendarContent(
    medicationRepository: MedicationRepository,
    theme: VisualTheme
) {
    val sounds = LocalAppSounds.current
    var visibleMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var calendarNow by remember { mutableStateOf(LocalDateTime.now()) }
    val statuses by remember(visibleMonth) {
        medicationRepository.observeMonthStatuses(visibleMonth)
    }.collectAsStateWithLifecycle(initialValue = emptyMap())
    val dueStatuses by remember(visibleMonth, calendarNow) {
        medicationRepository.observeMonthDueStatuses(visibleMonth, calendarNow)
    }.collectAsStateWithLifecycle(initialValue = emptyMap())
    val selectedGroups by remember(selectedDate) {
        medicationRepository.observeGroupsForDate(selectedDate)
    }.collectAsStateWithLifecycle(initialValue = emptyList())
    val cells = remember(visibleMonth) { calendarCells(visibleMonth) }

    LaunchedEffect(Unit) {
        while (true) {
            calendarNow = LocalDateTime.now()
            delay(60_000)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MonthHeader(
            month = visibleMonth,
            theme = theme,
            onPrevious = {
                sounds.play(SoundCue.SwitchLeaf)
                visibleMonth = visibleMonth.minusMonths(1)
            },
            onNext = {
                sounds.play(SoundCue.SwitchLeaf)
                visibleMonth = visibleMonth.plusMonths(1)
            }
        )

        Spacer(Modifier.height(18.dp))

        Row(Modifier.fillMaxWidth()) {
            WeekLabels.forEach { label ->
                Text(
                    text = label,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    color = theme.text.copy(alpha = 0.88f),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(Modifier.height(10.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            userScrollEnabled = false,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(9.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(286.dp)
        ) {
            items(cells) { cell ->
                CalendarDayCell(
                    cell = cell,
                    status = statuses[cell.date] ?: DayMedicationStatus.None,
                    dueStatus = dueStatuses[cell.date] ?: DueMedicationStatus.None,
                    selected = cell.date == selectedDate,
                    theme = theme,
                    currentMonth = visibleMonth,
                    onClick = {
                        sounds.play(SoundCue.OpenBloom)
                        selectedDate = cell.date
                    }
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        CalendarDetailPanel(
            selectedDate = selectedDate,
            groups = selectedGroups,
            theme = theme
        )
    }
}

@Composable
private fun MonthHeader(
    month: YearMonth,
    theme: VisualTheme,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        IconButton(onClick = onPrevious) {
            Icon(Icons.Default.ChevronLeft, contentDescription = "上个月", tint = theme.text)
        }
        Text(
            month.format(MonthFormatter),
            color = theme.text,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 28.dp)
        )
        IconButton(onClick = onNext) {
            Icon(Icons.Default.ChevronRight, contentDescription = "下个月", tint = theme.text)
        }
    }
}

@Composable
private fun CalendarDayCell(
    cell: CalendarCell,
    status: DayMedicationStatus,
    dueStatus: DueMedicationStatus,
    selected: Boolean,
    theme: VisualTheme,
    currentMonth: YearMonth,
    onClick: () -> Unit
) {
    val inMonth = YearMonth.from(cell.date) == currentMonth
    val alpha = if (inMonth) 1f else 0.34f
    val color = when (status) {
        DayMedicationStatus.Complete -> Color(0xFFDFF2D7).copy(alpha = 0.76f)
        DayMedicationStatus.Partial -> Color(0xFFFFF3BF).copy(alpha = 0.78f)
        DayMedicationStatus.Pending -> theme.glass
        DayMedicationStatus.None -> Color.White.copy(alpha = 0.34f)
    }
    val borderColor = when {
        selected -> theme.accentDeep
        status == DayMedicationStatus.Complete -> theme.accentDeep.copy(alpha = 0.72f)
        else -> Color.White.copy(alpha = 0.34f)
    }
    val capsuleSize = 22.dp
    val capsuleHalfSize = 11.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.92f)
            .alpha(alpha)
            .softClickable(onClick = onClick)
    ) {
        Surface(
            modifier = Modifier
                .matchParentSize(),
            color = color,
            contentColor = theme.text,
            shape = MaterialTheme.shapes.small,
            border = BorderStroke(width = if (selected) 2.dp else 1.dp, color = borderColor)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    cell.date.dayOfMonth.toString(),
                    color = theme.text,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        if (inMonth && dueStatus != DueMedicationStatus.None) {
            val statusIcon = if (dueStatus == DueMedicationStatus.Complete) {
                R.drawable.capsule_status_green
            } else {
                R.drawable.capsule_status_gray
            }
            Image(
                painter = painterResource(statusIcon),
                contentDescription = if (dueStatus == DueMedicationStatus.Complete) "当日已到点计划已全服" else "当日已到点计划有未服",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = capsuleHalfSize, y = -capsuleHalfSize)
                    .size(capsuleSize)
            )
        }
    }
}

@Composable
private fun CalendarDetailPanel(selectedDate: LocalDate, groups: List<DoseGroup>, theme: VisualTheme) {
    val sounds = LocalAppSounds.current
    val items = remember(groups) { groups.flatMap { it.items }.sortedWith(compareBy<PlannedIntake> { it.medicineName }.thenBy { it.time }) }
    val medicines = remember(items) {
        items.groupBy { "${it.prescriptionId}_${it.medicineName}" }
            .map { (key, intakes) -> CalendarMedicineGroup(key, intakes.first().medicineName, intakes.sortedBy { it.time }) }
            .sortedBy { it.name }
    }
    var selectedKey by remember(selectedDate, medicines.joinToString { it.key }) {
        mutableStateOf(medicines.firstOrNull()?.key)
    }
    val selectedMedicine = medicines.firstOrNull { it.key == selectedKey } ?: medicines.firstOrNull()
    val total = items.size
    val taken = items.count { it.taken }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFFFFBEA).copy(alpha = 0.88f),
        contentColor = theme.text,
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(1.dp, Color(0xFFB8C58C).copy(alpha = 0.62f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(11.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(36.dp),
                    shape = CircleShape,
                    color = Color(0xFFFFFDF0).copy(alpha = 0.80f),
                    border = BorderStroke(1.dp, theme.text.copy(alpha = 0.20f))
                ) {
                    Image(
                        painter = painterResource(R.drawable.detail_leaf_mark),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.padding(7.dp)
                    )
                }
                Text(
                    "${selectedDate.monthValue}月${selectedDate.dayOfMonth}日",
                    color = theme.text,
                    fontFamily = FontFamily.Serif,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                CalendarProgressPill(taken = taken, total = total, theme = theme)
            }

            HorizontalDivider(
                color = Color(0xFFB8C58C).copy(alpha = 0.48f),
                thickness = 1.dp
            )

            if (medicines.isEmpty()) {
                CalendarEmptyDetail(theme)
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    medicines.forEach { medicine ->
                        CalendarMedicineChip(
                            medicine = medicine,
                            selected = medicine.key == selectedMedicine?.key,
                            theme = theme,
                            onClick = {
                                sounds.play(SoundCue.SelectDrop)
                                selectedKey = medicine.key
                            }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.detail_leaf_mark),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        "点击药方切换下方详情",
                        color = theme.mutedText,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Image(
                        painter = painterResource(R.drawable.detail_leaf_mark),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }

                selectedMedicine?.let { medicine ->
                    CalendarMedicineDetailCard(medicine = medicine, theme = theme)
                }
            }
        }
    }
}

@Composable
private fun CalendarProgressPill(taken: Int, total: Int, theme: VisualTheme) {
    val text = if (total == 0) "无计划" else "已完成 $taken/$total"
    val shape = RoundedCornerShape(22.dp)
    Box(
        modifier = Modifier
            .selectedSoftPill(shape)
            .border(1.dp, Color.White.copy(alpha = 0.70f), shape)
            .padding(horizontal = 13.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text,
            color = theme.text,
            fontFamily = FontFamily.Serif,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}

@Composable
private fun CalendarMedicineChip(
    medicine: CalendarMedicineGroup,
    selected: Boolean,
    theme: VisualTheme,
    onClick: () -> Unit
) {
    val allTaken = medicine.items.all { it.taken }
    val statusText = if (allTaken) "已服" else "未服"
    val shape = RoundedCornerShape(22.dp)
    Box(
        modifier = Modifier
            .height(50.dp)
            .defaultMinSize(minWidth = 58.dp)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .height(42.dp)
                .defaultMinSize(minWidth = 58.dp)
                .then(if (selected) Modifier.shadow(2.dp, shape, clip = false) else Modifier)
                .selectedSoftPill(shape)
                .border(
                    width = if (selected) 2.dp else 1.dp,
                    color = if (selected) theme.accentDeep.copy(alpha = 0.60f) else Color.White.copy(alpha = 0.68f),
                    shape = shape
                )
                .softClickable(onClick = onClick)
                .padding(horizontal = 18.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                medicine.name,
                color = theme.text,
                fontFamily = FontFamily.Serif,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Text(
            statusText,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 7.dp, y = 0.dp)
                .background(
                    color = if (allTaken) Color(0xFFEAF4CE) else Color(0xFFFFF1C6),
                    shape = RoundedCornerShape(9.dp)
                )
                .border(
                    width = 1.dp,
                    color = if (allTaken) theme.accentDeep.copy(alpha = 0.28f) else Color(0xFFE0AA45).copy(alpha = 0.45f),
                    shape = RoundedCornerShape(9.dp)
                )
                .padding(horizontal = 5.dp, vertical = 1.dp),
            color = if (allTaken) theme.accentDeep else Color(0xFF9A6C18),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}

@Composable
private fun CalendarEmptyDetail(theme: VisualTheme) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFFFFDF0).copy(alpha = 0.70f),
        contentColor = theme.mutedText,
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color(0xFFB8C58C).copy(alpha = 0.40f))
    ) {
        Text(
            "当天没有服药计划",
            modifier = Modifier.padding(vertical = 30.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun CalendarMedicineDetailCard(medicine: CalendarMedicineGroup, theme: VisualTheme) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFFFFDF0).copy(alpha = 0.76f),
        contentColor = theme.text,
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color(0xFF8EAA68).copy(alpha = 0.70f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface(
                    modifier = Modifier.size(38.dp),
                    color = Color(0xFFEFF7D9).copy(alpha = 0.82f),
                    shape = CircleShape,
                    border = BorderStroke(1.dp, Color(0xFFB8C58C).copy(alpha = 0.55f))
                ) {
                    Image(
                        painter = painterResource(R.drawable.detail_leaf_mark),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.padding(7.dp)
                    )
                }
                Text(
                    medicine.name,
                    color = theme.text,
                    fontFamily = FontFamily.Serif,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            HorizontalDivider(color = Color(0xFFB8C58C).copy(alpha = 0.48f))

            CalendarDetailHeaderRow(theme)
            medicine.items.forEach { item ->
                CalendarIntakeRow(item = item, theme = theme)
            }
        }
    }
}

@Composable
private fun CalendarDetailHeaderRow(theme: VisualTheme) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CalendarHeaderText("时间", theme, Modifier.weight(1.08f))
        CalendarHeaderText("剂量", theme, Modifier.weight(0.92f))
        CalendarHeaderText("与进食关系", theme, Modifier.weight(1.28f))
        CalendarHeaderText("状态", theme, Modifier.weight(1.00f))
    }
}

@Composable
private fun CalendarHeaderText(text: String, theme: VisualTheme, modifier: Modifier) {
    Text(
        text,
        modifier = modifier,
        color = theme.text.copy(alpha = 0.88f),
        textAlign = TextAlign.Center,
        fontFamily = FontFamily.Serif,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun CalendarIntakeRow(item: PlannedIntake, theme: VisualTheme) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFFFFBED).copy(alpha = 0.68f),
        contentColor = theme.text,
        shape = RoundedCornerShape(15.dp),
        border = BorderStroke(1.dp, Color(0xFFE1D9A9).copy(alpha = 0.72f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .weight(1.08f)
                    .padding(vertical = 7.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.detail_clock_icon),
                    contentDescription = null,
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    item.time.format(TimeFormatter),
                    color = Color(0xFF171717),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp),
                    maxLines = 1
                )
            }
            Text(
                item.doseText,
                modifier = Modifier.weight(0.92f),
                color = Color(0xFF171717),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                item.mealTiming.label,
                modifier = Modifier.weight(1.28f),
                color = Color(0xFF171717),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            CalendarTakenPill(taken = item.taken, theme = theme, modifier = Modifier.weight(1.00f))
        }
    }
}

@Composable
private fun CalendarTakenPill(taken: Boolean, theme: VisualTheme, modifier: Modifier = Modifier) {
    val borderColor = if (taken) Color(0xFF7F985D) else Color(0xFFE0AA45)
    val textColor = if (taken) theme.text else Color(0xFF9A6C18)
    Surface(
        modifier = modifier.padding(end = 4.dp, top = 5.dp, bottom = 5.dp),
        color = Color(0xFFFFFDF0).copy(alpha = 0.78f),
        contentColor = textColor,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, borderColor.copy(alpha = 0.78f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                if (taken) Icons.Default.CheckCircle else Icons.Default.NotificationsNone,
                contentDescription = if (taken) "已服" else "待服",
                tint = if (taken) Color(0xFF4C8B45) else Color(0xFFA16B16),
                modifier = Modifier.size(12.dp)
            )
            Text(
                if (taken) "已服" else "待服",
                modifier = Modifier.padding(start = 4.dp),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
    }
}

private data class CalendarMedicineGroup(
    val key: String,
    val name: String,
    val items: List<PlannedIntake>
)

private fun dayStatusText(groups: List<DoseGroup>): String {
    if (groups.isEmpty()) return "无计划"
    val total = groups.sumOf { it.items.size }
    val taken = groups.sumOf { group -> group.items.count { it.taken } }
    return when {
        taken == total -> "全部已服"
        taken > 0 -> "部分已服"
        else -> "未服用"
    }
}

@Composable
private fun PrescriptionScreen(
    medicationRepository: MedicationRepository,
    alarmScheduler: AlarmScheduler,
    theme: VisualTheme
) {
    val sounds = LocalAppSounds.current
    val prescriptions by medicationRepository.observePrescriptions()
        .collectAsStateWithLifecycle(initialValue = emptyList())
    val scope = rememberCoroutineScope()

    var editingId by remember { mutableStateOf<Long?>(null) }
    var editorVisible by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf(LocalDate.now()) }
    var endDate by remember { mutableStateOf(LocalDate.now()) }
    var scheduleType by remember { mutableStateOf(ScheduleType.Daily) }
    var times by remember { mutableStateOf(listOf(PrescriptionTimeInput(LocalTime.of(8, 0), "1片", MealTiming.AfterMeal))) }
    var anchorDate by remember { mutableStateOf(LocalDate.now()) }
    var anchorDose by remember { mutableStateOf("2片") }
    var alternateDose by remember { mutableStateOf("2.5片") }
    var errorText by remember { mutableStateOf<String?>(null) }
    var deleteTarget by remember { mutableStateOf<PrescriptionWithTimes?>(null) }

    fun resetForm() {
        editingId = null
        name = ""
        startDate = LocalDate.now()
        endDate = LocalDate.now()
        scheduleType = ScheduleType.Daily
        times = listOf(PrescriptionTimeInput(LocalTime.of(8, 0), "1片", MealTiming.AfterMeal))
        anchorDate = LocalDate.now()
        anchorDose = "2片"
        alternateDose = "2.5片"
        errorText = null
    }

    fun loadForEdit(item: PrescriptionWithTimes) {
        val prescription = item.prescription
        editingId = prescription.id
        name = prescription.name
        startDate = LocalDate.parse(prescription.startDate)
        endDate = prescription.endDate?.let(LocalDate::parse) ?: startDate
        scheduleType = runCatching { ScheduleType.valueOf(prescription.scheduleType) }.getOrDefault(ScheduleType.Daily)
        anchorDate = prescription.anchorDate?.let(LocalDate::parse) ?: startDate
        anchorDose = prescription.anchorDoseText ?: "2片"
        alternateDose = prescription.alternateDoseText ?: "2.5片"
        times = item.times.map {
            PrescriptionTimeInput(LocalTime.parse(it.time), it.doseText, MealTiming.fromStored(it.mealTiming))
        }.ifEmpty { listOf(PrescriptionTimeInput(LocalTime.of(8, 0), anchorDose, MealTiming.AfterMeal)) }
        errorText = null
        editorVisible = true
    }

    fun saveCurrent() {
        val normalizedTimes = times
            .filter { it.doseText.isNotBlank() || scheduleType == ScheduleType.Alternating }
            .map { it.copy(doseText = if (scheduleType == ScheduleType.Alternating) anchorDose else it.doseText) }
        when {
            name.isBlank() -> errorText = "请填写药名。"
            normalizedTimes.isEmpty() -> errorText = "请至少添加一个服用时间。"
            endDate.isBefore(startDate) -> errorText = "结束日期不能早于起始日期。"
            scheduleType == ScheduleType.Alternating && (anchorDose.isBlank() || alternateDose.isBlank()) -> errorText = "请填写两个交替剂量。"
            else -> {
                sounds.play(SoundCue.SaveGlow)
                scope.launch {
                    medicationRepository.savePrescription(
                        PrescriptionInput(
                            id = editingId,
                            name = name,
                            startDate = startDate,
                            endDate = endDate,
                            enabled = true,
                            scheduleType = scheduleType,
                            times = normalizedTimes,
                            anchorDate = if (scheduleType == ScheduleType.Alternating) anchorDate else null,
                            anchorDoseText = if (scheduleType == ScheduleType.Alternating) anchorDose else null,
                            alternateDoseText = if (scheduleType == ScheduleType.Alternating) alternateDose else null
                        )
                    )
                    alarmScheduler.scheduleNext(medicationRepository)
                    editorVisible = false
                    resetForm()
                }
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            contentPadding = PaddingValues(top = 112.dp, bottom = 116.dp)
        ) {
            if (prescriptions.isEmpty()) {
                item {
                    EmptyPrescriptionCard(theme = theme)
                }
            } else {
                items(prescriptions, key = { it.prescription.id }) { item ->
                    PrescriptionCard(
                        item = item,
                        theme = theme,
                        onClick = {
                            sounds.play(SoundCue.OpenBloom)
                            loadForEdit(item)
                        },
                        onLongPressDelete = {
                            sounds.play(SoundCue.DeleteWarn)
                            deleteTarget = item
                        }
                    )
                }
            }
        }

        PrescriptionAddButton(
            onClick = {
                sounds.play(SoundCue.AddSeed)
                resetForm()
                editorVisible = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 28.dp, bottom = 30.dp)
        )
    }

    if (editorVisible) {
        PrescriptionEditorDialog(
            editing = editingId != null,
            theme = theme,
            name = name,
            onNameChange = { name = it },
            startDate = startDate,
            onStartDateChange = {
                startDate = it
                if (endDate.isBefore(it)) endDate = it
            },
            endDate = endDate,
            onEndDateChange = { endDate = it },
            scheduleType = scheduleType,
            onScheduleTypeChange = { scheduleType = it },
            times = times,
            onTimesChange = { times = it },
            anchorDate = anchorDate,
            onAnchorDateChange = { anchorDate = it },
            anchorDose = anchorDose,
            onAnchorDoseChange = { anchorDose = it },
            alternateDose = alternateDose,
            onAlternateDoseChange = { alternateDose = it },
            errorText = errorText,
            onDismiss = {
                sounds.play(SoundCue.CloseSoft)
                editorVisible = false
                resetForm()
            },
            onSave = { saveCurrent() }
        )
    }

    deleteTarget?.let { target ->
        DeletePrescriptionDialog(
            onDismiss = {
                sounds.play(SoundCue.CloseSoft)
                deleteTarget = null
            },
            onConfirm = {
                sounds.play(SoundCue.DeleteConfirm)
                scope.launch {
                    medicationRepository.deletePrescription(target.prescription)
                    alarmScheduler.scheduleNext(medicationRepository)
                    if (editingId == target.prescription.id) {
                        editorVisible = false
                        resetForm()
                    }
                    deleteTarget = null
                }
            }
        )
    }
}

@Composable
private fun EmptyPrescriptionCard(theme: VisualTheme) {
    val shape = RoundedCornerShape(30.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(184.dp)
            .shadow(
                elevation = 8.dp,
                shape = shape,
                ambientColor = Color(0xFF7D925A).copy(alpha = 0.16f),
                spotColor = Color(0xFF7D925A).copy(alpha = 0.20f)
            )
            .background(
                brush = Brush.linearGradient(
                    listOf(
                        Color(0xFFFFFDF0).copy(alpha = 0.84f),
                        Color(0xFFF9F5D3).copy(alpha = 0.76f),
                        Color(0xFFFFFDF0).copy(alpha = 0.88f)
                    )
                ),
                shape = shape
            )
            .border(1.3.dp, Color.White.copy(alpha = 0.92f), shape)
            .border(1.dp, Color(0xFFD8CE87).copy(alpha = 0.34f), shape)
    ) {
        Image(
            painter = painterResource(R.drawable.detail_leaf_mark),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 22.dp, y = 20.dp)
                .size(30.dp)
                .graphicsLayer { rotationZ = -18f }
                .alpha(0.82f)
        )
        Image(
            painter = painterResource(R.drawable.detail_leaf_mark),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-22).dp, y = (-18).dp)
                .size(34.dp)
                .graphicsLayer { rotationZ = 18f }
                .alpha(0.70f)
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier.size(46.dp),
                color = Color(0xFFEAF4CE).copy(alpha = 0.76f),
                contentColor = theme.text,
                shape = CircleShape,
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.72f)),
                shadowElevation = 2.dp
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Assignment,
                    contentDescription = null,
                    tint = theme.text,
                    modifier = Modifier.padding(10.dp)
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(
                "还没有药方",
                color = Color(0xFF2E6F3F),
                fontFamily = FontFamily.Serif,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Image(
                painter = painterResource(R.drawable.detail_title_divider),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .padding(top = 2.dp, bottom = 6.dp)
                    .width(126.dp)
                    .alpha(0.72f)
            )
            Text(
                "点击右下角加号新建",
                color = theme.text.copy(alpha = 0.84f),
                fontFamily = FontFamily.Serif,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun DeletePrescriptionDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.86f)
                .aspectRatio(1036f / 819f)
        ) {
            Image(
                painter = painterResource(R.drawable.delete_dialog_bg),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )
            BoxWithConstraints(Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .offset(x = maxWidth * 0.12f, y = maxHeight * 0.72f)
                        .size(width = maxWidth * 0.33f, height = maxHeight * 0.16f)
                        .softClickable(onClick = onDismiss)
                )
                Box(
                    modifier = Modifier
                        .offset(x = maxWidth * 0.52f, y = maxHeight * 0.72f)
                        .size(width = maxWidth * 0.36f, height = maxHeight * 0.16f)
                        .softClickable(onClick = onConfirm)
                )
            }
        }
    }
}

@Composable
private fun EditorSection(
    title: String,
    theme: VisualTheme,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFFFFDF0).copy(alpha = 0.58f),
        contentColor = theme.text,
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.72f)),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                title,
                color = Color(0xFF315B34),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            content()
        }
    }
}

@Composable
private fun PrescriptionEditorDialog(
    editing: Boolean,
    theme: VisualTheme,
    name: String,
    onNameChange: (String) -> Unit,
    startDate: LocalDate,
    onStartDateChange: (LocalDate) -> Unit,
    endDate: LocalDate,
    onEndDateChange: (LocalDate) -> Unit,
    scheduleType: ScheduleType,
    onScheduleTypeChange: (ScheduleType) -> Unit,
    times: List<PrescriptionTimeInput>,
    onTimesChange: (List<PrescriptionTimeInput>) -> Unit,
    anchorDate: LocalDate,
    onAnchorDateChange: (LocalDate) -> Unit,
    anchorDose: String,
    onAnchorDoseChange: (String) -> Unit,
    alternateDose: String,
    onAlternateDoseChange: (String) -> Unit,
    errorText: String?,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    val sounds = LocalAppSounds.current
    var datePickerTarget by remember { mutableStateOf<PrescriptionDateTarget?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        val dialogShape = RoundedCornerShape(34.dp)
        Surface(
            modifier = Modifier
                .padding(horizontal = 26.dp, vertical = 24.dp)
                .fillMaxWidth()
                .aspectRatio(941f / 1672f),
            color = Color.Transparent,
            contentColor = theme.text,
            shape = dialogShape,
            shadowElevation = 0.dp
        ) {
            Box {
                Image(
                    painter = painterResource(R.drawable.detail_editor_bg),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.fillMaxSize()
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp, vertical = 14.dp)
                ) {
                    DetailHeader(
                        editing = editing,
                        theme = theme,
                        onBack = onDismiss
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(top = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(11.dp)
                    ) {
                        DetailSectionTitle("药名", theme)
                        DetailNameField(
                            name = name,
                            theme = theme,
                            onNameChange = onNameChange
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.Bottom) {
                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                                DetailDateSectionTitle("开始日期", theme)
                                DetailDateButton(
                                    dateText = startDate.format(IsoDateFormatter),
                                    theme = theme,
                                    onClick = {
                                        sounds.play(SoundCue.SelectDrop)
                                        datePickerTarget = PrescriptionDateTarget.Start
                                    }
                                )
                            }
                            Text(
                                "~",
                                color = theme.text,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .width(12.dp)
                                    .height(38.dp)
                                    .offset(y = 4.dp),
                                textAlign = TextAlign.Center
                            )
                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                                DetailDateSectionTitle("结束日期", theme)
                                DetailDateButton(
                                    dateText = endDate.format(IsoDateFormatter),
                                    theme = theme,
                                    onClick = {
                                        sounds.play(SoundCue.SelectDrop)
                                        datePickerTarget = PrescriptionDateTarget.End
                                    }
                                )
                            }
                        }

                        DetailSectionTitle("服用方式", theme)
                        DetailSchedulePanel(
                            scheduleType = scheduleType,
                            anchorDate = anchorDate,
                            anchorDose = anchorDose,
                            alternateDose = alternateDose,
                            theme = theme,
                            onScheduleTypeChange = onScheduleTypeChange,
                            onAnchorDateChange = onAnchorDateChange,
                            onAnchorDoseChange = onAnchorDoseChange,
                            onAlternateDoseChange = onAlternateDoseChange
                        )

                        DetailSectionTitle("服药计划", theme)
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 10.dp),
                            color = theme.text.copy(alpha = 0.16f),
                            thickness = 0.7.dp
                        )
                        DetailPlanHeader(theme)
                        times.forEachIndexed { index, timeInput ->
                            DetailPlanRow(
                                index = index,
                                input = timeInput,
                                alternating = scheduleType == ScheduleType.Alternating,
                                theme = theme,
                                onChange = { updated ->
                                    onTimesChange(times.toMutableList().also { it[index] = updated })
                                },
                                onDelete = {
                                    onTimesChange(
                                        times.toMutableList().also { it.removeAt(index) }
                                            .ifEmpty {
                                                listOf(
                                                    PrescriptionTimeInput(
                                                        LocalTime.of(8, 0),
                                                        if (scheduleType == ScheduleType.Alternating) anchorDose else "1片",
                                                        MealTiming.AfterMeal
                                                    )
                                                )
                                            }
                                    )
                                }
                            )
                        }
                        errorText?.let {
                            Text(it, color = Color(0xFF9F352F), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.height(48.dp))
                    }

                    DetailAddSlotButton(
                        theme = theme,
                        onClick = {
                            sounds.play(SoundCue.AddSeed)
                            onTimesChange(
                                times + PrescriptionTimeInput(
                                    LocalTime.of(8, 0),
                                    if (scheduleType == ScheduleType.Alternating) anchorDose else "1片",
                                    MealTiming.AfterMeal
                                )
                            )
                        }
                    )

                    DetailSaveButton(
                        editing = editing,
                        theme = theme,
                        onSave = onSave,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }

    datePickerTarget?.let { target ->
        FloralDatePickerDialog(
            title = if (target == PrescriptionDateTarget.Start) "选择开始日期" else "选择结束日期",
            initialDate = if (target == PrescriptionDateTarget.Start) startDate else endDate,
            theme = theme,
            onDismiss = {
                sounds.play(SoundCue.CloseSoft)
                datePickerTarget = null
            },
            onConfirm = { pickedDate ->
                sounds.play(SoundCue.SelectDrop)
                if (target == PrescriptionDateTarget.Start) {
                    onStartDateChange(pickedDate)
                } else {
                    onEndDateChange(pickedDate)
                }
                datePickerTarget = null
            }
        )
    }
}

private enum class PrescriptionDateTarget {
    Start,
    End
}

@Composable
private fun FloralDatePickerDialog(
    title: String,
    initialDate: LocalDate,
    theme: VisualTheme,
    onDismiss: () -> Unit,
    onConfirm: (LocalDate) -> Unit
) {
    val currentYear = remember { LocalDate.now().year }
    val years = remember(initialDate) {
        val startYear = minOf(initialDate.year, currentYear - 20)
        val endYear = maxOf(initialDate.year, currentYear + 30)
        (startYear..endYear).toList()
    }
    val months = remember { (1..12).toList() }
    var year by remember(initialDate) { mutableIntStateOf(initialDate.year) }
    var month by remember(initialDate) { mutableIntStateOf(initialDate.monthValue) }
    var day by remember(initialDate) { mutableIntStateOf(initialDate.dayOfMonth) }
    val maxDay = remember(year, month) { YearMonth.of(year, month).lengthOfMonth() }
    val days = remember(maxDay) { (1..maxDay).toList() }
    val sounds = LocalAppSounds.current

    FloralPickerFrame(
        title = title,
        theme = theme,
        onDismiss = onDismiss,
        onConfirm = { onConfirm(LocalDate.of(year, month, day.coerceAtMost(maxDay))) }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FloralDropdownSelect(
                label = "年",
                value = "${year}年",
                options = years.map { it to "${it}年" },
                theme = theme,
                modifier = Modifier.weight(1.25f),
                onSelected = { selectedYear ->
                    sounds.play(SoundCue.SelectDrop)
                    year = selectedYear
                    day = day.coerceAtMost(YearMonth.of(year, month).lengthOfMonth())
                }
            )
            FloralDropdownSelect(
                label = "月",
                value = "${month}月",
                options = months.map { it to "${it}月" },
                theme = theme,
                modifier = Modifier.weight(0.88f),
                onSelected = { selectedMonth ->
                    sounds.play(SoundCue.SelectDrop)
                    month = selectedMonth
                    day = day.coerceAtMost(YearMonth.of(year, month).lengthOfMonth())
                }
            )
            FloralDropdownSelect(
                label = "日",
                value = "${day}日",
                options = days.map { it to "${it}日" },
                theme = theme,
                modifier = Modifier.weight(0.88f),
                onSelected = { selectedDay ->
                    sounds.play(SoundCue.SelectDrop)
                    day = selectedDay
                }
            )
        }
    }
}

@Composable
private fun FloralTimePickerDialog(
    title: String,
    initialTime: LocalTime,
    theme: VisualTheme,
    onDismiss: () -> Unit,
    onConfirm: (LocalTime) -> Unit
) {
    var hour by remember(initialTime) { mutableIntStateOf(initialTime.hour) }
    var minute by remember(initialTime) { mutableIntStateOf(initialTime.minute) }
    val hours = remember { (0..23).toList() }
    val minutes = remember { (0..59).toList() }
    val sounds = LocalAppSounds.current

    FloralPickerFrame(
        title = title,
        theme = theme,
        onDismiss = onDismiss,
        onConfirm = { onConfirm(LocalTime.of(hour, minute)) }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FloralDropdownSelect(
                label = "小时",
                value = "%02d".format(hour),
                options = hours.map { it to "%02d".format(it) },
                theme = theme,
                modifier = Modifier.weight(1f),
                onSelected = { selectedHour ->
                    sounds.play(SoundCue.SelectDrop)
                    hour = selectedHour
                }
            )
            Text(
                ":",
                color = theme.text,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            FloralDropdownSelect(
                label = "分钟",
                value = "%02d".format(minute),
                options = minutes.map { it to "%02d".format(it) },
                theme = theme,
                modifier = Modifier.weight(1f),
                onSelected = { selectedMinute ->
                    sounds.play(SoundCue.SelectDrop)
                    minute = selectedMinute
                }
            )
        }
    }
}

@Composable
private fun <T> FloralDropdownSelect(
    label: String,
    value: String,
    options: List<Pair<T, String>>,
    theme: VisualTheme,
    modifier: Modifier = Modifier,
    onSelected: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            label,
            color = theme.mutedText,
            fontFamily = FontFamily.Serif,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp)
        )
        Box {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clickable { expanded = true },
                color = Color(0xFFFFFDF0).copy(alpha = 0.68f),
                contentColor = theme.text,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, theme.text.copy(alpha = 0.18f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        value,
                        color = theme.text,
                        fontFamily = FontFamily.Serif,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Text("▾", color = theme.mutedText, fontWeight = FontWeight.Bold)
                }
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .background(Color(0xFFFFFBEA))
                    .heightIn(max = 228.dp)
            ) {
                options.forEach { (rawValue, text) ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text,
                                color = theme.text,
                                fontFamily = FontFamily.Serif,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        onClick = {
                            expanded = false
                            onSelected(rawValue)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun FloralPickerFrame(
    title: String,
    theme: VisualTheme,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .widthIn(max = 430.dp)
                .fillMaxWidth(0.84f),
            color = Color(0xFFFFFBEA).copy(alpha = 0.96f),
            contentColor = theme.text,
            shape = RoundedCornerShape(30.dp),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.76f)),
            shadowElevation = 8.dp
        ) {
            Box {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        LeafMark(theme, modifier = Modifier.size(20.dp))
                        Text(
                            title,
                            color = theme.text,
                            fontFamily = FontFamily.Serif,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        LeafMark(theme, modifier = Modifier.size(20.dp))
                    }
                    HorizontalDivider(color = theme.text.copy(alpha = 0.14f), thickness = 0.7.dp)
                    content()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        FloralPickerButton(
                            text = "取消",
                            primary = false,
                            theme = theme,
                            modifier = Modifier.weight(1f),
                            onClick = onDismiss
                        )
                        FloralPickerButton(
                            text = "确定",
                            primary = true,
                            theme = theme,
                            modifier = Modifier.weight(1f),
                            onClick = onConfirm
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FloralPickerButton(
    text: String,
    primary: Boolean,
    theme: VisualTheme,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(22.dp)
    Box(
        modifier = modifier
            .height(44.dp)
            .then(
                if (primary) {
                    Modifier.selectedSoftPill(shape)
                } else {
                    Modifier.background(Color(0xFFFFFDF0).copy(alpha = 0.70f), shape)
                }
            )
            .border(1.dp, Color.White.copy(alpha = 0.74f), shape)
            .softClickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text,
            color = theme.text,
            fontFamily = FontFamily.Serif,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}

@Composable
private fun DetailCornerDecor(theme: VisualTheme) {
    Canvas(Modifier.fillMaxSize()) {
        val leaf = theme.accent.copy(alpha = 0.13f)
        val flower = Color.White.copy(alpha = 0.44f)
        repeat(7) { index ->
            drawCircle(
                color = flower,
                radius = 18f + index * 2f,
                center = Offset(size.width - 68f - index * 28f, 58f + index * 17f)
            )
        }
        repeat(8) { index ->
            drawOval(
                color = leaf,
                topLeft = Offset(size.width - 174f + index * 16f, 112f + index * 18f),
                size = androidx.compose.ui.geometry.Size(48f, 22f)
            )
        }
        repeat(5) { index ->
            drawOval(
                color = leaf.copy(alpha = 0.10f),
                topLeft = Offset(size.width - 124f + index * 20f, size.height - 160f + index * 12f),
                size = androidx.compose.ui.geometry.Size(52f, 20f)
            )
        }
    }
}

@Composable
private fun DetailHeader(
    editing: Boolean,
    theme: VisualTheme,
    onBack: () -> Unit
) {
    Box(Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(42.dp)
                .softClickable(onClick = onBack),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.detail_back_button),
                contentDescription = "返回",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        }

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                LeafMark(theme)
                Text(
                    if (editing) "编辑药方" else "新增药方",
                    color = Color(0xFF214B29),
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
                LeafMark(theme)
            }
            Image(
                painter = painterResource(R.drawable.detail_title_divider),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.width(124.dp)
            )
        }
    }
}

@Composable
private fun DetailSectionTitle(title: String, theme: VisualTheme) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LeafMark(theme)
        Text(
            title,
            color = Color(0xFF214B29),
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleSmall
        )
    }
}

@Composable
private fun LeafMark(theme: VisualTheme, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.detail_leaf_mark),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = modifier.size(24.dp)
    )
}

@Composable
private fun DetailFieldLabel(text: String, theme: VisualTheme) {
    Text(
        text,
        color = theme.text,
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.bodyLarge
    )
}

@Composable
private fun DetailDateSectionTitle(title: String, theme: VisualTheme) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.detail_clock_icon),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(24.dp)
        )
        Text(
            title,
            color = Color(0xFF214B29),
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleSmall
        )
    }
}

@Composable
private fun DetailNameField(
    name: String,
    theme: VisualTheme,
    onNameChange: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp),
        color = Color(0xFFFFFBE8).copy(alpha = 0.48f),
        contentColor = theme.text,
        border = BorderStroke(1.dp, theme.text.copy(alpha = 0.20f)),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 18.dp, end = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = name,
                onValueChange = onNameChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                textStyle = MaterialTheme.typography.titleMedium.copy(
                    color = theme.text,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                ),
                decorationBox = { innerTextField ->
                    if (name.isBlank()) {
                        Text(
                            "请输入药名",
                            color = theme.mutedText.copy(alpha = 0.70f),
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1
                        )
                    }
                    innerTextField()
                }
            )
            Icon(Icons.Default.Edit, contentDescription = null, tint = theme.mutedText, modifier = Modifier.size(24.dp))
        }
    }
}

@Composable
private fun DetailDateButton(
    dateText: String,
    theme: VisualTheme,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(38.dp),
        shape = RoundedCornerShape(13.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color(0xFFFFFBE8).copy(alpha = 0.48f),
            contentColor = theme.text
        ),
        border = BorderStroke(1.dp, theme.text.copy(alpha = 0.22f)),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(13.dp))
        Spacer(Modifier.width(3.dp))
        Text(
            dateText,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        Text("▾", color = theme.mutedText, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun DetailSmallChip(
    text: String,
    selected: Boolean,
    theme: VisualTheme,
    modifier: Modifier = Modifier,
    danger: Boolean = false,
    onClick: () -> Unit
) {
    val selectedColor = if (danger) Color(0xFFD99C86) else Color(0xFFDDEBB5)
    Surface(
        modifier = modifier
            .height(30.dp)
            .softClickable(onClick = onClick),
        color = if (selected) selectedColor.copy(alpha = 0.88f) else Color(0xFFFFFBE8).copy(alpha = 0.50f),
        contentColor = if (danger && selected) Color(0xFF813728) else theme.text,
        shape = RoundedCornerShape(17.dp),
        border = BorderStroke(1.dp, theme.text.copy(alpha = 0.18f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (selected) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(15.dp))
            }
            Text(text, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun DetailSchedulePanel(
    scheduleType: ScheduleType,
    anchorDate: LocalDate,
    anchorDose: String,
    alternateDose: String,
    theme: VisualTheme,
    onScheduleTypeChange: (ScheduleType) -> Unit,
    onAnchorDateChange: (LocalDate) -> Unit,
    onAnchorDoseChange: (String) -> Unit,
    onAlternateDoseChange: (String) -> Unit
) {
    val sounds = LocalAppSounds.current
    var anchorPickerOpen by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFFFFDF0).copy(alpha = 0.58f),
        contentColor = theme.text,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, theme.text.copy(alpha = 0.16f))
    ) {
        Column(Modifier.padding(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DetailModeButton(
                    text = "每日N次",
                    selected = scheduleType == ScheduleType.Daily,
                    theme = theme,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        sounds.play(SoundCue.SelectDrop)
                        onScheduleTypeChange(ScheduleType.Daily)
                    }
                )
                DetailModeButton(
                    text = "隔日交替",
                    selected = scheduleType == ScheduleType.Alternating,
                    theme = theme,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        sounds.play(SoundCue.SelectDrop)
                        onScheduleTypeChange(ScheduleType.Alternating)
                    }
                )
            }
            if (scheduleType == ScheduleType.Alternating) {
                HorizontalDivider(color = theme.text.copy(alpha = 0.10f))
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    DetailDateButton(
                        dateText = "锚点 ${anchorDate.format(IsoDateFormatter)}",
                        theme = theme,
                        onClick = {
                            sounds.play(SoundCue.SelectDrop)
                            anchorPickerOpen = true
                        }
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        DetailLabeledCompactTextField(
                            value = anchorDose,
                            label = "锚点剂量",
                            theme = theme,
                            modifier = Modifier.weight(1f),
                            onValueChange = onAnchorDoseChange
                        )
                        DetailLabeledCompactTextField(
                            value = alternateDose,
                            label = "交替剂量",
                            theme = theme,
                            modifier = Modifier.weight(1f),
                            onValueChange = onAlternateDoseChange
                        )
                    }
                }
            }
        }
    }

    if (anchorPickerOpen) {
        FloralDatePickerDialog(
            title = "选择锚点日期",
            initialDate = anchorDate,
            theme = theme,
            onDismiss = {
                sounds.play(SoundCue.CloseSoft)
                anchorPickerOpen = false
            },
            onConfirm = { pickedDate ->
                sounds.play(SoundCue.SelectDrop)
                onAnchorDateChange(pickedDate)
                anchorPickerOpen = false
            }
        )
    }
}

@Composable
private fun DetailModeButton(
    text: String,
    selected: Boolean,
    theme: VisualTheme,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(14.dp)
    Surface(
        modifier = modifier
            .height(40.dp)
            .then(if (selected) Modifier.shadow(3.dp, shape, clip = false) else Modifier)
            .softClickable(onClick = onClick),
        color = Color.Transparent,
        contentColor = theme.text,
        shape = shape,
        border = BorderStroke(1.dp, if (selected) Color.White.copy(alpha = 0.70f) else Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(if (selected) Modifier.selectedSoftPill(shape) else Modifier),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (selected) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(7.dp))
                }
                Text(text, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

private fun Modifier.selectedSoftPill(shape: RoundedCornerShape): Modifier = this
    .background(
        Brush.verticalGradient(
            listOf(
                Color(0xFFF7F8DA).copy(alpha = 0.96f),
                Color(0xFFE6F1B9).copy(alpha = 0.98f),
                Color(0xFFD7E99C).copy(alpha = 0.95f)
            )
        ),
        shape
    )
    .border(1.dp, Color(0xFFFFFFFF).copy(alpha = 0.62f), shape)
    .drawBehind {
        drawRoundRect(
            color = Color.White.copy(alpha = 0.32f),
            size = size.copy(height = size.height * 0.42f),
            cornerRadius = CornerRadius(14.dp.toPx(), 14.dp.toPx())
        )
    }

private fun Modifier.softClickable(enabled: Boolean = true, onClick: () -> Unit): Modifier = composed {
    clickable(
        enabled = enabled,
        indication = null,
        interactionSource = remember { MutableInteractionSource() },
        onClick = onClick
    )
}

@Composable
private fun DetailPlanHeader(theme: VisualTheme) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DetailPlanHeaderCell(
            iconRes = R.drawable.detail_clock_icon,
            text = "时间",
            theme = theme,
            modifier = Modifier.weight(0.98f)
        )
        DetailPlanHeaderCell(
            iconRes = R.drawable.detail_dose_icon,
            text = "剂量",
            theme = theme,
            modifier = Modifier.weight(0.76f)
        )
        DetailPlanHeaderCell(
            iconRes = R.drawable.detail_meal_icon,
            text = "与进食关系",
            theme = theme,
            modifier = Modifier.weight(1.82f)
        )
        Spacer(Modifier.width(14.dp))
    }
}

@Composable
private fun DetailPlanHeaderCell(
    iconRes: Int,
    text: String,
    theme: VisualTheme,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(14.dp)
        )
        Spacer(Modifier.width(3.dp))
        Text(
            text,
            color = theme.mutedText,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun DetailPlanRow(
    index: Int,
    input: PrescriptionTimeInput,
    alternating: Boolean,
    theme: VisualTheme,
    onChange: (PrescriptionTimeInput) -> Unit,
    onDelete: () -> Unit
) {
    val sounds = LocalAppSounds.current
    var timePickerOpen by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(
            onClick = {
                sounds.play(SoundCue.SelectDrop)
                timePickerOpen = true
            },
            modifier = Modifier
                .weight(0.98f)
                .height(42.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color(0xFFFFFBE8).copy(alpha = 0.62f),
                contentColor = theme.text
            ),
            border = BorderStroke(1.dp, theme.text.copy(alpha = 0.18f)),
            contentPadding = PaddingValues(horizontal = 2.dp)
        ) {
            Text(input.time.format(TimeFormatter), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, maxLines = 1)
            Spacer(Modifier.width(1.dp))
            Text("▾", color = theme.mutedText, style = MaterialTheme.typography.labelSmall)
        }

        if (alternating) {
            Surface(
                modifier = Modifier
                    .weight(0.76f)
                    .height(42.dp),
                color = Color(0xFFFFFBE8).copy(alpha = 0.62f),
                contentColor = theme.text,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, theme.text.copy(alpha = 0.18f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("交替", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                }
            }
        } else {
            DetailCompactTextField(
                value = input.doseText,
                label = "",
                theme = theme,
                modifier = Modifier.weight(0.76f),
                onValueChange = { onChange(input.copy(doseText = it)) }
            )
        }

        Surface(
            modifier = Modifier
                .weight(1.82f)
                .height(42.dp),
            color = Color(0xFFFFFBE8).copy(alpha = 0.56f),
            contentColor = theme.text,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, theme.text.copy(alpha = 0.14f))
        ) {
            Row(Modifier.padding(2.dp), horizontalArrangement = Arrangement.spacedBy(1.dp)) {
                MealTiming.entries.forEach { mealTiming ->
                    MealChipButton(
                        text = mealTiming.label,
                        selected = input.mealTiming == mealTiming,
                        theme = theme,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            sounds.play(SoundCue.SelectDrop)
                            onChange(input.copy(mealTiming = mealTiming))
                        }
                    )
                }
            }
        }

        IconButton(
            onClick = {
                sounds.play(SoundCue.CloseSoft)
                onDelete()
            },
            modifier = Modifier
                .width(14.dp)
                .height(42.dp)
        ) {
            Icon(Icons.Default.Delete, contentDescription = "删除第${index + 1}个时间", tint = theme.mutedText, modifier = Modifier.size(13.dp))
        }
    }

    if (timePickerOpen) {
        FloralTimePickerDialog(
            title = "选择服药时间",
            initialTime = input.time,
            theme = theme,
            onDismiss = {
                sounds.play(SoundCue.CloseSoft)
                timePickerOpen = false
            },
            onConfirm = { pickedTime ->
                sounds.play(SoundCue.SelectDrop)
                onChange(input.copy(time = pickedTime))
                timePickerOpen = false
            }
        )
    }
}

@Composable
private fun MealChipButton(
    text: String,
    selected: Boolean,
    theme: VisualTheme,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(10.dp)
    Surface(
        modifier = modifier
            .fillMaxSize()
            .padding(1.dp)
            .then(if (selected) Modifier.shadow(2.dp, shape, clip = false) else Modifier)
            .softClickable(onClick = onClick),
        color = Color.Transparent,
        contentColor = theme.text,
        shape = shape,
        border = BorderStroke(1.dp, if (selected) Color.White.copy(alpha = 0.68f) else Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(if (selected) Modifier.selectedSoftPill(shape) else Modifier),
            contentAlignment = Alignment.Center
        ) {
            Text(text, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, maxLines = 1)
        }
    }
}

@Composable
private fun DetailLabeledCompactTextField(
    value: String,
    label: String,
    theme: VisualTheme,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            label,
            color = theme.mutedText,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1
        )
        DetailCompactTextField(
            value = value,
            label = "",
            theme = theme,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = onValueChange
        )
    }
}

@Composable
private fun DetailCompactTextField(
    value: String,
    label: String,
    theme: VisualTheme,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit
) {
    if (label.isBlank()) {
        Surface(
            modifier = modifier.height(42.dp),
            color = Color(0xFFFFFBE8).copy(alpha = 0.62f),
            contentColor = theme.text,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, theme.text.copy(alpha = 0.18f))
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 4.dp, vertical = 11.dp),
                singleLine = true,
                textStyle = MaterialTheme.typography.labelSmall.copy(
                    color = theme.text,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            )
        }
        return
    }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.height(42.dp),
        label = { Text(label) },
        singleLine = true,
        textStyle = MaterialTheme.typography.bodySmall.copy(
            color = theme.text,
            fontWeight = FontWeight.Bold
        ),
        colors = editorTextFieldColors(theme),
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
private fun DetailAddSlotButton(theme: VisualTheme, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp)
            .padding(horizontal = 22.dp)
            .drawBehind {
                val strokeWidth = 1.25.dp.toPx()
                drawRoundRect(
                    color = theme.accent.copy(alpha = 0.58f),
                    style = Stroke(
                        width = strokeWidth,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10.dp.toPx(), 7.dp.toPx()))
                    ),
                    cornerRadius = CornerRadius(18.dp.toPx(), 18.dp.toPx())
                )
            }
            .softClickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Add, contentDescription = null, tint = theme.text, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text(
                "添加时段",
                color = theme.text,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun DetailSaveButton(
    editing: Boolean,
    theme: VisualTheme,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(62.dp)
            .softClickable(onClick = onSave),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.detail_save_button),
            contentDescription = if (editing) "保存药方" else "创建药方",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun PrescriptionCard(
    item: PrescriptionWithTimes,
    theme: VisualTheme,
    onClick: () -> Unit,
    onLongPressDelete: () -> Unit
) {
    val prescription = item.prescription
    val mainText = Color(0xFF1F4A29)
    val muted = Color(0xFF5E794B)
    val timeText = item.times.timeSummary()
    val doseText = if (prescription.scheduleType == ScheduleType.Alternating.name) {
        "${prescription.anchorDoseText ?: "-"}/${prescription.alternateDoseText ?: "-"}"
    } else {
        item.times.doseSummary()
    }
    val startText = "起：${formatCardDate(prescription.startDate)}"
    val endText = "至：${prescription.endDate?.let(::formatCardDate) ?: formatCardDate(prescription.startDate)}"

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1365f / 680f)
            .pointerInput(prescription.id) {
                detectTapGestures(
                    onPress = {
                        coroutineScope {
                            var longPressed = false
                            val job = launch {
                                delay(2000)
                                longPressed = true
                                onLongPressDelete()
                            }
                            val released = tryAwaitRelease()
                            job.cancel()
                            if (released && !longPressed) {
                                onClick()
                            }
                        }
                    }
                )
            }
    ) {
        Image(
            painter = painterResource(R.drawable.card_bg_clean),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
        Text(
            prescription.name,
            color = mainText,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .offset(x = maxWidth * 0.085f, y = maxHeight * 0.38f)
                .width(maxWidth * 0.31f)
        )
        CardDateText(
            text = startText,
            color = muted,
            modifier = Modifier
                .offset(x = maxWidth * 0.090f, y = maxHeight * 0.61f)
                .width(maxWidth * 0.345f)
        )
        CardDateText(
            text = endText,
            color = muted,
            modifier = Modifier
                .offset(x = maxWidth * 0.090f, y = maxHeight * 0.72f)
                .width(maxWidth * 0.345f)
        )
        CardValueText(
            value = timeText.ifBlank { "-" },
            color = mainText,
            modifier = Modifier
                .offset(x = maxWidth * 0.59f, y = maxHeight * 0.32f)
                .width(maxWidth * 0.34f)
        )
        CardValueText(
            value = doseText.ifBlank { "-" },
            color = mainText,
            modifier = Modifier
                .offset(x = maxWidth * 0.59f, y = maxHeight * 0.65f)
                .width(maxWidth * 0.34f)
        )
    }
}

private fun List<com.ain.reminder.data.PrescriptionTimeEntity>.timeSummary(): String {
    if (isEmpty()) return "-"
    val sorted = sortedBy { it.time }
    if (sorted.size == 1) {
        val only = sorted.first()
        return "${MealTiming.fromStored(only.mealTiming).label} ${LocalTime.parse(only.time).format(TimeFormatter)}"
    }
    val periods = sorted
        .map { timePeriodLabel(LocalTime.parse(it.time)) }
        .distinct()
        .joinToString("")
    return "$periods${sorted.size}次"
}

private fun List<com.ain.reminder.data.PrescriptionTimeEntity>.doseSummary(): String {
    if (isEmpty()) return "-"
    val distinctDoses = map { it.doseText }.distinct()
    return distinctDoses.joinToString("/")
}

private fun timePeriodLabel(time: LocalTime): String = when {
    time.isBefore(LocalTime.of(11, 0)) -> "早"
    time.isBefore(LocalTime.of(15, 0)) -> "午"
    time.isBefore(LocalTime.of(21, 0)) -> "晚"
    else -> "睡"
}

private fun formatCardDate(date: String): String = date.replace("-", ".")

@Composable
private fun CardDateText(text: String, color: Color, modifier: Modifier = Modifier) {
    Text(
        text,
        color = color,
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.labelSmall,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        textAlign = TextAlign.Start,
        modifier = modifier
    )
}

@Composable
private fun CardValueText(value: String, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(Color(0xFFFAF6DB).copy(alpha = 0.70f), RoundedCornerShape(8.dp))
            .padding(horizontal = 5.dp, vertical = 1.dp)
    ) {
        Text(
            value,
            color = color,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun PrescriptionAddButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(112.dp)
            .softClickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.button_clean),
            contentDescription = "新增药方",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun DateButton(
    label: String,
    date: LocalDate,
    theme: VisualTheme,
    modifier: Modifier = Modifier.fillMaxWidth(),
    onDatePicked: (LocalDate) -> Unit
) {
    var pickerOpen by remember { mutableStateOf(false) }
    val sounds = LocalAppSounds.current
    OutlinedButton(
        onClick = {
            sounds.play(SoundCue.SelectDrop)
            pickerOpen = true
        },
        modifier = modifier,
        colors = ButtonDefaults.outlinedButtonColors(contentColor = theme.text),
        border = BorderStroke(1.dp, theme.text.copy(alpha = 0.28f))
    ) {
        Text("$label：${date.format(DateFormatter)}", maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
    if (pickerOpen) {
        FloralDatePickerDialog(
            title = label,
            initialDate = date,
            theme = theme,
            onDismiss = {
                sounds.play(SoundCue.CloseSoft)
                pickerOpen = false
            },
            onConfirm = { pickedDate ->
                sounds.play(SoundCue.SelectDrop)
                onDatePicked(pickedDate)
                pickerOpen = false
            }
        )
    }
}

@Composable
private fun TimeDoseRow(
    index: Int,
    input: PrescriptionTimeInput,
    alternating: Boolean,
    theme: VisualTheme,
    onChange: (PrescriptionTimeInput) -> Unit,
    onDelete: () -> Unit
) {
    val sounds = LocalAppSounds.current
    var timePickerOpen by remember { mutableStateOf(false) }
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFFFFAE3).copy(alpha = 0.62f),
        contentColor = theme.text,
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.72f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "第${index + 1}次",
                    color = Color(0xFF315B34),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDelete, modifier = Modifier.size(34.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "删除第${index + 1}个时间", tint = theme.text.copy(alpha = 0.76f))
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedButton(
                    onClick = {
                        sounds.play(SoundCue.SelectDrop)
                        timePickerOpen = true
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = theme.text),
                    border = BorderStroke(1.dp, theme.text.copy(alpha = 0.28f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(input.time.format(TimeFormatter), fontWeight = FontWeight.Bold)
                }
                if (alternating) {
                    Surface(
                        modifier = Modifier.weight(1f),
                        color = theme.accent.copy(alpha = 0.12f),
                        contentColor = theme.mutedText,
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Text(
                            "按交替剂量",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    OutlinedTextField(
                        value = input.doseText,
                        onValueChange = { onChange(input.copy(doseText = it)) },
                        modifier = Modifier.weight(1f),
                        label = { Text("剂量") },
                        singleLine = true
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                MealTiming.entries.forEach { mealTiming ->
                    FilterChip(
                        selected = input.mealTiming == mealTiming,
                        onClick = { onChange(input.copy(mealTiming = mealTiming)) },
                        label = { Text(mealTiming.label) },
                        colors = chipColors(theme),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
    if (timePickerOpen) {
        FloralTimePickerDialog(
            title = "选择服药时间",
            initialTime = input.time,
            theme = theme,
            onDismiss = {
                sounds.play(SoundCue.CloseSoft)
                timePickerOpen = false
            },
            onConfirm = { pickedTime ->
                sounds.play(SoundCue.SelectDrop)
                onChange(input.copy(time = pickedTime))
                timePickerOpen = false
            }
        )
    }
}

@Composable
private fun GlassPanel(theme: VisualTheme, content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = theme.glass,
        contentColor = theme.text,
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.54f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(13.dp),
            content = content
        )
    }
}

@Composable
private fun PanelTitle(icon: ImageVector, title: String, subtitle: String, theme: VisualTheme) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
        Surface(
            color = theme.accent.copy(alpha = 0.18f),
            contentColor = theme.text,
            shape = CircleShape
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.padding(10.dp).size(22.dp))
        }
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = theme.mutedText)
        }
    }
}

@Composable
private fun chipColors(theme: VisualTheme) = FilterChipDefaults.filterChipColors(
    selectedContainerColor = theme.accent.copy(alpha = 0.22f),
    selectedLabelColor = theme.text,
    labelColor = theme.mutedText
)

@Composable
private fun editorTextFieldColors(theme: VisualTheme) = OutlinedTextFieldDefaults.colors(
    focusedTextColor = theme.text,
    unfocusedTextColor = theme.text,
    focusedBorderColor = theme.accentDeep.copy(alpha = 0.48f),
    unfocusedBorderColor = theme.text.copy(alpha = 0.24f),
    focusedLabelColor = theme.accentDeep,
    unfocusedLabelColor = theme.mutedText,
    cursorColor = theme.accentDeep,
    focusedContainerColor = Color(0xFFFFFBE8).copy(alpha = 0.44f),
    unfocusedContainerColor = Color(0xFFFFFBE8).copy(alpha = 0.34f)
)

private data class CalendarCell(val date: LocalDate)

private fun calendarCells(month: YearMonth): List<CalendarCell> {
    val first = month.atDay(1)
    val sundayIndex = first.dayOfWeek.sundayBasedIndex()
    val start = first.minusDays(sundayIndex.toLong())
    return (0 until 42).map { CalendarCell(start.plusDays(it.toLong())) }
}

private fun DayOfWeek.sundayBasedIndex(): Int = when (this) {
    DayOfWeek.SUNDAY -> 0
    DayOfWeek.MONDAY -> 1
    DayOfWeek.TUESDAY -> 2
    DayOfWeek.WEDNESDAY -> 3
    DayOfWeek.THURSDAY -> 4
    DayOfWeek.FRIDAY -> 5
    DayOfWeek.SATURDAY -> 6
}

private fun themeForDate(): VisualTheme = VisualThemes.first()

