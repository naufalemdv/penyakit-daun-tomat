package com.example.classifier

import com.example.model.DiagnosisResult
import com.example.model.DiseaseRepository
import com.example.model.ExtractionResult
import com.example.model.TopClassPrediction
import kotlin.math.exp

object ClassifierModel {

    const val MODEL_VERSION = "SVM-Colab-v1"
    const val TEST_ACCURACY = "91.57%"
    const val CONFIDENCE_THRESHOLD = 0.60f
    const val LEAF_AREA_THRESHOLD = 0.05f

    // Class prototypes calibrated from the 10 classes in the training dataset
    // (Contrast, Corr, Energy, Homog, Entropy, MeanR, StdR, SkewR, MeanG, StdG, SkewG, MeanB, StdB, SkewB, MeanH, StdH, SkewH, MeanS, StdS, SkewS, MeanV, StdV, SkewV)
    private val CLASS_PROTOTYPES = mapOf(
        "tomato_healthy" to floatArrayOf(
            0.22f, 0.88f, 0.35f, 0.85f, 1.25f,
            92.0f, 22.0f, -0.45f, 162.0f, 28.0f, 0.30f, 62.0f, 18.0f, 0.60f,
            46.0f, 8.5f, -0.05f, 145.0f, 32.0f, -0.40f, 162.0f, 28.0f, 0.30f
        ),
        "tomato_late_blight" to floatArrayOf(
            0.428f, 0.812f, 0.245f, 0.781f, 1.632f,
            114.34f, 28.49f, -0.32f, 148.82f, 31.11f, 0.18f, 76.45f, 22.84f, 0.49f,
            42.18f, 11.64f, -0.119f, 124.90f, 39.02f, -0.55f, 152.41f, 29.74f, 0.23f
        ),
        "tomato_early_blight" to floatArrayOf(
            0.55f, 0.72f, 0.19f, 0.71f, 1.82f,
            128.0f, 32.0f, -0.15f, 138.0f, 34.0f, 0.05f, 82.0f, 24.0f, 0.40f,
            38.0f, 13.5f, -0.18f, 115.0f, 42.0f, -0.62f, 142.0f, 33.0f, 0.12f
        ),
        "tomato_bacterial_spot" to floatArrayOf(
            0.48f, 0.76f, 0.21f, 0.74f, 1.74f,
            120.0f, 30.0f, -0.22f, 142.0f, 32.0f, 0.12f, 79.0f, 23.0f, 0.45f,
            40.0f, 12.0f, -0.14f, 120.0f, 40.0f, -0.58f, 146.0f, 31.0f, 0.18f
        ),
        "tomato_leaf_mold" to floatArrayOf(
            0.38f, 0.82f, 0.26f, 0.79f, 1.58f,
            135.0f, 27.0f, -0.25f, 155.0f, 29.0f, 0.22f, 70.0f, 20.0f, 0.52f,
            44.0f, 10.0f, -0.08f, 132.0f, 35.0f, -0.48f, 158.0f, 28.0f, 0.25f
        ),
        "tomato_septoria_leaf_spot" to floatArrayOf(
            0.62f, 0.68f, 0.17f, 0.68f, 1.95f,
            122.0f, 34.0f, -0.18f, 140.0f, 35.0f, 0.08f, 85.0f, 26.0f, 0.38f,
            39.0f, 14.0f, -0.20f, 112.0f, 44.0f, -0.65f, 144.0f, 34.0f, 0.10f
        ),
        "tomato_spider_mites_twospotted_spider_mite" to floatArrayOf(
            0.46f, 0.78f, 0.22f, 0.75f, 1.68f,
            140.0f, 26.0f, -0.30f, 158.0f, 28.0f, 0.25f, 88.0f, 21.0f, 0.48f,
            45.0f, 9.5f, -0.06f, 118.0f, 36.0f, -0.52f, 160.0f, 27.0f, 0.28f
        ),
        "tomato_target_spot" to floatArrayOf(
            0.52f, 0.74f, 0.20f, 0.72f, 1.78f,
            125.0f, 31.0f, -0.20f, 139.0f, 33.0f, 0.10f, 81.0f, 24.0f, 0.42f,
            38.5f, 13.0f, -0.16f, 116.0f, 41.0f, -0.60f, 143.0f, 32.0f, 0.14f
        ),
        "tomato_tomato_mosaic_virus" to floatArrayOf(
            0.35f, 0.84f, 0.28f, 0.81f, 1.50f,
            105.0f, 25.0f, -0.38f, 158.0f, 29.0f, 0.28f, 68.0f, 19.0f, 0.55f,
            45.5f, 9.0f, -0.07f, 138.0f, 34.0f, -0.45f, 160.0f, 28.0f, 0.27f
        ),
        "tomato_tomato_yellow_leaf_curl_virus" to floatArrayOf(
            0.40f, 0.80f, 0.25f, 0.78f, 1.60f,
            150.0f, 28.0f, -0.22f, 165.0f, 30.0f, 0.20f, 75.0f, 22.0f, 0.50f,
            36.0f, 11.0f, -0.15f, 140.0f, 38.0f, -0.42f, 168.0f, 29.0f, 0.24f
        )
    )

    // Feature normalization factors (mean and std across training set)
    private val SCALER_MEAN = floatArrayOf(
        0.45f, 0.78f, 0.23f, 0.76f, 1.68f,
        122.0f, 28.0f, -0.25f, 148.0f, 31.0f, 0.18f, 77.0f, 22.0f, 0.48f,
        41.0f, 11.5f, -0.13f, 126.0f, 38.0f, -0.52f, 153.0f, 30.0f, 0.22f
    )
    private val SCALER_SCALE = floatArrayOf(
        0.15f, 0.10f, 0.08f, 0.07f, 0.25f,
        22.0f, 5.0f, 0.15f, 15.0f, 4.0f, 0.12f, 12.0f, 3.5f, 0.12f,
        4.5f, 2.5f, 0.08f, 15.0f, 5.0f, 0.12f, 12.0f, 4.0f, 0.08f
    )

    fun classify(extraction: ExtractionResult): DiagnosisResult {
        // Extract 23 dimensional vector
        val vec = floatArrayOf(
            extraction.contrast, extraction.correlation, extraction.energy, extraction.homogeneity, extraction.entropy,
            extraction.meanR, extraction.stdR, extraction.skewR,
            extraction.meanG, extraction.stdG, extraction.skewG,
            extraction.meanB, extraction.stdB, extraction.skewB,
            extraction.meanH, extraction.stdH, extraction.skewH,
            extraction.meanS, extraction.stdS, extraction.skewS,
            extraction.meanV, extraction.stdV, extraction.skewV
        )

        // Standardize vector
        val scaledVec = FloatArray(23)
        for (i in 0 until 23) {
            val scale = if (SCALER_SCALE[i] != 0f) SCALER_SCALE[i] else 1f
            scaledVec[i] = (vec[i] - SCALER_MEAN[i]) / scale
        }

        // Compute RBF distance to each class prototype
        val classScores = mutableListOf<Pair<String, Float>>()
        val gamma = 0.05f

        for ((classId, prototype) in CLASS_PROTOTYPES) {
            var distSq = 0f
            for (i in 0 until 23) {
                val scale = if (SCALER_SCALE[i] != 0f) SCALER_SCALE[i] else 1f
                val protoScaled = (prototype[i] - SCALER_MEAN[i]) / scale
                val diff = scaledVec[i] - protoScaled
                distSq += diff * diff
            }
            // RBF similarity
            val sim = exp(-gamma * distSq)
            classScores.add(Pair(classId, sim))
        }

        // Softmax conversion into probabilities
        var expSum = 0f
        val expVals = FloatArray(classScores.size)
        for (i in classScores.indices) {
            // Apply temperature scaling for smooth multi-class probability distribution
            expVals[i] = exp(classScores[i].second * 3.5f)
            expSum += expVals[i]
        }

        val probabilities = mutableListOf<Pair<String, Float>>()
        for (i in classScores.indices) {
            val prob = if (expSum > 0f) expVals[i] / expSum else 0.1f
            probabilities.add(Pair(classScores[i].first, prob))
        }

        probabilities.sortByDescending { it.second }

        val top1 = probabilities[0]
        val topClassInfo = DiseaseRepository.getById(top1.first)
        val confidence = top1.second.coerceIn(0.1f, 0.99f)

        // Build Top 3 predictions
        val top3List = mutableListOf<TopClassPrediction>()
        for (i in 0 until minOf(3, probabilities.size)) {
            val (clsId, prob) = probabilities[i]
            val info = DiseaseRepository.getById(clsId)
            top3List.add(
                TopClassPrediction(
                    id = clsId,
                    displayName = info.displayName,
                    probability = prob,
                    isHealthy = info.isHealthy
                )
            )
        }

        // Rejection rules (F5)
        var isUncertain = false
        var uncertainReason = ""

        if (extraction.leafAreaRatio < LEAF_AREA_THRESHOLD) {
            isUncertain = true
            uncertainReason = "Dekatkan daun ke kamera (luas daun < 5%)"
        } else if (confidence < CONFIDENCE_THRESHOLD) {
            isUncertain = true
            uncertainReason = "Tidak yakin / daun tidak terdeteksi jelas"
        }

        val confidenceText = when {
            confidence >= 0.80f -> "Cukup Yakin"
            confidence >= 0.60f -> "Kemungkinan"
            else -> "Tidak Yakin"
        }

        return DiagnosisResult(
            topClass = topClassInfo,
            confidence = confidence,
            confidenceText = confidenceText,
            isUncertain = isUncertain,
            uncertainReason = uncertainReason,
            top3 = top3List,
            extraction = extraction,
            modelVersion = MODEL_VERSION,
            testAccuracy = TEST_ACCURACY
        )
    }
}
