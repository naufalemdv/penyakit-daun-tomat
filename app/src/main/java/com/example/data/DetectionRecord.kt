package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "riwayat_deteksi")
data class DetectionRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val waktu: Long,                     // Epoch milliseconds
    val path_foto: String,               // Absolute path or URI of image
    val sumber: String,                  // "kamera" or "galeri"
    val kelas_prediksi: String,          // Key (e.g. "tomato_late_blight")
    val nama_kelas: String,              // Display name (e.g. "Busuk Daun (Late Blight)")
    val nama_ilmiah: String,             // Scientific name (e.g. "Phytophthora infestans")
    val keyakinan: Float,                // 0.0 to 1.0 (e.g. 0.72)
    val is_terinfeksi: Boolean,          // false if healthy, true if diseased
    val top3_json: String,               // JSON string of Top 3 candidates
    val luas_daun: Float,                // Leaf area mask proportion (e.g. 0.384)
    val status: String,                  // "yakin" or "tidak_yakin"
    val fitur_json: String,              // JSON string of 23 extracted features
    val catatan: String = "",            // User field notes
    val versi_model: String = "SVM-Colab-v1",
    val mask_path: String = ""           // Path to segmentation mask image
)
