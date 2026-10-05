package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.OfflinePin
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.DetectionRecord
import com.example.model.TopClassPrediction
import com.example.ui.components.DeleteConfirmDialog
import com.example.ui.components.NoticeBanner
import com.example.ui.components.TomatScanHeader
import com.example.ui.theme.Bata
import com.example.ui.theme.BataPucat
import com.example.ui.theme.Daun
import com.example.ui.theme.DaunPucat
import com.example.ui.theme.Garis
import com.example.ui.theme.Kertas
import com.example.ui.theme.Kunyit
import com.example.ui.theme.KunyitPucat
import com.example.ui.theme.KunyitTeks
import com.example.ui.theme.MonoDataLargeStyle
import com.example.ui.theme.MonoDataStyle
import com.example.ui.theme.Nila
import com.example.ui.theme.NilaContainer
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
fun ResultScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onOpenDiseaseGuide: (String) -> Unit,
    onOpenTransparency: (DetectionRecord) -> Unit
) {
    val context = LocalContext.current
    val record = viewModel.selectedRecord.collectAsState().value ?: return

    var showDeleteConfirm by remember { mutableStateOf(false) }
    var noteText by remember(record.id) { mutableStateOf(record.catatan) }
    var noteSavedFeedback by remember { mutableStateOf(false) }

    val top3List = remember(record.top3_json) {
        viewModel.parseTop3(record.top3_json)
    }

    val formattedFileName = remember(record.waktu) {
        val sdf = SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault())
        "IMG_${sdf.format(Date(record.waktu))}.JPG"
    }

    if (showDeleteConfirm) {
        DeleteConfirmDialog(
            title = "Hapus Riwayat Pengujian?",
            message = "Hasil deteksi ini akan dihapus permanen dari memori lokal.",
            onConfirm = {
                showDeleteConfirm = false
                viewModel.deleteCurrentRecord(record)
            },
            onDismiss = { showDeleteConfirm = false }
        )
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Kertas)
    ) {
        // App Header
        TomatScanHeader(
            title = "TomatScan",
            subtitle = "Hasil Diagnosis",
            showBack = true,
            onBack = onBack,
            showShareAction = true,
            onShareClick = {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(
                        Intent.EXTRA_TEXT,
                        "Hasil Pemindaian TomatScan:\n" +
                        "Penyakit: ${record.nama_kelas} (${record.nama_ilmiah})\n" +
                        "Keyakinan: ${(record.keyakinan * 100).toInt()}%\n" +
                        "Model: ${record.versi_model} (100% Offline)\n" +
                        if (record.catatan.isNotEmpty()) "Catatan: ${record.catatan}" else ""
                    )
                }
                context.startActivity(Intent.createChooser(shareIntent, "Bagikan Hasil Diagnosis"))
            },
            showDeleteAction = true,
            onDeleteClick = { showDeleteConfirm = true }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top Utility Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(Putih)
                        .border(1.dp, Garis, RoundedCornerShape(999.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.OfflinePin,
                        contentDescription = null,
                        tint = Daun,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Tersimpan di Riwayat Lokal",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = Tinta2
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "Hasil Pemindaian TomatScan: ${record.nama_kelas} (${(record.keyakinan * 100).toInt()}%)"
                                )
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Bagikan"))
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Putih)
                            .border(1.dp, Garis, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Bagikan",
                            tint = Tinta2,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = { showDeleteConfirm = true },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Putih)
                            .border(1.dp, Garis, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Hapus",
                            tint = Bata,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Square Leaf Photo Preview with Overlays
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Putih)
                    .border(1.dp, Garis, RoundedCornerShape(14.dp))
                    .testTag("preview_leaf_sample")
            ) {
                val file = File(record.path_foto)
                if (file.exists()) {
                    AsyncImage(
                        model = file,
                        contentDescription = "Sampel Daun Terdeteksi",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    val sampleBmp = ImageStorageHelper.createSampleLateBlightLeaf()
                    Image(
                        bitmap = sampleBmp.asImageBitmap(),
                        contentDescription = "Sampel Daun",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                // Top-Left metadata pill
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Black.copy(alpha = 0.75f))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = null,
                        tint = Putih,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = formattedFileName,
                        style = MonoDataStyle.copy(fontSize = 11.sp),
                        color = Putih
                    )
                }

                // Bottom-Right feature extraction pill
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Black.copy(alpha = 0.75f))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        tint = NilaPucat,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "GLCM+HSV-23",
                        style = MonoDataStyle.copy(fontSize = 11.sp),
                        color = Putih
                    )
                }
            }

            // Primary Detection Result Card
            val isUncertain = record.status == "tidak_yakin"
            val cardBg = when {
                isUncertain -> KunyitPucat
                record.is_terinfeksi -> BataPucat
                else -> DaunPucat
            }
            val accentColor = when {
                isUncertain -> KunyitTeks
                record.is_terinfeksi -> Bata
                else -> Daun
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(cardBg)
                    .border(1.dp, Garis, RoundedCornerShape(14.dp))
                    .padding(16.dp)
                    .testTag("card_primary_diagnosis")
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(Putih)
                                .border(1.dp, accentColor, RoundedCornerShape(999.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = if (record.is_terinfeksi) Icons.Default.Warning else Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = accentColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (record.is_terinfeksi) "Penyakit Terdeteksi" else "Tanaman Sehat",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = accentColor
                            )
                        }

                        Text(
                            text = "SVM-RBF",
                            style = MonoDataStyle.copy(fontSize = 13.sp, fontWeight = FontWeight.Bold),
                            color = accentColor
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = record.nama_kelas,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Tinta,
                            lineHeight = 32.sp
                        )
                        Text(
                            text = record.nama_ilmiah,
                            style = MaterialTheme.typography.bodyMedium,
                            fontStyle = FontStyle.Italic,
                            color = Tinta2
                        )
                    }

                    // Confidence Score Gauge Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Putih.copy(alpha = 0.9f))
                            .padding(12.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Tingkat Keyakinan",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Tinta
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "${(record.keyakinan * 100).toInt()}%",
                                        style = MonoDataLargeStyle.copy(fontSize = 16.sp),
                                        color = accentColor
                                    )
                                    Text(
                                        text = "— Kemungkinan",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Tinta2
                                    )
                                }
                            }

                            LinearProgressIndicator(
                                progress = { record.keyakinan.coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = accentColor,
                                trackColor = Garis
                            )
                        }
                    }
                }
            }

            // Top-3 Multiclass Probability Ranking Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Putih)
                    .border(1.dp, Garis, RoundedCornerShape(14.dp))
                    .padding(16.dp)
                    .testTag("card_top3_probabilities")
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = Nila,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "3 Kemungkinan Teratas",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Tinta
                            )
                        }
                        Text(
                            text = "Top-3 Kelas",
                            style = MonoDataStyle.copy(fontSize = 12.sp),
                            color = Tinta2
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        top3List.forEachIndexed { index, candidate ->
                            val rankColor = when (index) {
                                0 -> Bata
                                1 -> Nila
                                else -> Tinta2
                            }
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(22.dp)
                                                .clip(CircleShape)
                                                .background(if (index == 0) Bata else Garis),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "${index + 1}",
                                                style = MonoDataStyle.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                                                color = if (index == 0) Putih else Tinta
                                            )
                                        }
                                        Text(
                                            text = candidate.displayName,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = if (index == 0) FontWeight.Bold else FontWeight.Normal,
                                            color = Tinta,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    Text(
                                        text = String.format(Locale.US, "%.1f%%", candidate.probability * 100),
                                        style = MonoDataStyle.copy(fontSize = 13.sp, fontWeight = FontWeight.Bold),
                                        color = Tinta
                                    )
                                }

                                LinearProgressIndicator(
                                    progress = { candidate.probability.coerceIn(0f, 1f) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = rankColor,
                                    trackColor = Garis.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
                }
            }

            // Primary Field Action: Read Disease Guide
            Button(
                onClick = { onOpenDiseaseGuide(record.kelas_prediksi) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("btn_read_disease_guide"),
                shape = RoundedCornerShape(999.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Nila)
            ) {
                Icon(
                    imageVector = Icons.Default.MenuBook,
                    contentDescription = null,
                    tint = Putih,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Baca tentang penyakit ini (S8)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Putih
                )
            }

            // Field Notes Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Putih)
                    .border(1.dp, Garis, RoundedCornerShape(14.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.EditNote,
                            contentDescription = null,
                            tint = Daun,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = "Catatan Lapangan",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Tinta
                        )
                    }

                    Text(
                        text = "Tambahkan lokasi pohon, bedengan, atau perlakuan pupuk untuk rujukan:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Tinta2
                    )

                    OutlinedTextField(
                        value = noteText,
                        onValueChange = { noteText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_field_notes"),
                        placeholder = {
                            Text(
                                text = "Tambah catatan (misal: Bedengan A3, tanaman baris ke-4, tampak layu sejak kemarin)...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Tinta2.copy(alpha = 0.6f)
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Nila,
                            unfocusedBorderColor = Garis,
                            focusedContainerColor = Putih,
                            unfocusedContainerColor = Kertas
                        ),
                        shape = RoundedCornerShape(8.dp),
                        minLines = 2,
                        maxLines = 4
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                viewModel.updateNotes(record.id, noteText)
                                noteSavedFeedback = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (noteSavedFeedback) DaunPucat else NilaPucat
                            ),
                            shape = RoundedCornerShape(999.dp),
                            modifier = Modifier.testTag("btn_save_note")
                        ) {
                            Icon(
                                imageVector = if (noteSavedFeedback) Icons.Default.DoneAll else Icons.Default.Check,
                                contentDescription = null,
                                tint = if (noteSavedFeedback) Daun else Nila,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (noteSavedFeedback) "Tersimpan" else "Simpan Catatan",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (noteSavedFeedback) Daun else Nila
                            )
                        }
                    }
                }
            }

            // Academic Verification Link (S5 Inspection Navigation)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onOpenTransparency(record) }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .testTag("btn_inspect_features"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Biotech,
                        contentDescription = null,
                        tint = Nila,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Lihat proses analisis & nilai 23 fitur (S5)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Nila,
                        textDecoration = TextDecoration.Underline
                    )
                }
            }

            // Regulatory Field Screening Disclaimer (PRD F10)
            NoticeBanner()

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
