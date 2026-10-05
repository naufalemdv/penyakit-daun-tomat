package com.example

import android.graphics.Bitmap
import com.example.classifier.ClassifierModel
import com.example.classifier.FeatureExtractor
import com.example.model.DiseaseRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class TomatScanUnitTest {

    @Test
    fun testDiseaseRepositoryHas10Classes() {
        assertEquals(10, DiseaseRepository.DISEASES.size)
        val healthy = DiseaseRepository.getById("tomato_healthy")
        assertTrue(healthy.isHealthy)
        val lateBlight = DiseaseRepository.getById("tomato_late_blight")
        assertFalse(lateBlight.isHealthy)
        assertEquals("Busuk Daun (Late Blight)", lateBlight.displayName)
    }

    @Test
    fun testFeatureExtractionAndClassification() {
        val bmp = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888)
        // Fill center with foliage green
        for (x in 50..200) {
            for (y in 50..200) {
                bmp.setPixel(x, y, android.graphics.Color.rgb(65, 145, 55))
            }
        }

        val (extraction, mask) = FeatureExtractor.processBitmap(bmp)
        assertNotNull(extraction)
        assertNotNull(mask)
        assertEquals(23, extraction.features.size)
        assertTrue(extraction.leafAreaRatio > 0.05f)

        val result = ClassifierModel.classify(extraction)
        assertNotNull(result)
        assertEquals(3, result.top3.size)
        assertNotNull(result.topClass)
    }
}
