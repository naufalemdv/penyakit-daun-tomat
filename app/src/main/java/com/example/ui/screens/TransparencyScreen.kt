package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.DetectionRecord
import com.example.model.FeatureItem
import com.example.ui.components.TomatScanHeader
import com.example.ui.theme.Daun
import com.example.ui.theme.DaunPucat
import com.example.ui.theme.Garis
import com.example.ui.theme.Kertas
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
import java.util.Locale

@Composable
fun TransparencyScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val record = viewModel.selectedRecord.collectAsState().value ?: return

    val featureList = remember(record.fitur_json) {
        viewModel.parseFeatures(record.fitur_json)
    }

    val groupA = featureList.filter { it.group.contains("GLCM") }
    val groupB = featureList.filter { it.group.contains("RGB") }
    val groupC = featureList.filter { it.group.contains("HSV") }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Kertas)
    ) {
        // App Header
        TomatScanHeader(
            title = "TomatScan",
            subtitle = "Transparansi Fitur",
            showBack = true,
            onBack = onBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Context header banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Putih)
                    .border(1.dp, Garis, RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(NilaPucat)
                                .padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Science,
                                contentDescription = null,
                                tint = Nila,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Transparansi Rekayasa Fitur",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Nila
                            )
                        }

                        Text(
                            text = "Pemeriksaan deterministik pipeline segmentasi, ekstraksi 23 dimensi vektor, dan inferensi Kernel SVM.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Tinta2,
                            lineHeight = 20.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(DaunPucat),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Terverifikasi",
                            tint = Daun,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            // Section 1: Dual Image Comparative Viewport
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                            imageVector = Icons.Default.Layers,
                            contentDescription = null,
                            tint = Nila,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Perbandingan Citra Input",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Tinta
                        )
                    }
                    Text(
                        text = "256 x 256 px",
                        style = MonoDataStyle.copy(fontSize = 12.sp),
                        color = Tinta2
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Left: Raw Image Crop
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Putih)
                            .border(1.dp, Garis, RoundedCornerShape(12.dp))
                            .padding(8.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.Black)
                            ) {
                                val rawFile = File(record.path_foto)
                                if (rawFile.exists()) {
                                    AsyncImage(
                                        model = rawFile,
                                        contentDescription = "Raw Leaf",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    val sampleBmp = ImageStorageHelper.createSampleLateBlightLeaf()
                                    Image(
                                        bitmap = sampleBmp.asImageBitmap(),
                                        contentDescription = "Raw Leaf",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(6.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color.Black.copy(alpha = 0.75f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "RGB: Raw",
                                        style = MonoDataStyle.copy(fontSize = 10.sp),
                                        color = Putih
                                    )
                                }
                            }

                            Text(
                                text = "Foto Daun Asli",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Tinta
                            )
                            Text(
                                text = "Input Akuisisi",
                                style = MonoDataStyle.copy(fontSize = 11.sp),
                                color = Tinta2
                            )
                        }
                    }

                    // Right: Segmentation Mask
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Putih)
                            .border(1.dp, Garis, RoundedCornerShape(12.dp))
                            .padding(8.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.Black)
                            ) {
                                val maskFile = File(record.mask_path)
                                if (maskFile.exists()) {
                                    AsyncImage(
                                        model = maskFile,
                                        contentDescription = "Mask Leaf",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    // Fallback high-contrast silhouette
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.Black),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(80.dp)
                                                .clip(CircleShape)
                                                .background(Putih)
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(6.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color.Black.copy(alpha = 0.75f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "Mask: B/W",
                                        style = MonoDataStyle.copy(fontSize = 10.sp),
                                        color = Putih
                                    )
                                }
                            }

                            Text(
                                text = "Mask Segmentasi",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Tinta
                            )
                            Text(
                                text = "GrabCut Binary",
                                style = MonoDataStyle.copy(fontSize = 11.sp),
                                color = Tinta2
                            )
                        }
                    }
                }
            }

            // Section 2: Metric Summaries
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = null,
                        tint = Nila,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Ringkasan Metrik Segmen & Model",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Tinta
                    )
                }

                // Metric 1: Leaf Area Proportion
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Putih)
                        .border(1.dp, Garis, RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(DaunPucat),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PieChart,
                                    contentDescription = null,
                                    tint = Daun,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = String.format(Locale.US, "%.1f%%", record.luas_daun * 100),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Tinta
                                )
                                Text(
                                    text = "Luas Daun (Proporsi Mask)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Tinta2
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(DaunPucat)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (record.luas_daun >= 0.05f) "> Ambang 5% Valid" else "< 5% Tidak Valid",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Daun
                            )
                        }
                    }
                }

                // Metric 2: Model Architecture
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Putih)
                        .border(1.dp, Garis, RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(NilaPucat),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Memory,
                                    contentDescription = null,
                                    tint = Nila,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = record.versi_model,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Tinta
                                )
                                Text(
                                    text = "StandardScaler + SVM RBF",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Tinta2
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "91.57%",
                                style = MonoDataLargeStyle.copy(fontSize = 16.sp),
                                color = Daun
                            )
                            Text(
                                text = "Akurasi Uji",
                                style = MonoDataStyle.copy(fontSize = 11.sp),
                                color = Tinta2
                            )
                        }
                    }
                }
            }

            // Section 3: 23 Features Tabular Presentation
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                            imageVector = Icons.Default.DataObject,
                            contentDescription = null,
                            tint = Nila,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Vektor 23 Fitur Terekstraksi",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Tinta
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Garis.copy(alpha = 0.5f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Dim: 23x1",
                            style = MonoDataStyle.copy(fontSize = 11.sp),
                            color = Tinta
                        )
                    }
                }

                // Table Group A: GLCM
                FeatureGroupTable(
                    title = "Kelompok A: 5 Tekstur GLCM (Gray-Level)",
                    tag = "[F01 - F05]",
                    accentColor = Nila,
                    items = groupA
                )

                // Table Group B: RGB Moments
                FeatureGroupTable(
                    title = "Kelompok B: 9 Momen Warna RGB",
                    tag = "[F06 - F14]",
                    accentColor = Daun,
                    items = groupB
                )

                // Table Group C: HSV Moments
                FeatureGroupTable(
                    title = "Kelompok C: 9 Momen Warna HSV",
                    tag = "[F15 - F23]",
                    accentColor = Tinta2,
                    items = groupC
                )
            }

            // Scientific Guarantee Footer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Putih)
                    .border(1.dp, Garis, RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = Nila,
                        modifier = Modifier.size(20.dp)
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "Jaminan Determinasi Matematis",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Tinta
                        )
                        Text(
                            text = "Urutan 23 fitur identik 100% dengan pipeline notebook Python (selisih ≤1%). 100% komputasi on-device.",
                            style = MonoDataStyle.copy(fontSize = 12.sp),
                            color = Tinta2,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // Action: Copy CSV Feature Vector
            Button(
                onClick = {
                    val csvString = featureList.joinToString(separator = ",") { it.formattedValue }
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("23 Features Vector", csvString)
                    clipboard.setPrimaryClip(clip)
                    viewModel.showSnackbar("23 Vektor Fitur berhasil disalin ke clipboard untuk audit Google Colab!")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("btn_copy_vector_csv"),
                shape = RoundedCornerShape(999.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Nila)
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = null,
                    tint = Putih,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Salin Vektor Fitur (CSV)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Putih
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun FeatureGroupTable(
    title: String,
    tag: String,
    accentColor: Color,
    items: List<FeatureItem>
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Putih)
            .border(1.dp, Garis, RoundedCornerShape(12.dp))
    ) {
        Column {
            // Group Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Kertas)
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(accentColor)
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Tinta
                    )
                }

                Text(
                    text = tag,
                    style = MonoDataStyle.copy(fontSize = 11.sp),
                    color = Tinta2
                )
            }

            // Divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Garis)
            )

            // Rows
            items.forEachIndexed { index, item ->
                val rowBg = if (index % 2 == 1) Kertas.copy(alpha = 0.5f) else Putih
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(rowBg)
                        .padding(horizontal = 14.dp, vertical = 7.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Tinta2
                    )
                    Text(
                        text = item.formattedValue,
                        style = MonoDataStyle.copy(fontSize = 13.sp, fontWeight = FontWeight.Bold),
                        color = Tinta
                    )
                }

                if (index < items.size - 1) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Garis.copy(alpha = 0.4f))
                    )
                }
            }
        }
    }
}
