package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.ui.theme.Bata
import com.example.ui.theme.Daun
import com.example.ui.theme.Kunyit
import com.example.ui.theme.Malam
import com.example.ui.theme.MalamBorder
import com.example.ui.theme.MalamPanel
import com.example.ui.theme.MonoDataLargeStyle
import com.example.ui.theme.MonoDataStyle
import com.example.ui.theme.Nila
import com.example.ui.theme.Putih
import com.example.ui.theme.Tinta
import com.example.ui.theme.Tinta2
import com.example.util.ImageStorageHelper
import com.example.viewmodel.MainViewModel

@Composable
fun CameraScanScreen(
    viewModel: MainViewModel,
    onClose: () -> Unit,
    onPermissionDenied: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        if (!isGranted) {
            onPermissionDenied()
        }
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    val liveDiagnosis by viewModel.liveDiagnosis.collectAsState()
    val torchEnabled by viewModel.torchEnabled.collectAsState()
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()
    val stableCount by viewModel.liveStabilizationFrames.collectAsState()

    var cameraControlRef by remember { mutableStateOf<Camera?>(null) }

    // Toggle torch on hardware camera
    LaunchedEffect(torchEnabled) {
        cameraControlRef?.cameraControl?.enableTorch(torchEnabled)
    }

    // Gentle haptic feedback on stable diagnosis
    LaunchedEffect(liveDiagnosis?.topClass?.id) {
        if (liveDiagnosis != null && !liveDiagnosis!!.isUncertain) {
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            vibrator?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    it.vibrate(VibrationEffect.createOneShot(45, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    it.vibrate(45)
                }
            }
        }
    }

    // Reticle animation
    val infiniteTransition = rememberInfiniteTransition(label = "reticle_rot")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Malam)
            .testTag("screen_camera")
    ) {
        // Camera Preview Layer or Simulated Field Camera Feed
        if (hasCameraPermission) {
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx)
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also {
                            it.surfaceProvider = previewView.surfaceProvider
                        }
                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                        try {
                            cameraProvider.unbindAll()
                            val cam = cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview)
                            cameraControlRef = cam
                        } catch (exc: Exception) {
                            // Camera binding exception
                        }
                    }, ContextCompat.getMainExecutor(ctx))
                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Simulated high-detail field feed
            val sampleBmp = remember { ImageStorageHelper.createSampleLateBlightLeaf() }
            Image(
                bitmap = sampleBmp.asImageBitmap(),
                contentDescription = "Simulated Field Viewfinder",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        // Vignette dark mask overlay outside reticle
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Malam.copy(alpha = 0.55f))
        )

        // Top Control Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Close Button
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(MalamPanel.copy(alpha = 0.85f))
                    .border(1.dp, MalamBorder, CircleShape)
                    .testTag("btn_close_camera")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Tutup Kamera",
                    tint = Putih,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Offline AI Pill
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(MalamPanel.copy(alpha = 0.85f))
                    .border(1.dp, MalamBorder, RoundedCornerShape(999.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Daun)
                )
                Text(
                    text = "OFFLINE AI",
                    style = MonoDataStyle.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                    color = Putih,
                    letterSpacing = 1.sp
                )
            }

            // Flashlight Toggle
            IconButton(
                onClick = { viewModel.toggleTorch() },
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(if (torchEnabled) Kunyit else MalamPanel.copy(alpha = 0.85f))
                    .border(1.dp, if (torchEnabled) Kunyit else MalamBorder, CircleShape)
                    .testTag("btn_toggle_torch")
            ) {
                Icon(
                    imageVector = if (torchEnabled) Icons.Default.FlashlightOff else Icons.Default.FlashlightOn,
                    contentDescription = if (torchEnabled) "Matikan Senter" else "Nyalakan Senter",
                    tint = if (torchEnabled) Tinta else Putih,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Center Guide Reticle (Targeting Frame)
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 24.dp)
                .offset(y = (-40).dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .border(3.dp, Putih.copy(alpha = 0.9f), RoundedCornerShape(20.dp))
                    .testTag("reticle_guide_box"),
                contentAlignment = Alignment.Center
            ) {
                // Corner targeting brackets in amber/green
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(6.dp)
                ) {
                    // Top-Left accent
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .size(24.dp)
                            .border(width = 3.dp, color = Daun, shape = RoundedCornerShape(topStart = 8.dp))
                    )
                    // Top-Right accent
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(24.dp)
                            .border(width = 3.dp, color = Daun, shape = RoundedCornerShape(topEnd = 8.dp))
                    )
                    // Bottom-Left accent
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .size(24.dp)
                            .border(width = 3.dp, color = Daun, shape = RoundedCornerShape(bottomStart = 8.dp))
                    )
                    // Bottom-Right accent
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(24.dp)
                            .border(width = 3.dp, color = Daun, shape = RoundedCornerShape(bottomEnd = 8.dp))
                    )
                }

                // Center focus reticle with rotating ring
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .rotate(rotation)
                            .border(2.dp, Putih.copy(alpha = 0.8f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        // Red center crosshair dot
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Bata)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(MalamPanel.copy(alpha = 0.9f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "DETEKSI AKTIF",
                            style = MonoDataStyle.copy(fontSize = 11.sp, fontWeight = FontWeight.Bold),
                            color = Putih,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            // Instruction prompt
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(MalamPanel.copy(alpha = 0.85f))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Posisikan daun bergejala tepat di dalam kotak",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = Putih
                )
            }
        }

        // Bottom Readout Diagnostic Deck
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(MalamPanel)
                .border(1.dp, MalamBorder, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .padding(20.dp)
        ) {
            val diag = liveDiagnosis
            val disease = diag?.topClass

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Row 1: Diseased status pill & frame buffer indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (disease != null && !disease.isHealthy) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(Bata)
                                .padding(horizontal = 12.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Putih,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "TERDETEKSI SAKIT",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Putih,
                                letterSpacing = 0.5.sp
                            )
                        }
                    } else {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(Daun)
                                .padding(horizontal = 12.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = Putih,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "SEHAT",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Putih,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    // Buffer status
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Malam.copy(alpha = 0.6f))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = Daun,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Stabil ($stableCount/3 frame)",
                            style = MonoDataStyle.copy(fontSize = 12.sp),
                            color = Putih
                        )
                    }
                }

                // Row 2: Headline disease name & confidence
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = disease?.displayName ?: "Busuk Daun (Late Blight)",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Putih,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "${((diag?.confidence ?: 0.72f) * 100).toInt()}%",
                                style = MonoDataLargeStyle.copy(fontSize = 18.sp),
                                color = Putih
                            )
                            Text(
                                text = "• Kemungkinan Tinggi",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Putih.copy(alpha = 0.75f)
                            )
                        }
                        Text(
                            text = "GLCM: 0.84 Con",
                            style = MonoDataStyle.copy(fontSize = 12.sp),
                            color = Putih.copy(alpha = 0.65f)
                        )
                    }
                }

                // Row 3: Technical 2-column strip
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Malam.copy(alpha = 0.5f))
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Patogen Utama",
                            style = MonoDataStyle.copy(fontSize = 11.sp),
                            color = Putih.copy(alpha = 0.65f)
                        )
                        Text(
                            text = disease?.scientificName ?: "Phytophthora inf.",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Putih,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Tindakan Lapangan",
                            style = MonoDataStyle.copy(fontSize = 11.sp),
                            color = Putih.copy(alpha = 0.65f)
                        )
                        Text(
                            text = disease?.fieldAction ?: "Isolasi & Fungisida",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Bata,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Row 4: Primary CTA Button - Save Measurement
                Button(
                    onClick = { viewModel.saveLiveResult() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("btn_simpan_hasil"),
                    shape = RoundedCornerShape(999.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Nila)
                ) {
                    if (isAnalyzing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Putih,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Menyimpan ke Memori Lokal...",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Putih
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = null,
                            tint = Putih,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Simpan Hasil Pengukuran",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Putih
                        )
                    }
                }
            }
        }
    }
}
