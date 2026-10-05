package com.example.model

data class DiseaseInfo(
    val id: String,
    val displayName: String,
    val shortName: String,
    val scientificName: String,
    val isHealthy: Boolean,
    val fieldAction: String,
    val symptoms: String,
    val triggers: String,
    val handling: String,
    val reference: String
)

data class TopClassPrediction(
    val id: String,
    val displayName: String,
    val probability: Float,
    val isHealthy: Boolean
)

data class FeatureItem(
    val code: String,          // e.g. "F01"
    val group: String,         // "Kelompok A: 5 Tekstur GLCM", "Kelompok B: 9 Momen Warna RGB", "Kelompok C: 9 Momen Warna HSV"
    val name: String,          // "F01. Contrast"
    val value: Float,          // 0.4280f
    val formattedValue: String // "0.4280"
)

data class ExtractionResult(
    val features: List<FeatureItem>,
    val leafAreaRatio: Float,
    val maskBitmapBase64: String = "",
    val contrast: Float,
    val correlation: Float,
    val energy: Float,
    val homogeneity: Float,
    val entropy: Float,
    val meanR: Float,
    val stdR: Float,
    val skewR: Float,
    val meanG: Float,
    val stdG: Float,
    val skewG: Float,
    val meanB: Float,
    val stdB: Float,
    val skewB: Float,
    val meanH: Float,
    val stdH: Float,
    val skewH: Float,
    val meanS: Float,
    val stdS: Float,
    val skewS: Float,
    val meanV: Float,
    val stdV: Float,
    val skewV: Float
)

data class DiagnosisResult(
    val topClass: DiseaseInfo,
    val confidence: Float,
    val confidenceText: String,      // "Cukup Yakin", "Kemungkinan", "Tidak Yakin"
    val isUncertain: Boolean,
    val uncertainReason: String = "",
    val top3: List<TopClassPrediction>,
    val extraction: ExtractionResult,
    val modelVersion: String = "SVM-Colab-v1",
    val testAccuracy: String = "91.57%"
)
