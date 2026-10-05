package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DetectionDao {
    @Query("SELECT * FROM riwayat_deteksi ORDER BY waktu DESC")
    fun getAllRecords(): Flow<List<DetectionRecord>>

    @Query("SELECT * FROM riwayat_deteksi WHERE id = :id LIMIT 1")
    fun getRecordById(id: Long): Flow<DetectionRecord?>

    @Query("SELECT * FROM riwayat_deteksi ORDER BY waktu DESC LIMIT 1")
    fun getLatestRecord(): Flow<DetectionRecord?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: DetectionRecord): Long

    @Query("UPDATE riwayat_deteksi SET catatan = :catatan WHERE id = :id")
    suspend fun updateNotes(id: Long, catatan: String)

    @Delete
    suspend fun deleteRecord(record: DetectionRecord)

    @Query("DELETE FROM riwayat_deteksi WHERE id = :id")
    suspend fun deleteRecordById(id: Long)

    @Query("DELETE FROM riwayat_deteksi")
    suspend fun deleteAllRecords()
}
