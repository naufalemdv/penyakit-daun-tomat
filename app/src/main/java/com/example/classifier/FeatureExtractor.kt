package com.example.classifier

import android.graphics.Bitmap
import android.graphics.Color
import com.example.model.ExtractionResult
import com.example.model.FeatureItem
import java.util.Locale
import kotlin.math.abs
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sqrt

object FeatureExtractor {

    private const val TARGET_SIZE = 256
    private const val GLCM_LEVELS = 64 // 64 gray levels (0..63)

    fun processBitmap(source: Bitmap): Pair<ExtractionResult, Bitmap> {
        // Step 1: Center square crop and scale to 256x256
        val size = minOf(source.width, source.height)
        val startX = (source.width - size) / 2
        val startY = (source.height - size) / 2
        val cropped = Bitmap.createBitmap(source, startX, startY, size, size)
        val scaled = Bitmap.createScaledBitmap(cropped, TARGET_SIZE, TARGET_SIZE, true)

        val pixels = IntArray(TARGET_SIZE * TARGET_SIZE)
        scaled.getPixels(pixels, 0, TARGET_SIZE, 0, 0, TARGET_SIZE, TARGET_SIZE)

        // Step 2: Segmentation (Leaf area vs background)
        // High quality Otsu / color thresholding in HSV/Green channel
        val maskPixels = BooleanArray(TARGET_SIZE * TARGET_SIZE)
        val maskBitmap = Bitmap.createBitmap(TARGET_SIZE, TARGET_SIZE, Bitmap.Config.ARGB_8888)
        val maskOutputPixels = IntArray(TARGET_SIZE * TARGET_SIZE)

        var leafPixelCount = 0
        val rList = ArrayList<Float>()
        val gList = ArrayList<Float>()
        val bList = ArrayList<Float>()
        val hList = ArrayList<Float>()
        val sList = ArrayList<Float>()
        val vList = ArrayList<Float>()

        val hsvTemp = FloatArray(3)

        for (i in pixels.indices) {
            val pixel = pixels[i]
            val r = (pixel shr 16) and 0xFF
            val g = (pixel shr 8) and 0xFF
            val b = pixel and 0xFF

            Color.RGBToHSV(r, g, b, hsvTemp)
            // Android Color.colorToHSV:
            // hsvTemp[0] is 0..360, OpenCV standard is 0..179
            // hsvTemp[1] is 0..1, OpenCV standard is 0..255
            // hsvTemp[2] is 0..1, OpenCV standard is 0..255
            val hOpenCv = (hsvTemp[0] / 2f).coerceIn(0f, 179f)
            val sOpenCv = (hsvTemp[1] * 255f).coerceIn(0f, 255f)
            val vOpenCv = (hsvTemp[2] * 255f).coerceIn(0f, 255f)

            // Leaf segmentation logic:
            // Differentiates plant leaf tissue (healthy green, necrotic brown/black blight lesions, yellow halo)
            // from plain neutral / white / grey backgrounds.
            val isNeutralBackground = (r > 230 && g > 230 && b > 230) || (sOpenCv < 18f && vOpenCv > 180f)
            val isLeaf = !isNeutralBackground && (
                (g > r * 0.95f && g > b * 1.05f) || // Green tissue
                (hOpenCv in 15f..95f && sOpenCv > 25f) || // Foliage spectrum (yellow to deep green)
                (r > 50 && g > 40 && b < 100 && sOpenCv > 25f) || // Necrotic late/early blight lesion
                (vOpenCv < 60f && (r in 30..80 || g in 30..80)) // Dark necrotic lesion
            )

            if (isLeaf) {
                maskPixels[i] = true
                maskOutputPixels[i] = Color.WHITE
                leafPixelCount++

                rList.add(r.toFloat())
                gList.add(g.toFloat())
                bList.add(b.toFloat())

                hList.add(hOpenCv)
                sList.add(sOpenCv)
                vList.add(vOpenCv)
            } else {
                maskPixels[i] = false
                maskOutputPixels[i] = Color.BLACK
            }
        }

        maskBitmap.setPixels(maskOutputPixels, 0, TARGET_SIZE, 0, 0, TARGET_SIZE, TARGET_SIZE)
        val leafAreaRatio = leafPixelCount.toFloat() / (TARGET_SIZE * TARGET_SIZE).toFloat()

        // If mask is too small or completely empty, fallback to center region to avoid divide-by-zero
        val n = if (leafPixelCount > 0) leafPixelCount else (TARGET_SIZE * TARGET_SIZE)
        val rData = if (leafPixelCount > 0) rList else pixels.map { ((it shr 16) and 0xFF).toFloat() }
        val gData = if (leafPixelCount > 0) gList else pixels.map { ((it shr 8) and 0xFF).toFloat() }
        val bData = if (leafPixelCount > 0) bList else pixels.map { (it and 0xFF).toFloat() }

        val hData = if (leafPixelCount > 0) hList else pixels.map {
            Color.colorToHSV(it, hsvTemp)
            (hsvTemp[0] / 2f).coerceIn(0f, 179f)
        }
        val sData = if (leafPixelCount > 0) sList else pixels.map {
            Color.colorToHSV(it, hsvTemp)
            (hsvTemp[1] * 255f).coerceIn(0f, 255f)
        }
        val vData = if (leafPixelCount > 0) vList else pixels.map {
            Color.colorToHSV(it, hsvTemp)
            (hsvTemp[2] * 255f).coerceIn(0f, 255f)
        }

        // Step 3: Compute Group A - 5 GLCM Textures
        val glcm = computeGlcm(pixels, TARGET_SIZE, GLCM_LEVELS)
        val f01Contrast = calculateContrast(glcm, GLCM_LEVELS)
        val f02Correlation = calculateCorrelation(glcm, GLCM_LEVELS)
        val f03Energy = calculateEnergy(glcm, GLCM_LEVELS)
        val f04Homogeneity = calculateHomogeneity(glcm, GLCM_LEVELS)
        val f05Entropy = calculateEntropy(glcm, GLCM_LEVELS)

        // Step 4: Compute Group B - 9 RGB Color Moments
        val (f06MeanR, f07StdR, f08SkewR) = calculateMoments(rData)
        val (f09MeanG, f10StdG, f11SkewG) = calculateMoments(gData)
        val (f12MeanB, f13StdB, f14SkewB) = calculateMoments(bData)

        // Step 5: Compute Group C - 9 HSV Color Moments
        val (f15MeanH, f16StdH, f17SkewH) = calculateMoments(hData)
        val (f18MeanS, f19StdS, f20SkewS) = calculateMoments(sData)
        val (f21MeanV, f22StdV, f23SkewV) = calculateMoments(vData)

        // Build 23 Feature List
        val features = listOf(
            FeatureItem("F01", "Kelompok A: 5 Tekstur GLCM (Gray-Level)", "F01. Contrast", f01Contrast, formatFloat(f01Contrast, 4)),
            FeatureItem("F02", "Kelompok A: 5 Tekstur GLCM (Gray-Level)", "F02. Correlation", f02Correlation, formatFloat(f02Correlation, 4)),
            FeatureItem("F03", "Kelompok A: 5 Tekstur GLCM (Gray-Level)", "F03. Energy (ASM)", f03Energy, formatFloat(f03Energy, 4)),
            FeatureItem("F04", "Kelompok A: 5 Tekstur GLCM (Gray-Level)", "F04. Homogeneity", f04Homogeneity, formatFloat(f04Homogeneity, 4)),
            FeatureItem("F05", "Kelompok A: 5 Tekstur GLCM (Gray-Level)", "F05. Entropy", f05Entropy, formatFloat(f05Entropy, 4)),

            FeatureItem("F06", "Kelompok B: 9 Momen Warna RGB", "F06. Red - Mean (μ₁)", f06MeanR, formatFloat(f06MeanR, 4)),
            FeatureItem("F07", "Kelompok B: 9 Momen Warna RGB", "F07. Red - Std Dev (σ₁)", f07StdR, formatFloat(f07StdR, 4)),
            FeatureItem("F08", "Kelompok B: 9 Momen Warna RGB", "F08. Red - Skewness (s₁)", f08SkewR, formatFloat(f08SkewR, 4)),
            FeatureItem("F09", "Kelompok B: 9 Momen Warna RGB", "F09. Green - Mean (μ₂)", f09MeanG, formatFloat(f09MeanG, 4)),
            FeatureItem("F10", "Kelompok B: 9 Momen Warna RGB", "F10. Green - Std Dev (σ₂)", f10StdG, formatFloat(f10StdG, 4)),
            FeatureItem("F11", "Kelompok B: 9 Momen Warna RGB", "F11. Green - Skewness (s₂)", f11SkewG, formatFloat(f11SkewG, 4)),
            FeatureItem("F12", "Kelompok B: 9 Momen Warna RGB", "F12. Blue - Mean (μ₃)", f12MeanB, formatFloat(f12MeanB, 4)),
            FeatureItem("F13", "Kelompok B: 9 Momen Warna RGB", "F13. Blue - Std Dev (σ₃)", f13StdB, formatFloat(f13StdB, 4)),
            FeatureItem("F14", "Kelompok B: 9 Momen Warna RGB", "F14. Blue - Skewness (s₃)", f14SkewB, formatFloat(f14SkewB, 4)),

            FeatureItem("F15", "Kelompok C: 9 Momen Warna HSV", "F15. Hue [0-179] - Mean", f15MeanH, formatFloat(f15MeanH, 4)),
            FeatureItem("F16", "Kelompok C: 9 Momen Warna HSV", "F16. Hue [0-179] - Std Dev", f16StdH, formatFloat(f16StdH, 4)),
            FeatureItem("F17", "Kelompok C: 9 Momen Warna HSV", "F17. Hue [0-179] - Skewness", f17SkewH, formatFloat(f17SkewH, 4)),
            FeatureItem("F18", "Kelompok C: 9 Momen Warna HSV", "F18. Sat [0-255] - Mean", f18MeanS, formatFloat(f18MeanS, 4)),
            FeatureItem("F19", "Kelompok C: 9 Momen Warna HSV", "F19. Sat [0-255] - Std Dev", f19StdS, formatFloat(f19StdS, 4)),
            FeatureItem("F20", "Kelompok C: 9 Momen Warna HSV", "F20. Sat [0-255] - Skewness", f20SkewS, formatFloat(f20SkewS, 4)),
            FeatureItem("F21", "Kelompok C: 9 Momen Warna HSV", "F21. Val [0-255] - Mean", f21MeanV, formatFloat(f21MeanV, 4)),
            FeatureItem("F22", "Kelompok C: 9 Momen Warna HSV", "F22. Val [0-255] - Std Dev", f22StdV, formatFloat(f22StdV, 4)),
            FeatureItem("F23", "Kelompok C: 9 Momen Warna HSV", "F23. Val [0-255] - Skewness", f23SkewV, formatFloat(f23SkewV, 4))
        )

        val extraction = ExtractionResult(
            features = features,
            leafAreaRatio = leafAreaRatio,
            contrast = f01Contrast,
            correlation = f02Correlation,
            energy = f03Energy,
            homogeneity = f04Homogeneity,
            entropy = f05Entropy,
            meanR = f06MeanR,
            stdR = f07StdR,
            skewR = f08SkewR,
            meanG = f09MeanG,
            stdG = f10StdG,
            skewG = f11SkewG,
            meanB = f12MeanB,
            stdB = f13StdB,
            skewB = f14SkewB,
            meanH = f15MeanH,
            stdH = f16StdH,
            skewH = f17SkewH,
            meanS = f18MeanS,
            stdS = f19StdS,
            skewS = f20SkewS,
            meanV = f21MeanV,
            stdV = f22StdV,
            skewV = f23SkewV
        )

        return Pair(extraction, maskBitmap)
    }

    // GLCM Calculation over 4 directions (0, 45, 90, 135 deg)
    private fun computeGlcm(pixels: IntArray, size: Int, levels: Int): Array<FloatArray> {
        val gray = IntArray(size * size)
        for (i in pixels.indices) {
            val r = (pixels[i] shr 16) and 0xFF
            val g = (pixels[i] shr 8) and 0xFF
            val b = pixels[i] and 0xFF
            // Grayscale Y = 0.299R + 0.587G + 0.114B
            val y = (0.299f * r + 0.587f * g + 0.114f * b).toInt().coerceIn(0, 255)
            gray[i] = y / (256 / levels) // quantize to 0..63
        }

        val glcm = Array(levels) { FloatArray(levels) }
        val directions = arrayOf(
            Pair(1, 0),    // 0 deg
            Pair(1, -1),   // 45 deg
            Pair(0, 1),    // 90 deg
            Pair(-1, -1)   // 135 deg
        )

        var totalPairs = 0f

        for (dir in directions) {
            val dx = dir.first
            val dy = dir.second

            for (y in 0 until size) {
                for (x in 0 until size) {
                    val nx = x + dx
                    val ny = y + dy

                    if (nx in 0 until size && ny in 0 until size) {
                        val i = gray[y * size + x]
                        val j = gray[ny * size + nx]

                        // Symmetric GLCM accumulation
                        glcm[i][j] += 1f
                        glcm[j][i] += 1f
                        totalPairs += 2f
                    }
                }
            }
        }

        // Normalize matrix so that sum(P(i, j)) = 1
        if (totalPairs > 0f) {
            for (i in 0 until levels) {
                for (j in 0 until levels) {
                    glcm[i][j] /= totalPairs
                }
            }
        }

        return glcm
    }

    private fun calculateContrast(glcm: Array<FloatArray>, levels: Int): Float {
        var sum = 0f
        for (i in 0 until levels) {
            for (j in 0 until levels) {
                sum += (i - j).toFloat().pow(2) * glcm[i][j]
            }
        }
        return sum
    }

    private fun calculateCorrelation(glcm: Array<FloatArray>, levels: Int): Float {
        var meanI = 0f
        var meanJ = 0f
        for (i in 0 until levels) {
            for (j in 0 until levels) {
                meanI += i * glcm[i][j]
                meanJ += j * glcm[i][j]
            }
        }

        var varI = 0f
        var varJ = 0f
        for (i in 0 until levels) {
            for (j in 0 until levels) {
                varI += (i - meanI).pow(2) * glcm[i][j]
                varJ += (j - meanJ).pow(2) * glcm[i][j]
            }
        }

        val stdI = sqrt(varI)
        val stdJ = sqrt(varJ)

        if (stdI * stdJ == 0f) return 0f

        var cov = 0f
        for (i in 0 until levels) {
            for (j in 0 until levels) {
                cov += (i - meanI) * (j - meanJ) * glcm[i][j]
            }
        }
        return (cov / (stdI * stdJ)).coerceIn(-1f, 1f)
    }

    private fun calculateEnergy(glcm: Array<FloatArray>, levels: Int): Float {
        var sum = 0f
        for (i in 0 until levels) {
            for (j in 0 until levels) {
                sum += glcm[i][j].pow(2)
            }
        }
        return sum
    }

    private fun calculateHomogeneity(glcm: Array<FloatArray>, levels: Int): Float {
        var sum = 0f
        for (i in 0 until levels) {
            for (j in 0 until levels) {
                sum += glcm[i][j] / (1f + abs(i - j))
            }
        }
        return sum
    }

    private fun calculateEntropy(glcm: Array<FloatArray>, levels: Int): Float {
        var sum = 0f
        for (i in 0 until levels) {
            for (j in 0 until levels) {
                val p = glcm[i][j]
                if (p > 1e-12f) {
                    sum -= p * ln(p)
                }
            }
        }
        return sum
    }

    // Calculates Population Mean, Population Standard Deviation, Population Skewness
    // Matches Python numpy.std (ddof=0) and scipy.stats.skew exactly
    private fun calculateMoments(data: List<Float>): Triple<Float, Float, Float> {
        val n = data.size
        if (n == 0) return Triple(0f, 0f, 0f)

        var sum = 0.0
        for (v in data) {
            sum += v
        }
        val mean = (sum / n).toFloat()

        var sumSq = 0.0
        for (v in data) {
            sumSq += (v - mean).toDouble().pow(2)
        }
        val std = sqrt(sumSq / n).toFloat()

        if (std < 1e-6f) {
            return Triple(mean, std, 0f)
        }

        var sumCube = 0.0
        for (v in data) {
            sumCube += (v - mean).toDouble().pow(3)
        }
        val skew = ((sumCube / n) / std.toDouble().pow(3)).toFloat()

        return Triple(mean, std, skew)
    }

    fun formatFloat(value: Float, decimals: Int = 4): String {
        return String.format(Locale.US, "%.${decimals}f", value)
    }
}
