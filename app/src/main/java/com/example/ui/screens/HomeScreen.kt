package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.DetectionRecord
import com.example.ui.components.NoticeBanner
import com.example.ui.theme.Bata
import com.example.ui.theme.BataPucat
import com.example.ui.theme.Daun
import com.example.ui.theme.DaunPucat
import com.example.ui.theme.Garis
import com.example.ui.theme.Kertas
import com.example.ui.theme.Kunyit
import com.example.ui.theme.KunyitPucat
import com.example.ui.theme.KunyitTeks
import com.example.ui.theme.MonoDataStyle
import com.example.ui.theme.Nila
import com.example.ui.theme.NilaPucat
import com.example.ui.theme.Putih
import com.example.ui.theme.Tinta
import com.example.ui.theme.Tinta2
import com.example.util.ImageStorageHelper
import com.example.viewmodel.MainViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToCamera: () -> Unit,
    onOpenRecord: (DetectionRecord) -> Unit
) {
    val context = LocalContext.current
    val latestRecord by viewModel.latestRecord.collectAsState()
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()

    // Android Zero-Permission Photo Picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val bitmap = ImageStorageHelper.loadBitmapFromUri(context, uri)
            if (bitmap != null) {
                viewModel.processGalleryImage(bitmap)
            } else {
                viewModel.showSnackbar("Gagal memuat citra daun dari galeri")
            }
        }
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Kertas)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Section 1: Greeting & Field Readiness Status
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Daun)
                    )
                    Text(
                        text = "OPERASIONAL LAPANGAN",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Daun,
                        letterSpacing = 0.5.sp
                    )
                }

                // Offline badge
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Putih)
                        .border(1.dp, Garis, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.WifiOff,
                        contentDescription = "Offline Mode",
                        tint = Daun,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Offline",
                        style = MonoDataStyle.copy(fontSize = 12.sp),
                        color = Tinta2
                    )
                }
            }

            Text(
                text = "Halo, Petani Tomat!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Nila
            )
            Text(
                text = "Siap memindai di kebun. Pemrosesan citra 100% lokal tanpa sinyal internet.",
                style = MaterialTheme.typography.bodyLarge,
                color = Tinta2,
                lineHeight = 22.sp
            )
        }

        // Section 2: Last Detection Card (Prominent History Item)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val record = latestRecord
            val timeText = if (record != null) {
                val sdf = SimpleDateFormat("HH:mm 'WIB'", Locale.getDefault())
                sdf.format(Date(record.waktu))
            } else {
                "09:15 WIB"
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PEMINDAIAN TERAKHIR",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Tinta,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = timeText,
                    style = MonoDataStyle.copy(fontSize = 12.sp),
                    color = Tinta2
                )
            }

            if (record != null) {
                // Card for Latest Record
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Putih)
                        .border(1.dp, Garis, RoundedCornerShape(12.dp))
                        .padding(14.dp)
                        .testTag("card_latest_scan")
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Thumbnail with GLCM badge
                            Box(
                                modifier = Modifier
                                    .size(92.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(1.dp, Garis, RoundedCornerShape(8.dp))
                                    .background(Kertas)
                            ) {
                                val file = File(record.path_foto)
                                if (file.exists()) {
                                    AsyncImage(
                                        model = file,
                                        contentDescription = "Foto Sampel Daun",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    // Fallback synthetic preview
                                    val sampleBmp = ImageStorageHelper.createSampleLateBlightLeaf()
                                    Image(
                                        bitmap = sampleBmp.asImageBitmap(),
                                        contentDescription = "Sampel Daun",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }

                                // GLCM badge
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(4.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Putih.copy(alpha = 0.9f))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "GLCM",
                                        style = MonoDataStyle.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                        color = Tinta
                                    )
                                }
                            }

                            // Details
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Status pill
                                    if (record.is_terinfeksi) {
                                        Row(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(999.dp))
                                                .background(BataPucat)
                                                .border(1.dp, Bata, RoundedCornerShape(999.dp))
                                                .padding(horizontal = 8.dp, vertical = 2.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Warning,
                                                contentDescription = "Terinfeksi",
                                                tint = Bata,
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Text(
                                                text = "Terinfeksi",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = Bata
                                            )
                                        }
                                    } else {
                                        Row(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(999.dp))
                                                .background(DaunPucat)
                                                .border(1.dp, Daun, RoundedCornerShape(999.dp))
                                                .padding(horizontal = 8.dp, vertical = 2.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Sehat",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = Daun
                                            )
                                        }
                                    }

                                    Text(
                                        text = "Hari ini",
                                        style = MonoDataStyle.copy(fontSize = 12.sp),
                                        color = Tinta2
                                    )
                                }

                                Text(
                                    text = record.nama_kelas,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Tinta,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Text(
                                    text = record.nama_ilmiah,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontStyle = FontStyle.Italic,
                                    color = Tinta2,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Text(
                                    text = "${(record.keyakinan * 100).toInt()}% • Kemungkinan",
                                    style = MonoDataStyle.copy(fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
                                    color = Tinta
                                )
                            }
                        }

                        // Divider & Action Button inside card
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Garis)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Analytics,
                                    contentDescription = null,
                                    tint = Nila,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Fitur Tekstur Terekstraksi",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Tinta2
                                )
                            }

                            Button(
                                onClick = { onOpenRecord(record) },
                                colors = ButtonDefaults.buttonColors(containerColor = Nila),
                                shape = RoundedCornerShape(999.dp),
                                modifier = Modifier.testTag("btn_buka_hasil")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "Buka Hasil",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Putih
                                    )
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        tint = Putih,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // Empty state card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Putih)
                        .border(1.dp, Garis, RoundedCornerShape(12.dp))
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = null,
                            tint = Nila,
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = "Belum Ada Riwayat Pemindaian",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Tinta
                        )
                        Text(
                            text = "Arahkan kamera ke daun tomat di kebun atau pilih foto daun dari galeri untuk deteksi instan.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Tinta2,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }

        // Section 3: Secondary Gallery Action Button
        OutlinedButton(
            onClick = {
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("btn_open_gallery"),
            shape = RoundedCornerShape(999.dp),
            border = androidx.compose.foundation.BorderStroke(2.dp, Nila),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = Putih)
        ) {
            if (isAnalyzing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Nila,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Membaca Citra...",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Nila
                )
            } else {
                Icon(
                    imageVector = Icons.Default.PhotoLibrary,
                    contentDescription = null,
                    tint = Nila,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Pilih dari Galeri",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Nila
                )
            }
        }

        // Section 4: Three Outdoor Photo Tips Box
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = Nila,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Panduan Foto di Lapangan",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Tinta
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Putih)
                    .border(1.dp, Garis, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    FieldTipItem(
                        number = "1",
                        title = "Satu Daun Utama",
                        desc = "Pastikan hanya satu helai daun bergejala masuk dalam bingkai retikel pemindaian kamera."
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Garis)
                    )

                    FieldTipItem(
                        number = "2",
                        title = "Tepat di Tengah Bidik",
                        desc = "Pusatkan bercak atau area berciri tepat di persilangan bidik agar ekstraksi fitur GLCM presisi."
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Garis)
                    )

                    FieldTipItem(
                        number = "3",
                        title = "Pencahayaan Matahari Rata",
                        desc = "Gunakan cahaya alami yang cukup terang. Hindari bayangan badan atau topi menutupi permukaan daun."
                    )
                }
            }
        }

        // Bottom regulatory disclaimer
        NoticeBanner()

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun FieldTipItem(number: String, title: String, desc: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(NilaPucat),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                style = MonoDataStyle.copy(fontSize = 15.sp, fontWeight = FontWeight.Bold),
                color = Nila
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Tinta
            )
            Text(
                text = desc,
                style = MaterialTheme.typography.bodyMedium,
                color = Tinta2,
                lineHeight = 20.sp
            )
        }
    }
}
