package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.NoPhotography
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.classifier.ClassifierModel
import com.example.ui.components.NoticeBanner
import com.example.ui.components.TomatScanHeader
import com.example.ui.theme.Bata
import com.example.ui.theme.BataPucat
import com.example.ui.theme.Daun
import com.example.ui.theme.DaunPucat
import com.example.ui.theme.Garis
import com.example.ui.theme.Kertas
import com.example.ui.theme.MonoDataStyle
import com.example.ui.theme.Nila
import com.example.ui.theme.NilaPucat
import com.example.ui.theme.Putih
import com.example.ui.theme.Tinta
import com.example.ui.theme.Tinta2
import com.example.util.ImageStorageHelper
import com.example.viewmodel.MainViewModel

@Composable
fun AboutScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Kertas)
    ) {
        TomatScanHeader(
            title = "TomatScan",
            subtitle = "Tentang Aplikasi",
            showBack = true,
            onBack = onBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // App Branding Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Putih)
                    .border(1.dp, Garis, RoundedCornerShape(14.dp))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_tomatscan_logo),
                        contentDescription = "Logo",
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(14.dp))
                    )

                    Text(
                        text = "TomatScan Lapangan",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Nila
                    )

                    Text(
                        text = "Versi 1.0 (Build 2026.10)",
                        style = MonoDataStyle.copy(fontSize = 12.sp),
                        color = Tinta2
                    )

                    Text(
                        text = "Alat ukur lapangan deterministik untuk identifikasi dini penyakit daun tanaman tomat menggunakan ekstraksi 23 fitur citra dan kernel SVM.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Tinta,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                }
            }

            // Specs Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Putih)
                    .border(1.dp, Garis, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Spesifikasi Model & Komputasi",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Tinta
                    )

                    SpecRow(label = "Versi Model", value = ClassifierModel.MODEL_VERSION)
                    SpecRow(label = "Arsitektur Algoritma", value = "StandardScaler + SVM RBF")
                    SpecRow(label = "Akurasi Uji (Test)", value = ClassifierModel.TEST_ACCURACY)
                    SpecRow(label = "Dimensi Vektor Fitur", value = "23 Dimensi (GLCM + Momen Warna)")
                    SpecRow(label = "Resolusi Input Pipeline", value = "256 x 256 piksel")
                    SpecRow(label = "Ambang Keyakinan", value = ">= 60% (Dibawah = Tidak Yakin)")
                    SpecRow(label = "Ambang Luas Daun", value = ">= 5% Proporsi Area Mask")
                    SpecRow(label = "Koneksi Jaringan", value = "100% Offline (Tanpa Internet)")
                }
            }

            // Notice
            NoticeBanner(
                text = "Pemberitahuan Batasan: Model ini dilatih pada dataset laboratorium dengan 10 kelas daun tomat. Akurasi di lapangan bergantung pada pencahayaan matahari, kejernihan fokus, dan kebersihan lensa kamera. Selalu konfirmasi dengan penyuluh pertanian setempat."
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SpecRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Tinta2
        )
        Text(
            text = value,
            style = MonoDataStyle.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold),
            color = Tinta
        )
    }
}

@Composable
fun PermissionDeniedScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val bitmap = ImageStorageHelper.loadBitmapFromUri(context, uri)
            if (bitmap != null) {
                viewModel.processGalleryImage(bitmap)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Kertas)
    ) {
        TomatScanHeader(
            title = "TomatScan",
            subtitle = "Izin Kamera",
            showBack = true,
            onBack = onBack
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(BataPucat),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.NoPhotography,
                        contentDescription = null,
                        tint = Bata,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Text(
                    text = "Izin Kamera Diperlukan",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Tinta,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Aplikasi TomatScan membutuhkan akses kamera untuk memindai gejala penyakit pada daun tomat langsung di kebun secara offline.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Tinta2,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Primary: Open Settings
                Button(
                    onClick = {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("btn_open_app_settings"),
                    shape = RoundedCornerShape(999.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Nila)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = Putih,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Buka Pengaturan Aplikasi",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Putih
                    )
                }

                // Secondary: Pick from Gallery
                OutlinedButton(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("btn_denied_pick_gallery"),
                    shape = RoundedCornerShape(999.dp),
                    border = androidx.compose.foundation.BorderStroke(2.dp, Nila),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Putih)
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoLibrary,
                        contentDescription = null,
                        tint = Nila,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Pilih dari Galeri Saja",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Nila
                    )
                }
            }
        }
    }
}
