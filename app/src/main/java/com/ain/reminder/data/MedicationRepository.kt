package com.ain.reminder.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth

data class PrescriptionInput(
    val id: Long? = null,
    val name: String,
    val startDate: LocalDate,
    val endDate: LocalDate?,
    val enabled: Boolean,
    val scheduleType: ScheduleType,
    val times: List<PrescriptionTimeInput>,
    val anchorDate: LocalDate?,
    val anchorDoseText: String?,
    val alternateDoseText: String?
)

data class PrescriptionTimeInput(
    val time: LocalTime,
    val doseText: String,
    val mealTiming: MealTiming = MealTiming.AfterMeal
)

data class PrescriptionWithTimes(
    val prescription: PrescriptionEntity,
    val times: List<PrescriptionTimeEntity>
)

data class PlannedIntake(
    val prescriptionId: Long,
    val medicineName: String,
    val date: LocalDate,
    val time: LocalTime,
    val doseText: String,
    val mealTiming: MealTiming,
    val taken: Boolean,
    val confirmedAtMillis: Long?
)

data class DoseGroup(
    val date: LocalDate,
    val time: LocalTime,
    val items: List<PlannedIntake>
) {
    val allTaken: Boolean = items.isNotEmpty() && items.all { it.taken }
    val partlyTaken: Boolean = items.any { it.taken } && !allTaken
    val key: String = "${date}_${time}"
}

enum class DayMedicationStatus {
    None,
    Pending,
    Partial,
    Complete
}

enum class DueMedicationStatus {
    None,
    Incomplete,
    Complete
}

class MedicationRepository(private val dao: PrescriptionDao) {
    fun observePrescriptions(): Flow<List<PrescriptionWithTimes>> =
        combine(dao.observePrescriptions(), dao.observePrescriptionTimes()) { prescriptions, times ->
            prescriptions.map { prescription ->
                PrescriptionWithTimes(
                    prescription = prescription,
                    times = times.filter { it.prescriptionId == prescription.id }
                )
            }
        }

    fun observeGroupsForDate(date: LocalDate): Flow<List<DoseGroup>> =
        combine(
            dao.observePrescriptions(),
            dao.observePrescriptionTimes(),
            dao.observeRecordsForDate(date.toString())
        ) { prescriptions, times, records ->
            groupsForDate(prescriptions, times, records, date)
        }

    fun observeMonthStatuses(month: YearMonth): Flow<Map<LocalDate, DayMedicationStatus>> =
        combine(
            dao.observePrescriptions(),
            dao.observePrescriptionTimes(),
            dao.observeRecordsBetween(month.atDay(1).toString(), month.atEndOfMonth().toString())
        ) { prescriptions, times, records ->
            (1..month.lengthOfMonth()).associate { day ->
                val date = month.atDay(day)
                val groups = groupsForDate(
                    prescriptions = prescriptions,
                    times = times,
                    records = records.filter { it.date == date.toString() },
                    date = date
                )
                date to dayStatus(groups)
            }
        }

    fun observeMonthDueStatuses(month: YearMonth, now: LocalDateTime): Flow<Map<LocalDate, DueMedicationStatus>> =
        combine(
            dao.observePrescriptions(),
            dao.observePrescriptionTimes(),
            dao.observeRecordsBetween(month.atDay(1).toString(), month.atEndOfMonth().toString())
        ) { prescriptions, times, records ->
            (1..month.lengthOfMonth()).associate { day ->
                val date = month.atDay(day)
                val groups = groupsForDate(
                    prescriptions = prescriptions,
                    times = times,
                    records = records.filter { it.date == date.toString() },
                    date = date
                )
                date to dueStatus(groups, now)
            }
        }

    suspend fun groupsForDate(date: LocalDate): List<DoseGroup> =
        groupsForDate(
            prescriptions = dao.getPrescriptions(),
            times = dao.getPrescriptionTimes(),
            records = dao.getRecordsForDate(date.toString()),
            date = date
        )

    suspend fun nextReminderGroup(from: java.time.ZonedDateTime = java.time.ZonedDateTime.now()): DoseGroup? {
        val prescriptions = dao.getPrescriptions()
        val times = dao.getPrescriptionTimes()
        for (offset in 0..365) {
            val date = from.toLocalDate().plusDays(offset.toLong())
            val groups = groupsForDate(
                prescriptions = prescriptions,
                times = times,
                records = dao.getRecordsForDate(date.toString()),
                date = date
            )
            groups.firstOrNull { group ->
                !group.allTaken && date.atTime(group.time).atZone(from.zone).isAfter(from)
            }?.let { return it }
        }
        return null
    }

    suspend fun confirmGroup(group: DoseGroup, confirmedAtMillis: Long = System.currentTimeMillis()) {
        group.items.forEach { item ->
            dao.upsertRecord(
                IntakeRecordEntity(
                    prescriptionId = item.prescriptionId,
                    date = item.date.toString(),
                    time = item.time.toString(),
                    medicineNameSnapshot = item.medicineName,
                    doseTextSnapshot = item.doseText,
                    taken = true,
                    confirmedAtMillis = confirmedAtMillis
                )
            )
        }
    }

    suspend fun savePrescription(input: PrescriptionInput): Long {
        val entity = PrescriptionEntity(
            id = input.id ?: 0,
            name = input.name.trim(),
            startDate = input.startDate.toString(),
            endDate = input.endDate?.toString(),
            enabled = input.enabled,
            scheduleType = input.scheduleType.name,
            anchorDate = input.anchorDate?.toString(),
            anchorDoseText = input.anchorDoseText?.trim()?.takeIf { it.isNotEmpty() },
            alternateDoseText = input.alternateDoseText?.trim()?.takeIf { it.isNotEmpty() }
        )
        val id = if (input.id == null) {
            dao.insertPrescription(entity)
        } else {
            dao.updatePrescription(entity)
            input.id
        }
        dao.deleteTimesForPrescription(id)
        dao.insertTimes(
            input.times
                .filter { it.doseText.isNotBlank() }
                .distinctBy { Triple(it.time, it.doseText.trim(), it.mealTiming) }
                .map {
                    PrescriptionTimeEntity(
                        prescriptionId = id,
                        time = it.time.toString(),
                        doseText = it.doseText.trim(),
                        mealTiming = it.mealTiming.name
                    )
                }
        )
        return id
    }

    suspend fun setPrescriptionEnabled(prescription: PrescriptionEntity, enabled: Boolean) {
        dao.updatePrescription(prescription.copy(enabled = enabled))
    }

    suspend fun deletePrescription(prescription: PrescriptionEntity) {
        dao.deleteRecordsForPrescription(prescription.id)
        dao.deletePrescription(prescription)
    }

    suspend fun exportBackupJson(): String {
        val prescriptions = dao.getPrescriptions()
        val times = dao.getPrescriptionTimes()
        val records = dao.getIntakeRecords()
        return JSONObject()
            .put("format", "reminder-backup")
            .put("version", 1)
            .put("exportedAtMillis", System.currentTimeMillis())
            .put("prescriptions", JSONArray().apply {
                prescriptions.forEach { item ->
                    put(
                        JSONObject()
                            .put("id", item.id)
                            .put("name", item.name)
                            .put("startDate", item.startDate)
                            .putNullable("endDate", item.endDate)
                            .put("enabled", item.enabled)
                            .put("scheduleType", item.scheduleType)
                            .putNullable("anchorDate", item.anchorDate)
                            .putNullable("anchorDoseText", item.anchorDoseText)
                            .putNullable("alternateDoseText", item.alternateDoseText)
                    )
                }
            })
            .put("times", JSONArray().apply {
                times.forEach { item ->
                    put(
                        JSONObject()
                            .put("id", item.id)
                            .put("prescriptionId", item.prescriptionId)
                            .put("time", item.time)
                            .put("doseText", item.doseText)
                            .put("mealTiming", item.mealTiming)
                    )
                }
            })
            .put("records", JSONArray().apply {
                records.forEach { item ->
                    put(
                        JSONObject()
                            .put("prescriptionId", item.prescriptionId)
                            .put("date", item.date)
                            .put("time", item.time)
                            .put("medicineNameSnapshot", item.medicineNameSnapshot)
                            .put("doseTextSnapshot", item.doseTextSnapshot)
                            .put("taken", item.taken)
                            .putNullable("confirmedAtMillis", item.confirmedAtMillis)
                    )
                }
            })
            .toString(2)
    }

    suspend fun importBackupJson(json: String) {
        val root = JSONObject(json)
        require(root.optString("format") == "reminder-backup") { "不是该吃药啦的备份文件。" }
        require(root.optInt("version") == 1) { "备份版本暂不支持。" }

        val prescriptions = root.getJSONArray("prescriptions").mapObjects { item ->
            PrescriptionEntity(
                id = item.getLong("id"),
                name = item.getString("name"),
                startDate = item.getString("startDate"),
                endDate = item.nullableString("endDate"),
                enabled = item.getBoolean("enabled"),
                scheduleType = item.getString("scheduleType"),
                anchorDate = item.nullableString("anchorDate"),
                anchorDoseText = item.nullableString("anchorDoseText"),
                alternateDoseText = item.nullableString("alternateDoseText")
            )
        }
        val prescriptionIds = prescriptions.map { it.id }.toSet()
        val times = root.getJSONArray("times").mapObjects { item ->
            PrescriptionTimeEntity(
                id = item.getLong("id"),
                prescriptionId = item.getLong("prescriptionId"),
                time = item.getString("time"),
                doseText = item.getString("doseText"),
                mealTiming = item.optString("mealTiming", MealTiming.AfterMeal.name)
            )
        }
        require(times.all { it.prescriptionId in prescriptionIds }) { "备份中的服药时间数据不完整。" }

        val records = root.optJSONArray("records")?.mapObjects { item ->
            IntakeRecordEntity(
                prescriptionId = item.getLong("prescriptionId"),
                date = item.getString("date"),
                time = item.getString("time"),
                medicineNameSnapshot = item.getString("medicineNameSnapshot"),
                doseTextSnapshot = item.getString("doseTextSnapshot"),
                taken = item.getBoolean("taken"),
                confirmedAtMillis = item.nullableLong("confirmedAtMillis")
            )
        } ?: emptyList()

        dao.replaceAllData(prescriptions, times, records)
    }

    private fun groupsForDate(
        prescriptions: List<PrescriptionEntity>,
        times: List<PrescriptionTimeEntity>,
        records: List<IntakeRecordEntity>,
        date: LocalDate
    ): List<DoseGroup> {
        val recordMap = records.associateBy { Triple(it.prescriptionId, it.date, it.time) }
        val planned = prescriptions
            .filter { it.enabled && it.appliesOn(date) }
            .flatMap { prescription ->
                times.filter { it.prescriptionId == prescription.id }.map { time ->
                    val record = recordMap[Triple(prescription.id, date.toString(), time.time)]
                    PlannedIntake(
                        prescriptionId = prescription.id,
                        medicineName = prescription.name,
                        date = date,
                        time = LocalTime.parse(time.time),
                        doseText = prescription.doseFor(date, time),
                        mealTiming = MealTiming.fromStored(time.mealTiming),
                        taken = record?.taken == true,
                        confirmedAtMillis = record?.confirmedAtMillis
                    )
                }
            }

        val plannedKeys = planned.map { Triple(it.prescriptionId, it.date.toString(), it.time.toString()) }.toSet()
        val historicalOnly = records
            .filter { Triple(it.prescriptionId, it.date, it.time) !in plannedKeys }
            .map {
                PlannedIntake(
                    prescriptionId = it.prescriptionId,
                    medicineName = it.medicineNameSnapshot,
                    date = LocalDate.parse(it.date),
                    time = LocalTime.parse(it.time),
                    doseText = it.doseTextSnapshot,
                    mealTiming = MealTiming.AfterMeal,
                    taken = it.taken,
                    confirmedAtMillis = it.confirmedAtMillis
                )
            }

        return (planned + historicalOnly)
            .groupBy { it.time }
            .map { (time, items) -> DoseGroup(date = date, time = time, items = items.sortedBy { it.medicineName }) }
            .sortedBy { it.time }
    }

    private fun dayStatus(groups: List<DoseGroup>): DayMedicationStatus {
        if (groups.isEmpty()) return DayMedicationStatus.None
        val total = groups.sumOf { it.items.size }
        val taken = groups.sumOf { group -> group.items.count { it.taken } }
        return when {
            taken == 0 -> DayMedicationStatus.Pending
            taken == total -> DayMedicationStatus.Complete
            else -> DayMedicationStatus.Partial
        }
    }

    private fun dueStatus(groups: List<DoseGroup>, now: LocalDateTime): DueMedicationStatus {
        val dueItems = groups
            .flatMap { it.items }
            .filter { !LocalDateTime.of(it.date, it.time).isAfter(now) }
        if (dueItems.isEmpty()) return DueMedicationStatus.None
        return if (dueItems.all { it.taken }) DueMedicationStatus.Complete else DueMedicationStatus.Incomplete
    }

    private fun PrescriptionEntity.appliesOn(date: LocalDate): Boolean {
        val start = LocalDate.parse(startDate)
        val end = endDate?.let(LocalDate::parse)
        return !date.isBefore(start) && (end == null || !date.isAfter(end))
    }

    private fun PrescriptionEntity.doseFor(date: LocalDate, time: PrescriptionTimeEntity): String {
        if (scheduleType != ScheduleType.Alternating.name) return time.doseText
        val anchor = anchorDate?.let(LocalDate::parse) ?: return time.doseText
        val first = anchorDoseText?.takeIf { it.isNotBlank() } ?: time.doseText
        val second = alternateDoseText?.takeIf { it.isNotBlank() } ?: time.doseText
        return ScheduleRules.alternatingDoseForDate(anchor, first, second, date)
    }

    private fun JSONObject.putNullable(name: String, value: Any?): JSONObject =
        put(name, value ?: JSONObject.NULL)

    private fun JSONObject.nullableString(name: String): String? =
        if (isNull(name)) null else getString(name)

    private fun JSONObject.nullableLong(name: String): Long? =
        if (isNull(name)) null else getLong(name)

    private fun <T> JSONArray.mapObjects(transform: (JSONObject) -> T): List<T> =
        (0 until length()).map { index -> transform(getJSONObject(index)) }
}
