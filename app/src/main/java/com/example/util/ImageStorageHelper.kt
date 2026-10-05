package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object ImageStorageHelper {

    fun saveBitmapToInternalStorage(context: Context, bitmap: Bitmap, prefix: String = "leaf"): String {
        val filename = "${prefix}_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}.jpg"
        val file = File(context.filesDir, filename)
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
        }
        return file.absolutePath
    }

    fun saveMaskToInternalStorage(context: Context, maskBitmap: Bitmap): String {
        val filename = "mask_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}.png"
        val file = File(context.filesDir, filename)
        FileOutputStream(file).use { out ->
            maskBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        return file.absolutePath
    }

    fun loadBitmapFromPath(path: String): Bitmap? {
        return try {
            val file = File(path)
            if (file.exists()) {
                android.graphics.BitmapFactory.decodeFile(file.absolutePath)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    fun loadBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                android.graphics.BitmapFactory.decodeStream(stream)
            }
        } catch (e: Exception) {
            null
        }
    }

    // Creates an artistic synthetic sample tomato leaf bitmap (used as initial sample / fallback)
    fun createSampleLateBlightLeaf(): Bitmap {
        val size = 512
        val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)

        // Warm sunny background
        canvas.drawColor(Color.rgb(238, 235, 226))

        val leafPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(65, 138, 52)
            style = Paint.Style.FILL
        }

        val leafPath = Path().apply {
            moveTo(256f, 60f)
            cubicTo(120f, 130f, 70f, 290f, 160f, 410f)
            cubicTo(210f, 470f, 256f, 490f, 256f, 490f)
            cubicTo(256f, 490f, 302f, 470f, 352f, 410f)
            cubicTo(442f, 290f, 392f, 130f, 256f, 60f)
            close()
        }
        canvas.drawPath(leafPath, leafPaint)

        // Main vein
        val veinPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(90, 165, 75)
            strokeWidth = 6f
            style = Paint.Style.STROKE
        }
        canvas.drawLine(256f, 90f, 256f, 470f, veinPaint)

        // Secondary veins
        veinPaint.strokeWidth = 3f
        canvas.drawLine(256f, 180f, 180f, 230f, veinPaint)
        canvas.drawLine(256f, 180f, 332f, 230f, veinPaint)
        canvas.drawLine(256f, 270f, 160f, 330f, veinPaint)
        canvas.drawLine(256f, 270f, 352f, 330f, veinPaint)
        canvas.drawLine(256f, 360f, 190f, 410f, veinPaint)
        canvas.drawLine(256f, 360f, 322f, 410f, veinPaint)

        // Necrotic Late Blight Lesions (dark brown/black water-soaked spots with yellow chlorotic halos)
        val haloPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(205, 190, 60)
            style = Paint.Style.FILL
        }
        val lesionPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(52, 36, 25)
            style = Paint.Style.FILL
        }

        // Lesion 1 (Center)
        canvas.drawOval(200f, 210f, 312f, 340f, haloPaint)
        canvas.drawOval(215f, 225f, 297f, 325f, lesionPaint)

        // Lesion 2 (Left edge)
        canvas.drawOval(140f, 290f, 220f, 380f, haloPaint)
        canvas.drawOval(150f, 300f, 210f, 370f, lesionPaint)

        // Lesion 3 (Right edge)
        canvas.drawOval(290f, 190f, 360f, 260f, haloPaint)
        canvas.drawOval(300f, 200f, 350f, 250f, lesionPaint)

        return bmp
    }
}
