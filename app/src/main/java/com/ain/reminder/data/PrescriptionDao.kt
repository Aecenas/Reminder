package com.ain.reminder.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface PrescriptionDao {
    @Query("SELECT * FROM prescriptions ORDER BY enabled DESC, startDate DESC, name ASC")
    fun observePrescriptions(): Flow<List<PrescriptionEntity>>

    @Query("SELECT * FROM prescriptions ORDER BY enabled DESC, startDate DESC, name ASC")
    suspend fun getPrescriptions(): List<PrescriptionEntity>

    @Query("SELECT * FROM prescription_times ORDER BY time ASC")
    fun observePrescriptionTimes(): Flow<List<PrescriptionTimeEntity>>

    @Query("SELECT * FROM prescription_times ORDER BY time ASC")
    suspend fun getPrescriptionTimes(): List<PrescriptionTimeEntity>

    @Query("SELECT * FROM intake_records WHERE date = :date")
    fun observeRecordsForDate(date: String): Flow<List<IntakeRecordEntity>>

    @Query("SELECT * FROM intake_records WHERE date = :date")
    suspend fun getRecordsForDate(date: String): List<IntakeRecordEntity>

    @Query("SELECT * FROM intake_records WHERE date BETWEEN :startDate AND :endDate")
    fun observeRecordsBetween(startDate: String, endDate: String): Flow<List<IntakeRecordEntity>>

    @Insert
    suspend fun insertPrescription(entity: PrescriptionEntity): Long

    @Update
    suspend fun updatePrescription(entity: PrescriptionEntity)

    @Delete
    suspend fun deletePrescription(entity: PrescriptionEntity)

    @Query("SELECT * FROM prescriptions WHERE id = :id")
    suspend fun getPrescription(id: Long): PrescriptionEntity?

    @Query("DELETE FROM prescription_times WHERE prescriptionId = :prescriptionId")
    suspend fun deleteTimesForPrescription(prescriptionId: Long)

    @Query("DELETE FROM intake_records WHERE prescriptionId = :prescriptionId")
    suspend fun deleteRecordsForPrescription(prescriptionId: Long)

    @Insert
    suspend fun insertTimes(times: List<PrescriptionTimeEntity>)

    @Upsert
    suspend fun upsertRecord(record: IntakeRecordEntity)
}
