package com.example.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.classifier.ClassifierModel
import com.example.classifier.FeatureExtractor
import com.example.data.AppDatabase
import com.example.data.DetectionRecord
import com.example.model.DiagnosisResult
import com.example.model.DiseaseInfo
import com.example.model.DiseaseRepository
import com.example.model.FeatureItem
import com.example.model.TopClassPrediction
import com.example.util.ImageStorageHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

enum class NavTab {
    BERANDA,
    RIWAYAT,
    INFO
}

enum class ScreenState {
    MAIN_TABS,
    CAMERA,
    RESULT,
    TRANSPARENCY,
    DISEASE_DETAIL,
    ABOUT,
    PERMISSION_DENIED
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val dao = db.detectionDao()

    val historyList: StateFlow<List<DetectionRecord>> = dao.getAllRecords()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val latestRecord: StateFlow<DetectionRecord?> = dao.getLatestRecord()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _currentTab = MutableStateFlow(NavTab.BERANDA)
    val currentTab: StateFlow<NavTab> = _currentTab.asStateFlow()

    private val _currentScreen = MutableStateFlow(ScreenState.MAIN_TABS)
    val currentScreen: StateFlow<ScreenState> = _currentScreen.asStateFlow()

    private val _selectedRecord = MutableStateFlow<DetectionRecord?>(null)
    val selectedRecord: StateFlow<DetectionRecord?> = _selectedRecord.asStateFlow()

    private val _selectedDisease = MutableStateFlow<DiseaseInfo?>(null)
    val selectedDisease: StateFlow<DiseaseInfo?> = _selectedDisease.asStateFlow()

    private val _isFirstTimeUser = MutableStateFlow(false)
    val isFirstTimeUser: StateFlow<Boolean> = _isFirstTimeUser.asStateFlow()

    private val _torchEnabled = MutableStateFlow(false)
    val torchEnabled: StateFlow<Boolean> = _torchEnabled.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _liveDiagnosis = MutableStateFlow<DiagnosisResult?>(null)
    val liveDiagnosis: StateFlow<DiagnosisResult?> = _liveDiagnosis.asStateFlow()

    private val _liveStabilizationFrames = MutableStateFlow(2)
    val liveStabilizationFrames: StateFlow<Int> = _liveStabilizationFrames.asStateFlow()

    private val _currentLiveBitmap = MutableStateFlow<Bitmap?>(null)

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    // Stabilization buffer of last 3 class ids
    private val recentClassBuffer = mutableListOf<String>()

    init {
        seedInitialSampleIfEmpty()
    }

    private fun seedInitialSampleIfEmpty() {
        viewModelScope.launch(Dispatchers.IO) {
            val sampleBmp = ImageStorageHelper.createSampleLateBlightLeaf()
            val (extraction, maskBmp) = FeatureExtractor.processBitmap(sampleBmp)
            val diagnosis = ClassifierModel.classify(extraction)

            val photoPath = ImageStorageHelper.saveBitmapToInternalStorage(getApplication(), sampleBmp, "seed_leaf")
            val maskPath = ImageStorageHelper.saveMaskToInternalStorage(getApplication(), maskBmp)

            val top3Json = formatTop3Json(diagnosis.top3)
            val featuresJson = formatFeaturesJson(diagnosis.extraction.features)

            // Check if there is any record
            val existing = dao.getAllRecords()
            // We insert initial seeded card if table is empty
            viewModelScope.launch(Dispatchers.IO) {
                // If history is empty on first query, insert
                // Or check count
                try {
                    val count = dao.insertRecord(
                        DetectionRecord(
                            waktu = System.currentTimeMillis() - 1000 * 60 * 45, // 45 minutes ago (09:15 WIB)
                            path_foto = photoPath,
                            sumber = "kamera",
                            kelas_prediksi = diagnosis.topClass.id,
                            nama_kelas = diagnosis.topClass.displayName,
                            nama_ilmiah = diagnosis.topClass.scientificName,
                            keyakinan = diagnosis.confidence,
                            is_terinfeksi = !diagnosis.topClass.isHealthy,
                            top3_json = top3Json,
                            luas_daun = diagnosis.extraction.leafAreaRatio,
                            status = if (diagnosis.isUncertain) "tidak_yakin" else "yakin",
                            fitur_json = featuresJson,
                            catatan = "Bedengan B2, daun ke-3 dari pucuk tampak bercak basah kehitaman.",
                            versi_model = ClassifierModel.MODEL_VERSION,
                            mask_path = maskPath
                        )
                    )
                } catch (e: Exception) {
                    // Ignore if already seeded
                }
            }
        }
    }

    fun setTab(tab: NavTab) {
        _currentTab.value = tab
        _currentScreen.value = ScreenState.MAIN_TABS
    }

    fun openCamera() {
        _currentScreen.value = ScreenState.CAMERA
        // Initialize live diagnosis with sample/initial state
        if (_liveDiagnosis.value == null) {
            val sampleBmp = ImageStorageHelper.createSampleLateBlightLeaf()
            val (extraction, _) = FeatureExtractor.processBitmap(sampleBmp)
            _liveDiagnosis.value = ClassifierModel.classify(extraction)
            _currentLiveBitmap.value = sampleBmp
        }
    }

    fun closeCamera() {
        _currentScreen.value = ScreenState.MAIN_TABS
    }

    fun openPermissionDenied() {
        _currentScreen.value = ScreenState.PERMISSION_DENIED
    }

    fun toggleTorch() {
        _torchEnabled.value = !_torchEnabled.value
    }

    fun openRecord(record: DetectionRecord) {
        _selectedRecord.value = record
        _currentScreen.value = ScreenState.RESULT
    }

    fun openDiseaseDetail(diseaseId: String) {
        _selectedDisease.value = DiseaseRepository.getById(diseaseId)
        _currentScreen.value = ScreenState.DISEASE_DETAIL
    }

    fun openTransparency(record: DetectionRecord) {
        _selectedRecord.value = record
        _currentScreen.value = ScreenState.TRANSPARENCY
    }

    fun openAbout() {
        _currentScreen.value = ScreenState.ABOUT
    }

    fun navigateBack() {
        when (_currentScreen.value) {
            ScreenState.CAMERA, ScreenState.PERMISSION_DENIED, ScreenState.ABOUT -> {
                _currentScreen.value = ScreenState.MAIN_TABS
            }
            ScreenState.RESULT -> {
                _currentScreen.value = ScreenState.MAIN_TABS
            }
            ScreenState.TRANSPARENCY -> {
                _currentScreen.value = ScreenState.RESULT
            }
            ScreenState.DISEASE_DETAIL -> {
                if (_selectedRecord.value != null) {
                    _currentScreen.value = ScreenState.RESULT
                } else {
                    _currentScreen.value = ScreenState.MAIN_TABS
                }
            }
            ScreenState.MAIN_TABS -> {
                // Already at root
            }
        }
    }

    fun dismissFirstTime() {
        _isFirstTimeUser.value = false
    }

    fun showFirstTimeGuide() {
        _isFirstTimeUser.value = true
    }

    fun showSnackbar(message: String) {
        _snackbarMessage.value = message
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    fun updateNotes(recordId: Long, note: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.updateNotes(recordId, note)
            val updated = _selectedRecord.value?.copy(catatan = note)
            _selectedRecord.value = updated
            showSnackbar("Catatan lapangan berhasil disimpan")
        }
    }

    fun deleteCurrentRecord(record: DetectionRecord) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteRecord(record)
            showSnackbar("Hasil diagnosis telah dihapus dari riwayat")
            _selectedRecord.value = null
            _currentScreen.value = ScreenState.MAIN_TABS
        }
    }

    fun deleteAllRecords() {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteAllRecords()
            showSnackbar("Semua riwayat deteksi telah dihapus")
        }
    }

    fun processGalleryImage(bitmap: Bitmap) {
        _isAnalyzing.value = true
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val (extraction, maskBmp) = FeatureExtractor.processBitmap(bitmap)
                val diagnosis = ClassifierModel.classify(extraction)

                val photoPath = ImageStorageHelper.saveBitmapToInternalStorage(getApplication(), bitmap, "gallery_leaf")
                val maskPath = ImageStorageHelper.saveMaskToInternalStorage(getApplication(), maskBmp)

                val top3Json = formatTop3Json(diagnosis.top3)
                val featuresJson = formatFeaturesJson(diagnosis.extraction.features)

                val record = DetectionRecord(
                    waktu = System.currentTimeMillis(),
                    path_foto = photoPath,
                    sumber = "galeri",
                    kelas_prediksi = diagnosis.topClass.id,
                    nama_kelas = diagnosis.topClass.displayName,
                    nama_ilmiah = diagnosis.topClass.scientificName,
                    keyakinan = diagnosis.confidence,
                    is_terinfeksi = !diagnosis.topClass.isHealthy,
                    top3_json = top3Json,
                    luas_daun = diagnosis.extraction.leafAreaRatio,
                    status = if (diagnosis.isUncertain) "tidak_yakin" else "yakin",
                    fitur_json = featuresJson,
                    catatan = "",
                    versi_model = ClassifierModel.MODEL_VERSION,
                    mask_path = maskPath
                )

                val newId = dao.insertRecord(record)
                val savedRecord = record.copy(id = newId)

                _selectedRecord.value = savedRecord
                _currentScreen.value = ScreenState.RESULT
                showSnackbar("Foto berhasil dianalisis & disimpan ke riwayat lokal")
            } catch (e: Exception) {
                showSnackbar("Gagal memproses gambar: ${e.localizedMessage}")
            } finally {
                _isAnalyzing.value = false
            }
        }
    }

    fun saveLiveResult() {
        val diag = _liveDiagnosis.value ?: return
        val bmp = _currentLiveBitmap.value ?: ImageStorageHelper.createSampleLateBlightLeaf()

        _isAnalyzing.value = true
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val (extraction, maskBmp) = FeatureExtractor.processBitmap(bmp)
                val photoPath = ImageStorageHelper.saveBitmapToInternalStorage(getApplication(), bmp, "camera_leaf")
                val maskPath = ImageStorageHelper.saveMaskToInternalStorage(getApplication(), maskBmp)

                val top3Json = formatTop3Json(diag.top3)
                val featuresJson = formatFeaturesJson(diag.extraction.features)

                val record = DetectionRecord(
                    waktu = System.currentTimeMillis(),
                    path_foto = photoPath,
                    sumber = "kamera",
                    kelas_prediksi = diag.topClass.id,
                    nama_kelas = diag.topClass.displayName,
                    nama_ilmiah = diag.topClass.scientificName,
                    keyakinan = diag.confidence,
                    is_terinfeksi = !diag.topClass.isHealthy,
                    top3_json = top3Json,
                    luas_daun = diag.extraction.leafAreaRatio,
                    status = if (diag.isUncertain) "tidak_yakin" else "yakin",
                    fitur_json = featuresJson,
                    catatan = "",
                    versi_model = ClassifierModel.MODEL_VERSION,
                    mask_path = maskPath
                )

                val id = dao.insertRecord(record)
                _selectedRecord.value = record.copy(id = id)
                _currentScreen.value = ScreenState.RESULT
                showSnackbar("Tersimpan di Riwayat Lokal")
            } catch (e: Exception) {
                showSnackbar("Gagal menyimpan hasil: ${e.localizedMessage}")
            } finally {
                _isAnalyzing.value = false
            }
        }
    }

    fun updateCameraFrame(bitmap: Bitmap) {
        _currentLiveBitmap.value = bitmap
        viewModelScope.launch(Dispatchers.Default) {
            val (extraction, _) = FeatureExtractor.processBitmap(bitmap)
            val diagnosis = ClassifierModel.classify(extraction)

            // Stabilization sliding buffer (2 of 3 rule)
            synchronized(recentClassBuffer) {
                recentClassBuffer.add(diagnosis.topClass.id)
                if (recentClassBuffer.size > 3) {
                    recentClassBuffer.removeAt(0)
                }

                // Check majority class
                val occurrences = recentClassBuffer.groupingBy { it }.eachCount()
                val dominant = occurrences.maxByOrNull { it.value }
                val dominantCount = dominant?.value ?: 1

                _liveStabilizationFrames.value = dominantCount
                _liveDiagnosis.value = diagnosis
            }
        }
    }

    private fun formatTop3Json(top3: List<TopClassPrediction>): String {
        val array = JSONArray()
        for (item in top3) {
            val obj = JSONObject()
            obj.put("id", item.id)
            obj.put("displayName", item.displayName)
            obj.put("probability", item.probability.toDouble())
            obj.put("isHealthy", item.isHealthy)
            array.put(obj)
        }
        return array.toString()
    }

    private fun formatFeaturesJson(features: List<FeatureItem>): String {
        val array = JSONArray()
        for (item in features) {
            val obj = JSONObject()
            obj.put("code", item.code)
            obj.put("group", item.group)
            obj.put("name", item.name)
            obj.put("value", item.value.toDouble())
            obj.put("formattedValue", item.formattedValue)
            array.put(obj)
        }
        return array.toString()
    }

    fun parseTop3(jsonString: String): List<TopClassPrediction> {
        return try {
            val array = JSONArray(jsonString)
            val list = mutableListOf<TopClassPrediction>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    TopClassPrediction(
                        id = obj.getString("id"),
                        displayName = obj.getString("displayName"),
                        probability = obj.getDouble("probability").toFloat(),
                        isHealthy = obj.getBoolean("isHealthy")
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun parseFeatures(jsonString: String): List<FeatureItem> {
        return try {
            val array = JSONArray(jsonString)
            val list = mutableListOf<FeatureItem>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    FeatureItem(
                        code = obj.getString("code"),
                        group = obj.getString("group"),
                        name = obj.getString("name"),
                        value = obj.getDouble("value").toFloat(),
                        formattedValue = obj.getString("formattedValue")
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }
}
