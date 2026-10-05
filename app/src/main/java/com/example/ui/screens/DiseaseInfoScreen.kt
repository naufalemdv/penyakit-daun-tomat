package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Coronavirus
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DiseaseInfo
import com.example.model.DiseaseRepository
import com.example.ui.components.NoticeBanner
import com.example.ui.components.StatusBadge
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
import com.example.viewmodel.MainViewModel

@Composable
fun DiseaseListScreen(
    onSelectDisease: (String) -> Unit
) {
    val diseases = DiseaseRepository.DISEASES

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Kertas)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "Ensiklopedia Penyakit Tomat",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Nila
            )
            Text(
                text = "10 Kelas Deteksi Model SVM (1 Sehat, 9 Penyakit)",
                style = MaterialTheme.typography.bodyMedium,
                color = Tinta2
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(diseases, key = { it.id }) { item ->
                DiseaseListItem(
                    disease = item,
                    onClick = { onSelectDisease(item.id) }
                )
            }
        }
    }
}

@Composable
private fun DiseaseListItem(
    disease: DiseaseInfo,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Putih)
            .border(1.dp, Garis, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(14.dp)
            .testTag("disease_item_${disease.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatusBadge(isHealthy = disease.isHealthy)
                }

                Text(
                    text = disease.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Tinta,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = disease.scientificName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = FontStyle.Italic,
                    color = Tinta2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = disease.symptoms,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Tinta2,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 18.sp
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Buka Detail",
                tint = Nila,
                modifier = Modifier
                    .size(20.dp)
                    .padding(start = 8.dp)
            )
        }
    }
}

@Composable
fun DiseaseDetailScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onScanClick: () -> Unit
) {
    val disease = viewModel.selectedDisease.collectAsState().value ?: DiseaseRepository.DISEASES[1]
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Kertas)
    ) {
        TomatScanHeader(
            title = "TomatScan",
            subtitle = "Info Penyakit",
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
            // Main card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (disease.isHealthy) DaunPucat else BataPucat)
                    .border(1.dp, Garis, RoundedCornerShape(14.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatusBadge(isHealthy = disease.isHealthy)

                    Text(
                        text = disease.displayName,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Tinta
                    )

                    Text(
                        text = disease.scientificName,
                        style = MaterialTheme.typography.titleMedium,
                        fontStyle = FontStyle.Italic,
                        color = Tinta2
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Putih.copy(alpha = 0.9f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Tindakan: ${disease.fieldAction}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (disease.isHealthy) Daun else Bata
                        )
                    }
                }
            }

            // Section 1: Gejala pada Daun
            DiseaseInfoSectionCard(
                icon = Icons.Default.Eco,
                title = "Gejala pada Daun",
                content = disease.symptoms
            )

            // Section 2: Kondisi Pemicu
            DiseaseInfoSectionCard(
                icon = Icons.Default.Thermostat,
                title = "Kondisi Pemicu",
                content = disease.triggers
            )

            // Section 3: Penanganan Lapangan
            DiseaseInfoSectionCard(
                icon = Icons.Default.MedicalServices,
                title = "Penanganan & Rekomendasi Umum",
                content = disease.handling
            )

            // Section 4: Sumber Rujukan
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Putih)
                    .border(1.dp, Garis, RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Sumber Rujukan Ilmiah",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Tinta2
                    )
                    Text(
                        text = disease.reference,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Tinta,
                        lineHeight = 18.sp
                    )
                }
            }

            // CTA Button: Scan Leaf
            Button(
                onClick = onScanClick,
                colors = ButtonDefaults.buttonColors(containerColor = Nila),
                shape = RoundedCornerShape(999.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("btn_scan_this_disease")
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoCamera,
                    contentDescription = null,
                    tint = Putih,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Buka Kamera Pindai",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Putih
                )
            }

            NoticeBanner()

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun DiseaseInfoSectionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    content: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Putih)
            .border(1.dp, Garis, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Nila,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Tinta
                )
            }

            Text(
                text = content,
                style = MaterialTheme.typography.bodyLarge,
                color = Tinta2,
                lineHeight = 22.sp
            )
        }
    }
}
