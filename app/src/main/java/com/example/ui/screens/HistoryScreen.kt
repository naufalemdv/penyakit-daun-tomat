package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.DetectionRecord
import com.example.ui.components.DeleteConfirmDialog
import com.example.ui.components.StatusBadge
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
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    viewModel: MainViewModel,
    onOpenRecord: (DetectionRecord) -> Unit,
    onScanClick: () -> Unit
) {
    val historyList by viewModel.historyList.collectAsState()
    var recordToDelete by remember { mutableStateOf<DetectionRecord?>(null) }
    var showDeleteAllConfirm by remember { mutableStateOf(false) }

    if (recordToDelete != null) {
        DeleteConfirmDialog(
            title = "Hapus Riwayat?",
            message = "Hasil pemindaian ini akan dihapus permanen.",
            onConfirm = {
                val toDelete = recordToDelete
                recordToDelete = null
                toDelete?.let { viewModel.deleteCurrentRecord(it) }
            },
            onDismiss = { recordToDelete = null }
        )
    }

    if (showDeleteAllConfirm) {
        DeleteConfirmDialog(
            title = "Hapus Semua Riwayat?",
            message = "Semua riwayat pemindaian dan file citra akan dihapus permanen dari memori HP.",
            onConfirm = {
                showDeleteAllConfirm = false
                viewModel.deleteAllRecords()
            },
            onDismiss = { showDeleteAllConfirm = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Kertas)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Screen Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "Riwayat Pemindaian",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Nila
                )
                Text(
                    text = "${historyList.size} pengujian tersimpan lokal",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Tinta2
                )
            }

            if (historyList.isNotEmpty()) {
                IconButton(
                    onClick = { showDeleteAllConfirm = true },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Putih)
                        .border(1.dp, Garis, CircleShape)
                        .testTag("btn_delete_all_history")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "Hapus Semua",
                        tint = Bata,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        if (historyList.isEmpty()) {
            // Empty State
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.padding(horizontal = 24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(NilaPucat),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = Nila,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Text(
                        text = "Belum Ada Riwayat",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Tinta
                    )

                    Text(
                        text = "Data foto dan hasil diagnosis tanaman tomat akan tersimpan di sini secara otomatis.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Tinta2,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Button(
                        onClick = onScanClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Nila),
                        shape = RoundedCornerShape(999.dp),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = null,
                            tint = Putih,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Pindai Sekarang", color = Putih, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(historyList, key = { it.id }) { record ->
                    HistoryItemCard(
                        record = record,
                        onClick = { onOpenRecord(record) },
                        onDelete = { recordToDelete = record }
                    )
                }
            }
        }
    }
}

@Composable
private fun HistoryItemCard(
    record: DetectionRecord,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val dateStr = remember(record.waktu) {
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        sdf.format(Date(record.waktu))
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Putih)
            .border(1.dp, Garis, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(12.dp)
            .testTag("history_item_${record.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Leaf Thumbnail
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, Garis, RoundedCornerShape(8.dp))
                    .background(Kertas)
            ) {
                val file = File(record.path_foto)
                if (file.exists()) {
                    AsyncImage(
                        model = file,
                        contentDescription = "Foto Daun",
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
            }

            // Info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusBadge(
                        isHealthy = !record.is_terinfeksi,
                        isUncertain = record.status == "tidak_yakin"
                    )

                    Text(
                        text = "${(record.keyakinan * 100).toInt()}%",
                        style = MonoDataStyle.copy(fontSize = 13.sp, fontWeight = FontWeight.Bold),
                        color = if (record.is_terinfeksi) Bata else Daun
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
                    text = dateStr,
                    style = MonoDataStyle.copy(fontSize = 11.sp),
                    color = Tinta2
                )
            }

            // Delete action
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Hapus Item",
                    tint = Tinta2,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
